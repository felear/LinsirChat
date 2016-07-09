package com.foxconn.linsirchat.module.conversation.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.foxconn.linsirchat.MyApplication;
import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.foxconn.linsirchat.base.NetCallback;
import com.foxconn.linsirchat.common.adapter.ChatAdapter;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.common.utils.WDUtils;
import com.foxconn.linsirchat.module.conversation.bean.MsgBean;
import com.foxconn.linsirchat.module.main.ui.MainActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.se7en.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends BaseActivity implements GestureDetector.OnGestureListener,View.OnTouchListener{

    private TextView mtvTile;
    private EditText meditMsg;
    private Button mbtnSend;
    private String mstrTel;

    @ViewInject(R.id.rcv_chat)
    private RecyclerView mrcvChat;
    @ViewInject(R.id.srflayout_chat)
    private SwipeRefreshLayout msrfLayout;

    private List<MsgBean> mList;
    private ChatAdapter mAdapter;
    private MsgBean mMsgBean;
    private BroadcastReceiver mLocalBroadcastReceiver;
    private GestureDetector mDetector;

    @Override
    protected int setViewId() {
        return R.layout.activity_chatroom;
    }

    @Override
    protected void findViews() {
        mtvTile = (TextView) findViewById(R.id.tv_chat_frendName);
        meditMsg = (EditText) findViewById(R.id.edit_message);
        mbtnSend = (Button) findViewById(R.id.btn_send);
        ViewUtils.inject(this);
    }

    @Override
    protected void onResume() {
        MyApplication.mForegroundChatUserTel = mstrTel;
        super.onResume();
    }

    @Override
    protected void onPause() {
        MyApplication.mForegroundChatUserTel = Constant.CHAT_IN_BACK;
        super.onPause();
    }

    @Override
    protected void init() {
        Bundle bundle = getIntent().getExtras();

        mstrTel = bundle.getString("tel");
        MyApplication.mForegroundChatUserTel = mstrTel;
        readMsg(mstrTel);
        sendBroad();

        mtvTile.setText(bundle.getString("nick"));
        Log.d("print", mstrTel);

        mcanBack = true;
        try {
            mList = MyApplication.mDbUtils.findAll(Selector.from(MsgBean.class)
                    .where("receiver", "=", mstrTel).or("sender", "=", mstrTel).orderBy("time"));

        } catch (DbException e) {
            e.printStackTrace();
        }

        if (mList == null) {
            mList = new ArrayList<MsgBean>();
        }

        mAdapter = new ChatAdapter(this, mList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mrcvChat.setLayoutManager(linearLayoutManager);
        mrcvChat.setAdapter(mAdapter);
        mrcvChat.scrollToPosition(mList.size() - 1);

    }

    private void sendBroad() {
        // 发送广播
        Intent intent = new Intent(Constant.BROAD_URI_CHAT);
        intent.putExtra(Constant.USER_TEL, mstrTel);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void readMsg(String tel) {
        MsgBean msgBean = new MsgBean();
        msgBean.setRead(true);

        try {
            MyApplication.mDbUtils.update(msgBean,
                    WhereBuilder.b(Constant.MSG_SENDER, "=", tel),Constant.MSG_IS_READ);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initEvent() {

        // 创建手势监听器
        mDetector = new GestureDetector(this, this);
        mrcvChat.setOnTouchListener(this);

        meditMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    mbtnSend.setEnabled(true);
                } else {
                    mbtnSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mLocalBroadcastReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {

                if (!mstrTel.equals(intent.getStringExtra("tel"))) {
                    return;
                }

                List<MsgBean> list = null;
                try {
                    if (mList.size() > 1) {

                        list = MyApplication.mDbUtils.findAll(Selector.from(MsgBean.class)
                                .where("time", ">", mList.get(mList.size() - 1).getTime()));
                    } else {
                        list = MyApplication.mDbUtils.findAll(MsgBean.class);
                    }

                    if (list != null && list.size() > 0) {
                        mList.addAll(list);
                        mAdapter.notifyDataSetChanged();
                        mrcvChat.scrollToPosition(mList.size() - 1);
                    }

                } catch (DbException e) {
                    e.printStackTrace();
                    Log.d("print", "chat 132 : " + e);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalBroadcastReceiver, new IntentFilter(Constant.BROAD_URI_MSG));


    }

    @Override
    protected void loadData() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                mMsgBean = new MsgBean(meditMsg.getText().toString(), System.currentTimeMillis() + "");

                mMsgBean.setSender(SystemUtil.getSharedString(Constant.USER_TEL));
                mMsgBean.setReceiver(mstrTel);
                try {
                    MyApplication.mDbUtils.save(mMsgBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mMsgBean.setSendType(1);
                mList.add(mMsgBean);
                mAdapter.notifyDataSetChanged();
                mrcvChat.scrollToPosition(mList.size() - 1);
                WDUtils.sendMsd(mMsgBean, new NetCallback() {
                    @Override
                    public void success(String strResult) {
                        mMsgBean.setSendType(0);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void fail(String strResult) {
                        showLongToast("发送失败");
                        mMsgBean.setSendType(2);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                meditMsg.setText("");
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mLocalBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalBroadcastReceiver);

        // 置空当前聊天对象
        MyApplication.mForegroundChatUserTel = null;
        super.onDestroy();
    }

    // 将触碰事件交给Gesture处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (velocityX > 0) {

            // 向右滑动返回主菜单
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            overridePendingTransition(R.anim.anim_chat_start,R.anim.anim_chat_exit);

            finish();
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }
}

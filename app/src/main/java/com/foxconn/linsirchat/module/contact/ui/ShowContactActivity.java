package com.foxconn.linsirchat.module.contact.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.common.widget.CircleImageView;
import com.foxconn.linsirchat.module.contact.bean.ConversationBean;
import com.foxconn.linsirchat.module.conversation.ui.ChatRoomActivity;
import com.foxconn.linsirchat.module.me.ui.ModifyUserInfoActivity;
import com.foxconn.linsirchat.module.me.ui.ShowIconActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ShowContactActivity extends BaseActivity {

    private ConversationBean mUser;

    @ViewInject(R.id.iv_contact_icon)
    private CircleImageView mcivIcon;

    @ViewInject(R.id.tv_contact_name)
    private TextView mtvName;
    @ViewInject(R.id.tv_contact_gender)
    private TextView mtvGender;
    @ViewInject(R.id.tv_contact_age)
    private TextView mtvAge;
    @ViewInject(R.id.tv_contact_local)
    private TextView mtvLocal;
    @ViewInject(R.id.tv_contact_nick)
    private TextView mtvNick;
    @ViewInject(R.id.tv_me_tel)
    private TextView mtvTel;
    @ViewInject(R.id.tv_me_signature)
    private TextView mtvSignature;
    private BroadcastReceiver mLocalBroadcastReceiver;

    @Override
    protected int setViewId() {
        return R.layout.activity_show_contact;
    }

    @Override
    protected void findViews() {
        ViewUtils.inject(this);
    }

    @Override
    protected void init() {
        mcanBack = true;
    }

    @Override
    protected void initEvent() {

        // 接收消息列表变化监听
        mLocalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String stringExtra = intent.getStringExtra(Constant.ME_ITEM_MSG);
                switch (intent.getIntExtra(Constant.ME_ITEM_TYPE, -1)) {
                    case Constant.ME_TYPE_NAME:
                        mtvName.setText(stringExtra);
                        break;

                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalBroadcastReceiver, new IntentFilter(Constant.BROAD_URI_MODIFY));

    }

    @Override
    public void onDestroy() {
        // 取消广播注册
        if (mLocalBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void loadData() {

        mUser = (ConversationBean) getIntent().getExtras().getSerializable("user");

        Log.d("print1", mUser + "");
        String name = mUser.getName();
        String gender = mUser.getGender();

        if (name == null) {
            mtvName.setText(mUser.getNick());
        } else {
            mtvName.setText(name);
        }

        if (mUser.getIcon() == null) {
            if (TextUtils.equals(gender, "男")) {
                mcivIcon.setImageResource(R.mipmap.man1);
            } else {
                mcivIcon.setImageResource(R.mipmap.woman2);
            }
        }

        mtvGender.setText(gender);
        mtvAge.setText(mUser.getAge());
        mtvLocal.setText(mUser.getLocal());
        mtvNick.setText(mUser.getNick());
        mtvTel.setText(mUser.getTel());
        mtvSignature.setText(mUser.getSignature());

    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent();
        Class c = null;

        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.iv_contact_icon:

                // 查看头像
                intent.putExtra("gender", mUser.getGender());
                c = ShowIconActivity.class;

                break;
            case R.id.layout_name:
                // 修改备注
                intent.putExtra(Constant.ME_ITEM_TITLE, "设置备注");
                intent.putExtra(Constant.ME_ITEM_MSG, mtvName.getText());
                intent.putExtra(Constant.ME_ITEM_TYPE, Constant.ME_TYPE_NAME);
                intent.putExtra(Constant.USER_TEL, mUser.getTel());
                c = ModifyUserInfoActivity.class;

                break;
            case R.id.btn_send:

                // 跳转至聊天界面
                Bundle bundle = new Bundle();
                bundle.putString("nick", mUser.getNick());
                bundle.putString("tel", mUser.getTel());
                intent.putExtras(bundle);
                c = ChatRoomActivity.class;

                break;
        }
        if (c != null) {
            intent.setClass(this, c);
            startActivity(intent);
        }
    }
}

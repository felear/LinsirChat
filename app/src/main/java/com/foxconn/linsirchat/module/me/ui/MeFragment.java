package com.foxconn.linsirchat.module.me.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foxconn.linsirchat.MyApplication;
import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseFragment;
import com.foxconn.linsirchat.base.NetCallback;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.common.utils.WDUtils;
import com.foxconn.linsirchat.common.widget.CircleImageView;
import com.foxconn.linsirchat.module.contact.bean.ConversationBean;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.se7en.utils.SystemUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/7/5.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {

    @ViewInject(R.id.tv_me_nick)
    private TextView mtvNick;
    @ViewInject(R.id.tv_me_tel)
    private TextView mtvTel;
    @ViewInject(R.id.tv_me_gender)
    private TextView mtvGender;
    @ViewInject(R.id.tv_me_signature)
    private TextView mtvSignature;
    @ViewInject(R.id.tv_me_age)
    private TextView mtvAge;
    @ViewInject(R.id.tv_me_local)
    private TextView mtvLocal;

    @ViewInject(R.id.civ_me_icon)
    private CircleImageView mcivIcon;

    @ViewInject(R.id.layout_sign)
    private LinearLayout mLayoutSign;
    @ViewInject(R.id.layout_age)
    private LinearLayout mLayoutAge;
    @ViewInject(R.id.layout_gender)
    private LinearLayout mLayoutGender;
    @ViewInject(R.id.layout_nick)
    private LinearLayout mLayoutNick;
    @ViewInject(R.id.layout_icon)
    private LinearLayout mLayoutIcon;
    @ViewInject(R.id.layout_tel)
    private LinearLayout mLayoutTel;
    @ViewInject(R.id.layout_local)
    private LinearLayout mLayoutLocal;

    private String mstrTel;
    private ConversationBean mConversation = new ConversationBean();
    private BroadcastReceiver mLocalBroadcastReceiver;
    private int mNum;

    @Override
    protected int setViewId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void findViews(View view) {
        ViewUtils.inject(this, view);
    }

    @Override
    protected void init() {
        mstrTel = SystemUtil.getSharedString(Constant.USER_TEL);
        try {
            List<ConversationBean> list = MyApplication.mDbUtils.findAll(Selector.from(ConversationBean.class)
                    .where(Constant.USER_TEL, "=", mstrTel));
            if (list != null && list.size() > 0) {
                mConversation = list.get(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initEvent() {
        mLayoutSign.setOnClickListener(this);
        mLayoutAge.setOnClickListener(this);
        mLayoutGender.setOnClickListener(this);
        mLayoutNick.setOnClickListener(this);
        mLayoutTel.setOnClickListener(this);
        mLayoutIcon.setOnClickListener(this);
        mLayoutLocal.setOnClickListener(this);
    }

    @Override
    protected void loadData() {

        String gender = mConversation.getGender();

        setIcon(gender);
        mtvNick.setText(mConversation.getNick());
        mtvGender.setText(gender);
        mtvTel.setText(mstrTel);
        mtvSignature.setText(mConversation.getSignature());
        mtvAge.setText(mConversation.getAge());
        mtvLocal.setText(mConversation.getLocal());


        // 接受消息列表变化监听
        mLocalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String stringExtra = intent.getStringExtra(Constant.ME_ITEM_MSG);
                switch (intent.getIntExtra(Constant.ME_ITEM_TYPE, -1)) {
                    case Constant.ME_TYPE_SIGN:
                        mtvSignature.setText(stringExtra);
                        break;
                    case Constant.ME_TYPE_AGE:
                        mtvAge.setText(stringExtra);
                        break;
                    case Constant.ME_TYPE_Nick:
                        mtvNick.setText(stringExtra);
                        break;
                    case Constant.ME_TYPE_LOCAL:
                        mtvLocal.setText(stringExtra);
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mLocalBroadcastReceiver, new IntentFilter(Constant.BROAD_URI_MODIFY));

    }

    private void setIcon(String gender) {
        if (TextUtils.equals(gender, "男")) {
            mcivIcon.setImageResource(R.mipmap.icon_man);
        } else {
            mcivIcon.setImageResource(R.mipmap.icon_woman2);
        }
    }

    @Override
    public void onDestroy() {
        // 取消广播注册
        if (mLocalBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLocalBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent();
        Class c = null;

        switch (view.getId()) {
            case R.id.layout_sign:
                // 跳转页面，开始更改签名
                intent.putExtra(Constant.ME_ITEM_TITLE, "更改签名");
                intent.putExtra(Constant.ME_ITEM_MSG, mtvSignature.getText());
                intent.putExtra(Constant.ME_ITEM_TYPE, Constant.ME_TYPE_SIGN);
                c = ModifyUserInfoActivity.class;
                break;
            case R.id.layout_age:
                // 跳转也，开始更改年龄
                intent.putExtra(Constant.ME_ITEM_TITLE, "更改年龄");
                intent.putExtra(Constant.ME_ITEM_MSG, mtvAge.getText());
                intent.putExtra(Constant.ME_ITEM_TYPE, Constant.ME_TYPE_AGE);
                c = ModifyUserInfoActivity.class;
                break;
            case R.id.layout_nick:
                // 跳转也，开始更改昵称
                intent.putExtra(Constant.ME_ITEM_TITLE, "更改昵称");
                intent.putExtra(Constant.ME_ITEM_MSG, mtvNick.getText());
                intent.putExtra(Constant.ME_ITEM_TYPE, Constant.ME_TYPE_Nick);
                c = ModifyUserInfoActivity.class;
                break;
            case R.id.layout_gender:
                // 更改性别
                changeGender();

                break;
            case R.id.layout_tel:
                // 更改电话号码

                break;
            case R.id.layout_icon:
                // 更改头像

                break;
            case R.id.layout_local:
                // 更改地区
                intent.putExtra(Constant.ME_ITEM_TITLE, "更改地区");
                intent.putExtra(Constant.ME_ITEM_MSG, mtvLocal.getText());
                intent.putExtra(Constant.ME_ITEM_TYPE, Constant.ME_TYPE_LOCAL);
                c = ModifyUserInfoActivity.class;
                break;
        }
        if (c != null) {
            intent.setClass(getActivity(), c);
            getActivity().startActivity(intent);
        }
    }

    private void changeGender() {
        final String[] sex = {"男", "女"};
        mNum = 0;
        if (TextUtils.equals(mConversation.getGender(), "女")) {
            mNum = 1;
        }
        new AlertDialog.Builder(getActivity())
                .setTitle("请选择性别")
                .setSingleChoiceItems(sex, mNum, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNum = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 判断是否更改了性别
                        if (!TextUtils.equals(mConversation.getGender(), sex[mNum])) {
                            // 更改云端数据库
                            WDUtils.changeElement(mstrTel, Constant.ME_TYPE_GENDER, sex[mNum], new NetCallback() {
                                @Override
                                public void success(String strResult) {
                                    mtvGender.setText(sex[mNum]);
                                    mConversation.setGender(sex[mNum]);
                                    setIcon(sex[mNum]);
                                }

                                @Override
                                public void fail(String strResult) {

                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

}

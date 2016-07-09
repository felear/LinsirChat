package com.foxconn.linsirchat.module.me.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.foxconn.linsirchat.base.NetCallback;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.common.utils.WDUtils;
import com.foxconn.linsirchat.common.utils.WindowUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.se7en.utils.SystemUtil;

public class ModifyUserInfoActivity extends BaseActivity {
    @ViewInject(R.id.tv_modify_title)
    private TextView mtvTitle;
    @ViewInject(R.id.tv_modify_tips)
    private TextView mtvTips;
    @ViewInject(R.id.edit_modify)
    private EditText medit;
    @ViewInject(R.id.tv_me_save)
    private TextView mtvSave;


    private String mstrTel;
    private int mType;
    private PopupWindow mPopupWindow;

    @Override
    protected int setViewId() {
        return R.layout.activity_modify_user_info;
    }

    @Override
    protected void findViews() {
        ViewUtils.inject(this);
    }

    @Override
    protected void init() {
        mstrTel = SystemUtil.getSharedString(Constant.USER_TEL);
        mcanBack = true;

    }

    @Override
    protected void initEvent() {
        medit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() < 1) {
                    mtvSave.setVisibility(View.GONE);
                } else {
                    mtvSave.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void loadData() {
        Intent intent = getIntent();
        medit.setText(intent.getStringExtra(Constant.ME_ITEM_MSG));
        mtvTitle.setText(intent.getStringExtra(Constant.ME_ITEM_TITLE));
        mtvTips.setText(intent.getStringExtra(Constant.ME_ITEM_TIPS));
        mType = intent.getIntExtra(Constant.ME_ITEM_TYPE, -1);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_me_save:
                // TODO 保存数据

                mPopupWindow = WindowUtils.showLoadPopopWindow(this, mtvTitle);

                WDUtils.changeElement(mstrTel, mType, medit.getText().toString(), new NetCallback() {
                    @Override
                    public void success(String strResult) {

                        // 延时1s返回
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                showLongToast("更改成功");

                                // 发送广播通知其他界面更改UI
                                Intent intent = new Intent(Constant.BROAD_URI_MODIFY);
                                intent.putExtra(Constant.ME_ITEM_TYPE, mType);
                                intent.putExtra(Constant.ME_ITEM_MSG, medit.getText().toString());
                                LocalBroadcastManager.getInstance(ModifyUserInfoActivity.this).sendBroadcast(intent);

                                // 收起弹出框
                                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                                    mPopupWindow.dismiss();
                                }
                                finish();
                            }
                        }, 1000);
                    }

                    @Override
                    public void fail(String strResult) {
                        // 修改失败提示
                        if (mPopupWindow != null && mPopupWindow.isShowing()) {
                            mPopupWindow.dismiss();
                        }
                        showLongToast("更改失败");
                    }
                });

                break;
        }
    }
}

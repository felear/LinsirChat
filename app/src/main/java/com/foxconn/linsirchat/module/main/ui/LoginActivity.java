package com.foxconn.linsirchat.module.main.ui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.foxconn.linsirchat.base.NetCallback;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.common.utils.WDUtils;
import com.se7en.utils.SystemUtil;

public class LoginActivity extends BaseActivity {

    private Button mbtnSend;
    private EditText meditTel;
    private EditText meditPwd;
    private String mstrTel;
    private String mstrPwd;
    private TextView mtvTip;

    @Override
    protected int setViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void findViews() {
        mbtnSend = (Button) findViewById(R.id.btn_send);
        meditTel = (EditText) findViewById(R.id.edit_tel);
        meditPwd = (EditText) findViewById(R.id.edit_pwd);
        mtvTip = (TextView) findViewById(R.id.tv_log_tip);
    }

    @Override
    protected void init() {
    }

    @Override
    protected void initEvent() {
        meditTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    mstrTel = s.toString();
                else
                    mstrTel = null;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mstrTel != null && mstrPwd != null) {
                    mbtnSend.setEnabled(true);
                } else {
                    mbtnSend.setEnabled(false);
                }
            }
        });

        meditPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    mstrPwd = s.toString();
                else
                    mstrPwd = null;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mstrTel != null && mstrPwd != null) {
                    mbtnSend.setEnabled(true);
                } else {
                    mbtnSend.setEnabled(false);
                }
            }
        });

    }

    @Override
    protected void loadData() {
        mstrPwd = SystemUtil.getSharedString(Constant.USERPWD);
        mstrTel = SystemUtil.getSharedString(Constant.USERTEL);
        if (mstrTel != null) {
            meditTel.setText(mstrTel);
        }
        if (mstrPwd != null) {
            meditPwd.setText(mstrPwd);
        }
    }

    @Override
    public void onClick(View view) {

        Class c = null;
        switch (view.getId()) {
            case R.id.tv_forgot:
                c = FindPwdActivity.class;
                break;
            case R.id.btn_log2reg:
                c = RegisterActivity.class;
                break;
            case R.id.btn_send:

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);

                if (!WDUtils.containTel(mstrTel)) {
                    mtvTip.setVisibility(View.VISIBLE);
                    mtvTip.setText("用户名不存在！");
                    return;
                }

                WDUtils.login(mstrTel, mstrPwd, new NetCallback() {
                    @Override
                    public void success(String strResult) {
                        if ("success".equals(strResult)) {

                            SystemUtil.setSharedString(Constant.USERTEL, mstrTel);
                            SystemUtil.setSharedString(Constant.USERPWD, mstrPwd);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            mtvTip.setVisibility(View.VISIBLE);
                            mtvTip.setText("密码错误！");
                        }
                    }

                    @Override
                    public void fail(String strResult) {

                    }
                });

                break;
        }
        if (c != null) {
            Intent intent = new Intent(this, c);
            startActivity(intent);
        }
    }
}

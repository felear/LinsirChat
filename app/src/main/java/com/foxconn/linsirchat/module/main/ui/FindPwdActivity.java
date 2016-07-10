package com.foxconn.linsirchat.module.main.ui;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.foxconn.linsirchat.common.utils.WDUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class FindPwdActivity extends BaseActivity {


    @ViewInject(R.id.tv_tel_tip)
    private TextView mtvTelTip;
    @ViewInject(R.id.tv_pwd_tip)
    private TextView mtvPwdTip;
    @ViewInject(R.id.btn_code)
    private TextView btn_code;

    @ViewInject(R.id.btn_send)
    private Button mbtnSend;

    @ViewInject(R.id.edit_tel)
    private EditText meditTel;
    @ViewInject(R.id.edit_code)
    private EditText meditCode;
    @ViewInject(R.id.edit_pwd)
    private EditText meditPwd;
    private String mstrPwd;
    private String mstrTel;
    private String mstrCode;
    private int miCnt;

    @Override
    protected int setViewId() {
        return R.layout.activity_find_pwd;
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

        meditTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mstrTel = s.toString();
                } else {
                    mstrTel = null;
                }

                if (s.length() == 11 && !WDUtils.containTel(s.toString())) {
                    mtvTelTip.setVisibility(View.VISIBLE);
                } else {
                    mtvTelTip.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                setBtnEnable();
            }
        });

        meditCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mstrCode = s.toString();
                } else {
                    mstrCode = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                setBtnEnable();
            }
        });

        meditPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5) {
                    mtvPwdTip.setVisibility(View.GONE);
                    mstrPwd = s.toString();
                } else {
                    mtvPwdTip.setVisibility(View.VISIBLE);
                    mstrPwd = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                setBtnEnable();
            }
        });

    }

    // 判断"注册"按键是否可点击方法
    private void setBtnEnable() {
        // 当必选项不为空时，按键设置为可点击
        if (mstrTel != null && mstrPwd != null && mstrCode != null) {
            mbtnSend.setEnabled(true);
        } else {
            mbtnSend.setEnabled(false);
        }
    }


    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View view) {
        Class c = null;
        switch (view.getId()) {
            case R.id.btn_2reg:
                c = RegisterActivity.class;
                break;
            case R.id.btn_back:
                c = LoginActivity.class;
                break;
            case R.id.btn_code:
                // 获取验证码事件处理
                btn_code.setClickable(false);
                miCnt = 120;
                new CountDownTimer(miCnt * 1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        btn_code.setText("" + miCnt--);
                    }

                    @Override
                    public void onFinish() {
                        btn_code.setClickable(true);
                        btn_code.setText("重新获取验证码");
                    }
                }.start();

                break;
            case R.id.btn_send:
                // 提交

                break;
        }

        if (c != null) {
            Intent intent = new Intent(this, c);
            startActivity(intent);
            finish();
        }

    }
}

package com.foxconn.linsirchat.module.main.ui;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.foxconn.linsirchat.base.NetCallback;
import com.foxconn.linsirchat.common.utils.WDUtils;
import com.foxconn.linsirchat.module.contact.bean.UserBean;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity {

    private CheckBox mcheckBox;
    private TextView btn_code;
    private int miCnt = 120;
    private EditText meditTel;
    private EditText meditPwd;
    private EditText meditNick;
    private RadioButton mrbMan;
    private EditText meditAge;
    private TextView mtvTip;
    private String mstrTel;
    private String mstrPwd;
    private Button mbtnSend;
    private String mstrNick;
    private String mgender = "先生";

    @Override
    protected int setViewId() {
        return R.layout.activity_register;
    }

    @Override
    protected void findViews() {

        mcheckBox = (CheckBox) findViewById(R.id.checkBox);
        mcheckBox.setMovementMethod(new LinkMovementMethod());
        meditTel = (EditText) findViewById(R.id.edit_tel);
        meditPwd = (EditText) findViewById(R.id.edit_pwd);
        meditNick = (EditText) findViewById(R.id.edit_nick);
        meditAge = (EditText) findViewById(R.id.edit_age);
        mrbMan = (RadioButton) findViewById(R.id.rb_reg_man);
        btn_code = (TextView) findViewById(R.id.btn_code);
        mtvTip = (TextView) findViewById(R.id.tv_reg_tip);
        mbtnSend = (Button) findViewById(R.id.btn_send);
    }

    @Override
    protected void init() {
        mcanBack = true;
    }

    @Override
    protected void initEvent() {

        mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBtnEnable();
            }
        });

        meditNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    mstrNick = s.toString();
                else
                    mstrNick = null;

            }

            @Override
            public void afterTextChanged(Editable s) {
                setBtnEnable();
            }
        });

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
                setBtnEnable();
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
                setBtnEnable();
            }
        });

    }

    private void setBtnEnable() {
        if (mstrTel != null && mstrPwd != null &&
                mcheckBox.isChecked() && mstrNick != null) {
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
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_code:
                btn_code.setClickable(false);
                new CountDownTimer(miCnt * 1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        btn_code.setText("" + miCnt--);
                    }

                    @Override
                    public void onFinish() {
                        btn_code.setClickable(true);
                        miCnt = 120;
                        btn_code.setText("重新获取验证码");
                    }
                }.start();
                break;

            case R.id.tv_reg_agree:
                Intent intent = new Intent(this, AgreementActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_send:


                if (WDUtils.containTel(mstrTel)) {
                    mtvTip.setVisibility(View.VISIBLE);
                    mtvTip.setText("手机号码已存在");
                    return;
                }

                UserBean userBean = new UserBean();

                userBean.setNick(mstrNick);
                userBean.setTel(mstrTel);
                userBean.setPwd(mstrPwd);

                userBean.setAge(meditAge.getText() + "");

                if (!mrbMan.isChecked()) {
                    userBean.setGender("女");
                    mgender = "女士";
                } else {
                    userBean.setGender("男");
                }



                WDUtils.register(userBean, new NetCallback() {
                    @Override
                    public void success(String strResult) {
                        showLongToast(mstrNick + mgender + "，注册成功");
                        finish();
                    }

                    @Override
                    public void fail(String strResult) {
                        showLongToast(strResult);
                    }
                });
                break;
        }
    }
}

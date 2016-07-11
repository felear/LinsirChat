package com.foxconn.linsirchat.module.main.ui;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.foxconn.linsirchat.base.NetCallback;
import com.foxconn.linsirchat.common.utils.WDUtils;
import com.foxconn.linsirchat.common.utils.WindowUtils;
import com.foxconn.linsirchat.module.contact.bean.UserBean;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.se7en.utils.NetworkUtils;

import cn.smssdk.DefaultOnSendMessageHandler;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends BaseActivity {

    @ViewInject(R.id.checkBox)
    private CheckBox mcheckBox;
    @ViewInject(R.id.rb_reg_man)
    private RadioButton mrbMan;
    @ViewInject(R.id.btn_code)
    private TextView btn_code;
    @ViewInject(R.id.tv_reg_tip)
    private TextView mtvTip;
    @ViewInject(R.id.tv_tel_tip)
    private TextView mtvTelTip;
    @ViewInject(R.id.tv_pwd_tip)
    private TextView mtvPwdTip;

    @ViewInject(R.id.btn_send)
    private Button mbtnSend;

    @ViewInject(R.id.edit_tel)
    private EditText meditTel;
    @ViewInject(R.id.edit_code)
    private EditText meditCode;
    @ViewInject(R.id.edit_pwd)
    private EditText meditPwd;
    @ViewInject(R.id.edit_nick)
    private EditText meditNick;
    @ViewInject(R.id.edit_age)
    private EditText meditAge;

    private PopupWindow mPopupWindow;

    private int miCnt = 120;
    private String mstrTel;
    private String mstrPwd;
    private String mstrNick;
    private String mgender = "先生";
    private String mstrCode;
    private String mstrPhone;

    @Override
    protected int setViewId() {
        return R.layout.activity_register;
    }

    @Override
    protected void findViews() {
        ViewUtils.inject(this);
    }

    @Override
    protected void init() {
        // 设置界面单击可以返回，详情看BaseActivity
        mcanBack = true;
    }

    @Override
    protected void initEvent() {
        // 同意协议事件监听
        mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBtnEnable();
            }
        });

        // 设置编辑昵称事件监听
        meditNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入的文字长度大于0时，赋值给全局变量
                if (s.length() > 0)
                    mstrNick = s.toString();
                else
                    mstrNick = null;

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 判断“注册”按键是否可点击
                setBtnEnable();
            }
        });

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

                if (s.length() == 11 && WDUtils.containTel(s.toString())) {
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
        if (mstrTel != null && mstrPwd != null &&
                mcheckBox.isChecked() && mstrNick != null && mstrCode != null) {
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
                // 获取短信验证码
                SMSSDK.registerEventHandler(new EventHandler() {
                    public void afterEvent(int event, int result, Object data) {
                        // 解析注册结果
                        if (result == SMSSDK.RESULT_COMPLETE) {
//                            @SuppressWarnings("unchecked")
                            Log.d("print", data + "");
                        }
                        // 提交用户
                    }
                });
                SMSSDK.getVerificationCode("86", mstrTel, new DefaultOnSendMessageHandler());

                // 获取验证码事件处理
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
                // 提交注册事件处理
                if (mstrPhone != null && mstrPhone.equals(mstrTel))
                    showLongToast("验证成功！");
                else {
                    showLongToast("验证失败！");
                    return;
                }

                // 当手机号码已存在，提示用户，并停止处理事件
                if (WDUtils.containTel(mstrTel)) {
                    mtvTip.setVisibility(View.VISIBLE);
                    mtvTip.setText("手机号码已存在");
                    return;
                }

                // 新建保存用户信息对象
                UserBean userBean = new UserBean();

                // 设置用户昵称，手机号码，密码，年龄
                userBean.setNick(mstrNick);
                userBean.setTel(mstrTel);
                userBean.setPwd(mstrPwd);
                userBean.setAge(meditAge.getText() + "");

                // 保存用户性别
                if (!mrbMan.isChecked()) {
                    userBean.setGender("女");
                    mgender = "女士";
                } else {
                    userBean.setGender("男");
                }

                // 判断网络是否已连接，如未连接，停止处理
                if (!NetworkUtils.isOnline()) {
                    mtvTip.setVisibility(View.VISIBLE);
                    mtvTip.setText("网络无法连接！");
                    return;
                }

                // 弹出对话框
                mPopupWindow = WindowUtils.showLoadPopopWindow(this, meditTel);

                WDUtils.register(userBean, new NetCallback() {
                    @Override
                    public void success(String strResult) {

                        // 延时1秒执行
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 注册成功提示
                                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                                    mPopupWindow.dismiss();
                                }
                                showLongToast(mstrNick + mgender + "，注册成功");
                                finish();
                            }
                        }, 1000);
                    }

                    @Override
                    public void fail(String strResult) {


                        // 注册失败提示
                        if (mPopupWindow != null && mPopupWindow.isShowing()) {
                            mPopupWindow.dismiss();
                        }
                        showLongToast("注册失败");
                    }
                });
                break;
        }
    }
}

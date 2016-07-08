package com.foxconn.linsirchat.module.main.ui;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.se7en.utils.NetworkUtils;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected int setViewId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_welcome;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void init() {
        if (NetworkUtils.isOnline()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        } else {
            showLongToast("当前没有可用网络");
        }
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void onClick(View view) {

    }
}

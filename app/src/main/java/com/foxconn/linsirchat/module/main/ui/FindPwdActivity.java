package com.foxconn.linsirchat.module.main.ui;

import android.content.Intent;
import android.view.View;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;

public class FindPwdActivity extends BaseActivity {

    @Override
    protected int setViewId() {
        return R.layout.activity_find_pwd;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void init() {
        mcanBack = true;
    }

    @Override
    protected void initEvent() {

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
        }

        if (c != null) {
            Intent intent = new Intent(this, c);
            startActivity(intent);
            finish();
        }

    }
}

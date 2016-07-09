package com.foxconn.linsirchat.module.me.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ShowIconActivity extends BaseActivity {

    @ViewInject(R.id.iv_show_icon)
    private ImageView mivIcon;

    @Override
    protected int setViewId() {
        return R.layout.activity_show_icon;
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

    }

    @Override
    protected void loadData() {
        Intent intent = getIntent();
        String iconUrl = intent.getStringExtra("iconUrl");
        if (iconUrl == null) {
            String gender = intent.getStringExtra("gender");
            if (TextUtils.equals(gender, "男")) {
                mivIcon.setImageResource(R.mipmap.man1);
            } else {
                mivIcon.setImageResource(R.mipmap.woman2);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}

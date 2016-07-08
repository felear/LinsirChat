package com.foxconn.linsirchat.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/6/27.
 */
public abstract class BaseActivity extends AppCompatActivity{

    private long mend;
    private long mstart;
    protected boolean mcanBack;
    private PopupWindow mpopupWindow;

    @Override
    protected void onResume() {
        super.onResume();
        mstart = 0;
    }

    @Override
    public void onBackPressed() {

        if (mcanBack) {
            finish();
        }else {
            mend = System.currentTimeMillis();
            if (mend - mstart < 2000) {
                finish();
            } else {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mstart = mend;
            }
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setViewId());

        findViews();
        init();
        initEvent();
        loadData();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
        }
    }


    /**
     * 加载布局
     */
    protected abstract int setViewId();

    /**
     * 查找控件
     */
    protected abstract void findViews();

    /**
     * 数据初始化
     */
    protected  abstract void init();

    /**
     * 初始化各种事件监听
     */
    protected abstract void initEvent();

    /**
     * 加载数据
     */
    protected abstract void loadData();

    /**
     * 按键监听事件
     */
    public abstract void onClick(View view);

    public void showLongToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

}

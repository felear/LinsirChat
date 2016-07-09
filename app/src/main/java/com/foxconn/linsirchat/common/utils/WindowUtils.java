package com.foxconn.linsirchat.common.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.foxconn.linsirchat.R;

/**
 * Created by Administrator on 2016/7/8.
 */
public class WindowUtils {

    // 设置加载数据弹出框
    public static PopupWindow showLoadPopopWindow(Context context,View view) {
        // TODO 加载布局视图
        View pwView = LayoutInflater.from(context).inflate(R.layout.layout_pw_load, null, false);
        // 创建PopupWindow，参数4 false为不获取焦点
        PopupWindow popupWindow = new PopupWindow(pwView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                false);

        // 是否可以取消
        popupWindow.setOutsideTouchable(true);
        // 设置背景颜色
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置弹出动画
        popupWindow.setAnimationStyle(android.support.v7.appcompat.R.anim.abc_popup_enter);

        //  弹出视图方式二
         popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupWindow;
    }

}

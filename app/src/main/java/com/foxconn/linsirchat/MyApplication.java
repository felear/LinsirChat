package com.foxconn.linsirchat;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.lidroid.xutils.DbUtils;
import com.se7en.utils.NetworkUtils;
import com.se7en.utils.SystemUtil;
import com.wilddog.client.Wilddog;

import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/7/4.
 */
public class MyApplication extends Application {

    public static DbUtils mDbUtils;
    public static String mForegroundChatUserTel;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onTerminate() {
        unbindService(connection);
        super.onTerminate();
    }

    private void init() {
        SystemUtil.setContext(this);
        NetworkUtils.setContext(this);
        mDbUtils = DbUtils.create(this);
        Wilddog.setAndroidContext(this);
        SMSSDK.initSDK(this, "14c864772d70e", "fdb4b7a1dd9a2a1e8652b1fbb3e349ad");
        bindService(new Intent(this, MyService.class), connection, BIND_AUTO_CREATE);
    }

    private MyService myService;
    // 定义类继承ServiceConnection
    private ServiceConnection connection = new ServiceConnection() {

        // 连接成功时调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获得服务对象
            MyService.MyBind myBind = (MyService.MyBind) service;
            myService = myBind.getMyServiceInstance();

        }

        // 断开连接是调用
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void startReceviverMsg() {
        myService.reciverMsg();
    }

}

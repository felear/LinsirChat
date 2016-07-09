package com.foxconn.linsirchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.foxconn.linsirchat.base.NetCallback;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.common.utils.WDUtils;
import com.foxconn.linsirchat.module.contact.bean.ConversationBean;
import com.foxconn.linsirchat.module.conversation.ui.ChatRoomActivity;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

public class MyService extends Service {

    private LocalBroadcastManager broadcastManager;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotiManager;
    private String mstrTel;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stringExtra = intent.getStringExtra(Constant.USER_TEL);
            if (TextUtils.equals(stringExtra, mstrTel)) {
                mNotiManager.cancel(1003);
            }
        }

    };

    public MyService() {
    }

    // 定义绑定服务内部类
    public class MyBind extends Binder {

        public MyService getMyServiceInstance() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        init();
        loadUser();
    }

    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void init() {

        broadcastManager = LocalBroadcastManager.getInstance(this);
        mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true);//是否自动取消自己的通知，默认false不取消

        //必须设置小图标
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);//通知小图标，会在状态栏显示
        mBuilder.setTicker("您有1条新信息！");//通知弹出时状态栏的提示文本
        mBuilder.setContentTitle("user");//通知栏的通知标题
        mBuilder.setContentText("点击查看消息");//通知栏的通知内容

        IntentFilter filter = new IntentFilter(Constant.BROAD_URI_CHAT);
        broadcastManager.registerReceiver(mReceiver, filter);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }

    // 加载联系人方法
    public void loadUser() {
        WDUtils.getAllUser(new NetCallback() {
            @Override
            public void success(String strResult) {
                // 发送广播通知其他节目更新联系人列表
                Intent intent = new Intent(Constant.BROAD_URI_CONTACT);
                intent.putExtra("nick", strResult);
                broadcastManager.sendBroadcast(intent);
            }

            @Override
            public void fail(String strResult) {

            }
        });
    }

    public void reciverMsg() {
        WDUtils.receiveMsg(new NetCallback() {
            @Override
            public void success(String strResult) {
                if (WDUtils.containTel(strResult)) {
                    List<ConversationBean> list = null;
                    try {
                        list = MyApplication.mDbUtils.findAll(Selector.from(ConversationBean.class)
                                .where("tel", "=", strResult));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    ConversationBean iUser = list.get(0);

                    // 发送广播通知其他节目更新消息列表
                    Intent intent = new Intent(Constant.BROAD_URI_MSG);
                    intent.putExtra("tel", strResult);
                    broadcastManager.sendBroadcast(intent);

                    // 判断是否需要发送通知
                    if (TextUtils.equals(MyApplication.mForegroundChatUserTel, iUser.getTel()))
                        return;

                    //要得到一个pendingIntent对象，使用方法类的静态方法 getActivity(Context, int, Intent, int)
                    Intent intentNotify = null;
                    if (TextUtils.equals(MyApplication.mForegroundChatUserTel, Constant.CHAT_IN_BACK)) {
                        intentNotify = new Intent(Intent.ACTION_MAIN);
                        intentNotify.addCategory(Intent.CATEGORY_LAUNCHER);
                        intentNotify.setClass(MyService.this, ChatRoomActivity.class);
                        intentNotify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    } else {
                        intentNotify = new Intent(MyService.this, ChatRoomActivity.class);// this可以为四大组件
                    }
                    mstrTel = iUser.getTel();
                    Bundle bundle = new Bundle();
                    bundle.putString("nick", iUser.getNick());
                    bundle.putString("tel", mstrTel);
                    intentNotify.putExtras(bundle);
                    PendingIntent pIntent = PendingIntent.getActivity(MyService.this, 1003, intentNotify, PendingIntent.FLAG_UPDATE_CURRENT);
                    // requestCode 当requestCode值一样时，后面的消息才会覆盖之前的消息。不想覆盖，则可以设置不同的requestCode
                    mBuilder.setContentIntent(pIntent);//关联PendingIntent

                    mBuilder.setContentTitle(iUser.getNick());
                    mBuilder.setContentText(iUser.getBody());
                    mNotiManager.notify(1003, mBuilder.build());

                }
            }

            @Override
            public void fail(String strResult) {

            }
        });
    }

}

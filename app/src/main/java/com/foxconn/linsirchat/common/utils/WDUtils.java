package com.foxconn.linsirchat.common.utils;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.foxconn.linsirchat.MyApplication;
import com.foxconn.linsirchat.base.NetCallback;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.module.contact.bean.UserInfoBean;
import com.foxconn.linsirchat.module.conversation.bean.MsgBean;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.se7en.utils.SystemUtil;
import com.wilddog.client.ChildEventListener;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Query;
import com.wilddog.client.Wilddog;
import com.wilddog.client.WilddogError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2016/7/4.
 */
public class WDUtils {

//    public static boolean mFlag;

    public static void sendMsd(MsgBean msgBean, final NetCallback netCallback) {
        Wilddog wilddog = new Wilddog(Constant.CONVERSATION_URL + msgBean.getReceiver() + "/"
                + msgBean.getTime() + "/");

        Wilddog wilddog2 = new Wilddog(Constant.CONVERSATION_URL + msgBean.getSender() + "/"
                + msgBean.getTime() + "/");
        wilddog2.setValue(msgBean);

        saveToUser(msgBean.getReceiver(), msgBean);

        wilddog.setValue(msgBean, new Wilddog.CompletionListener() {
            @Override
            public void onComplete(WilddogError wilddogError, Wilddog wilddog) {
                if (netCallback == null) {
                    return;
                }
                if (wilddogError == null) {
                    netCallback.success("success");
                } else {
                    netCallback.fail(wilddogError.getMessage());
                }
            }
        });
    }

    public static void receiveMsg(final NetCallback netCallback) {
        String iTel = SystemUtil.getSharedString(Constant.USERTEL);
        Wilddog wilddog = new Wilddog(Constant.CONVERSATION_URL + iTel + "/");
        wilddog.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // 获取消息
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();

                try {
                    // 判断消息是否已经存在
                    List<MsgBean> list = MyApplication.mDbUtils.findAll(Selector.from(MsgBean.class)
                            .where("time", "=", value.get("time") + ""));
                    // 消息不存在则将消息存入数据库，并触发回调
                    if (list == null || list.size() < 1) {
                        // 将消息存入对象
                        MsgBean msgBean = new MsgBean();
                        msgBean.setBody(value.get("body") + "");
                        msgBean.setTime(value.get("time") + "");
                        msgBean.setReceiver(value.get("receiver") + "");
                        msgBean.setSender(value.get("sender") + "");
                        if (value.get("tpye") != null) {
                            msgBean.setType(Integer.parseInt(value.get("type") + ""));
                        }

                        // TODO 存入数据库
                        MyApplication.mDbUtils.save(msgBean);
                        if (containTel(value.get("sender") + ""))
                            saveToUser(value.get("sender") + "", msgBean);

                        // 通知调用者
                        if (netCallback != null) {
                            netCallback.success(value.get("sender") + "");
                        }

                    }
                } catch (DbException e) {
                    e.printStackTrace();
                    Log.d("print", "receiveMsg --- DbException : " + e);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("print", "receiveMsg --- onChildChanged : " + dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("print", "receiveMsg --- onChildRemoved : " + dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("print", "receiveMsg --- onChildMoved : " + dataSnapshot);
            }

            @Override
            public void onCancelled(WilddogError wilddogError) {
                Log.d("print", "receiveMsg --- onCancelled : " + wilddogError);
            }
        });
    }

    private static void saveToUser(String userTel, MsgBean msgBean) {
        if (TextUtils.equals(SystemUtil.getSharedString(Constant.USERTEL), userTel)) {
            return;
        }
        // 更新联系人信息
        List<UserInfoBean> listUser = null;
        UserInfoBean userInfoBean = null;

        if (containTel(userTel)) {
            try {
                listUser = MyApplication.mDbUtils.findAll(Selector.from(UserInfoBean.class)
                        .where("tel", "=", userTel));
            } catch (DbException e) {
                e.printStackTrace();
            }
            // 获得联系人
            userInfoBean = listUser.get(0);
            // 添加联系人最后消息
            userInfoBean.setMsg(msgBean);
            try {
                MyApplication.mDbUtils.update(userInfoBean, WhereBuilder
                        .b("tel", "=", userInfoBean.getTel()), "time", "body");
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean containTel(String strTel) {

        List<UserInfoBean> list = null;

        try {
            list = MyApplication.mDbUtils.findAll(Selector.from(UserInfoBean.class)
                    .where("tel", "=", strTel));
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (list == null || list.size() < 1) {
            return false;
        } else {
            return true;
        }

    }

    public static void getAllUser(final NetCallback netCallback) {

        Wilddog wilddog = new Wilddog(Constant.USER_INFO_URL);
        Query query = wilddog.orderByKey();
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                if (!containTel(dataSnapshot.getKey())) {

                    UserInfoBean iUser = new UserInfoBean();
                    iUser.setTel(dataSnapshot.getKey() + "");
                    iUser.setNick(map.get("nick") + "");
                    iUser.setAge(map.get("age") + "");
                    iUser.setGender(map.get("gender") + "");

                    try {
                        MyApplication.mDbUtils.save(iUser);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    if (netCallback != null) {
                        netCallback.success(map.get("nick") + "");
                    }
                    Log.d("print", "onChildAdded : " + iUser);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("print", "onChildChanged : " + dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("print", "onChildRemoved : " + dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("print", "onChildMoved : " + dataSnapshot);
            }

            @Override
            public void onCancelled(WilddogError wilddogError) {
                Log.d("print", "WilddogError : " + wilddogError.toString());
                if (netCallback != null) {
                    netCallback.fail(wilddogError + "");
                }
            }
        });
    }

    public static void register(HashMap<String, String> map, final NetCallback netCallback) {
        Wilddog wilddog = new Wilddog(Constant.USER_INFO_URL + map.get("tel") + "/");
        wilddog.setValue(map, new Wilddog.CompletionListener() {
            @Override
            public void onComplete(WilddogError wilddogError, Wilddog wilddog) {
                if (wilddogError == null) {
                    netCallback.success("success");
                } else {
                    netCallback.fail(wilddogError.getMessage());
                }
            }
        });
    }

    public static void login(String userName, final String userPwd, final NetCallback netCallback) {
        Wilddog wilddog = new Wilddog(Constant.USER_INFO_URL);
        Query query = wilddog.orderByKey().equalTo(userName);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                if (userPwd.equals(map.get("pwd") + "")) {
                    netCallback.success("success");
                } else {
                    netCallback.success("fail");
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(WilddogError wilddogError) {

            }
        });
    }

}

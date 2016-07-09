package com.foxconn.linsirchat.module.conversation.bean;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/5.
 */
@Table(name = "tbl_msg")
public class MsgBean implements Serializable{

    @Id
    private int id;
    private int type = 0;
    private int sendType = 0;

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    @NotNull
    private String body;
    @NotNull
    private String sender;
    @NotNull
    private String receiver;
    @NotNull
    private String time;

    private boolean isRead;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public MsgBean() {
    }

    public MsgBean(String body, String time) {
        this.body = body;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "MsgBean{" +
                "id=" + id +
                ", type=" + type +
                ", sendType=" + sendType +
                ", body='" + body + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

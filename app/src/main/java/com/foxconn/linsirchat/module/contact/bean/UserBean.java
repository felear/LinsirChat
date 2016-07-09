package com.foxconn.linsirchat.module.contact.bean;

import com.foxconn.linsirchat.module.conversation.bean.MsgBean;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Unique;

/**
 * Created by Administrator on 2016/7/7.
 */
@Table(name = "tbl_user")
public class UserBean {

    @Id
    private int id;
    private String nick;

    @Unique
    private String tel;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    private String pwd;
    private String Icon;
    private String age;
    private String gender;
    private String local;
    private String signature;

    public void setByConversationBean(ConversationBean conversationBean) {
        nick = conversationBean.getNick();
        tel = conversationBean.getTel();
        Icon = conversationBean.getIcon();
        age = conversationBean.getAge();
        gender = conversationBean.getGender();
        local = conversationBean.getLocal();
        signature = conversationBean.getSignature();
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserBean that = (UserBean) o;

        if (id != that.id) return false;
        return tel.equals(that.tel);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + tel.hashCode();
        return result;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }


}

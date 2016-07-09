package com.foxconn.linsirchat.module.contact.bean;

import com.foxconn.linsirchat.module.conversation.bean.MsgBean;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Unique;

/**
 * Created by Administrator on 2016/7/7.
 */
@Table(name = "tbl_user_info")
public class ConversationBean {

    @Id
    private int id;
    private String nick;
    private String name;

    @Unique
    private String tel;
    private String body;
    private String time;
    private String Icon;
    private String age;
    private String gender;
    private String local;
    private String signature;

    public void setByUserBean(UserBean userBean) {
        nick = userBean.getNick();
        tel = userBean.getTel();
        Icon = userBean.getIcon();
        age = userBean.getAge();
        gender = userBean.getGender();
        local = userBean.getLocal();
        signature = userBean.getSignature();
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

        ConversationBean that = (ConversationBean) o;

        if (id != that.id) return false;
        return tel.equals(that.tel);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + tel.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ConversationBean{" +
                "id=" + id +
                ", nick='" + nick + '\'' +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", body='" + body + '\'' +
                ", time='" + time + '\'' +
                ", Icon='" + Icon + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", local='" + local + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public void setMsg(MsgBean msg) {
        time = msg.getTime();
        if (msg.getType() == 0) {
            body = msg.getBody();
        }
    }

}

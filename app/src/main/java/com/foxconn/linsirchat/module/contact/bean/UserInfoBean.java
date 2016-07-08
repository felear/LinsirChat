package com.foxconn.linsirchat.module.contact.bean;

import com.foxconn.linsirchat.module.conversation.bean.MsgBean;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Unique;

/**
 * Created by Administrator on 2016/7/7.
 */
@Table(name = "tbl_user_info")
public class UserInfoBean {

    @Id
    private int id;
    private String nick;
    @Unique
    private String tel;
    private String body;
    private String time;
    private String Icon;
    private String age;
    private String gender;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfoBean that = (UserInfoBean) o;

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
        return "UserInfoBean{" +
                "id=" + id +
                ", nick='" + nick + '\'' +
                ", tel='" + tel + '\'' +
                ", body='" + body + '\'' +
                ", time='" + time + '\'' +
                ", Icon='" + Icon + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                '}';
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

package com.zoctan.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 用户
 *
 * @author Zoctan
 * @date 2018/06/25
 */
public class User {
    private String username;
    private String realname;
    private String phone;
    private String email;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(final String realname) {
        this.realname = realname;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(final Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

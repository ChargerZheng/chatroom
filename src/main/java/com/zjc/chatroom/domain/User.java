package com.zjc.chatroom.domain;

import javax.websocket.Session;

public class User {
    private String userName;
    private Session session;

    public User() {
    }

    public User(String userName, Session session) {
        this.userName = userName;
        this.session = session;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}

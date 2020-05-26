package com.zjc.chatroom.domain;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {
    private Integer chatRoomId;
    private String chatRoomName;
    private String password;
    private ConcurrentHashMap<String,User> users=new ConcurrentHashMap<>();
    private Integer usersNumber=0;

    public String getChatRoomName() {
        return chatRoomName;
    }

    public Integer getUsersNumber() {
        return usersNumber;
    }

    public ChatRoom() {
    }

    public Integer getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Integer chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public ChatRoom(Integer chatRoomId,String chatRoomName, Integer usersNumber) {
        this.chatRoomId=chatRoomId;
        this.chatRoomName = chatRoomName;
        this.usersNumber = usersNumber;
    }

    public User getUser(String sessionId){
        return users.get(sessionId);
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    public void addUser(User user){//添加用户sessionI的，user（username，session）
        this.users.put(user.getSession().getId(),user);
        this.usersNumber++;
    }

    public boolean remove(Session session){
        if(this.users.get(session.getId())!=null) {
            this.users.remove(session.getId());
            this.usersNumber--;
            return true;
        }
        return false;
    }

}

package com.zjc.chatroom.domain;

public class MessageEntity {
    int status;
    String message;

    public MessageEntity(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public MessageEntity() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

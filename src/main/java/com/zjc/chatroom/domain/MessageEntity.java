package com.zjc.chatroom.domain;

public class MessageEntity {
    int status;
    String sender;
    String message;

    public MessageEntity() {
    }

    public MessageEntity(int status,String message) {
        this.status = status;
        this.message = message;
    }

    public MessageEntity(int status,String sender,String message) {
        this.status = status;
        this.sender=sender;
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "status=" + status +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

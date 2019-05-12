package com.example.softwareproject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message implements  java.io.Serializable{
    private User sender = null;
    private User recipient = null;
    private String content = null;
    private Date messageTime;
    private int messageID = 0;

    public Message() {}
    public Message(User sender, User recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.messageTime = Calendar.getInstance().getTime();
    }

    public Message(User sender, User recipient, String content, Date messageTime) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.messageTime = messageTime;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getContent() {
        return content;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }
}

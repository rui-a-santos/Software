package com.example.softwareproject;

import java.util.ArrayList;
import java.util.Date;

public class ChatItem implements java.io.Serializable {


private Date lastMessage = null;
private ArrayList<Message> messages = null;
private ArrayList<User> users = null;

    public ChatItem(ArrayList<User> users, Date lastMessage, ArrayList<Message> messages) {
       this.users = users;
        this.lastMessage = lastMessage;
        this.messages = messages;
    }

    public ChatItem(ArrayList<User> users,  ArrayList<Message> messages) {
        this.users = users;
        this.messages = messages;
        this.lastMessage = messages.get(messages.size()-1).getMessageTime();
    }

    public ChatItem() {}
    public Date getLastMessage() {
        return lastMessage;
    }

    public void addMessage (Message message){
        this.messages.add(message);
    }


    public ArrayList<User> getUsers(){


        return this.users;


    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void setLastMessage(Date lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}

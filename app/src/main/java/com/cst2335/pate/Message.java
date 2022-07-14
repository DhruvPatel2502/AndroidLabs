package com.cst2335.pate;

class Messages {
    public long id;
    String messageInput;
    public Boolean sendOrReceive;


    Messages(String messageIn, Boolean SorR, long id) {
        this.messageInput = messageIn;
        this.sendOrReceive = SorR;
        this.id=id;
    }


    public String getMessageInput() {
        return messageInput;
    }

    public long getId() {
        return id;
    }

}
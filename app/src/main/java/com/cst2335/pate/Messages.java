package com.cst2335.pate;

public class Messages {
    public long id;
    String messageInput;
    public Boolean sendOrReceive;

    /**
     * Constructor:
     */
    public Messages(String messageIn, Boolean SorR, long id) {
        this.messageInput = messageIn;
        this.sendOrReceive = SorR;
        this.id=id;
    }

    /**
     * Chaining construct
     */
    public String getMessageInput() {
        return messageInput;
    }

    public void setMessage(String message) {
        this.messageInput = message;
    }

    public long getId() {
        return id;
    }

    public void set_id(long id) {
        this.id = id;
    }
}

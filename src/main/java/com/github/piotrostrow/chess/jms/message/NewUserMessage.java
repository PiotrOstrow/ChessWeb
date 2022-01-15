package com.github.piotrostrow.chess.jms.message;

public class NewUserMessage {

    private String username;
    private String email;

    public NewUserMessage() {

    }

    public NewUserMessage(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

package com.byebug.automation.utils.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SimpleAuthenticator extends Authenticator {

    private String username;
    private String password;

    public SimpleAuthenticator(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }

}
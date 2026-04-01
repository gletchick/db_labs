package org.gletchick.db.util;

import org.gletchick.db.model.Client;

public class UserSession {
    private static UserSession instance;
    private Client currentClient;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(Client client) {
        this.currentClient = client;
    }

    public void logout() {
        this.currentClient = null;
    }

    public Client getCurrentClient() {
        return currentClient;
    }

    public boolean isLoggedIn() {
        return currentClient != null;
    }
}
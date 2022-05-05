package com.example.wytwornia;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class User {

    private String login;
    private String password;
    private float wallet;
    private int settings; // 11 - white theme + polski, 12 - whitetheme + angielski, 21 - dark theme + polski, 22 - dark theme + angielski
    private int admin;// 0 - normalny user, 1 - admin
    public ObservableList<Film> koszyk;

    public int getAdmin() { return admin; }

    public void setAdmin(int admin) { this.admin = admin; }

    public int getSettings() {
        return settings;
    }

    public void setSettings(int settings) {
        this.settings = settings;
    }

    public float getWallet() {
        return wallet;
    }

    public void setWallet(float wallet) {
        this.wallet = wallet;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public User(){
        this.login = "";
        this.password = "";
        this.admin = 0;
        this.settings = 11;
        this.wallet= 0;
        this.koszyk = FXCollections.observableArrayList();
    }
    public User(String login, String password, int admin, int settings, int wallet){
        this.login = login;
        this.password = password;
        this.admin = admin;
        this.settings = settings;
        this.wallet = wallet;
        this.koszyk = FXCollections.observableArrayList();
    } public User(String login, String password, int admin){
        this.login = login;
        this.password = password;
        this.admin = admin;
        this.settings = 0;
        this.wallet = 0;
        this.koszyk = null;
    }


}



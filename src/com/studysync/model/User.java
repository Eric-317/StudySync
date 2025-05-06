package com.studysync.model;

public class User {
    private int uid;
    private String email;
    private String password;
    private String birthDate;

    public User(int uid, String email, String password, String birthDate) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}

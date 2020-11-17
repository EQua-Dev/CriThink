package com.example.onlinequizexpo.Model;

public class User {

    private String userName, age, password;

    public User() {
    }

    public User(String userName, String age, String password) {
        this.userName = userName;
        this.age = age;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

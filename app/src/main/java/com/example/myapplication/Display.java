package com.example.myapplication;

import java.util.ArrayList;

public class Display {
    private User user;
    public Display(User user){this.user = user;}
    public Display(){user = new User("","","","",
            new ArrayList<>());}
    public void displayView(){}
    public User getUser(){return user;}
    public void setUser(User user){this.user = user;}
}

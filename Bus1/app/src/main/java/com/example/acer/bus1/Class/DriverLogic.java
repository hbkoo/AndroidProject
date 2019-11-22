package com.example.acer.bus1.Class;

/**
 * 司机登录信息
 */

public class DriverLogic {

    private String Username,password;

    public DriverLogic(String Username, String password) {
        this.Username = Username;
        this.password = password;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getUsername() {
        return this.Username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return  this.password;
    }

}

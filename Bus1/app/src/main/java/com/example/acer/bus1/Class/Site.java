package com.example.acer.bus1.Class;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * 当前位置
 */

public class Site implements Serializable{

    private String name,introduce;
    private double position_x,position_y;
    private LatLng latLng;

    public Site(String name,String introduce,double position_x,double position_y) {
        this.name = name;
        this.introduce = introduce;
        this.position_x = position_x;
        this.position_y = position_y;
        this.latLng = new LatLng(position_x,position_y);
    }
    public Site() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public double getPosition_x() {
        return position_x;
    }

    public void setPosition_x(double position_x) {
        this.position_x = position_x;
    }

    public double getPosition_y() {
        return position_y;
    }

    public void setPosition_y(double position_y) {
        this.position_y = position_y;
    }

    public void setLatLng(double position_x,double position_y) {
        this.latLng = new LatLng(position_y,position_x);
    }

    public void setLatLng() {
        this.latLng = new LatLng(position_y,position_x);
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

}

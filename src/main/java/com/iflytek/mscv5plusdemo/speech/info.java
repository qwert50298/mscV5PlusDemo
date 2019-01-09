package com.iflytek.mscv5plusdemo.speech;

/**
 * Created by Administrator on 2016/11/20.
 */

public class info {
    String name;
    String number;

    public info() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "info{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}

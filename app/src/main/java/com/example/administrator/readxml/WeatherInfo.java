package com.example.administrator.readxml;

/**
 * Created by Administrator on 2018/1/2.
 */

class WeatherInfo {
    String date;
    String high;
    String low;
    W day;
    W night;
}
class W{
    String type;
    String fengli;
    String fengxiang;
    public String info() {
        String str = type + "风向" + fengxiang + "风力" + fengli;
        return str;
    }

}

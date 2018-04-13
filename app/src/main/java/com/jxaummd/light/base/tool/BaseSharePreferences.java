package com.jxaummd.light.base.tool;

import android.content.SharedPreferences;

import com.jxaummd.light.MyApplication;

/**
 * Created by sangx on 2018/2/21.
 */

public class BaseSharePreferences {

    //初始化Setting
    public  static final SharedPreferences SetiingsharedPreferences=
            MyApplication.getContext().getSharedPreferences("sushesetting",MyApplication.MODE_PRIVATE);

    //设置相关
    public  static final  String  LIGHT_STATE = "lightstate";       //灯的状态
    public  static final  String  VOICE_WAKEUP = "voiceopenclose";       //灯的状态
    public  static final  String  DEVICE_CONNECT = "deviceconnect";       //连接状态
    public  static  final  String  LIGHT_MODE = "lightmode";                // 灯光模式


    public static  final   String   S_LIGHT = "sushelight";// 宿舍的灯
    public static  final   String   S_DOOR = "sushefoor";// 宿舍的灯
    public static  final   String   S_FAN = "sushefan";// 宿舍的灯
    public static  final   String   S_WATER = "sushewater";// 宿舍的灯




    public static SharedPreferences  getSettingPreference(){
        return  SetiingsharedPreferences;
    }

    //设置状态
    public static void setIntState(String operate,int state){
        SetiingsharedPreferences.edit().putInt(operate,state).apply();//更改灯的状态
    }

    //设置状态
    public static void setStringState(String operate,String state){
        SetiingsharedPreferences.edit().putString(operate,state).apply();//更改灯的状态
    }

    //设置状态
    public static void setBooleanState(String operate,boolean state){
        SetiingsharedPreferences.edit().putBoolean(operate,state).apply();//更改灯的状态
    }


    //修改状态
    public static int getIntValue(String key){
       return SetiingsharedPreferences.getInt(key,0);//更改灯的状态
    }

    //修改状态
    public static String getStringValue(String key){
        return SetiingsharedPreferences.getString(key,"");//更改灯的状态
    }

    //修改状态
    public static boolean getBoolean(String key){
        return SetiingsharedPreferences.getBoolean(key,false);//更改灯的状态
    }




}

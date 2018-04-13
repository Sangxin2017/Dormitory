package com.jxaummd.light.base.tool;

/**
 * Created by sangx on 2018/2/21.
 */

public class SettingStateUpdate {
    private int  operator ; //操作
    private boolean  boostate  ; //布尔
    private int intstate ;  //整形
    private String  stringstate; //字符串
    private String  date  ; //数据

    public SettingStateUpdate(int operator, boolean boostate) {
        this.operator = operator;
        this.boostate = boostate;
    }

    public SettingStateUpdate(int operator, int intstate) {
        this.operator = operator;
        this.intstate = intstate;
    }

    public SettingStateUpdate(int operator, String stringstate) {
        this.operator = operator;
        this.stringstate = stringstate;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public boolean isBoostate() {
        return boostate;
    }

    public void setBoostate(boolean boostate) {
        this.boostate = boostate;
    }

    public int getIntstate() {
        return intstate;
    }

    public void setIntstate(int intstate) {
        this.intstate = intstate;
    }

    public String getStringstate() {
        return stringstate;
    }

    public void setStringstate(String stringstate) {
        this.stringstate = stringstate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

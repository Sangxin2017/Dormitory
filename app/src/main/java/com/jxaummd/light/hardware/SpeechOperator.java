package com.jxaummd.light.hardware;

/**
 * Created by sangx on 2018/2/7.
 */

public class SpeechOperator {
    public static  final  int   ASR_START =  0;
    public static  final  int   ASR_STOP =   1;
    public static  final  int   WAKR_START =  2;
    public static  final  int   WAKE_STOP =  3;

    public static  final   int  ASR_RESULT = 4;
    public static  final   int  WAKE_RESULT = 5;

    private  int  operatortype = 0;
    private  String  data = "";

    public SpeechOperator(int operatortype, String data) {
        this.operatortype = operatortype;
        this.data = data;
    }

    public int getOperatortype() {
        return operatortype;
    }

    public void setOperatortype(int operatortype) {
        this.operatortype = operatortype;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

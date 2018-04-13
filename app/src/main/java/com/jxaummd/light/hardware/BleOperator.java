package com.jxaummd.light.hardware;

import android.bluetooth.BluetoothDevice;

/**
 * Created by sangx on 2018/2/3.
 */

public class BleOperator {
    public  static  final  int  OPERATOR_CONNECT = 1;
    public  static  final  int  OPERATOR_DISCONNECT = 2;
    public  static  final  int  OPERATOR_SENDDATA= 3;
    public  static  final  int  OPERATOR_ENABLE_NPTIFY = 4;
    public  static  final  int  OPERATOR_INITSCANER= 5;
    public  static  final  int  OPERATOR_STARTSCANER= 6;
    public  static  final  int  OPERATOR_STOPSCANER= 7;



    private  int   operatorMode = 0;
    private String data ;
    private BluetoothDevice mDevice ;

    public String getMyUuid() {
        return myUuid;
    }

    public void setMyUuid(String myUuid) {
        this.myUuid = myUuid;
    }

    private String  myUuid ;

    public BleOperator(int operatorMode, String data) {
        this.operatorMode = operatorMode;
        this.data = data;
    }

    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }

    public int getOperatorMode() {
        return operatorMode;
    }

    public void setOperatorMode(int operatorMode) {
        this.operatorMode = operatorMode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

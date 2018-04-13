package com.jxaummd.light;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by sangx on 2018/1/14.
 */

public class MyApplication extends Application {
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }


    //获取context
    public  static  Context getContext(){
            return context;
    }

    //封装Toast
    public static  void  MyToast(String content){
        if(content.equals("")||content==null);else
         Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }


    //封装广播
    public static void broadcastUpdate(Context context ,final String action) {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);   //发送广播通知
    }

    public static void  startaactivity(Class aclass){

    }


}

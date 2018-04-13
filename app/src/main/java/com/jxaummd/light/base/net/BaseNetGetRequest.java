package com.jxaummd.light.base.net;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by sangx on 2018/2/27.
 */

public class BaseNetGetRequest {
    private static final OkHttpClient client = new OkHttpClient();


    public static   void  RequestUrlNoResonpe(final String url) throws IOException {
        //Client.newCall()
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(url).build();
                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    public void   ResquestUrlResone(final String url, final RequestresultCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(url).build();
                try {
                    callback.success(client.newCall(request).execute().body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }




    public  interface     RequestresultCallback {
        void   success(String result) throws JSONException; //成功
        void  fail();  //失败

    }


}

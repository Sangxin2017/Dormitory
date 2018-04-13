package com.jxaummd.light.hardware;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jxaummd.light.MyApplication;
import com.jxaummd.light.base.tool.BaseSharePreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by sangx on 2018/2/7.
 */

public class MediaPlayService extends Service {
    //操作类
    public static class MediaPlayOperatyor{
        public static final int PLAT_MUSIC = 1;
        public static final int PAUSE_MUSIC = 2;
        public static final int STOP_MUSIC = 3;
        private int operator ;
        private String data;

        public MediaPlayOperatyor(int operator, String data) {
            this.operator = operator;
            this.data = data;
        }

        public int getOperator() {
            return operator;
        }

        public void setOperator(int operator) {
            this.operator = operator;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    //播放类
    private MediaPlayer mediaPlayer = null;
    private boolean isStop=true;
    private boolean isprepare = false;
    private int  isattachmusic = 0;

    //创建服务
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        MyApplication.MyToast("初始化播放服务成功");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReciver(MediaPlayOperatyor operator) throws IOException {
        switch (operator.getOperator()) {
            case MediaPlayOperatyor.PLAT_MUSIC:
                initMedia("http://120.79.0.116/light/yzz/"+operator.getData()+".m4a");
                if(operator.getData().equals("10005")){
                    isattachmusic=1;  //附带放歌
                }else 
                if(operator.getData().equals("10001")){
                isattachmusic=2;  //附带开始识别
                  }else
                      isattachmusic=Integer.valueOf(operator.getData());
                break;

        }
    }

    private void initMedia(String adress) throws IOException {
        if(mediaPlayer!=null)
            mediaPlayer.stop();

        mediaPlayer=new MediaPlayer();

        mediaPlayer.setDataSource(adress);

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //准备好了
                isprepare=true;
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                final OkHttpClient client = new OkHttpClient();

                //播放好了
                switch (isattachmusic){
                    case 1:
                        try {
                            initMedia("http://120.79.0.116/light/voice/"+101+".mp3");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        EventBus.getDefault().post(new SpeechOperator(SpeechOperator.ASR_START,""));
                        break;
                    case 10003:
                       // EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,"#lc#"));
                        break;
                }
                isattachmusic=0;

            }
        });
    }

}
package com.jxaummd.light.hardware;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.jxaummd.light.MyApplication;
import com.jxaummd.light.base.net.BaseNetGetRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sangx on 2018/2/7.
 */

public class SpeechRecWakService extends Service implements EventListener {


    private EventManager wakeUpManager =  null ;   //唤醒事件
    private EventManager asrManager =  null ;     //识别事件
    private boolean enableOffline = false; // 测试离线命令词，需要改成true
    private boolean isresponse = false;

    private  Map<String,String>  voice = new TreeMap<>();
    private  BaseNetGetRequest request= new BaseNetGetRequest();


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        initAsraWak();
        voice.put("放歌","10005");
        voice.put("放音乐","10005");
        voice.put("来歌","10005");
        voice.put("关歌","10006");
        voice.put("看书","10007");
        voice.put("睡觉","10008");
        voice.put("休息","10008");
        voice.put("消闹钟","10010");
        voice.put("天气提醒","10011");
        voice.put("工作","10012");
        voice.put("晚安","10015");
        voice.put("关灯","10003");
        voice.put("开灯","10004");
        voice.put("开饮机","10002");
        voice.put("关饮机","100021");
        voice.put("关门","10003");
        voice.put("开门","10004");
        voice.put("开风扇","10002");
        voice.put("关风扇","100021");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onReciver(SpeechOperator operator){
        switch (operator.getOperatortype()){
            case SpeechOperator.ASR_START:
                asrStart();
                break;
            case SpeechOperator.ASR_STOP:
                asrStop();
                break;
            case SpeechOperator.WAKR_START:
                wakStart();
                break;
            case SpeechOperator.WAKE_STOP:
                wakStop();
                break;
        }
        MyApplication.MyToast(operator.getData());


    }

    //初始化
    private void initAsraWak(){
        asrManager = EventManagerFactory.create(this, "asr");
        asrManager.registerListener(this); //  EventListener 中 onEvent方法
        wakeUpManager = EventManagerFactory.create(this, "wp");
        wakeUpManager.registerListener(this); //  EventListener 中 onEvent方法
        if (enableOffline) {
            loadOfflineEngine(); // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }

    }
    //加载离线识别引擎
    private void loadOfflineEngine() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.DECODER, 2);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets://baidu_speech_grammar.bsg");
        asrManager.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, new JSONObject(params).toString(), null, 0, 0);
    }
    //卸载
    private void unloadOfflineEngine() {
        asrManager.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0); //
    }
    //开始语音识别
    private void asrStart() {
     //   MyApplication.MyToast("开始识别");
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

        if (enableOffline) {
            params.put(SpeechConstant.DECODER, 2);
        }
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.PID, 1536); // 中文输入法模型，有逗号
        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asrManager.send(event, json, null, 0, 0);

    }
    //停止语音识别
    private void asrStop() {

        asrManager.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }
    //开始语音唤醒
    private void wakStart(){
//        MyApplication.MyToast("开始唤醒");
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
        wakeUpManager.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);

    }
    //停止语音唤醒
    private void wakStop(){
      //  MyApplication.MyToast("停止唤醒");
        wakeUpManager.send(SpeechConstant.WAKEUP_STOP,null,null,0,0);
    }
    //关闭程序
    private void close(){
        wakeUpManager.send(SpeechConstant.WAKEUP_STOP, "{}", null, 0, 0);
        asrManager.send(SpeechConstant.ASR_STOP,"{}",null,0,0);
    }
    //结果回调
    @Override
    public void onEvent(String name, String parmer, byte[] bytes, int i, int i1)  {
        if(name!=null&&parmer!=null) {
            Log.d(name, parmer);
            JSONObject result = null;
            try {
                result= new JSONObject(parmer);

                //唤醒事件
                if(name.equals("wp.data")){
                   // if(result.getString("word").equals("小娜同学")){
                        EventBus.getDefault().post(new MediaPlayService.MediaPlayOperatyor(MediaPlayService.MediaPlayOperatyor.PLAT_MUSIC,"10001"));
                        wakStop();
                 //   }

                }else {
                //识别事件
                    if(result.getString("result_type").equals("final_result")){
                        String number = getvoidString(result.getString("best_result"));
                        if(number!=null){
                            EventBus.getDefault().post(new MediaPlayService.MediaPlayOperatyor(MediaPlayService.MediaPlayOperatyor.PLAT_MUSIC,number));
                            EventBus.getDefault().post(new SpeechOperator(SpeechOperator.ASR_STOP,""));
                            EventBus.getDefault().post(new SpeechOperator(SpeechOperator.WAKR_START,""));
                        }
                        operatorLight(number);//操作灯
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    //操作灯
    private void operatorLight(String number) throws IOException {
        String senddata="";
        switch (number){
            case "10003":
                senddata="#lc#";
         //       request.RequestUrlNoResonpe("http://120.79.0.116/park/ipark?n=10003&s=3");
                break;
            case "10004":
                senddata="#lo#";
        //        request.RequestUrlNoResonpe("http://120.79.0.116/park/ipark?n=10003&s=2");
                break;
            case "10005":
                senddata="#lm4#";
                break;
            case "10007":
                senddata="#lm3#";
                break;
            case "10008":
                senddata="#lm2##lh13100#";
                break;
            case "10012":
                senddata="#lm1#";
                break;
            case "10015":
                senddata="#lc#";
                break;
            case "10002":
                request.RequestUrlNoResonpe("http://120.79.0.116/park/ipark?n=10003&s=4");
                break;
            case "100021":
                request.RequestUrlNoResonpe("http://120.79.0.116/park/ipark?n=10003&s=5");
                break;
            case "10009":

                break;
        }
        //发送数据
        if(!senddata.equals("")) {
            EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA, "#lc#"));
            EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA, senddata));
        }
    }


    //闹钟分析
    private  void   anglysenotify(String data){
        int  hour,min;      //时与分
        HashMap<String,Integer>   timetable = new HashMap<>();
        timetable.put("零",0);
        timetable.put("一",1);
        timetable.put("二",2);
        timetable.put("三",3);
        timetable.put("四",0);
        timetable.put("五",1);
        timetable.put("六",2);
        timetable.put("七",3);
        timetable.put("八",1);
        timetable.put("九",2);
        timetable.put("十",3);

        //点所在的位置
        int dianindex = data.indexOf('点');
        //前后搜索数字





    }

    //语句分析
    private  String  getvoidString (String content){
        for (String key : voice.keySet()) {
           boolean flag = true;
           char[] chars = key.toCharArray();
           for(char c:chars){
               if(content.indexOf(c)==-1){
                   flag=false;
               }
           }
           if(flag)
               return voice.get(key);
        }
        return  "10014";
    }



}

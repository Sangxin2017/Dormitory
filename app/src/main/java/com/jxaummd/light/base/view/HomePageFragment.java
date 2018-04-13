package com.jxaummd.light.base.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jxaummd.light.MainActivity;
import com.jxaummd.light.MyApplication;
import com.jxaummd.light.R;
import com.jxaummd.light.base.tool.BaseSharePreferences;
import com.jxaummd.light.base.toolview.ProgressWebView;
import com.jxaummd.light.base.toolview.TimePickActivity;
import com.jxaummd.light.hardware.BleOperator;
import com.jxaummd.light.hardware.BluetoothLeService;
import com.jxaummd.light.hardware.SpeechOperator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by sangx on 2018/2/20.
 */

public class HomePageFragment extends Fragment implements View.OnClickListener{
    private Toolbar toolbar;



    private ImageView page1light;

    private TextView page1lightmode ;
    private TextView page1currentlight ;
    private TextView page1warkup ;
    private TextView page1closetime ;



    private ImageView page2workmode;
    private ImageView page2sleepmode;
    private ImageView  page2readmode;
    private ImageView  page2funmode;



    private ImageView page3shebei;      //设备连接
    private ImageView  page3timeplan;   //定时
    private ImageView  page3yuyin;      //语音
    private ImageView  page3light;      //亮度
    private ImageView  page3color;      //颜色
    private ImageView  page3sewen;      //色温


    private ProgressDialog  progressDialog ;

    @ColorInt public static final int colorred        = 0xFFFF4081;
    @ColorInt public static final int colorgreen       = 0xFF26da83;
    @ColorInt public static final int cologray      = 0xFF8a8a8a;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onReciver(TimePickActivity.TimePieckerOperate operate){
        page1closetime.setText(operate.getHour()+":"+operate.getMin());

    }


    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onReciver(BleOperator operate){
        switch(operate.getOperatorMode()){
            case BluetoothLeService.STAETE_HAVEGETMYCHARACTER:
                //真的连接上了
                page3shebei.setColorFilter(colorred);        //开的颜色
                page1light.setColorFilter(colorred);
                BaseSharePreferences.setBooleanState(BaseSharePreferences.DEVICE_CONNECT,true);
                progressDialog.cancel();
                break;


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout,container,false);
        page1light=view.findViewById(R.id.home_page1_light);     //控件灯
        page1closetime=view.findViewById(R.id.home_page1_closetime);    //定时时间
        page1closetime.setText("未开启");
        page1currentlight = view.findViewById(R.id.home_page1_currentlight); //当前亮度
        page1warkup = view.findViewById(R.id.home_page1_voice); //语音唤醒
        page1warkup.setText("未开启");
        page1lightmode=view.findViewById(R.id.home_page1_lightmode);//灯光模式
        page1lightmode.setText("工作");


        page2workmode=view.findViewById(R.id.home_page2_moshi_gongzuo);   //工作模式
        page2sleepmode=view.findViewById(R.id.home_page2_moshi_shuimian);       //睡觉模式
        page2readmode=view.findViewById(R.id.home_page2_moshi_yuedu);   //阅读模式
        page2funmode=view.findViewById(R.id.home_page2_moshi_yule); //娱乐模式



        page3timeplan = view.findViewById(R.id.home_page3_gongneng_dingshirenwu);      //定时任务
        page3shebei = view.findViewById(R.id.home_page3_gongneng_shebeilianjie);        //设备连接
        page3yuyin = view.findViewById(R.id.home_page3_gongneng_yuyin);                 //语音唤醒
        page3color=view.findViewById(R.id.home_page3_gongneng_secaixuanze);
        page3light=view.findViewById(R.id.home_page3_gongneng_liangdu);
  //      page3sewen=view.findViewById(R.id.home_page3_gongneng_sewenxuanze);


        page2workmode.setOnClickListener(this);
        page2sleepmode.setOnClickListener(this);
        page2readmode.setOnClickListener(this);
        page2funmode.setOnClickListener(this);

        page3timeplan.setOnClickListener(this);
        page3shebei.setOnClickListener(this);
        page3yuyin.setOnClickListener(this);
        page1light.setOnClickListener(this);
    //    page3sewen.setOnClickListener(this);
        page3color.setOnClickListener(this);

        progressDialog=new ProgressDialog(MainActivity.activity);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在连接中，请稍等！");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initset();          //初始化原来的设置
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_page1_light:
                if(BaseSharePreferences.getBoolean(BaseSharePreferences.LIGHT_STATE)){
                    page1light.setColorFilter(colorgreen);      //关灯的颜色
                    EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,"#lc#"));
                    BaseSharePreferences.setBooleanState(BaseSharePreferences.LIGHT_STATE,false);
                }else {
                    page1light.setColorFilter(colorred);        //开灯的颜色
                    EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,"#lo#"));
                    BaseSharePreferences.setBooleanState(BaseSharePreferences.LIGHT_STATE,true);
                }
                break;

            case R.id.home_page2_moshi_gongzuo:
                resumecolor();
                page2workmode.setColorFilter(colorgreen);
                EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,"#lm1#"));
                break;
            case R.id.home_page2_moshi_shuimian:
                resumecolor();
                page2sleepmode.setColorFilter(colorgreen);
                EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,"#lm2##lh13100#"));
                break;
            case R.id.home_page2_moshi_yuedu:
                resumecolor();
                page2readmode.setColorFilter(colorgreen);
                EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,"#lm3#"));
                break;
            case R.id.home_page2_moshi_yule:
                resumecolor();
                page2funmode.setColorFilter(colorgreen);
                EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,"#lm4#"));
                break;

            case R.id.home_page3_gongneng_dingshirenwu:
                Intent intent = new Intent(MyApplication.getContext(),TimePickActivity.class);
                startActivity(intent);
                page3timeplan.setColorFilter(colorred);
                break;

            case R.id.home_page3_gongneng_shebeilianjie:
                //连接设备
                if(BaseSharePreferences.getBoolean(BaseSharePreferences.DEVICE_CONNECT)){
                    page3shebei.setColorFilter(cologray);      //关的颜色
                    page1light.setColorFilter(colorgreen);
                    EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_DISCONNECT,"断开连接"));
                    BaseSharePreferences.setBooleanState(BaseSharePreferences.DEVICE_CONNECT,false);
                }else {
               //     page3shebei.setColorFilter(colorred);        //开的颜色
                //    page1light.setColorFilter(colorred);
                    BleOperator operator = new BleOperator(BleOperator.OPERATOR_CONNECT,"34:15:13:1C:BE:B1");
                    EventBus.getDefault().post(operator);
                //    BaseSharePreferences.setBooleanState(BaseSharePreferences.DEVICE_CONNECT,true);
                    progressDialog.show();
                }
                break;

            case R.id.home_page3_gongneng_yuyin:
                if(BaseSharePreferences.getBoolean(BaseSharePreferences.VOICE_WAKEUP)){
                    page3yuyin.setColorFilter(cologray);      //关的颜色
                    EventBus.getDefault().post(new SpeechOperator(SpeechOperator.WAKE_STOP,"关闭唤醒"));
                    BaseSharePreferences.setBooleanState(BaseSharePreferences.VOICE_WAKEUP,false);
                    page1warkup.setText("未开启");
                }else {
                    page3yuyin.setColorFilter(colorred);        //开的颜色
                    EventBus.getDefault().post(new SpeechOperator(SpeechOperator.WAKR_START,"开始唤醒"));
                    BaseSharePreferences.setBooleanState(BaseSharePreferences.VOICE_WAKEUP,true);
                    page1warkup.setText("开启");
                }
                break;



        }

    }
    void  resumecolor(){
        EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,"#lc#"));
        page2funmode.setColorFilter(cologray);
        page2workmode.setColorFilter(cologray);
        page2sleepmode.setColorFilter(cologray);
        page2readmode.setColorFilter(cologray);
    }

    void initset(){
        if(BaseSharePreferences.getBoolean(BaseSharePreferences.VOICE_WAKEUP)){
            page3yuyin.setColorFilter(colorred);        //开的颜色
            EventBus.getDefault().post(new SpeechOperator(SpeechOperator.WAKR_START,"开始唤醒"));
            BaseSharePreferences.setBooleanState(BaseSharePreferences.VOICE_WAKEUP,true);
            page1warkup.setText("开启");
        }
        if(BaseSharePreferences.getBoolean(BaseSharePreferences.DEVICE_CONNECT)){
            page3shebei.setColorFilter(colorred);        //开的颜色
            BleOperator operator = new BleOperator(BleOperator.OPERATOR_CONNECT,"34:15:13:1C:BE:B1");
            EventBus.getDefault().post(operator);
            BaseSharePreferences.setBooleanState(BaseSharePreferences.DEVICE_CONNECT,true);
        }
        if(BaseSharePreferences.getBoolean(BaseSharePreferences.LIGHT_STATE)){
            page1light.setColorFilter(colorred);        //开灯的颜色
            EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,"#lo#"));
            BaseSharePreferences.setBooleanState(BaseSharePreferences.LIGHT_STATE,true);
        }



    }
}

package com.jxaummd.light;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.joker.annotation.PermissionsGranted;
import com.joker.api.Permissions4M;
import com.jxaummd.light.base.view.FragmentTab;
import com.jxaummd.light.base.view.FragmentTabHost;
import com.jxaummd.light.base.view.HomePageFragment;
import com.jxaummd.light.base.view.MusicPageFragment;
import com.jxaummd.light.base.view.MyselfPageFrangment;
import com.jxaummd.light.base.view.StorePageFragment;
import com.jxaummd.light.hardware.BluetoothLeService;
import com.jxaummd.light.hardware.MediaPlayService;
import com.jxaummd.light.hardware.SpeechOperator;
import com.jxaummd.light.hardware.SpeechRecWakService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private FragmentTabHost mTabHost;
    private LayoutInflater mInflater;
    private List<FragmentTab> fragmentTbaList ;
    public static  Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();         //初始化界面
        initpermission();   //初始化权限
        initService();
        activity=this;
    }

    private void initpermission() {
        Permissions4M.get(MainActivity.this).requestForce(true) .requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION).requestCode(0) .request();
        Permissions4M.get(MainActivity.this).requestForce(true) .requestPermission(Manifest.permission.RECORD_AUDIO).requestCode(1) .request();
        Permissions4M.get(MainActivity.this).requestForce(true) .requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).requestCode(2) .request();

    }
    @PermissionsGranted({0,1,2})
    public void granted(int code) {
            MyApplication.MyToast("");
    }


    private void initView() {
        setContentView(R.layout.activity_main);
        mInflater=LayoutInflater.from(this);

        mTabHost=findViewById(android.R.id.tabhost);
        mTabHost.setup(this,getSupportFragmentManager(),R.id.flayout_content);

        //三个界面，三个Fragment
        fragmentTbaList= new ArrayList<>();
        FragmentTab tab = new FragmentTab(R.drawable.main_bottom_home_selector,"home", HomePageFragment.class);
        FragmentTab music = new FragmentTab(R.drawable.main_bottom_music_selector,"music", MusicPageFragment.class);
          FragmentTab tab1 = new FragmentTab(R.drawable.sushe_voice,"store", StorePageFragment.class);

        FragmentTab tab2 = new FragmentTab(R.drawable.main_bottom_myself_selector,"myself", MyselfPageFrangment.class);

    //    fragmentTbaList.add(tab);
    //    fragmentTbaList.add(music);
       fragmentTbaList.add(tab1);
    //    fragmentTbaList.add(tab2);

        //添加到FragmentTabHost中
        for (FragmentTab tabs:
             fragmentTbaList) {
            TabHost.TabSpec  tabSpec  = mTabHost.newTabSpec(tabs.getTitle()).setIndicator(buildIndicator(tabs));

            mTabHost.addTab(tabSpec,tabs.getFragment(),null);
        }
        //设置启动的fragment
       mTabHost.setCurrentTab(0);

    }

    //构建Indicator
    private View buildIndicator(FragmentTab tab){
        View view =mInflater.inflate(R.layout.tab_indicator,null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.MyToast("点击了,开始唤醒了");
                EventBus.getDefault().post(new SpeechOperator(SpeechOperator.ASR_START,""));
            }
        });
        img.setImageResource(tab.getIcon());
        return  view;
    }

    @Override
    protected void onResume() {
        super.onResume();
    //    initService();          //初始化线程
    }


    //初始化线程
    private void initService() {
        Intent intent = new Intent(this, SpeechRecWakService.class);
        startService(intent);
        intent = new Intent(this, MediaPlayService.class);
        startService(intent);
        intent = new Intent(this, BluetoothLeService.class);
        startService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

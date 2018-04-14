package com.jxaummd.light.base.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jxaummd.light.MainActivity;
import com.jxaummd.light.R;
import com.jxaummd.light.base.net.BaseNetGetRequest;
import com.jxaummd.light.base.tool.BaseSharePreferences;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by sangx on 2018/2/20.
 */

public class StorePageFragment extends Fragment {
    @BindView(R.id.home_page_toolbar)
    Toolbar homePageToolbar;
    @BindView(R.id.fuzhu_text_homepage2)
    TextView fuzhuTextHomepage2;
    @BindView(R.id.sushe_page_gongneng_gongzuo)
    ImageView light;
    @BindView(R.id.sushe_page_gongneng_shuimian)
    ImageView door;
    @BindView(R.id.sushe_page_gongneng_yuedu)
    ImageView yinshuiji;
    @BindView(R.id.sushe_page_gongneng_yule)
    ImageView fan;
    Unbinder unbinder;

    @ColorInt
    int click = Color.RED;
    @BindView(R.id.app_dormitry_image)
    ImageView appDormitryImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.store_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1523516993359&di=ffc43a94cb0d1fe8ef5bec492150e9ee&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01212057a062c30000012e7ed325fe.gif").into(appDormitryImage);
       // startActivity(new Intent(MainActivity.activity,LoginActivity.class));;
        saveinstance();        //根据Sharepreference 进行更新Button 状态
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.sushe_page_gongneng_gongzuo, R.id.sushe_page_gongneng_shuimian, R.id.sushe_page_gongneng_yuedu, R.id.sushe_page_gongneng_yule})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sushe_page_gongneng_gongzuo:
                changebutton(light, BaseSharePreferences.S_LIGHT, 1,true);
                break;
            case R.id.sushe_page_gongneng_shuimian:
                changebutton(door, BaseSharePreferences.S_DOOR, 2,true);
                break;
            case R.id.sushe_page_gongneng_yuedu:
                changebutton(yinshuiji, BaseSharePreferences.S_WATER, 3,true);
                break;
            case R.id.sushe_page_gongneng_yule:
                changebutton(fan, BaseSharePreferences.S_FAN, 4,true);
                break;
        }
    }

    //改变选中的状态
    void changebutton(ImageView imageView, String Shareprederencename, int val,boolean  qufan) {
        boolean isselect = BaseSharePreferences.getBoolean(Shareprederencename);
        val--;
        if(qufan)
            BaseSharePreferences.setBooleanState(Shareprederencename, !isselect);

        if (isselect) {
            imageView.setColorFilter(HomePageFragment.colorred);
            try {
                int s = val * 2 + 1;
                BaseNetGetRequest.RequestUrlNoResonpe("http://120.79.0.116/park/ipark?n=10008&s=" + s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            imageView.setColorFilter(HomePageFragment.cologray);
            try {
                BaseNetGetRequest.RequestUrlNoResonpe("http://120.79.0.116/park/ipark?n=10008&s=" + val * 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    void  saveinstance(){
        changebutton(light, BaseSharePreferences.S_LIGHT, 1,false);
        changebutton(door, BaseSharePreferences.S_DOOR, 2,false);
        changebutton(yinshuiji, BaseSharePreferences.S_WATER, 3,false);
        changebutton(fan, BaseSharePreferences.S_FAN, 4,false);
    }


}

package com.jxaummd.light.base.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jxaummd.light.MainActivity;
import com.jxaummd.light.MyApplication;
import com.jxaummd.light.R;
import com.jxaummd.light.base.adapter.MySelfAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by sangx on 2018/2/20.
 */

public class MyselfPageFrangment extends Fragment{
    private RecyclerView SettingList;
    private List<MySelfAdapter.SetItem> list = new ArrayList<>();
    private MySelfAdapter mySelfAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myself_layout,container,false);
        SettingList=view.findViewById(R.id.myself_myselflist);
        mySelfAdapter = new MySelfAdapter();
        mySelfAdapter.addItem(R.drawable.geren_shijian,"时间校准");
        mySelfAdapter.addItem(R.drawable.geren_fayinren,"发音人选择");
        mySelfAdapter.addItem(R.drawable.geren_guanjianci,"唤醒词选择");
        mySelfAdapter.addItem(R.drawable.geren_tianqi,"天气同步");
        mySelfAdapter.addItem(R.drawable.geren_naozhong,"闹铃选择");


        SettingList.setLayoutManager(new LinearLayoutManager(MainActivity.activity,LinearLayoutManager.VERTICAL,true));
        SettingList.setAdapter(mySelfAdapter);


        return view;
    }
}

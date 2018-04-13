package com.jxaummd.light.base.toolview;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jxaummd.light.R;

/**
 * Created by sangx on 2018/2/21.
 */

public class PrograseSeek extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_seekbar);
    }
}

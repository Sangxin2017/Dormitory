package com.jxaummd.light.base.toolview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.jxaummd.light.R;
import com.jxaummd.light.hardware.BleOperator;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.Calendar;


/**
 * Created by sangx on 2018/2/21.
 */

public class TimePickActivity extends AppCompatActivity {

    private TimePicker timePicker ;
    private Button  confimbutton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化视图
        setContentView(R.layout.activity_timeppicker);
        timePicker=findViewById(R.id.activity_timepicker);          //时间选择器
        confimbutton=findViewById(R.id.activity_timepicker_confirm);
        confimbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int hour =    timePicker.getHour();
            int min = timePicker.getMinute();
            String code = "#lt"+new DecimalFormat("000").format(caculatetime(hour,min))+"#";
            EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,code));

        //发送数据

            finish();
            }
        });

    }


    public  class  TimePieckerOperate {
            private  int hour ;
            private  int min;

        public TimePieckerOperate(int hour, int min) {
            this.hour = hour;
            this.min = min;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }
    }


    int  caculatetime(int hour ,int min){
        EventBus.getDefault().post(new TimePieckerOperate(hour,min));
        Calendar calendar =  Calendar.getInstance();
        int currenthour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentmin = calendar.get(Calendar.MINUTE);
//        if(hour>currenthour)
        int i = Math.abs(hour - currenthour) * 60 + Math.abs(currentmin - min);
        return i;
    }

}

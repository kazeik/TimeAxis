package com.scenery7f.timeaxis;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.scenery7f.timeaxis.model.PeriodTime;
import com.scenery7f.timeaxis.util.TestUtil;
import com.scenery7f.timeaxis.view.TimerShaft;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimerShaft timerShaft = (TimerShaft) findViewById(R.id.timer_shaft);

        LinearLayout back = (LinearLayout) findViewById(R.id.activity_main);

        TimerShaft s = new TimerShaft(this, Color.YELLOW, Color.BLUE);
        back.addView(s);

        timerShaft.setOnTimeChange(new TimerShaft.OnTimeChange() {
            @Override
            public void timeChangeOver(String time) {
                TestUtil.showToast(MainActivity.this, time);
            }

            @Override
            public void timeChangeAction() {
                TestUtil.log(MainActivity.this.getClass(), "正在移动时间轴");
            }
        });

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(System.currentTimeMillis() - 1000 * 60 * 30);

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(start.getTimeInMillis() + 1000 * 60 * 10);


        Calendar start1 = Calendar.getInstance();
        start1.setTimeInMillis(start1.getTimeInMillis() + 5000);

        Calendar end1 = Calendar.getInstance();
        end1.setTimeInMillis(start1.getTimeInMillis() + 1000 * 60 * 10);


        PeriodTime periodTime = new PeriodTime(start, end);
        PeriodTime periodTime1 = new PeriodTime(start1, end1);

        List<PeriodTime> list = new ArrayList<>();
        list.add(periodTime);
        list.add(periodTime1);

        timerShaft.setRecordList(list);
//        s.setRecordList(list);
    }


}

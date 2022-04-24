package com.learning.costomviewgroup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.learning.costomviewgroup.flow.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FlowLayout mFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFlowLayout = this.findViewById(R.id.flow_layout);
        List<String> data = new ArrayList<>();
        data.add("Android手机");
        data.add("iso");
        data.add("笔记本电脑");
        data.add("超级好用高大上的键盘");
        data.add("男生服装");
        data.add("女生服装");
        data.add("鼠标");
        data.add("充电器");
        data.add("Android手机");
        data.add("iso");
        data.add("笔记本电脑");
        data.add("超级好用高大上的键盘超级好用高大上的键盘超级好用高大上的键盘超级好用高大上的键盘超级好用高大上的键盘超级好用高大上的键盘超级好adas用高大上的键盘");
        data.add("男生服装");
        data.add("女生服装");
        data.add("鼠标");
        data.add("充电器");
        data.add("Android手机");
        data.add("iso");
        data.add("笔记本电脑");
        data.add("超级好用高大上的键盘");
        data.add("男生服装");
        data.add("女生服装");
        data.add("鼠标");
        data.add("充电器");
        data.add("超级好用高大上的键盘");
        data.add("男生服装");
        data.add("女生服装");
        data.add("鼠标");
        data.add("充电器");
        mFlowLayout.setTextData(data);
        mFlowLayout.setOnItemClickListener(new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View v, String text) {
                Log.d(TAG, "onItemClick: ---> " + text);
            }
        });
    }
}
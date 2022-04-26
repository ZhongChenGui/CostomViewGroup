package com.learning.costomviewgroup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.learning.costomviewgroup.flow.FlowLayout;
import com.learning.costomviewgroup.keyboard.KeyboardView;
import com.learning.costomviewgroup.slide.SlideMenuView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FlowLayout mFlowLayout;
    private KeyboardView mKeyboardView;
    private SlideMenuView mSlideItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        useFlowLayout();
//        useKeyboard();
        useSlideView();

    }

    private void useSlideView() {
        mSlideItemView = this.findViewById(R.id.slide_item);
        mSlideItemView.setOnEditClickListener(new SlideMenuView.OnEditClickListener() {
            @Override
            public void onReadClick() {
                Log.d(TAG, "onReadClick: ..........mSlideItemView.isOpen(); ........." + mSlideItemView.isOpen());

            }

            @Override
            public void onTopClick() {
                Log.d(TAG, "onTopClick: ..........................");
            }

            @Override
            public void onDeleteClick() {
                Log.d(TAG, "onDeleteClick: ...........................");
            }
        });
    }

    private void useKeyboard() {
        mKeyboardView = this.findViewById(R.id.keyboard_view);
        mKeyboardView.setOnItemClickListener(new KeyboardView.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Log.d(TAG, "onItemClick: ---> " + text);
            }
        });

    }

    private void useFlowLayout() {
        mFlowLayout = this.findViewById(R.id.flow_layout);
        List<String> data = new ArrayList<>();
        data.add("Android手机");
        data.add("iso");
        data.add("笔记本电脑");
        data.add("超级好用高大上的键盘");
        data.add("男生服装");
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
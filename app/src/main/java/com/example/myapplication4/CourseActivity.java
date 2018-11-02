package com.example.myapplication4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class CourseActivity extends AppCompatActivity {
    private Button CloseButton;
    private WebView web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        web = findViewById(R.id.we2);
        CloseButton=findViewById(R.id.closebutton);
        Intent intent = getIntent();
        Log.i("hxx","传递过来的课程表"+intent.getStringExtra("content"));
        search(intent.getStringExtra("content"));
        CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void search(String course) {//显示课程表在webview中
        //支持js
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //多窗口
//        web.getSettings().setSupportMultipleWindows(true);
        //设置可以支持缩放 , 设置出现缩放工具
//        web.getSettings().setSupportZoom(true);
//        web.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        web.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        web.getSettings().setLoadsImagesAutomatically(true);
        web.loadDataWithBaseURL(null, course, "text/html", "utf-8", null);
    }
}

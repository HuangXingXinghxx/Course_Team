package com.example.myapplication4;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class CourseActivity extends AppCompatActivity {
    private WebView web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        setContentView(R.layout.activity_course);
        web = findViewById(R.id.we2);
        Intent intent = getIntent();
        String content = intent.getStringExtra("content");
        String teacherName = intent.getStringExtra("teacher");
        if(content==null||"".equals(content)){
            showDialog(teacherName+"老师今年没有排课哦！");
        }
        search(intent.getStringExtra("content"));
        actionBar.setTitle(teacherName+" 2018学年课表");

    }
    public void showDialog(String msg){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(CourseActivity.this);
        normalDialog.setTitle("您好");
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        normalDialog.show();
    }

    public void search(String course) {//显示课程表在webview中
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        web.getSettings().setSupportMultipleWindows(true);
//        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
//        web.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        web.getSettings().setLoadsImagesAutomatically(true);
        web.loadDataWithBaseURL(null, course, "text/html", "utf-8", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

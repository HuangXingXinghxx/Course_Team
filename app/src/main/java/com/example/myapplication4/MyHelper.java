package com.example.myapplication4;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MyHelper extends SQLiteOpenHelper {
    private String ddlCreate = "create table Course(id integer primary key autoincrement," +
            "content varchar(20)," + "teacher varcahr(20),teacherNum varchar(50))";

    public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ddlCreate);
        Log.i("hxx","创建本地数据库成功");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

package com.example.myapplication4;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DbOperation {
    private MyHelper myHelper;
    public DbOperation(Context context){
        myHelper = new MyHelper(context, "hxx4", null, 1);
    }


    public ArrayList<Course> select() {
        //select
        ArrayList<Course> courseList = new ArrayList<>();
        String sql = "select * from Course";
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor c = myHelper.getReadableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex("id"));
            String content = c.getString(c.getColumnIndex("content"));
            String teacher = c.getString(c.getColumnIndex("teacher"));
            String teacherNum = c.getString(c.getColumnIndex("teacherNum"));
            Course cu = new Course(content, teacher, teacherNum);
            courseList.add(cu);
        }
        db.close();
        return courseList;
    }

    public Course selectByTeacherNum(String teacherNum) {
        ArrayList<Course> courseList = new ArrayList<Course>();
        SQLiteDatabase db = myHelper.getReadableDatabase();
        String sql = "select * from Course where teacherNum='" + teacherNum + "'";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            String content = c.getString(c.getColumnIndex("content"));
            String teacher1 = c.getString(c.getColumnIndex("teacher"));
            String teacherNum1 = c.getString(c.getColumnIndex("teacherNum"));
            Course cu = new Course(content, teacher1, teacherNum1);
            courseList.add(cu);
        }
        if(courseList.size()==0) return null;
        return courseList.get(0);
    }
    public void add(Course course) {
        //add
        SQLiteDatabase db = myHelper.getReadableDatabase();
        String sql1 = "insert into Course(content,teacher,teacherNum) values(?,?,?)";
        db.execSQL(sql1, new Object[]{ course.getContent(), course.getTeacher(), course.getTeacherNum()});
    }
    public ArrayList<String>SelectTeaName(){
        ArrayList<String> localteacherName = new ArrayList<>();
        String sql = "select * from Course";
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor cur =  db.rawQuery(sql,null);
        while(cur.moveToNext()){
            String teacher = cur.getString(cur.getColumnIndex("teacher"));
            localteacherName.add(teacher);
        }
        return localteacherName;
    }
    public void Delete(String name){
        SQLiteDatabase db = myHelper.getReadableDatabase();
        String sql1 = "delete from course where teacher=?";
        db.execSQL(sql1,new Object[]{name});
    }
}

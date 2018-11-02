package com.example.myapplication4;
public class Course {
    private String content;//内容
    private String teacher;//老师名
    private String teacherNum;//编号
    public Course() {
    }
    public Course( String content, String teacher,String teacherNum) {
        this.content = content;
        this.teacher = teacher;
        this.teacherNum=teacherNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTeacherNum() {
        return teacherNum;
    }

    public void setTeacherNum(String teacherNum) {
        this.teacherNum = teacherNum;
    }
}


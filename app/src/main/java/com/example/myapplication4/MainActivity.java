package com.example.myapplication4;
import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication4.adapter.HistoryAdapter;
import com.example.myapplication4.beans.Course;
import com.example.myapplication4.utill.DbOperation;
import com.example.myapplication4.utill.Spider;
import com.example.myapplication4.utill.Teacher;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //常量
    private static final int GET_COURSE = 1;
    private static final int GET_VERIFICATION = 0;
    //数据
    private ArrayList<String> teacherNameList = new ArrayList<String>();
    private Set loaclTeacherNameSet = new HashSet();
    private ArrayList<Map<String,String>> historyCourseList = new ArrayList<Map<String,String>>();
    private ArrayList<Teacher> teachers = null;
//    private Teacher currentTeacher;
    //工具类
    private Handler handler;
    private DbOperation dbOperation;
    private Spider spider;
    private Bitmap bitmap;
    //视图
    private Spinner sp;
    private AutoCompleteTextView autocomplementView;
    private ImageView imageview;
    private EditText edittext;
    private Button displayCourse;
    private LinearLayout linearLayout;
    private ListView historyList;
    //Adapter
    private ArrayAdapter<String> spinnerAdapter;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.search);
        displayCourse = findViewById(R.id.displayCourse);
        imageview = findViewById(R.id.imageview);
        autocomplementView = findViewById(R.id.teacher);
        edittext=findViewById(R.id.et1);
        sp = findViewById(R.id.weekSpinner);
        historyList = findViewById(R.id.history);
        linearLayout = findViewById(R.id.linearLayout2);
        actionBar.setTitle("江苏林业学院课表查询");
        dbOperation = new DbOperation(getApplicationContext());
        spider = new Spider(getApplicationContext());
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == GET_VERIFICATION){
                    bitmap = (Bitmap) msg.obj;
                    imageview.setImageBitmap(bitmap);
                    if(teachers==null) teachers = new ArrayList<>();
                    updateAllList();
                    initSpinner();
                    initHistoryList();
                }
                if(msg.what == GET_COURSE){
                    Course course = (Course) msg.obj;
                    Intent intent = new Intent(MainActivity.this,CourseActivity.class);
                    intent.putExtra("content",course.getContent());
                    intent.putExtra("teacher",autocomplementView.getText().toString().trim());
                    Log.i("hxx","传递了数据给课表页面"+course.getContent());
                    startActivity(intent);
                }
            }
        };
        requestPermissions(new String[]{Manifest.permission.INSTALL_LOCATION_PROVIDER, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autocomplementView.setText(historyCourseList.get(position).get("teacher"));
            }
        });
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//下拉框监听
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                autocomplementView.setText( teachers.get(position).getName().replace("(已缓存)",""));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        displayCourse.setOnClickListener(new View.OnClickListener() {//显示课程表按钮监听
            @Override
            public void onClick(View v) {
                String verification = edittext.getText().toString().trim();
                if("".equals(verification)||verification.length()!=4){
                    Toast.makeText(getApplicationContext(),"请输入验证码",Toast.LENGTH_SHORT).show();
                    return;
                }
                String teacherName = autocomplementView.getText().toString().trim().replace("(已缓存)","");
                for(Teacher teacher :teachers){
                    if(teacher.getName().replace("(已缓存)","").equals(teacherName)){
                        try {
                            getCourseList(teacher,verification);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                Toast.makeText(getApplicationContext(),"未找到教师信息",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(spinnerAdapter!=null){
            updateAllList();
            spinnerAdapter.notifyDataSetChanged();
            linearLayout.setVisibility(View.INVISIBLE);
            historyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        SetTeachersAndVerification();
    }

    public void initSpinner() {//设置下拉框和自动补充框的适配器
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teacherNameList);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);//spinner下拉视图
        sp.setAdapter(spinnerAdapter);
        ArrayAdapter<String> autocomplementviewadapter= new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,teacherNameList);
        autocomplementView.setAdapter(autocomplementviewadapter);
    }

    public void initHistoryList(){
        historyAdapter = new HistoryAdapter(getLayoutInflater(),historyCourseList);
        historyList.setAdapter(historyAdapter);
    }
    public void SetTeachersAndVerification() {//获得图片验证码和老师列表
        new Thread() {
            @Override
            public void run() {
                InputStream input = null;
                Log.i("hxx", "********线程开始");
                try {
                    input = spider.getVerficationAndcookie();
                    teachers = spider.getTeacher();
                    Log.i("hxx", "********获得内容");
                    if (input != null) {
                        Log.i("hxx", "********内容获取成功");
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        Message msg = new Message();
                        msg.obj = bitmap;
                        msg.what = GET_VERIFICATION;
                        handler.sendMessageDelayed(msg, 100);
                    } else {
                        Log.i("hxx", "********error");
                    }
                } catch (IOException e) {
                    Log.i("hxx", "********error: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void updateAllList(){
        ArrayList<Course> courses = dbOperation.select();
        updataTeacherList(courses);
        updateHistory(courses);
    }

    private void updataTeacherList(ArrayList<Course> courses){
        teacherNameList.clear();
        for(Course course:courses){
            loaclTeacherNameSet.add(course.getTeacherNum());
        }
        for(int i=0;i<teachers.size();i++){
            String teacherName = teachers.get(i).getName();
            if(loaclTeacherNameSet.contains(teachers.get(i).getNumber())){
                teacherName = teacherName +" (已缓存)";
            }
            teacherNameList.add(teacherName);
        }
    }

    private void updateHistory(ArrayList<Course> courses){
        historyCourseList.clear();
        for(int i=courses.size()-1;i>=0;i--){
            Course course = courses.get(i);
            Map courseMap = new HashMap();
            courseMap.put("teacher",course.getTeacher());
            courseMap.put("classNum",getClassNum(course.getContent()));
            historyCourseList.add(courseMap);
        }
    }

    private String getClassNum(String content){
        if("".equals(content)) return " ";
        return "本学期有课程";
    }

    public void getCourseList(final Teacher teacher, final String verification)throws IOException {//查询本地数据库或者更新本地数据库
        // TODO Auto-generated method stub
        new Thread(new Runnable() {
            @Override
            public void run() {
                Course course = null;
                if(loaclTeacherNameSet.contains(teacher.getNumber())){
                     course = dbOperation.selectByTeacherNum(teacher.getNumber());
                }
                else {
                    int  port = 12345;
                    String url = "47.107.136.58";//服务器数据库地址
                    try {
                        spider.saveVerification(verification);
                        String  cur = spider.getCourse(teacher,url,port);//服务器中得数据
                        Log.d("hxx",cur);
                        JSONObject jsa = new JSONObject(cur);
                        course = new Course();
                        course.setContent(jsa.getString("text"));
                        course.setTeacher(jsa.getString("teacher"));
                        course.setTeacherNum(jsa.getString("teacherNum"));
                        dbOperation.add(course);
                    } catch (IOException e) {
                        Log.d("hxx","!!!!!!!!!!!!!IO异常"+e.getMessage());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.d("hxx","!!!!!!!!!!!!!JSONException异常"+e.getMessage());
                        e.printStackTrace();
                    }
                }
                Message message = new Message();
                message.obj = course;
                message.what = GET_COURSE;
                handler.sendMessage(message);
            }
        }).start();
    }
}

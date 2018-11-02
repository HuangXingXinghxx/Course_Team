package com.example.myapplication4;
import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class MainActivity extends AppCompatActivity {
    private static final int GET_COURSE = 1;
    private static final int GET_VERIFICATION = 0;
    private ArrayList<String> teacherNameList = new ArrayList<String>();
    private Spinner sp;
    private AutoCompleteTextView autocomplementView;
    private ImageView imageview;
    private Handler handler;
    private Bitmap bitmap;
    private DbOperation dbOperation;
    private ArrayList<Course> cusList;
    private Spider spider;
    private EditText edittext;
    private String vertification;
    private ArrayList<Teacher> teachers = null;
    private Teacher currentTeacher;
    private Button displayCourse;
    private ArrayList<String> localTeaNameList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        displayCourse = findViewById(R.id.displayCourse);
        imageview = findViewById(R.id.imageview);
        autocomplementView = findViewById(R.id.teacher);
        edittext=findViewById(R.id.et1);
        sp = (Spinner) findViewById(R.id.weekSpinner);
        dbOperation = new DbOperation(getApplicationContext());
        String namestring="Allison";
        dbOperation.Delete(namestring);//删除数据库信息
        spider = new Spider(getApplicationContext());
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == GET_VERIFICATION){
                    bitmap = (Bitmap) msg.obj;
                    Log.i("hxx", "*********getBitmap: " + bitmap.toString());
                    imageview.setImageBitmap(bitmap);
                    if(teachers==null) teachers = new ArrayList<>();
                    for(int i=0;i<teachers.size();i++){
                        teacherNameList.add(teachers.get(i).getName());
                    }
                    setAdapterOnSpinner();
                }
                if(msg.what == GET_COURSE){
                    Course course = (Course) msg.obj;
                    Intent intent = new Intent(MainActivity.this,CourseActivity.class);
                    intent.putExtra("content",course.getContent());
                    Log.i("hxx","传递了数据给课表页面"+course.getContent());
                    startActivity(intent);
                    //search(course);//设置在webview上
                }
            }
        };
        requestPermissions(new String[]{Manifest.permission.INSTALL_LOCATION_PROVIDER, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//下拉框监听
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    currentTeacher=null;
                }
                 else
                     {
                         currentTeacher = teachers.get(position);
                         TextView tx = view.findViewById(android.R.id.text1);
                         tx.setTextColor(getResources().getColor(R.color.colorAccent));
                     }
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
                spider.saveVerification(verification);
                Toast.makeText(getApplicationContext(),"获得验证码成功！",Toast.LENGTH_SHORT).show();
                if (currentTeacher!=null){//下拉框为主
                    try {
                        Log.d("hxx","!!!!!!!!!!!!!"+currentTeacher.toString());
                        Toast.makeText(getApplicationContext(),"教师信息获取成功",Toast.LENGTH_SHORT).show();
                        updataCourseList(currentTeacher);
                        currentTeacher=null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{//自动补充框
                    String str= autocomplementView.getText().toString();
                    Log.i("hxx","输入框的值："+autocomplementView.getText().toString());
                    localTeaNameList=dbOperation.SelectTeaName();
                    if(localTeaNameList.contains(str)){
                        autocomplementView.setTextColor(getResources().getColor(R.color.colorAccent));//设置颜色
                    }
                    int i = 0;
                    for(i=0;i<teachers.size();i++){
                       if(str.equals(teachers.get(i).getName())){
                           currentTeacher=teachers.get(i);
                       }
                    }
                    Log.i("hxx","匹配数据库的教师信息"+currentTeacher);
                    if(currentTeacher!=null){
                        try {
                            Toast.makeText(getApplicationContext(),"教师信息获取成功",Toast.LENGTH_SHORT).show();
                            updataCourseList(currentTeacher);
                            currentTeacher=null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"教师信息为空",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("hxx", "*********权限获取成功: ");
        SetImage();

    }
    public InputStream getVerfication() throws IOException {//获得验证码
        OkHttpClient client = new OkHttpClient();
        String cookies = "ASP.NET_SessionId=mgo1ai55ekbyqf55sy5yja55";
        String url = "http://121.248.70.120/jwweb/sys/ValidateCode.aspx?t=113";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Host", "121.248.70.120")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Referer", "http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx")
                .addHeader("Cookie", cookies)
                .build();
        Response response = client.newCall(request).execute();
        if (response.body() != null) {
            return response.body().byteStream();
        }
        return null;
    }
    public void setAdapterOnSpinner() {//设置下拉框和自动补充框的适配器
        localTeaNameList=dbOperation.SelectTeaName();
        Log.i("hxx","****本地数据库的值："+localTeaNameList);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teacherNameList);//spinner_item默认视图
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);//spinner下拉视图
        sp.setAdapter(adapter);
        ArrayAdapter<String> autocomplementviewadapter= new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,teacherNameList);
        autocomplementView.setAdapter(autocomplementviewadapter);
    }
    public void SetImage() {//验证码显示在图片控件中
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
    public void  updataCourseList(final Teacher teacher)throws IOException {//查询本地数据库或者更新本地数据库
        // TODO Auto-generated method stub
        new Thread(new Runnable() {
            @Override
            public void run() {
                Course course = dbOperation.selectByTeacherNum(teacher.getNumber());//本地数据库
                if(course==null){
                    int  port = 12345;
                    String url = "47.107.136.58";//服务器数据库地址
                    try {
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void search(View view){
        if(currentTeacher!=null){
            try {
                updataCourseList(currentTeacher);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

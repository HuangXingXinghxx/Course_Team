package com.example.myapplication4.utill;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spider {
    private String cookies ="" ;
    private String verification = "";
    private Context context;

    public Spider(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        SharedPreferences settings =context.getSharedPreferences ("setting", 0);
        cookies = settings.getString("cookies","");
        verification = settings.getString("verification","");
    }

    private void saveCookie(String cookie){
        this.cookies = cookie;
        SharedPreferences settings =context.getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("cookies",cookie);
        editor.commit();
    }

    /**
     * 用于保存验证码
     * @param verfication
     */
    public void saveVerification(String verfication){
        this.verification = verfication;
        SharedPreferences settings =context. getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("verfication",verfication);
        editor.commit();
    }

    /**
     * 用于获取教师列表 cookie如果是空会直接返回null(网络请求)
     * @return
     * @throws IOException
     */
    public ArrayList<Teacher> getTeacher() throws IOException {
        Log.i("hxx", "**getTeacher : "+cookies);
        if("".equals(cookies)){
            return null;
        }
        ArrayList<Teacher> teacherList = new ArrayList<Teacher>();
        OkHttpClient client = new OkHttpClient();
        String url = "http://121.248.70.120/jwweb/ZNPK/Private/List_JS.aspx?xnxq=20180&t=128";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Host","121.248.70.120")
                .addHeader("Upgrade-Insecure-Requests","1")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                .addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .addHeader("Referer","http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx")
                .addHeader("Cookie",cookies)
                .build();
        Response response = client.newCall(request).execute();
        String html = response.body().string();
        Pattern pattern = Pattern.compile("[0-9A-Z]{7}>.*?<");
        Matcher match = pattern.matcher(html);
        while (match.find()){
            String[] teacheStr = match.group().replace("<","").split(">");
            Teacher teacher = new Teacher();
            teacher.setName(teacheStr[1]);
            teacher.setNumber(teacheStr[0]);
            teacherList.add(teacher);
        }
        return teacherList;
    }

    /***
     * 用于获取验证码 cookie如果是空会更新cookie,cookie不为空会直接获得验证码(网络请求)
     * @return
     * @throws IOException
     */
    public InputStream getVerficationAndcookie() throws IOException {
        OkHttpClient client = new OkHttpClient();
        if("".equals(cookies)){
            String url = "http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx";
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Host","121.248.70.120")
                    .addHeader("Upgrade-Insecure-Requests","1")
                    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                    .addHeader("Accept","image/webp,image/apng,image/*,*/*;q=0.8")
                    .addHeader("Referer","http://121.248.70.120/jwweb/_data/index_KBFB.aspx")
                    .build();
            Response response = client.newCall(request).execute();
            cookies = response.header("Set-Cookie").replace("; path=\\/","");
        }
        if(cookies == null||"".equals(cookies)) return null;
        saveCookie(cookies);
        String url = "http://121.248.70.120/jwweb/sys/ValidateCode.aspx?t=113";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Host","121.248.70.120")
                .addHeader("Upgrade-Insecure-Requests","1")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                .addHeader("Accept","image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Referer","http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx")
                .addHeader("Cookie",cookies)
                .build();
        Response response = client.newCall(request).execute();
        if(response.body() != null){

            return response.body().byteStream();
        }
        return null;

    }

    /***
     * 用于获取课程列表 如果没有cookie和验证码会直接返回null(网络请求)
     * @param teacher
     * @param address
     * @param port
     * @return
     * @throws IOException
     */
    public String getCourse(Teacher teacher,String address,int port) throws IOException,JSONException {
//        address = "localhost";//服务器地址
//        port = 12345;//服务器端口号
        if("".equals(cookies)||"".equals(verification)){
            return null;
        }
        Log.d("hxx","!!!!!!!!!!!!!cookies:"+cookies+"ver:"+verification);
        Socket socket = new Socket(address,port);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        JSONObject netObj = new JSONObject();
        netObj.put("cookies",cookies);
        netObj.put("verification",verification);
        JSONObject teacherObj = new JSONObject();
        teacherObj.put("teacher",teacher.getName());
        teacherObj.put("teacherNum",teacher.getNumber());
        JSONObject request = new JSONObject();
        request.put("teacher",teacherObj);
        request.put("net",netObj);
        out.write(request.toString());
        out.flush();
        socket.shutdownOutput();
        InputStream input = socket.getInputStream();
        byte[] lenb = new byte[8];
        input.read(lenb,0,8);
        String len =new String(lenb,"utf-8");
        byte[] buffer  = new byte[Integer.parseInt(len)+8];
        int nIdx = 0;
        int nTotalLen = buffer.length;
        int nReadLen = 0;
        while (nIdx < nTotalLen){
            nReadLen = input.read(buffer, nIdx, nTotalLen - nIdx);
            if (nReadLen > 0){
                nIdx = nIdx + nReadLen;
            }else{
                break;
            }
        }
        input.close();
        return new String(buffer,"UTF-8");
    }
}

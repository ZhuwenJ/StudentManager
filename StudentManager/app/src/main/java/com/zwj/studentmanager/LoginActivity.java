package com.zwj.studentmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import model.User;
import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;
import util.JsonUtil;
import util.UserUtil;

/**
 * 这个是用来实现普通用户登录主界面的一个类
 * @author zwj
 * @Time 2019-11-02 13:25:00
 */
public class LoginActivity extends AppCompatActivity {
    private Button log_in;
    private Button tv_register;
    private Button tv_findpwd;
    private static final int UPDATE_TEXT=1;
    private String erromsg;

    /**
     * 这个是当前活动被创建时候的逻辑
     * @author zwj
     * @Time 2019-11-02 13:25:00
     * @param
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        log_in=findViewById(R.id.btn_login);
        tv_register=findViewById(R.id.tv_register);
        tv_findpwd = findViewById(R.id.tv_find_psw);

        tv_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
            }
        });

        tv_findpwd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RepwdActivity.class);
                startActivity(intent);
            }
        });
        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=((EditText)findViewById(R.id.et_user_name)).getText().toString();
                String pwd=((EditText)findViewById(R.id.et_psw)).getText().toString();
                Map<String,String> map=new HashMap<>();
                map.put("uid",username);
                map.put("pwd",pwd);
                HttpUtil.postRequest("http://api.wangz.online/api/user/login",map,new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resonseData =response.body().string();
                        Log.d("LoginActivity","String"+resonseData);
                        JSONObject obj = null;  //建立json对象
                        String forResult;
                        forResult = "not Found!"; //如果没有输出字符，则显示forResult
                        if (!resonseData.isEmpty())
                        {
                            try  //加上异常处理
                            {
                                obj = new JSONObject(resonseData);    //将字符串转为json对象
                                forResult = obj.optString("msg");//把result对象提取出来，并转化为字符串
                                Log.d("LoginActivity","msg "+forResult);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        if(forResult.equals("success")){
                            String res= JsonUtil.getdataObj(resonseData);
                            User user=UserUtil.getUser(res);
                            Log.d("LoginActivity","name "+user.getAppointmentTime().toString());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            erromsg=forResult;
                            Message msg = new Message();
                            msg.what=UPDATE_TEXT;
                            handle.sendMessage(msg); //发送修改界面的消息
                        }


                    }
                });

            }
        });
    }


    /**
     * 这个是用来异步处理错误信息
     * @Time 2019-11-02 13:25:00
     * @param msg:异步子进程传过来的消息
     */
    @SuppressLint("HandlerLeak")
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case UPDATE_TEXT:
                    //取出线程传过来的值String ss = (String) msg.obj
                    Toast.makeText(LoginActivity.this, erromsg, Toast.LENGTH_SHORT).show();
                    break;
                default:

                    break;
            }
        }
    };
}

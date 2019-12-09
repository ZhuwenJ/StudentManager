package com.zwj.studentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;
import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;

/**
 * 这个是用来实现普通用户注册的一个类
 * @author zwj
 * @Time 2019-11-02 13:25:00
 */
public class RegistActivity extends AppCompatActivity {

    private Button register_in,back;
    private EditText username;
    private EditText uid;
    private EditText branch;
    private EditText classes;
    private RadioGroup sex;
    private EditText phone;
    private EditText email;
    private EditText pwd1;
    private EditText pwd2;
    private TextView title;
    private static final int UPDATE_TEXT=1;
    private static String  faildmsg;

    /**
     * 这个是用来初始化所有文本控件
     * @author zwj
     * @Time 2019-11-02 13:25:00
     */
    private void init(){
        back=findViewById(R.id.titleback);
        title=findViewById(R.id.all_title);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    finish();
            }
        });
        title.setText("用户注册");
        View tit=findViewById(R.id.title);
        tit.bringToFront();
        register_in = findViewById(R.id.btn_enroll);
        username=findViewById(R.id.username);
        uid=findViewById(R.id.uid);
        branch=findViewById(R.id.branch);
        classes=findViewById(R.id.classes);
        sex=findViewById(R.id.sex_rg);
        phone=findViewById(R.id.phonenumber);
        email=findViewById(R.id.email);
        pwd1=findViewById(R.id.pwd1);
        pwd2=findViewById(R.id.pwd2);
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public void onOptionPicker(View view) {
        OptionPicker picker = new OptionPicker(this, new String[]{
                "计算学院", "商学院", "外语学院","工程学院","法学院","信电学院","医学院","UW学院","传媒学院"
        });
        picker.setOffset(2);
        picker.setSelectedIndex(1);
        picker.setTextSize(16);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                branch.setText(option);
            }
        });
        picker.show();
    }
    /**
     * 这个是当前活动被创建时候的逻辑
     * @author zwj
     * @Time 2019-11-02 13:25:00
     * @param
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        register_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> map=new HashMap<>();
                if(!pwd1.getText().toString().equals(pwd2.getText().toString())) {
                    Toast.makeText(RegistActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                map.put("uid",uid.getText().toString());
                map.put("pwd",pwd1.getText().toString());
                map.put("username",username.getText().toString());
                map.put("branch",branch.getText().toString());
                map.put("classId",classes.getText().toString());
                map.put("sex",((Button)findViewById(sex.getCheckedRadioButtonId())).getText().toString());
                map.put("phonenumber",phone.getText().toString());
                map.put("email",email.getText().toString());
                map.put("departments","暂无");
                map.put("rank","1");

                HttpUtil.postRequest("http://api.wangz.online/api/user/register", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resonseData = response.body().string();
                        Log.d("Test", "String" + resonseData);
                        JSONObject obj = null;  //建立json对象
                        String forResult;
                        forResult = "not Found!"; //如果没有输出字符，则显示forResult
                        if (!resonseData.isEmpty()) {
                            try  //加上异常处理
                            {
                                obj = new JSONObject(resonseData);    //将字符串转为json对象
                                forResult = obj.optString("msg");//把result对象提取出来，并转化为字符串
                                Log.d("Test", "msg " + forResult);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (forResult.equals("success")) {
                            Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Message msg = new Message();
                            msg.what=UPDATE_TEXT;
                            faildmsg=forResult;
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
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case UPDATE_TEXT:
                    //取出线程传过来的值String ss = (String) msg.obj
                    Toast.makeText(RegistActivity.this, faildmsg, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}

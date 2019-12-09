package com.zwj.studentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;

public class RepwdActivity extends AppCompatActivity {
    private Button reset_pwd,back;
    private EditText stuid;
    private TextView title;
    private static final int UPDATE_TEXT=1;
    private static String  faildmsg;

    /**
     * 这个是用来初始化文本控件
     * @author hcj
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
        title.setText("重置密码");
        reset_pwd = findViewById(R.id.btn_reset_pwd);
        stuid = findViewById(R.id.stuid);
    }


    /**
     * 这个是当前活动被创建时候的逻辑
     * @author hcj
     * @Time 2019-11-02 13:25:00
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset);
        init();
        reset_pwd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                HttpUtil.getRequest("http://api.wangz.online/api/user/resetSec?uid="+stuid.getText().toString(),new okhttp3.Callback(){

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
                        if(!resonseData.isEmpty()){
                            try //异常处理
                            {
                                obj = new JSONObject(resonseData);
                                forResult = obj.optString("msg");
                                Log.d("Test",forResult);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(forResult.equals("pwd已经重置完毕")){
                            Message msg = new Message();
                            msg.what = UPDATE_TEXT;
                            faildmsg = "重置成功";
                            handle.sendMessage(msg);
                            Intent intent = new Intent(RepwdActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Message msg = new Message();
                            msg.what = UPDATE_TEXT;
                            faildmsg = forResult;
                            handle.sendMessage(msg);
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
                    Toast.makeText(RepwdActivity.this, faildmsg, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}

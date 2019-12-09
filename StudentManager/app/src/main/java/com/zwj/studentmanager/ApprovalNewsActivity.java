package com.zwj.studentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.User;
import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;
import util.JsonUtil;
import util.UserUtil;

public class ApprovalNewsActivity extends AppCompatActivity {
    private List<String> teamList;
    private Map<String,Integer> map;
    private TextView title;
    private Spinner spinner1;
    private String curString;
    private ArrayAdapter<String> arrayAdapter;
    private Button change,back;
    private static final int UPDATE_TEXT=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_position);
        change=findViewById(R.id.change_position);
        back=findViewById(R.id.titleback);
        title=findViewById(R.id.all_title);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    finish();
            }
        });
        initView();
        //设置下拉列表的风格
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner1.setAdapter(arrayAdapter);
        //设置点击事件
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            curString=teamList.get(i);
            }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> map1=new HashMap<>();
                map1.put("uid", User.currentUser.getUid());
                map1.put("username",User.currentUser.getUsername());
                map1.put("branch",User.currentUser.getBranch());
                map1.put("classId",User.currentUser.getClassId());
                map1.put("sex",User.currentUser.getSex());
                map1.put("phonenumber",User.currentUser.getPhonenumber());
                map1.put("email",User.currentUser.getEmail());
                map1.put("rank",map.get(curString)+"");
                map1.put("departments",User.currentUser.getDepartments());

                HttpUtil.postRequest("http://api.wangz.online/api/user/userinfoedit",map1,new okhttp3.Callback(){

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resonseData =response.body().string();
                        JSONObject obj = null;  //建立json对象
                        String forResult;
                        forResult = "not Found!"; //如果没有输出字符，则显示forResult
                        if (!resonseData.isEmpty())
                        {
                            try  //加上异常处理
                            {
                                obj = new JSONObject(resonseData);    //将字符串转为json对象
                                forResult = obj.optString("msg");//把result对象提取出来，并转化为字符串
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        Message msg = new Message();
                        if(forResult.equals("success")){
                            String res= JsonUtil.getdataObj(resonseData);
                            User.currentUser= UserUtil.getUser(res);
                            msg.what=UPDATE_TEXT;
                            handle.sendMessage(msg); //发送修改界面的消息
                        }else{
                            msg.what=2;
                            handle.sendMessage(msg); //发送修改界面的消息
                        }
                    }
                });
            }
        });
    }
    public void initView(){
        teamList = new ArrayList<>();
        map=new HashMap<>();
        initList();
        spinner1 = findViewById(R.id.spinner1);
        arrayAdapter = new ArrayAdapter<String>(ApprovalNewsActivity.this,android.R.layout.simple_spinner_item,teamList);
    }
    public void initList(){

        teamList.add("游客");
        map.put("游客",1);

        teamList.add("干事");
        map.put("干事",2);

        teamList.add("部长");
        map.put("部长",3);

        teamList.add("副主席");
        map.put("副主席",4);

        teamList.add("主席");
        map.put("主席",5);

        teamList.add("指导老师");
        map.put("指导老师",6);
    }

    @SuppressLint("HandlerLeak")
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case UPDATE_TEXT:
                    //取出线程传过来的值String ss = (String) msg.obj
                    Toast.makeText(ApprovalNewsActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ApprovalNewsActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}

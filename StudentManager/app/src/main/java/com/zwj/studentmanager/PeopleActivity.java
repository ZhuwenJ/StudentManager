package com.zwj.studentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ListView.MyDepAdapter;
import ListView.MyPeoAdapter;
import model.Department;
import model.User;
import okhttp3.Call;
import okhttp3.Response;
import util.DepUtil;
import util.HttpUtil;
import util.JsonUtil;
import util.ListUtil;
import util.UserUtil;

public class PeopleActivity extends AppCompatActivity {
    TextView title;
    RadioButton back;
    SwipeMenuListView peoplist;
    ArrayList<User> data=new ArrayList<User>();
    MyPeoAdapter adapter;
    private String erromessage;
    private String depid;
    public void title(){
        back=findViewById(R.id.titleback);
        title=findViewById(R.id.all_title);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(title.getText().equals("人员管理")){
                    finish();
                }else {
                    setContentView(R.layout.people_manage);
                    getdata();
                    title.setText("人员管理");
                }
            }
        });
    }

    TextView branch,email,classid,department,sex,phonenumber;
    private void Peodetail(User user) {
        setContentView(R.layout.personal_information);
        title();
        findViewById(R.id.modifypwd).setVisibility(View.GONE);
        title.setText(user.getUsername());
        branch=findViewById(R.id.branch);
        email=findViewById(R.id.email);
        classid=findViewById(R.id.classid);
        department=findViewById(R.id.department);
        sex=findViewById(R.id.sex);
        phonenumber=findViewById(R.id.phonenumber);

        branch.setText(user.getBranch());
        if(user.getEmail().equals("null"))
            email.setText("暂无");
        else
            email.setText(user.getEmail());
        classid.setText(user.getClassId());
        department.setText(user.getDepartments());
        sex.setText(user.getSex());
        phonenumber.setText(user.getPhonenumber());


    }

    private void getdepid(){
        title();
        title.setText("人员管理");
        HttpUtil.getRequest("http://api.wangz.online/api/depart/searchBydepName?depName="+User.currentUser.getDepartments(),new okhttp3.Callback(){
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

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                if(forResult.equals("success")){
                    String jsonData= JsonUtil.getdataObj(resonseData);
                    Log.d("Test",jsonData);
                    depid= DepUtil.getDepId(jsonData);
                   //发送修改界面的消息
                    Message msg = new Message();
                    msg.what = 1;
                    handle.sendMessage(msg); //发送修改界面的消息

                }else{
                    erromessage=forResult;
                    Message msg = new Message();
                    msg.what = 0;
                    handle.sendMessage(msg); //发送修改界面的消息
                }
            }
        });

    }

    private void getdata(){
        title();
        title.setText("人员管理");
        String url="";
        if(Integer.parseInt(User.currentUser.getRank())<=3)
            url+="http://api.wangz.online/api/depart/getdepMenberById?depId="+depid;
        else if(User.currentUser.getRank().equals("4"))
            url+="http://api.wangz.online/api/depart/getdepMenberByfenguan?chairId="+User.currentUser.getUid();
        else{
            url="http://api.wangz.online/api/user/getallusr";
        }
        HttpUtil.getRequest(url, new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resonseData = response.body().string();
                JSONObject obj = null;  //建立json对象
                String forResult;
                forResult = "not Found!"; //如果没有输出字符，则显示forResult
                String msgs="";
                if (!resonseData.isEmpty()) {
                    try  //加上异常处理
                    {
                        obj = new JSONObject(resonseData);    //将字符串转为json对象
                        msgs =obj.optString("msg");
                        forResult = obj.optString("dataObj");//把result对象提取出来，并转化为字符串
                        Log.d("UserList",forResult);
                        if(msgs.equals("success")) {
                            if(User.currentUser.getRank().equals("4"))
                                data=UserUtil.getFenguanUsers(forResult);
                            else
                                data=UserUtil.getUsers(forResult);
                            User.users=data;
                            Message msg = new Message();
                            msg.what = 2;
                            handle.sendMessage(msg); //发送修改界面的消息
                        }
                        else{
                            erromessage=forResult;
                            Message msg = new Message();
                            msg.what = 0;
                            handle.sendMessage(msg); //发送修改界面的消息
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void init(){
        peoplist=(SwipeMenuListView) findViewById(R.id.people_list);
        //设置一个设配器，最重要的一个方法
        adapter=new MyPeoAdapter(PeopleActivity.this,data);
        peoplist.setAdapter(adapter);
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1= ListUtil.SwipeMenuItemTitle(PeopleActivity.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                menu.addMenuItem(item1);
                if(Integer.parseInt(User.currentUser.getRank())>=2) {
                    SwipeMenuItem deleteItem = ListUtil.SwipeMenuItemIcon(PeopleActivity.this, R.mipmap.delete,new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                    menu.addMenuItem(deleteItem);
                }
                else if(Integer.parseInt(User.currentUser.getRank())==1){
                    SwipeMenuItem applyItem = ListUtil.SwipeMenuItemTitle(PeopleActivity.this,"申请",new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                    menu.addMenuItem(applyItem);
                }

            }
        };
        peoplist.setMenuCreator(creater);
        peoplist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0: {
                        //置顶的逻辑
                        if (position == 0) {
                            Toast.makeText(PeopleActivity.this, "此项已经置顶", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        User dep = data.get(position);
                        for (int i = position; i > 0; i--) {
                            User s = data.get(i - 1);
                            data.remove(i);
                            data.add(i, s);
                        }
                        data.remove(0);
                        data.add(0, dep);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    case 1: {
                        if(Integer.parseInt(User.currentUser.getRank())>=2) {
                            //删除的逻辑
                            data.remove(position);
                            adapter.notifyDataSetChanged();
                        }else{

                        }
                        break;
                    }
                }
                return false;
            }
        });

        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.prl_view);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //修改数据的代码，最后记得填上此行代码
                getdata();
                layout.setRefreshing(false);
            }
        });
        title();
        title.setText("人员管理");


        peoplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user=(User) adapter.getItem(position);
                Peodetail(user);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_manage);
        getdepid();
    }

    /**
     * Description:异步处理子线程发过来的消息，并对消息处理
     * @author zwj
     * @Time 2019-11-02 13:25:00
     */
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    Toast.makeText(PeopleActivity.this, erromessage, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //取出线程传过来的值String ss = (String) msg.obj
                    getdata();
                    break;
                case 2:
                    init();
                default:
                    break;
            }
        }
    };
}

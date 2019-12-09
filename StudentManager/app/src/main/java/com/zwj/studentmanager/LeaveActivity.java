package com.zwj.studentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ListView.MyVacAdapter;
import model.User;
import model.Vacation;
import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;
import util.ListUtil;
import util.VactionUtil;

public class LeaveActivity extends AppCompatActivity implements View.OnClickListener, OnDateSetListener {
    TextView myleave,applyleave,title;
    RadioButton back;
    ArrayList<Vacation> data=new ArrayList<>();
    MyVacAdapter adapter;
    private Vacation curvac=new Vacation();
    private String erromessage;
    SwipeMenuListView vaclist;

    public void title(){
        title=findViewById(R.id.all_title);
        title.setText("请假管理");
        back=findViewById(R.id.titleback);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(title.getText().equals("请假管理")){
                    finish();
                }
                else if(title.getText().equals("请假详情")){
                    setContentView(R.layout.vacate_list);
                    getVacData_me();
                }
                else {
                    setContentView(R.layout.leave_manage);
                    init();
                }
            }
        });
    }
    public void init(){
        title();
        title.setText("请假管理");
        myleave=findViewById(R.id.myleave);
        applyleave=findViewById(R.id.applyleave);
        myleave.setOnClickListener(this);
        applyleave.setOnClickListener(this);
    }

    private void getVacData_me() {
        title();
        title.setText("我的请假");
        String url = "";
        url += "http://api.wangz.online/api/judge/getapplyInfoById?applyId="+ User.currentUser.getUid();

        Log.d("Test",url);
        HttpUtil.getRequest(url,new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resonseData = response.body().string();
                JSONObject obj = null;  //建立json对象
                String forResult;
                forResult = "not Found!"; //如果没有输出字符，则显示forResult
                String judge;

                if (!resonseData.isEmpty()) {
                    try{
                        obj = new JSONObject(resonseData);    //将字符串转为json对象
                        forResult = obj.optString("dataObj");//把result对象提取出来，并转化为字符串
                        judge = obj.optString("msg");

                        if(judge.equals("success")){
                            Log.d("Test",forResult);
                            data= Vacation.getVacation_me(forResult);
                            if(data.size()==0){
                                Log.d("Test","faild"+data.size());
                                erromessage="暂无申请记录";
                                Message msg = new Message();
                                msg.what=0;//handle里0代表错误信息
                                handle.sendMessage(msg);
                            }else {
                                Log.d("Test","success"+data.size());
                                Message msg = new Message();
                                msg.what = 1;//1代表成功
                                handle.sendMessage(msg);
                            }
                        }
                        else{
                            erromessage=judge;
                            Message msg = new Message();
                            msg.what=0;
                            handle.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void Vacinit_me() {
        for (int i=0;i<data.size();i++){
            Log.d("Test",data.get(i).getJudgeid());
        }
        vaclist=(SwipeMenuListView) findViewById(R.id.vacate_list);
        //设置一个设配器，最重要的一个方法
        adapter=new MyVacAdapter(LeaveActivity.this,data);
        vaclist.setAdapter(adapter);
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1= ListUtil.SwipeMenuItemTitle(LeaveActivity.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                menu.addMenuItem(item1);
                SwipeMenuItem editeItem = ListUtil.SwipeMenuItemTitle(LeaveActivity.this, "详情",new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                menu.addMenuItem(editeItem);
            }
        };
        vaclist.setMenuCreator(creater);
        vaclist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int posi=position;
                switch (index) {
                    case 0: {
                        //置顶的逻辑
                        if (position == 0) {
                            Toast.makeText(LeaveActivity.this, "此项已经置顶", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Vacation vacation = data.get(position);
                        for (int i = position; i > 0; i--) {
                            Vacation s = data.get(i - 1);
                            data.remove(i);
                            data.add(i, s);
                        }
                        data.remove(0);
                        data.add(0, vacation);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    case 1: {
                        Vacation vacation = data.get(position);
                        getVacDetailData(position);
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
                getVacData_me();
                vaclist.deferNotifyDataSetChanged();
                layout.setRefreshing(false);
            }
        });
        vaclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setContentView(R.layout.vacate_detail);
                getVacDetailData(position);
            }
        });

    }

    private void getVacDetailData(final int position){
        title();
        title.setText("请假详情");

        String url="http://api.wangz.online/api/qingjia/getQingByjudgeId?judgeId="+data.get(position).getJudgeid();
        Log.d("Test",url);
        HttpUtil.getRequest(url,new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resonseData = response.body().string();
                JSONObject obj = null;  //建立json对象
                String forResult;
                forResult = "not Found!"; //如果没有输出字符，则显示forResult
                String panduan;
                if (!resonseData.isEmpty()) {
                    try  //加上异常处理
                    {
                        obj = new JSONObject(resonseData);    //将字符串转为json对象
                        forResult = obj.optString("dataObj");//把result对象提取出来，并转化为字符串
                        panduan =obj.optString("msg");
                        if(panduan.equals("success")){
                            Log.d("Test",forResult);
                            Vacation vac=new Vacation();
                            vac= VactionUtil.getVacationDetail(forResult);
                            data.get(position).setVacateid(vac.getVacateid());
                            data.get(position).setVacateperson(vac.getVacateperson());
                            data.get(position).setBacktime(vac.getBacktime());
                            data.get(position).setLeavetime(vac.getLeavetime());
                            data.get(position).setVacatereason(vac.getVacatereason());
                            data.get(position).setPersondepartment(vac.getPersondepartment());
                            curvac=data.get(position);

                            if(data.size()==0){
                                erromessage="获取失败";
                                Message msg = new Message();
                                msg.what=0;
                                handle.sendMessage(msg);
                            }else {
                                Message msg = new Message();
                                msg.what = 2;
                                handle.sendMessage(msg);
                            }
                        }
                        else{
                            erromessage=panduan;
                            Message msg = new Message();
                            msg.what=0;
                            handle.sendMessage(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    TextView vacateID,departmentname,leavetime,backtime,reason;
    private void VacDetailint() {
        vacateID=findViewById(R.id.vacateID);
        departmentname=findViewById(R.id.departmentname);
        leavetime=findViewById(R.id.leavetime);
        backtime=findViewById(R.id.backtime);
        reason=findViewById(R.id.reason);

        vacateID.setText(curvac.getVacateperson());
        departmentname.setText(curvac.getPersondepartment());
        leavetime.setText(curvac.getLeavetime());
        backtime.setText(curvac.getBacktime());
        reason.setText(curvac.getVacatereason());


    }

    TextView applyreason,leave_time,back_time;
    Button applyleavetime,applybacktime;
    TimePickerDialog  mDialogAll;
    int flage;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public  void AddVac(){
        title();
        View tit = findViewById(R.id.title);
        tit.bringToFront();
        title.setText("请假申请");
        Button add = findViewById(R.id.btn_leave_apply);
        add.setText("申请");

        leave_time=findViewById(R.id.leave_time);
        back_time=findViewById(R.id.back_time);
        applyreason = findViewById(R.id.departmentname);//这里没改名字，应该是请假原因
        applyleavetime = findViewById(R.id.choose_leavetime);
        applybacktime = findViewById(R.id.choose_backtime);
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("时间选择")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();
        applyleavetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flage=0;
                mDialogAll.show(getSupportFragmentManager(), "all");
            }
        });
        applybacktime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flage=1;
                mDialogAll.show(getSupportFragmentManager(), "all");
            }
        });

        add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Map<String,String> map = new HashMap<>();

                map.put("vacateperson",User.currentUser.getUid().toString());
                map.put("persondepartment",User.currentUser.getDepartments().toString());
                map.put("vacatereason",applyreason.getText().toString());
                map.put("leavetime",leave_time.getText().toString());
                map.put("backtime",back_time.getText().toString());

                HttpUtil.postRequest("http://api.wangz.online/api/qingjia/addvacate",map,new okhttp3.Callback(){

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resonseData = response.body().string();
                        Log.d("Test", resonseData);
                        JSONObject obj = null;  //建立json对象
                        String forResult;
                        forResult = "not Found!"; //如果没有输出字符，则显示forResult

                        if(!resonseData.isEmpty()){
                            try //异常处理
                            {
                                obj = new JSONObject(resonseData);
                                forResult = obj.optString("msg");
                                Log.d("Test","msg"+forResult);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(forResult.equals("success")){
                            Message msg = new Message();
                            msg.what = 1;//1代表添加成功
                            handle.sendMessage(msg);//发送添加成功修改界面消息
                        }
                        else{
                            erromessage = forResult;
                            Message msg = new Message();
                            msg.what = 0;
                            handle.sendMessage(msg);//修改界面消息
                        }
                    }
                });
            }
        });
    }
    @Override
    public void onDateSet(TimePickerDialog timePickerDialog, long millseconds) {
        String text = getDateToString(millseconds);
        if(flage==0)
            leave_time.setText(text);
        else
            back_time.setText(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave_manage);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.myleave:{
                setContentView(R.layout.vacate_list);
                getVacData_me();
                break;
            }
            case R.id.applyleave:{
                setContentView(R.layout.leave_apply);
                AddVac();
                break;
            }
        }

    }

    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    Toast.makeText(LeaveActivity.this, erromessage, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //取出线程传过来的值String ss = (String) msg.obj
                    Vacinit_me();
                    break;
                case 2:
                    VacDetailint();
                    break;
                default:
                    break;
            }
        }
    };
}

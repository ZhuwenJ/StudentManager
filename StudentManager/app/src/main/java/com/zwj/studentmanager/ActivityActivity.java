package com.zwj.studentmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuAdapter;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.bruce.pickerview.popwindow.DatePickerPopWin;
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
import java.util.List;
import java.util.Map;

import ListView.MyActivityAdapter;
import model.Activity;
import model.Department;
import model.User;
import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;
import util.ListUtil;
import util.Util;

public class ActivityActivity extends AppCompatActivity implements View.OnClickListener, OnDateSetListener  {
    Button apply,list,starttime;
    RadioButton back;
    TextView title;
    private String erromessage;
    ArrayList<Activity> data=new ArrayList<>();
    ArrayList<Department> depdata=new ArrayList<>();

    SwipeMenuListView activitylist;//定义活动列表，根据框架自定义的ListView
    MyActivityAdapter adapter;//适配器
    int flage;



    public void title(){
        back=findViewById(R.id.titleback);
        title=findViewById(R.id.all_title);
        back.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                checkBoxs=new ArrayList<CheckBox>();
                if(title.getText().equals("活动管理")){
                    finish();
                }
                else if(title.getText().equals("所有活动")|title.getText().equals("申请活动")){
                    setContentView(R.layout.activity);
                    init();
                    title.setText("活动管理");
                }
                else{
                    setContentView(R.layout.activity_list);
                    getData();
                    title.setText("所有活动");
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void init(){
        title();
        title.setText("活动管理");
        View apply=findViewById(R.id.act_apply);
        View list=findViewById(R.id.vac_relative);
        RadioButton id=findViewById(R.id.changes);
        Drawable drawable=this.getResources().getDrawable(R.drawable.ic_search);
        id.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
        apply.setOnClickListener(this);
        list.setOnClickListener(this);
    }

    public void getData(){
        title();
        title.setText("所有活动");
        HttpUtil.getRequest("http://api.wangz.online/api/activity/getActList", new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e){
                Message msg = new Message();
                msg.what=0;
                erromessage=e.toString();
                handle.sendMessage(msg); //发送修改界面的消息
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String responseData = response.body().string();
                Log.d("ActList",responseData);
                JSONObject obj = null;//建立json对象;
                String forResult;
                forResult = "NOT Fount!"; //如果没有输出字符，则显示forResult;
                String stringdata="";
                if(!responseData.isEmpty()){
                    try //异常值处理，catch异常
                    {
                        obj = new JSONObject(responseData);//将字符串转为json对象;
                        forResult = obj.optString("msg");//把result对象提取出来，转化为字符串
                        stringdata=obj.getString("dataObj");
                        Log.d("ActList",forResult);
                        Activity.activitys = Activity.getAct(stringdata);
                        data=Activity.activitys;
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    if(forResult.equals("success")){
                        Message msg = new Message();
                        msg.what=1;
                        handle.sendMessage(msg); //发送修改界面的消息
                    }else{
                        Message msg = new Message();
                        msg.what=0;
                        erromessage=forResult;
                        handle.sendMessage(msg); //发送修改界面的消息
                    }
                }
            }

        });
    }

    public void getDepdata(){
        title();
        title.setText("申请活动");
        HttpUtil.getRequest("http://api.wangz.online/api/depart/getdepList", new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resonseData = response.body().string();
                Log.d("DepList",resonseData);
                JSONObject obj = null;  //建立json对象
                String forResult;
                forResult = "not Found!"; //如果没有输出字符，则显示forResult
                if (!resonseData.isEmpty()) {
                    try  //加上异常处理
                    {
                        obj = new JSONObject(resonseData);    //将字符串转为json对象
                        forResult = obj.optString("dataObj");//把result对象提取出来，并转化为字符串
                        Log.d("DepList",forResult);
                        depdata=Department.getDeps(forResult);
                        Department.deps=depdata;
                        Message msg = new Message();
                        msg.what=6;
                        handle.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void ActivityList(){
        activitylist=(SwipeMenuListView) findViewById(R.id.activity_list);
        //设置适配器，这很重要

        adapter = new MyActivityAdapter(ActivityActivity.this,data);
        activitylist.setAdapter(adapter);
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1= ListUtil.SwipeMenuItemTitle(ActivityActivity.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                menu.addMenuItem(item1);
                if(Integer.parseInt(User.currentUser.getRank())==3) {
                    SwipeMenuItem applyItem = ListUtil.SwipeMenuItemTitle(ActivityActivity.this, "修改", new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                    menu.addMenuItem(applyItem);
                }else if(Integer.parseInt(User.currentUser.getRank())>=4){
                    SwipeMenuItem deleteItem = ListUtil.SwipeMenuItemIcon(ActivityActivity.this, R.mipmap.delete,new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                    menu.addMenuItem(deleteItem);
                }

            }
        };

        activitylist.setMenuCreator(creater);
        activitylist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:{
                        //置顶部分
                        if(position==0){
                            Toast.makeText(ActivityActivity.this, "此项已经置顶", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Activity activity = data.get(position);
                        for(int i = position;i>0;i--){
                            Activity s = data.get(i-1);
                            data.remove(i);
                            data.add(i,s);
                        }
                        data.remove(0);
                        data.add(0,activity);
                        adapter.notifyDataSetChanged();
                        break;
                    }//case 0
                    case 1:
                        if(Integer.parseInt(User.currentUser.getRank())>=4){
                        //删除的逻辑
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityActivity.this);
                        dialog.setTitle("删除活动确认");  //对话框标题
                        dialog.setMessage("确认删除么?");//内容
                        dialog.setCancelable(true);//可撤销性
                        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ActivityActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                data.remove(position);
                                adapter.notifyDataSetChanged();//提示内容
                            }   //确定按钮的点击事件
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ActivityActivity.this,"取消",Toast.LENGTH_SHORT).show();  //提示内容
                            }//取消按钮的点击事件
                        });
                        dialog.show();  //全部显示出来
                    }
                        break;
                    case 2:{
                        if(Integer.parseInt(User.currentUser.getRank())==3) {
                            //删除的逻辑
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityActivity.this);
                            dialog.setTitle("删除活动确认");  //对话框标题
                            dialog.setMessage("确认删除么?");//内容
                            dialog.setCancelable(true);//可撤销性
                            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(ActivityActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                    data.remove(position);
                                    adapter.notifyDataSetChanged();//提示内容
                                }   //确定按钮的点击事件
                            });
                            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(ActivityActivity.this,"取消",Toast.LENGTH_SHORT).show();  //提示内容
                                }//取消按钮的点击事件
                            });
                            dialog.show();  //全部显示出来
                        }else{

                        }
                        break;
                    }//case 1
                }
                return false;
            }//onMenuItemClick
        });

        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.prl_activity_view);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //修改数据的代码，最后记得填上此行代码
                getData();
                layout.setRefreshing(false);
            }
        });

        activitylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity act=(Activity) adapter.getItem(position);
                ActivityDetail(act);
            }
        });
    }

    TextView act_name,hold_dep,peo_count,start_time,end_time,location;
    public void ActivityDetail(Activity act){
        setContentView(R.layout.activity_detail);
        title();
        title.setText("活动详情");
        act_name=findViewById(R.id.act_name);
        hold_dep=findViewById(R.id.hold_dep);
        peo_count=findViewById(R.id.peo_count);
        start_time=findViewById(R.id.start_time);
        end_time=findViewById(R.id.end_time);
        location=findViewById(R.id.location);
        TextView assist_dep=findViewById(R.id.assist_dep);

        assist_dep.setText("协同部门:"+act.getDepartmentassist());
        act_name.setText("活动名称："+act.getActivityname());
        hold_dep.setText("主办部门："+act.getApplydep());
        peo_count.setText("人          数：  "+act.getPeopleamount()+"");
        start_time.setText("开始时间： "+act.getStarttime());
        end_time.setText("结束时间："+act.getOvertime());
        location.setText("地点："+act.getActlocation());

    }

    TimePickerDialog  mDialogAll;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<CheckBox> checkBoxs = new ArrayList<CheckBox>();
    EditText introduction;
    private void ActivityApply() {
        LinearLayout root=findViewById(R.id.checkboxlist);
        // 动态加载布局
        // 给指定的checkbox赋值
        for (int i = 0; i < depdata.size(); i++) {
            // 先获得checkbox.xml的对象
            CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(
                    R.layout.checkbox, null);
            checkBoxs.add(checkBox);
            checkBox.setOnClickListener(this);
            checkBoxs.get(i).setText(depdata.get(i).getDepartmentname());
            // 实现了在
            root.addView(checkBox, i);
        }

        start_time=findViewById(R.id.start_time);
        starttime=findViewById(R.id.starttime);
        end_time=findViewById(R.id.over_time);
        Button deltime=findViewById(R.id.overtime);
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

        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flage=0;
                mDialogAll.show(getSupportFragmentManager(), "all");
            }
        });
        deltime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flage=1;
                mDialogAll.show(getSupportFragmentManager(), "all");
            }
        });

        act_name=findViewById(R.id.activity_name);
        peo_count=findViewById(R.id.peopleamount);
        location=findViewById(R.id.place);
        introduction=findViewById(R.id.content);

        Button add=findViewById(R.id.activity_apply);
        add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Map<String,String> map=new HashMap<>();
                if(result.equals("您还没有选中协同部门")){
                    result="";
                    Toast.makeText(ActivityActivity.this,"您还没有选中协同部门",Toast.LENGTH_SHORT).show();
                }
                map.put("activityname",act_name.getText().toString());
                map.put("applydep",User.currentUser.getDepartments());
                map.put("peopleamount",peo_count.getText().toString());
                map.put("actlocation",location.getText().toString());
                map.put("starttime",start_time.getText().toString());
                map.put("overtime",end_time.getText().toString());
                map.put("actcontent",introduction.getText().toString());
                map.put("departmentassist",result);
                map.put("applyuserid",User.currentUser.getUid());

                HttpUtil.postRequest("http://api.wangz.online/api/activity/addAct", map, new okhttp3.Callback(){

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
                        if(forResult.equals("success")) {
                            Message msg = new Message();
                            msg.what = 3;
                            handle.sendMessage(msg); //发送修改界面的消息

                        }
                        else{
                            erromessage=result;
                            Message msg = new Message();
                            msg.what = 0;
                            handle.sendMessage(msg); //发送修改界面的消息
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
            start_time.setText(text);
        else
            end_time.setText(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        init();
        title.setText("活动管理");
    }

    private String result = "";
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        result="";
        boolean i=true;
        for (CheckBox checkBox : checkBoxs) {
            if (checkBox.isChecked()) {
                if(i){
                    result += checkBox.getText();
                    i=false;
                }else
                    result =result+","+checkBox.getText() ;
            }
        }
        if ("".equals(result)) {
            result = "您还没有选中协同部门";
        }
        switch(v.getId()){
            case R.id.back:{
                if(title.getText().equals("活动管理")){
                    finish();
                }
                else {
                    setContentView(R.layout.activity);
                    init();
                    title.setText("活动管理");
                }
                break;
            }
            case R.id.act_apply:{
                setContentView(R.layout.activity_apply);
                title();
                title.setText("申请活动");
                getDepdata();
                break;
            }
            case R.id.vac_relative:{
                setContentView(R.layout.activity_display);
                title();
                title.setText("所有活动");
                getData();
                break;
            }
        }
    }

    private Handler handle = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    Toast.makeText(ActivityActivity.this, erromessage, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //取出线程传过来的值String ss = (String) msg.obj
                    ActivityList();
                    break;
                case 3:
                    Toast.makeText(ActivityActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.activity);
                    init();
                    break;
                case 4:
                    setContentView(R.layout.department_chairman);
                    getData();
                    title.setText("部门管理");
                    break;
                case 5:
                    Toast.makeText(ActivityActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.department_chairman);
                    getData();
                    title.setText("部门管理");
                    break;
                case 6:
                    ActivityApply();
                    break;
                default:
                    break;
            }
        }
    };
}

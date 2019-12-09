package com.zwj.studentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ListView.MyActivityAdapter;
import ListView.MyDepAdapter;
import ListView.MyPeoForDepAdapter;
import ListView.MyVacAdapter;
import model.Activity;
import model.Department;
import model.PepForDep;
import model.User;
import model.Vacation;
import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;
import util.ListUtil;
import util.VactionUtil;

public class ApprovalActivity extends AppCompatActivity implements View.OnClickListener {
    Drawable drawable;
    View actrel,leaverel,peorel;
    RadioButton back;
    TextView title;
    ArrayList<Vacation> data=new ArrayList<>();
    ArrayList<Activity> actdata=new ArrayList<>();
    ArrayList<Activity> resultdata=new ArrayList<>();
    ArrayList<PepForDep> pepdata=new ArrayList<>();
    MyVacAdapter adapter;
    SwipeMenuListView vaclist;
    private Vacation curvac=new Vacation();
    private Activity curact=new Activity();
    int page;

    private String erromessage;
    public void title(){
        back=findViewById(R.id.titleback);
        title=findViewById(R.id.all_title);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(title.getText().equals("审批管理")){
                    finish();
                }else if(title.getText().equals("请假审批")|title.getText().equals("活动审批")|title.getText().equals("加入部门申请")){
                    setContentView(R.layout.approval_manage);
                    init();
                    title.setText("审批管理");
                }else if(title.getText().equals("请假详情")){
                    setContentView(R.layout.vacate_list);
                    getVacData();
                }else if(title.getText().equals("活动详情")){
                    setContentView(R.layout.activity_list);
                    applyActinit();
                }
            }
        });
    }
    public void init(){
        title();
        page=1;
        actrel=findViewById(R.id.act_relative);
        leaverel=findViewById(R.id.vac_relative);
        peorel=findViewById(R.id.peo_relative);
        peorel.setVisibility(View.GONE);
        if(Integer.parseInt(User.currentUser.getRank())==3){
            actrel.setVisibility(View.GONE);
            peorel.setVisibility(View.VISIBLE);
        }

        actrel.setOnClickListener(this);
        leaverel.setOnClickListener(this);
        peorel.setOnClickListener(this);

    }


    private void approve(int flage,String result,String objid,String judgeid){
        String url="";
        if(flage==1)
            url=url+"http://api.wangz.online/api/activity/judgeAct?actid="+objid+"&judgeid="+judgeid+"&isjudge="+result;
        else if(flage==2)
            url+="http://api.wangz.online/api/qingjia/judgeQing?vacateid="+objid+"&judgeid="+judgeid+"&isjudge="+result;
        else
            url+="http://api.wangz.online/api/applydep/changeapplyflag?key="+objid+"&flag="+result;
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
                        panduan = obj.optString("msg");
                        if (panduan.equals("success")) {
                            erromessage = "审批成功";
                            Message msg = new Message();
                            msg.what = 0;
                            handle.sendMessage(msg);
                        }

                        } catch(JSONException e){
                        e.printStackTrace();
                    }
                    }
                }
        });
    }

    private void getVacData() {
        title();
        title.setText("请假审批");

        String url="";
        if(Integer.parseInt(User.currentUser.getRank())==3){
            url+="http://api.wangz.online/api/qingjia/getdepMenberQingByministerxId?ministerId="+User.currentUser.getUid();
        }
        else if(Integer.parseInt(User.currentUser.getRank())==4) {
            url+="http://api.wangz.online/api/qingjia/getministMenberQingByfenguanchairId?chairId="+User.currentUser.getUid();
        }
        else if(Integer.parseInt(User.currentUser.getRank())>=5){
            url+="http://api.wangz.online/api/qingjia/getfuchairMenberQingByzhengchairId?chairId="+User.currentUser.getUid();
        }
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
                            data=Vacation.getVacation(forResult);
                           if(data.size()==0){
                               erromessage="暂无申请记录";
                               Message msg = new Message();
                               msg.what=0;
                               handle.sendMessage(msg);
                           }else {
                               Message msg = new Message();
                               msg.what = 1;
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
                            vac=VactionUtil.getVacationDetail(forResult);
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
    private void Vacinit() {
        for (int i=0;i<data.size();i++){
            Log.d("Test",data.get(i).getJudgeid());
        }
        vaclist=(SwipeMenuListView) findViewById(R.id.vacate_list);
        //设置一个设配器，最重要的一个方法
        adapter=new MyVacAdapter(ApprovalActivity.this,data);
        vaclist.setAdapter(adapter);
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1= ListUtil.SwipeMenuItemTitle(ApprovalActivity.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                menu.addMenuItem(item1);
                SwipeMenuItem pass = ListUtil.SwipeMenuItemTitle(ApprovalActivity.this, "通过",new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                menu.addMenuItem(pass);
                SwipeMenuItem refuse = ListUtil.SwipeMenuItemTitle(ApprovalActivity.this, "拒绝",new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                menu.addMenuItem(refuse);
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
                            Toast.makeText(ApprovalActivity.this, "此项已经置顶", Toast.LENGTH_SHORT).show();
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
                        approve(1,"1",data.get(posi).getJudgecontentid(),data.get(posi).getJudgeid());
                        break;
                    }
                    case 2:
                        approve(1,"0",data.get(posi).getJudgecontentid(),data.get(posi).getJudgeid());
                        break;
                }
                return false;
            }
        });


        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.prl_view);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //修改数据的代码，最后记得填上此行代码
                getVacData();
                vaclist.deferNotifyDataSetChanged();
                layout.setRefreshing(false);
            }
        });
        vaclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vacation vacation=(Vacation) adapter.getItem(position);
                setContentView(R.layout.vacate_detail);
                getVacDetailData(position);
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

    private void getPeoData() {
        title();
        title.setText("加入部门申请");

        String url="http://api.wangz.online/api/applydep/getapplydepBydepid?depId="+User.currentUser.getDepId();
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
                            if(forResult.equals("[]")){
                                erromessage="暂无申请加入部门记录";
                                Message msg = new Message();
                                msg.what=3;
                                handle.sendMessage(msg);
                                return;
                            }
                            pepdata= PepForDep.getPeoForDepList(forResult);
                            if(pepdata.size()==0){
                                erromessage="暂无申请记录";
                                Message msg = new Message();
                                msg.what=3;
                                handle.sendMessage(msg);
                            }else {
                                Message msg = new Message();
                                msg.what = 5;
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
    MyPeoForDepAdapter pepadapter;
    private void initPeo(){
        vaclist=(SwipeMenuListView) findViewById(R.id.vacate_list);
        //设置一个设配器，最重要的一个方法

        pepadapter=new MyPeoForDepAdapter(ApprovalActivity.this,pepdata);
        vaclist.setAdapter(pepadapter);
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1= ListUtil.SwipeMenuItemTitle(ApprovalActivity.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                menu.addMenuItem(item1);
                SwipeMenuItem pass = ListUtil.SwipeMenuItemTitle(ApprovalActivity.this, "通过",new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                menu.addMenuItem(pass);
                SwipeMenuItem refuse = ListUtil.SwipeMenuItemTitle(ApprovalActivity.this, "拒绝",new ColorDrawable(Color.rgb(0xFF, 0x00, 0x00)));
                menu.addMenuItem(refuse);
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
                            Toast.makeText(ApprovalActivity.this, "此项已经置顶", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        PepForDep pep = pepdata.get(position);
                        for (int i = position; i > 0; i--) {
                            PepForDep s = pepdata.get(i - 1);
                            pepdata.remove(i);
                            pepdata.add(i, s);
                        }
                        pepdata.remove(0);
                        pepdata.add(0, pep);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    case 1: {
                        //通过逻辑
                        approve(3,"2",pepdata.get(posi).getApplyid(),"");
                        break;
                    }
                    case 2:
                        //拒绝逻辑
                        approve(3,"1",pepdata.get(posi).getApplyid(),"");
                        break;
                }
                return false;
            }
        });


        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.prl_view);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //修改数据的代码，最后记得填上此行代码
                getPeoData();
                vaclist.deferNotifyDataSetChanged();
                layout.setRefreshing(false);
            }
        });
    }

    private void getActData(){
        title();
        title.setText("活动审批");

        String url="http://api.wangz.online/api/activity/getActJudgeList?page="+page+"&limit=5";
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
                            if(forResult.equals("[]")|forResult.equals("null")|forResult==null){
                                Log.d("Test","data_null");
                                erromessage="暂无更多活动申请";
                                Message msg = new Message();
                                msg.what=3;
                                handle.sendMessage(msg);
                                return;
                            }
                            actdata= Activity.getapplyActs(forResult);
                            if(actdata.size()==0){
                                Log.d("Test","data_null_1");
                                erromessage="暂无更多活动申请";
                                Message msg = new Message();
                                msg.what=0;
                                handle.sendMessage(msg);
                            }else {
                                resultdata.addAll(actdata);
                                Message msg = new Message();
                                msg.what = 4;
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
    SwipeMenuListView activitylist;//定义活动列表，根据框架自定义的ListView
    MyActivityAdapter actadapter;//适配器
    private void applyActinit(){
        title();
        title.setText("活动审批");
        activitylist=(SwipeMenuListView) findViewById(R.id.activity_list);
        //设置适配器，这很重要
        actadapter = new MyActivityAdapter(ApprovalActivity.this,resultdata);
        activitylist.setAdapter(actadapter);
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1= ListUtil.SwipeMenuItemTitle(ApprovalActivity.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                menu.addMenuItem(item1);
                SwipeMenuItem pass = ListUtil.SwipeMenuItemTitle(ApprovalActivity.this, "通过", new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                menu.addMenuItem(pass);
                SwipeMenuItem refuse = ListUtil.SwipeMenuItemTitle(ApprovalActivity.this, "拒绝", new ColorDrawable(Color.rgb(0xFF, 0x00, 0x00)));
                menu.addMenuItem(refuse);
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
                            Toast.makeText(ApprovalActivity.this, "此项已经置顶", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Activity activity = resultdata.get(position);
                        for(int i = position;i>0;i--){
                            Activity s = resultdata.get(i-1);
                            resultdata.remove(i);
                            resultdata.add(i,s);
                        }
                        resultdata.remove(0);
                        resultdata.add(0,activity);
                        actadapter.notifyDataSetChanged();
                        break;
                    }//case 0
                    case 1:
                        approve(1,"1",actdata.get(position).getJudgecontentid(),actdata.get(position).getJudgeid());
                        break;
                    case 2:{
                        approve(1,"0",actdata.get(position).getJudgecontentid(),actdata.get(position).getJudgeid());
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
                page+=1;
                getActData();
                actadapter.notifyDataSetChanged();
                layout.setRefreshing(false);
            }
        });

        activitylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity act=(Activity) resultdata.get(position);
                Log.d("Test",act.getJudgecontentid());
                getData(act.getJudgecontentid());
            }
        });
    }

    public void getData(final String actid){
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
                        if(forResult.equals("success")) {
                            //发送修改界面的消息
                            Activity.activitys = Activity.getAct(stringdata);
                            for (int i = 0; i < Activity.activitys.size(); i++) {
                                if (Activity.activitys.get(i).getActivityid().equals(actid)) {
                                    curact=Activity.activitys.get(i);
                                    Message msg = new Message();
                                    msg.what = 6;
                                    handle.sendMessage(msg);
                                    return;
                                }
                            }
                        }else{
                                Message msg = new Message();
                                msg.what=0;
                                erromessage=forResult;
                                handle.sendMessage(msg); //发送修改界面的消息
                            }

                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approval_manage);
        init();
        title.setText("审批管理");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.act_relative:{
                setContentView(R.layout.activity_list);
                page=1;
                resultdata=new ArrayList<>();
                getActData();
                break;
            }
            case R.id.peo_relative:{
                setContentView(R.layout.vacate_list);
                getPeoData();
                break;
            }
            case R.id.vac_relative:{
                setContentView(R.layout.vacate_list);
                getVacData();
                break;
            }
        }

    }

    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    Toast.makeText(ApprovalActivity.this, erromessage, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //取出线程传过来的值String ss = (String) msg.obj
                    Vacinit();
                    break;
                case 2:
                    VacDetailint();
                    break;
                case 3:
                    Toast.makeText(ApprovalActivity.this, erromessage, Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.approval_manage);
                    init();
                    title.setText("审批管理");
                    break;
                case 4:
                    applyActinit();
                    break;
                case 5:
                    initPeo();
                    break;
                case 6:
                    ActivityDetail(curact);
                default:
                    break;
            }
        }
    };



}

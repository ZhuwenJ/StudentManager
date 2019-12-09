package com.zwj.studentmanager;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import ListView.MyVacAdapter;
import model.Department;
import model.User;
import model.Vacation;
import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;
import util.ListUtil;
import util.VactionUtil;

public class VacateActivity_me extends AppCompatActivity implements View.OnClickListener{
    /** myvaclist:数据ListView的控件
     * back: 返回按钮
     * title:界面标题控件
     * data:部门ListView的数据源
     * adapter:自定义的ListView
     * UPDATE_TEXT:是否更新界面
     * erromessage:线程返回错误消息
     * */
    private String erromessage;
    View my_vac,apply_leave;
    SwipeMenuListView myvaclist;
    RadioButton back;
    TextView title;
    ArrayList<Vacation> data=new ArrayList<>();
    MyVacAdapter adapter;
    private Vacation curvac=new Vacation();

    /**
     * Description:这个是用来初始化标题栏，设置返回按钮的监听事件
     * @author hcj
     * @Time 2019-12-02 13:25:00
     */
    public void title(){
        back = findViewById(R.id.titleback);
        title=findViewById(R.id.all_title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().equals("请假管理")){
                    finish();
                }else if(title.getText().equals("我的请假")){
                    setContentView(R.layout.leave_manage);
                    initManag();
                    title.setText("请假管理");
                }else if(title.getText().equals("请假详情")){
                    setContentView(R.layout.vacate_list);
                    //getVacData_me();
                }
            }
        });
    }// 初始化标题

    public void initManag(){
        title();
        my_vac = findViewById(R.id.myleave);
        apply_leave = findViewById(R.id.applyleave);

        my_vac.setOnClickListener(this);
        apply_leave.setOnClickListener(this);
    }
    /**
     * Description:这个函数是用于获取自己的请假记录
     * @author  hcj
     * @Time  2019-12-02 13:35:00
     */
    private void getVacData_me() {
        setContentView(R.layout.vacate_list);
        title();
        title.setText("我的请假");
        String url = "";
        url += "http://api.wangz.online/api/judge/getapplyInfoById?applyId="+User.currentUser.getUid();

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
                            data=Vacation.getVacation_me(forResult);
                            if(data.size()==0){
                                erromessage="暂无申请记录";
                                Message msg = new Message();
                                msg.what=0;//handle里0代表错误信息
                                handle.sendMessage(msg);
                            }else {
                                Log.d("Test","success"+data.size());
                                Message msg = new Message();
                                msg.what = 3;//1代表成功
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


    /**
     * Description:这个是用于分级显示不同权限用户所能看到的按钮，比如全部、申请两个按钮
     * @author  hcj
     * @Time  2019-12-02 13:35:00
     */
    public void control(){
        View chairman = findViewById(R.id.click);//主席看到的按钮
        View student = findViewById(R.id.click2);//学生干事
        View teacher = findViewById(R.id.click3);//教师
        chairman.setVisibility(View.GONE);
        student.setVisibility(View.GONE);
        teacher.setVisibility(View.GONE);
        if(User.currentUser.getRank().equals("1")){
            student.setVisibility(View.VISIBLE);

        }else if(Integer.parseInt(User.currentUser.getRank())>=4){
            teacher.setVisibility(View.VISIBLE);
        }else{
            chairman.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Description:这个是用于初始化请假详细信息界面
     * @author  hcj
     * @Time  2019-12-02 13:35:00
     */
    private void Vacinit_me(){
        for (int i=0;i<data.size();i++){
            Log.d("Test",data.get(i).getJudgeid());
        }
        //添加
        myvaclist = (SwipeMenuListView) findViewById(R.id.vacate_list);

        //设置适配器
        adapter=new MyVacAdapter(VacateActivity_me.this,data);
        myvaclist.setAdapter(adapter);
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1= ListUtil.SwipeMenuItemTitle(VacateActivity_me.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                menu.addMenuItem(item1);
                SwipeMenuItem editeItem = ListUtil.SwipeMenuItemTitle(VacateActivity_me.this, "详情",new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                menu.addMenuItem(editeItem);
            }
        };

        myvaclist.setMenuCreator(creater);
        myvaclist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int posi = position;
                switch (index){
                    case 0:{
                        //置顶的逻辑
                        if (position == 0) {
                            Toast.makeText(VacateActivity_me.this, "此项已经置顶", Toast.LENGTH_SHORT).show();
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
                    case 1:{
                        Vacation vacation = data.get(position);
                        getVacDetailData_me(position);
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
                //修改数据，刷新后需要
                getVacData_me();
                myvaclist.deferNotifyDataSetChanged();
                layout.setRefreshing(false);
            }
        });
        myvaclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vacation vacation=(Vacation) adapter.getItem(position);
                setContentView(R.layout.vacate_detail);
                getVacDetailData_me(position);
            }
        });
    }

    TextView vacateID,departmentname,leavetime,backtime,reason;
    private void VacDetailinit_me() {
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

    /**
     * Description:添加请假详情信息
     * @author hcj
     * @Time 2019-12-02 13:25:00
     */

    private void getVacDetailData_me(final int position) {
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


    //点击函数
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.applyleave:{
                setContentView(R.layout.leave_apply);
                title();
                title.setText("请假申请");
                break;
            }
            case R.id.myleave:{
                setContentView(R.layout.vacate_list);
                getVacData_me();
                break;
            }
        }
    }//点击函数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave_manage);
        initManag();
        title.setText("请假管理");
    }


    /**
     * Description:添加请假信息
     * @author hcj
     * @Time 2019-12-02 13:25:00
     */
    //添加reason  leavetime backtime
    TextView applyleavetime,applybacktime;
    EditText applyreason;

    public  void AddVac(){
        setContentView(R.layout.leave_apply);
        title();
        View tit = findViewById(R.id.title);
        tit.bringToFront();
        title.setText("请假申请");
        Button add = findViewById(R.id.btn_leave_apply);
        add.setText("请假申请");

        applyreason = findViewById(R.id.departmentname);//这里没改名字，应该是请假原因
        applyleavetime = findViewById(R.id.leavetime);
        applybacktime = findViewById(R.id.backtime);

        add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Map<String,String> map = new HashMap<>();

                map.put("vacateperson",User.currentUser.getUid().toString());
                map.put("persondepartment",User.currentUser.getDepartments().toString());
                map.put("vacatereason",applyreason.getText().toString());
                map.put("leavetime",applyleavetime.getText().toString());
                map.put("backtime",applybacktime.getText().toString());

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

    /**
     * Description:异步处理子线程发过来的消息，并对消息处理
     * @author hcj
     * @Time 2019-12-02 13:25:00
     */

    private Handler handle = new Handler(){
        public void handMessage(Message msg){
            switch (msg.what){
                case 0:
                    Toast.makeText(VacateActivity_me.this,erromessage,Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //取出线程传过来的值String ss = (String) msg.obj
                    break;
                case 2:
                    VacDetailinit_me();
                    break;
                case 3:
                    Vacinit_me();
                    break;
                 default:
                     break;
            }
        }
    };
}

package com.zwj.studentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import ListView.InformAdapter;
import model.Activity;
import model.Department;
import model.Inform;
import model.User;
import okhttp3.Call;
import okhttp3.Response;
import util.ActUtil;
import util.HttpUtil;
import util.ListUtil;

import java.io.IOException;
import java.lang.Integer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView username, uid, bumen,zhiwei;
    TextView deparmentmanage,activitymanage,peoplemanage,leavemanage,assessmanage,approvalmanage;
    RadioButton manage,me,news,approval_news,aboutus,back;

    Banner banner;
    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {

            Glide.with(context).load(path).into(imageView);

        }
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

    public void init(){
        manage=findViewById(R.id.manage);
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.fragment_manage);
                initme();
                init();
            }
        });
        me=findViewById(R.id.me);
        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.fragment_me);
                fra_me();
                init();
            }
        });
        news=findViewById(R.id.news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.inform_list);
                getInformdata();
            }
        });
    }
    SwipeMenuListView informlist;
    TextView title;
    ArrayList<Inform> data = new ArrayList<Inform>();
    ArrayList<Activity> actdata = new ArrayList<Activity>();
    InformAdapter adapter;
    private static final int UPDATE_TEXT=1;
    private String erromessage;
    private  Activity curact = new Activity();

    /**
     * Description:这个是用来获取ListView的数据，线程获取到数据后显示在主函数UI上，更新主进程UI
     * @author hcj
     * @Time 2019-12-03 13:25:00
     */
    public void getInformdata(){
        init();
        String url="";
        url += "http://api.wangz.online/api/notice/getnoticeByuid?uid="+ User.currentUser.getUid();
        Log.d("Test",url);
        HttpUtil.getRequest(url,new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resonseData = response.body().string();
                Log.d("Test",resonseData);
                JSONObject obj = null;//建立JSON对象
                String forResult;
                forResult = "not Found";//如果没有输出字符，则显示not Found
                String judge;
                if(!resonseData.isEmpty()){
                    try //异常处理
                    {
                        obj = new JSONObject(resonseData); //字符串转化为json对象
                        forResult = obj.optString("dataObj");//提取数据并转化为字符串
                        Log.d("Test",forResult);

                        data = Inform.getInforms(forResult);
                        Inform.informm = data;
                        Message msg = new Message();
                        msg.what = 1;
                        handle.sendMessage(msg);
                        //添加handle记得
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        data = Inform.informm;
    }

    /**
     * Description:这个是用来初始化ListView的UI,设置左滑按钮,和对应点击事件
     * @author hcj
     * @Time 2019-12-02 13:25:00
     * @return String:返回的部门ID
     */
    public  void initInform(){
        informlist = (SwipeMenuListView) findViewById(R.id.inform_thelist);
        //设置适配器，适配器非常重要，不然会显示不出信息
        adapter = new InformAdapter(MainActivity.this,data);
        informlist.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1= ListUtil.SwipeMenuItemTitle(MainActivity.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                menu.addMenuItem(item1);
            }

        };

        informlist.setMenuCreator(creator);
        informlist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener(){

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int posi = position;
                switch (index){
                    //0代表置顶
                    case 0 :{

                        if(position ==0){
                            Toast.makeText(MainActivity.this,"该项已经置顶",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Inform inf = data.get(position);
                        for(int i = position; i > 0; i--){
                            Inform is = data.get(i-1);
                            data.remove(i);
                            data.add(i,is);
                        }
                        data.remove(position);
                        data.add(0,inf);
                        adapter.notifyDataSetChanged();
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

                //刷新修改
                getInformdata();
                informlist.deferNotifyDataSetChanged();
                layout.setRefreshing(false);
            }
        });
        informlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Inform inf = (Inform) adapter.getItem(position);
                if(data.get(position).getDepuid().equals("活动通知")) {
                    setContentView(R.layout.activity_detail);
                    getActivityDetailData(position);
                }else;
            }
        });
    }

    /**
     * Description:这个函数负责获取点击item后显示的活动数据
     * @author hcj
     * @Time 2019-12-02 13:25:00
     */
     private void getActivityDetailData(final  int position){
         title();
         title.setText("活动详情");

         String url="http://api.wangz.online/api/activity/getActList?activityid="+data.get(position).getInformerid();
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
                             try //异常处理
                             {
                                 obj = new JSONObject(resonseData);    //将字符串转为json对象
                                 forResult = obj.optString("dataObj");//把result对象提取出来，并转化为字符串
                                 panduan =obj.optString("msg");


                                 if(panduan.equals("success")){
                                     Log.d("Test",forResult);
                                     curact = ActUtil.getActDetail(forResult);
                                     Log.d("Test",forResult);
                                    //actdata 是ArrayList<Activity>定义的
                                    //curact是新的Activity对象，用于接收actdata
                                    if(data.size()==0){
                                        erromessage = "获取失败";
                                        Message msg = new Message();
                                        msg.what = 0 ;
                                        handle.sendMessage(msg);
                                    }else{
                                        Message msg = new Message();
                                        msg.what = 2;
                                        handle.sendMessage(msg);
                                    }
                                 }
                                 else{
                                     erromessage = panduan;
                                     Message msg = new Message();
                                     msg.what = 0;
                                     handle.sendMessage(msg);
                                 }
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                         }
                     }
         });

     }
    /**
     * Description:这个函数初始化活动详情
     * @author hcj
     * @Time 2019-12-04 13:25:00
     */
    TextView activityname,maindep,assistdep,peopleamount,start_time,over_time,location;
     private void ActDetailInit(){
        activityname = findViewById(R.id.act_name);
        maindep = findViewById(R.id.hold_dep);
        assistdep = findViewById(R.id.assist_dep);
        peopleamount = findViewById(R.id.peo_count);
        start_time = findViewById(R.id.start_time);
        over_time = findViewById(R.id.end_time);
        location = findViewById(R.id.location);

        activityname.setText("活动名称:"+curact.getActivityname());
        maindep.setText("申请部门:"+curact.getApplydep());
        assistdep.setText("协同部门:"+curact.getDepartmentassist());
        peopleamount.setText("人员数量:"+curact.getPeopleamount()+"");
        start_time.setText("开始时间:"+curact.getStarttime());
        over_time.setText("结束时间:"+curact.getOvertime());
        location.setText("活动地点:"+curact.getActlocation());
     }


    /**
     * Description:这个函数修改标题名
     * @author hcj
     * @Time 2019-12-04 13:25:00
     */
    public void title(){
         back = findViewById(R.id.titleback);
         title = findViewById(R.id.all_title);
         back.setOnClickListener(new View.OnClickListener(){

             @Override
             public void onClick(View v) {
                  if(title.getText().equals("活动详情")){
                      setContentView(R.layout.inform_list);
                      getInformdata();
                  } else if(title.getText().equals("关于我们")){
                     setContentView(R.layout.fragment_me);
                     fra_me();
                 }
             }
         });
    }
    public void initme(){
        View bottom=findViewById(R.id.bottom);
        bottom.bringToFront();
        View title=findViewById(R.id.title);
        title.bringToFront();
        deparmentmanage=findViewById(R.id.deparmentmanage);
        activitymanage=findViewById(R.id.activitymanage);
        peoplemanage=findViewById(R.id.peoplemanage);
        leavemanage=findViewById(R.id.leavemanage);
        assessmanage=findViewById(R.id.assessmanage);
        approvalmanage=findViewById(R.id.approvalmanage);

        banner = findViewById(R.id.banner);
        ArrayList<Integer> imgs = new ArrayList<>();
        imgs.add(R.mipmap.communicate);
        imgs.add(R.mipmap.lecture);
        imgs.add(R.mipmap.run);
        imgs.add(R.mipmap.zucc);
        imgs.add(R.mipmap.act);
        banner.setImages(imgs).setImageLoader(new GlideImageLoader()).start();

        activitymanage.setVisibility(View.GONE);
        peoplemanage.setVisibility(View.GONE);
        leavemanage.setVisibility(View.GONE);
        assessmanage.setVisibility(View.GONE);
        approvalmanage.setVisibility(View.GONE);

        if(Integer.parseInt(User.currentUser.getRank())>=2){
            leavemanage.setVisibility(View.VISIBLE);
            peoplemanage.setVisibility(View.VISIBLE);
            if(Integer.parseInt(User.currentUser.getRank())>=3){
                activitymanage.setVisibility(View.VISIBLE);
                approvalmanage.setVisibility(View.VISIBLE);
                if(Integer.parseInt(User.currentUser.getRank())>=6){
                    assessmanage.setVisibility(View.VISIBLE);
                }
            }
        }
        deparmentmanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DepartmentActivity.class);
                startActivity(intent);
            }
        });

        activitymanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ActivityActivity.class);
                startActivity(intent);
            }
        });
        peoplemanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,PeopleActivity.class);
                startActivity(intent);
            }
        });
        leavemanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LeaveActivity.class);
                startActivity(intent);

            }
        });
        assessmanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "请到web端进行考核", Toast.LENGTH_SHORT).show();
//                Intent intent=new Intent(MainActivity.this,AssessmentActivity.class);
//                startActivity(intent);

            }
        });
        approvalmanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ApprovalActivity.class);
                startActivity(intent);

            }
        });


    }
    public void fra_me(){

        RadioButton button1=findViewById(R.id.button1);
        username=findViewById(R.id.username);
        uid=findViewById(R.id.uid);
        bumen=findViewById(R.id.bumen);
        zhiwei=findViewById(R.id.zhiwei);
        approval_news=findViewById(R.id.approval_news);
        View aboutus=findViewById(R.id.about_us);
        username.setText(User.currentUser.getUsername());
        uid.setText("学号: "+User.currentUser.getUid());

        button1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Peodetail(User.currentUser);
            }
        });

        if(User.currentUser.getRank().equals("1")) {
            bumen.setText("游客");
            zhiwei.setText("学生");
        }else if (User.currentUser.getRank().equals("2")){
            bumen.setText(User.currentUser.getDepartments());
            zhiwei.setText("干事");
        }else if(User.currentUser.getRank().equals("3")){
            bumen.setText(User.currentUser.getDepartments());
            zhiwei.setText("部长");
        }else if(User.currentUser.getRank().equals("4")){
            bumen.setText(User.currentUser.getDepartments());
            zhiwei.setText("副主席");
        }else if(User.currentUser.getRank().equals("5")){
            bumen.setText(User.currentUser.getDepartments());
            zhiwei.setText("主席");
        }else if(User.currentUser.getRank().equals("6")){
            bumen.setText(User.currentUser.getDepartments());
            zhiwei.setText("指导老师");
        }
        approval_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LeaveActivity.class);
                startActivity(intent);
            }
        });
        aboutus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setContentView(R.layout.aboutus);
                title();
                title.setText("关于我们");
            }
        });

        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inform_list);
        getInformdata();
    }

    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this, erromessage, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    initInform();
                    break;
                case 2:
                    ActDetailInit();
                    break;
                default:
                    break;
            }
        }
    };

}

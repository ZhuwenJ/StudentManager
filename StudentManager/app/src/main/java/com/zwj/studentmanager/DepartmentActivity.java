package com.zwj.studentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import ListView.MyDepAdapter;

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
import java.util.HashMap;
import java.util.Map;

import model.Department;
import model.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.ListUtil;
import util.ModelUtil;
import util.UIUtil;


public class DepartmentActivity extends AppCompatActivity implements View.OnClickListener {
    /** deplist:数据ListView的控件
     * back: 返回按钮
     * searchView:搜索界面
     * title:界面标题控件
     * data:部门ListView的数据源
     * adapter:自定义的ListView
     * UPDATE_TEXT:是否更新界面
     * erromessage:线程返回错误消息
     * */
    SwipeMenuListView deplist;
    RadioButton back;
    SearchView searchView;
    TextView title;
    ArrayList<Department> data=new ArrayList<>();
    MyDepAdapter adapter;
    private static final int UPDATE_TEXT=1;
    private String erromessage;

    /**
     * Description:这个是用来获取ListView的数据，线程获取到数据后展示在主函数UI上，更新主进程UI
     * @author zwj
     * @Time 2019-11-02 13:25:00
     */
    public void getdata(){
        control();
        title();
        title.setText("部门管理");
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
                        data=Department.getDeps(forResult);
                        Department.deps=data;
                        Message msg = new Message();
                        msg.what=3;
                        handle.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        data=Department.deps;
    }

    /**
     * Description:这个是用来初始化标题栏，设置返回按钮监听事件
     * @author zwj
     * @Time 2019-11-02 13:25:00
     */
    public void title(){
        back=findViewById(R.id.titleback);
        title=findViewById(R.id.all_title);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(title.getText().equals("部门管理")){
                    finish();
                }else {
                    setContentView(R.layout.department_chairman);
                    getdata();
                    title.setText("部门管理");
                }
            }
        });
    }

    /**
     * Description:这个是用来分级显示不同权限用户应该看到的标题栏下的两个按钮。
     * @author zwj
     * @Time 2019-11-02 13:25:00
     */
    public void control(){
        View chairman=findViewById(R.id.click);
        View student=findViewById(R.id.click2);
        View teacher=findViewById(R.id.click3);
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
     * Description:这个是用来初始化ListView的UI,设置左滑按钮,和对应点击事件
     * @author zwj
     * @Time 2019-11-02 13:25:00
     * @return String:返回的部门ID
     */
    public void init(){
        if(Integer.parseInt(User.currentUser.getRank())>=4) {
            Button adddep = findViewById(R.id.adddep);
            adddep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddDep();
                }
            });
        }
        deplist=(SwipeMenuListView) findViewById(R.id.department_list);
        //设置一个设配器，最重要的一个方法
        adapter=new MyDepAdapter(DepartmentActivity.this,data);
        deplist.setAdapter(adapter);
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1=ListUtil.SwipeMenuItemTitle(DepartmentActivity.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                menu.addMenuItem(item1);
                if(Integer.parseInt(User.currentUser.getRank())==3){
                    SwipeMenuItem editeItem = ListUtil.SwipeMenuItemTitle(DepartmentActivity.this, "编辑",new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                    menu.addMenuItem(editeItem);
                }
                if(Integer.parseInt(User.currentUser.getRank())>=4) {
                    SwipeMenuItem deleteItem = ListUtil.SwipeMenuItemIcon(DepartmentActivity.this, R.mipmap.delete,new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                    menu.addMenuItem(deleteItem);
                }
                else if(Integer.parseInt(User.currentUser.getRank())==1){
                    SwipeMenuItem applyItem = ListUtil.SwipeMenuItemTitle(DepartmentActivity.this,"申请",new ColorDrawable(Color.rgb(0x00, 0x33, 0x66)));
                    menu.addMenuItem(applyItem);
                }

            }
        };
        deplist.setMenuCreator(creater);
        deplist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int posi=position;
                switch (index) {
                    case 0: {
                        //置顶的逻辑
                        if (position == 0) {
                            Toast.makeText(DepartmentActivity.this, "此项已经置顶", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Department dep = data.get(position);
                        for (int i = position; i > 0; i--) {
                            Department s = data.get(i - 1);
                            data.remove(i);
                            data.add(i, s);
                        }
                        data.remove(0);
                        data.add(0, dep);
                        adapter.notifyDataSetChanged();
                        break;
                        }
                    case 1: {
                        if(Integer.parseInt(User.currentUser.getRank())==3) {
                            Department dep=data.get(position);
                            if(User.currentUser.getDepartments().equals(dep.getDepartmentname())) {
                                setContentView(R.layout.department_edit);
                                title();
                                title.setText(dep.getDepartmentname());
                                View tit = findViewById(R.id.title);
                                tit.bringToFront();
                                depedit(dep);
                            }else
                                Toast.makeText(DepartmentActivity.this,"非自己部门无法编辑",Toast.LENGTH_SHORT).show();

                        }else if(Integer.parseInt(User.currentUser.getRank())==1) {
                            //申请操作
                        }else if(Integer.parseInt(User.currentUser.getRank())>=4){
                            //删除的逻辑
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DepartmentActivity.this);
                            dialog.setTitle("删除部门确认");  //对话框标题
                            dialog.setMessage("确认删除么?");//内容
                            dialog.setCancelable(true);//可撤销性

                            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(DepartmentActivity.this,"取消",Toast.LENGTH_SHORT).show();  //提示内容
                                }//取消按钮的点击事件
                            });
                            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DepDel(data.get(posi));
                                    data.remove(posi);
                                    adapter.notifyDataSetChanged();//提示内容

                                }   //确定按钮的点击事件
                            });
                            dialog.show();  //全部显示出来
                        }
                        break;
                    }case 2:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(DepartmentActivity.this);
                        dialog.setTitle("删除部门确认");  //对话框标题
                        dialog.setMessage("确认删除么?");//内容
                        dialog.setCancelable(true);//可撤销性

                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(DepartmentActivity.this,"取消",Toast.LENGTH_SHORT).show();  //提示内容
                            }//取消按钮的点击事件
                        });
                        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DepDel(data.get(posi));
                                data.remove(posi);
                                adapter.notifyDataSetChanged();//提示内容
                                Toast.makeText(DepartmentActivity.this,"删除成功",Toast.LENGTH_SHORT).show();

                            }   //确定按钮的点击事件
                        });
                        dialog.show();  //全部显示出来

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
                getdata();
                deplist.deferNotifyDataSetChanged();
                layout.setRefreshing(false);
            }
        });
        deplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Department dep=(Department) adapter.getItem(position);
                depinit(dep);
            }
        });

    }

    /**
     * Description:这个是用来设置部门具体信息的界面
     * @author zwj
     * @Time 2019-11-02 13:25:00
     * @param dep:具体部门的信息
     */
    TextView depname,ministerid,chairmanid,introduction,peocount;
    public void depinit(Department dep){
        setContentView(R.layout.department_detail);
        title();
        depname=findViewById(R.id.depname);
        ministerid=findViewById(R.id.ministerid);
        chairmanid=findViewById(R.id.chairmanid);
        introduction=findViewById(R.id.introduction);
        peocount=findViewById(R.id.peocount);

        depname.setText("部门名称: "+dep.getDepartmentname());
        ministerid.setText("部长姓名："+dep.ministername);
        chairmanid.setText("分管主席:"+dep.chaimanname);
        introduction.setText("部门介绍：\n"+dep.getIntroduction());
        peocount.setText("人数："+dep.getPeoplenumber()+"");
        title.setText(dep.getDepartmentname());

    }

    /**
     * Description:这个是用来初始化修改部门信息的界面
     * @author zwj
     * @Time 2019-11-02 13:25:00
     * @param dep:具体部门信息
     */
    EditText dep_name,dep_introduction;
    public void depedit(final Department dep){
        dep_name=findViewById(R.id.depname);
        dep_name.setHint(UIUtil.getHint(dep.getDepartmentname()));
        findViewById(R.id.ministerid).setFocusableInTouchMode(false);
        ((EditText)findViewById(R.id.ministerid)).setHint(UIUtil.getHint(dep.ministername));
        findViewById(R.id.chairmanid).setFocusableInTouchMode(false);
        ((EditText)findViewById(R.id.chairmanid)).setHint(UIUtil.getHint(dep.chaimanname));
        findViewById(R.id.peocount).setFocusableInTouchMode(false);
        ((EditText)findViewById(R.id.peocount)).setHint(UIUtil.getHint(dep.getPeoplenumber()+""));
        dep_introduction=findViewById(R.id.introduction);
        dep_introduction.setHint(UIUtil.getHint(dep.getIntroduction()));
        Button change=findViewById(R.id.dep_edit);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(dep_introduction.getText().toString().equals("")))
                    dep.setIntroduction(dep_introduction.getText().toString());
                if(!(dep_name.getText().toString().equals("")))
                    dep.setDepartmentname(dep_name.getText().toString());
                Map<String,String> map=new HashMap<>();
                map= ModelUtil.objectToMap(dep);
                map.remove("deps");
                map.remove("flage");

                HttpUtil.postRequest("http://api.wangz.online/api/depart/departinfoedit",map,new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message msg = new Message();
                        msg.what=0;
                        erromessage=e.toString();
                        handle.sendMessage(msg); //发送修改界面的消息
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
                        Log.d("Test",forResult);
                        if(forResult.equals("success")){
                            Message msg = new Message();
                            msg.what=UPDATE_TEXT;
                            handle.sendMessage(msg);//发送修改界面的消息
                        }else {
                            Message msg = new Message();
                            msg.what=0;
                            erromessage=forResult;
                            handle.sendMessage(msg); //发送修改界面的消息
                        }

                    }
                });
            }
        });



    }


    /**
     * Description:这个是用来添加部门
     * @author zwj
     * @Time 2019-11-02 13:25:00
     */
    public void AddDep(){
        setContentView(R.layout.department_edit);
        title();
        View tit=findViewById(R.id.title);
        tit.bringToFront();
        title.setText("添加部门");
        Button add=findViewById(R.id.dep_edit);
        add.setText("添加部门");
        depname=findViewById(R.id.depname);
        ministerid=findViewById(R.id.ministerid);
        chairmanid=findViewById(R.id.chairmanid);
        introduction =findViewById(R.id.introduction);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,String> map=new HashMap<>();

                map.put("departmentname",depname.getText().toString());
                map.put("ministerid",ministerid.getText().toString());
                map.put("chairmanid",chairmanid.getText().toString());
                map.put("introduction",introduction.getText().toString());

                HttpUtil.postRequest("http://api.wangz.online/api/depart/addDepart", map, new okhttp3.Callback(){

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
                            msg.what = 4;
                            handle.sendMessage(msg); //发送修改界面的消息
                        }
                        else{
                            erromessage=forResult;
                            Message msg = new Message();
                            msg.what = 0;
                            handle.sendMessage(msg); //发送修改界面的消息
                        }
                    }
                });
            }
        });

    }

    /**
     * Description:这个是用来删除部门
     * @author zwj
     * @Time 2019-11-02 13:25:00
     */
    public void DepDel(Department dep){
        String url="http://api.wangz.online/api/depart/deleteDepart?depId="+dep.getDepartmentid();
        HttpUtil.getRequest(url, new Callback() {
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
                if(forResult.equals("注销成功")) {
                    Message msg = new Message();
                    msg.what = 5;
                    handle.sendMessage(msg); //发送修改界面的消息
                }
                else{
                    erromessage=forResult;
                    Message msg = new Message();
                    msg.what = 0;
                    handle.sendMessage(msg); //发送修改界面的消息
                }
            }
        });


    }

    /**
     * Description:活动初始化
     * @author zwj
     * @Time 2019-11-02 13:25:00
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.department_chairman);
        getdata();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.titleback:{
                finish();
                break;
            }
        }
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
                    Toast.makeText(DepartmentActivity.this, erromessage, Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_TEXT:
                    //取出线程传过来的值String ss = (String) msg.obj
                    Toast.makeText(DepartmentActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.department_chairman);
                    getdata();
                    title.setText("部门管理");
                    break;
                case 3:
                    init();
                    break;
                case 4:
                    setContentView(R.layout.department_chairman);
                    getdata();
                    title.setText("部门管理");
                    break;
                case 5:
                    Toast.makeText(DepartmentActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.department_chairman);
                    getdata();
                    title.setText("部门管理");
                    break;
                default:
                    break;
            }
        }
    };

}

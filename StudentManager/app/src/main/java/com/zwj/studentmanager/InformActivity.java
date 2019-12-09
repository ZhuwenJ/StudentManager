package com.zwj.studentmanager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.io.IOException;
import java.util.ArrayList;

import ListView.InformAdapter;
import model.Department;
import model.Inform;
import model.User;
import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;
import util.ListUtil;

public class InformActivity extends AppCompatActivity implements View.OnClickListener{

    /**informlist:数据ListView的控件
     * title:界面标题控件
     * data:通知ListView的数据源
     * adapter:自定义的ListView
     * erromessage:线程返回错误信息
     * UPDATE_TEXT:是否更新界面
     */

    SwipeMenuListView informlist;
    TextView title;
    ArrayList<Inform> data = new ArrayList<Inform>();
    InformAdapter adapter;
    private static final int UPDATE_TEXT=1;
    private String erromessage;

    /**
     * Description:这个是用来获取ListView的数据，线程获取到数据后显示在主函数UI上，更新主进程UI
     * @author hcj
     * @Time 2019-12-03 13:25:00
     */
    public void getInformdata(){
        title();
        String url="";
        url += "http://api.wangz.online/api/notice/getnoticeByuid?uid="+ User.currentUser.getUid();
        HttpUtil.getRequest("url",new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resonseData = response.body().string();
                Log.d("InformList",resonseData);
                JSONObject obj = null;//建立JSON对象
                String forResult;
                forResult = "not Found";//如果没有输出字符，则显示not Found
                String judge;
                if(!resonseData.isEmpty()){
                    try //异常处理
                    {
                        obj = new JSONObject(resonseData); //字符串转化为json对象
                        forResult = obj.optString("dataObj");//提取数据并转化为字符串
                        Log.d("InformList",forResult);

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
        adapter = new InformAdapter(InformActivity.this,data);
        informlist.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1= ListUtil.SwipeMenuItemTitle(InformActivity.this,"置顶",new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
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
                            Toast.makeText(InformActivity.this,"该项已经置顶",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Inform inf = data.get(position);
                        for(int i = position; i > 0; i--){
                            Inform is = data.get(i-1);
                            data.remove(i);
                            data.add(i,is);
                        }
                        data.remove(9);
                        data.add(0,inf);
                        adapter.notifyDataSetChanged();;
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
//        informlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Inform inf = (Inform) adapter.getItem(position);
//
//            }
//        });
    }


    private void title() {
        title = findViewById(R.id.title);
        title.setText("消息界面");
    }

    /**
     * Description:活动初始化
     * @author hcj
     * @Time 2019-12-03 13:25:00
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inform_list);
        getInformdata();
    }

    @Override
    public void onClick(View v) {

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
                    Toast.makeText(InformActivity.this, erromessage, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    initInform();
                    break;
                default:
                    break;
            }
        }
    };
}

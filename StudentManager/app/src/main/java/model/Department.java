package model;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zwj.studentmanager.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;
import util.HttpUtil;

public class Department {

    private String departmentid;
    private String departmentname;
    private int ministerid;
    private int chairmanid;
    private int peoplenumber;
    private String introduction;
    public String chaimanname;
    public String ministername;

    public static ArrayList<Department> deps;

    public void setDepartmentid(String departmentid){
        this.departmentid=departmentid;
    }
    public  String getDepartmentid(){
        return departmentid;
    }

    public void setDepartmentname(String departmentname){
        this.departmentname=departmentname;
    }
    public  String getDepartmentname(){
        return departmentname;
    }

    public void setMinisterid(int ministerid){
        this.ministerid=ministerid;
    }
    public  int getMinisterid(){
        return ministerid;
    }

    public void setChairmanid(int chairmanid){
        this.chairmanid=chairmanid;
    }
    public  int getChairmanid(){
        return chairmanid;
    }

    public void setPeoplenumber(int peoplenumber){
        this.peoplenumber=peoplenumber;
    }
    public  int getPeoplenumber(){
        return peoplenumber;
    }

    public void setIntroduction(String introduction){
        this.introduction=introduction;
    }
    public  String getIntroduction(){
        return introduction;
    }



    public static ArrayList<Department> getDeps(String jsonData){
        ArrayList<Department> deps=new ArrayList<Department>();
        JSONArray obj;  //建立json对象
        if (!jsonData.isEmpty()) {
            try {
                obj = new JSONArray(jsonData);
                for (int i = 0; i < obj.length(); i++) {
                    Department dep = new Department();
                    JSONObject jsonObject = obj.getJSONObject(i);
                    if(jsonObject.getString("departmentname").equals("主席团"))
                        continue;
                    dep.chaimanname=jsonObject.optString("chairmanname");
                    dep.ministername=jsonObject.optString("ministername");
                    dep.setDepartmentid(jsonObject.getString("departmentid"));
                    dep.setChairmanid(jsonObject.getInt("chairmanid"));
                    dep.setIntroduction(jsonObject.getString("introduction"));
                    dep.setDepartmentname(jsonObject.getString("departmentname"));
                    dep.setMinisterid(jsonObject.getInt("ministerid"));
                    dep.setPeoplenumber(jsonObject.getInt("peoplenumber"));

                    Log.d("DepList",dep.getDepartmentname());
                    deps.add(dep);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return deps;
    }










}
package model;

//还未编写完
import java.sql.Timestamp;
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

public class Activity {

    private String activityid;
    private String applydep;
    private String activityname;
    private String activitycontent;
    private String actlocation;
    private int peopleamount;
    private String departmentassist;
    private String starttime;
    private String overtime;
    private  String judgeid;
    private  int iscancel;
    private String judgecontentid;

    public  static ArrayList<Activity> activitys;

    public String getActivityid() {
        return activityid;
    }

    public void setActivityid(String activityid) {
        this.activityid = activityid;
    }

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }

    public String getActivitycontent() {
        return activitycontent;
    }

    public void setActivitycontent(String activitycontent) {
        this.activitycontent = activitycontent;
    }

    public int getPeopleamount() {
        return peopleamount;
    }

    public void setPeopleamount(int peopleamount) {
        this.peopleamount = peopleamount;
    }

    public String getDepartmentassist() {
        return departmentassist;
    }

    public void setDepartmentassist(String departmentassist) {
        this.departmentassist = departmentassist;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getOvertime() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime = overtime;
    }

    public String getJudgeid() {
        return judgeid;
    }

    public void setJudgeid(String judgeid) {
        this.judgeid = judgeid;
    }

    public int getIscancel() {
        return iscancel;
    }
    public void setIscancel(int iscancel) {
        this.iscancel = iscancel;
    }

    public void setApplydep(String applydep){
        this.applydep=applydep;
    }
    public String getApplydep(){
        return applydep;
    }

    public void setActlocation(String actlocation){
        this.actlocation=actlocation;
    }
    public String getActlocation(){
        return actlocation;
    }

    public void setJudgecontentid(String judgecontentid) {
        this.judgecontentid = judgecontentid;
    }
    public String getJudgecontentid() {
        return judgecontentid;
    }

    public static  ArrayList<Activity> getAct(String jsonData){
        ArrayList<Activity> activity = new ArrayList<Activity>();
        JSONArray obj; // 建立json对象
        if (!jsonData.isEmpty()){
            try{
                obj = new JSONArray(jsonData);
                for (int i = 0 ; i < obj.length();i++){
                    Activity act = new Activity();
                    JSONObject jsonObject = obj.getJSONObject(i);
                    act.setActivityid(jsonObject.getString("activityid"));
                    act.setApplydep(jsonObject.getString("applydep"));
                    act.setActlocation(jsonObject.getString("actlocation"));
                    act.setActivityname(jsonObject.getString("activityname"));
                    act.setActivitycontent(jsonObject.getString("actcontent"));
                    act.setPeopleamount(jsonObject.getInt("peopleamount"));
                    act.setDepartmentassist(jsonObject.getString("departmentassist"));
                    act.setJudgeid(jsonObject.getString("judgeid"));
                    act.setIscancel(jsonObject.getInt("iscancel"));
                    act.setStarttime(jsonObject.getString("starttime"));
                    act.setOvertime(jsonObject.getString("overtime"));

                    Log.d("ActList",act.getActivitycontent());
                    if(User.currentUser.getRank().equals("3")){
                        if(act.getApplydep().equals(User.currentUser.getDepartments()))
                            activity.add(act);
                        else
                            continue;
                    }else
                        activity.add(act);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return activity;
    }

    public static ArrayList<Activity> getapplyActs (String jsonData){
        ArrayList<Activity> activities = new ArrayList<Activity>();
        JSONArray obj;
        Log.d("Test",jsonData);
        if(!jsonData.isEmpty()){
            try{
                JSONObject temp=new JSONObject(jsonData);
                String data=temp.getString("list");
                obj = new JSONArray(data);
                for(int i = 0 ; i<obj.length();i++){
                    Activity activity = new Activity();
                    JSONObject jsonObject = obj.getJSONObject(i);
                    if(jsonObject.getString("isjudge").equals("0")|jsonObject.getString("isjudge").equals("1"))
                        continue;
                    activity.setJudgecontentid(jsonObject.getString("judgecontentid"));
                    activity.setJudgeid(jsonObject.getString("judgeid"));
                    Log.d("Test",activity.getJudgecontentid());
                    activities.add(activity);

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return activities;
    }
}

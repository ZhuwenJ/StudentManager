package model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Vacation {
    private String vacateid;
    private String vacateperson;
    private  String  persondepartment;
    private  String vacatereason;
    private String leavetime;
    private String backtime;
    private String judgeid;
    private String judgecontentid;
    private String isjudge;


    public String getVacateid() {
        return vacateid;
    }

    public void setVacateid(String vacateid) {
        this.vacateid = vacateid;
    }

    public String getVacateperson() {
        return vacateperson;
    }

    public void setVacateperson(String vacateperson) {
        this.vacateperson = vacateperson;
    }

    public String getPersondepartment() {
        return persondepartment;
    }

    public void setPersondepartment(String persondepartment) {
        this.persondepartment = persondepartment;
    }

    public String getVacatereason() {
        return vacatereason;
    }

    public void setVacatereason(String vacatereason) {
        this.vacatereason = vacatereason;
    }

    public String getLeavetime() {
        return leavetime;
    }

    public void setLeavetime(String leavetime) {
        this.leavetime = leavetime;
    }

    public String getBacktime() {
        return backtime;
    }

    public void setBacktime(String backtime) {
        this.backtime = backtime;
    }

    public String getJudgeid() {
        return judgeid;
    }

    public String getJudgecontentid() {
        return judgecontentid;
    }

    public void setJudgecontentid(String judgecontentid) {
        this.judgecontentid = judgecontentid;
    }

    public void setJudgeid(String judgeid) {
        this.judgeid = judgeid;
    }

    public String getIsjudge() {
        return isjudge;
    }

    public void setIsjudge(String isjudge) {
        this.isjudge = isjudge;
    }

    public static ArrayList<Vacation> getVacation (String jsonData){
        ArrayList<Vacation> vacate = new ArrayList<Vacation>();
        JSONArray obj;
        Log.d("Test",jsonData);
        if(!jsonData.isEmpty()){
            try{
                obj = new JSONArray(jsonData);
                for(int i = 0 ; i<obj.length();i++){
                    Vacation vacat = new Vacation();
                    JSONObject jsonObject = obj.getJSONObject(i);
                    Log.d("Test",jsonObject.getString("judgeid"));
                    if(jsonObject.getString("isjudge").equals("0")|jsonObject.getString("isjudge").equals("1"))
                        continue;
                    vacat.setJudgecontentid(jsonObject.getString("judgecontentid"));
                    vacat.setJudgeid(jsonObject.getString("judgeid"));
                    vacate.add(vacat);

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vacate;
    }
    public static ArrayList<Vacation> getVacation_me (String jsonData){
        ArrayList<Vacation> vacate = new ArrayList<Vacation>();
        JSONArray obj;
        Log.d("Test",jsonData);
        if(!jsonData.isEmpty()){
            try{
                JSONObject obj1 = new JSONObject(jsonData);    //将字符串转为json对象
                String data = obj1.optString("qing");
                obj = new JSONArray(data);
                for(int i = 0 ; i<obj.length();i++){
                    Vacation vacat = new Vacation();
                    JSONObject jsonObject = obj.getJSONObject(i);
                    Log.d("Test",jsonObject.getString("judgeid"));
                    vacat.setIsjudge(jsonObject.getString("isjudge"));
                    vacat.setJudgecontentid(jsonObject.getString("judgecontentid"));
                    vacat.setJudgeid(jsonObject.getString("judgeid"));
                    vacate.add(vacat);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vacate;
    }
}

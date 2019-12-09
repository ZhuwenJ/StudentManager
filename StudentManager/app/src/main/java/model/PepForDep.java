package model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PepForDep {

    private String applyreson;
    private String applyid;
    private String applyuser;
    private String flage;

    public String getApplyid() {
        return applyid;
    }
    public void setApplyid(String applyid) {
        this.applyid = applyid;
    }

    public String getApplyreson() {
        return applyreson;
    }
    public void setApplyreson(String applyreson) {
        this.applyreson = applyreson;
    }

    public String getApplyuser() {
        return applyuser;
    }
    public void setApplyuser(String applyuser) {
        this.applyuser = applyuser;
    }

    public void setFlage(String flage) {
        this.flage = flage;
    }
    public String getFlage() {
        return flage;
    }

    public static ArrayList<PepForDep> getPeoForDepList(String jsonData){
        ArrayList<PepForDep> pepForDeps=new ArrayList<PepForDep>();
        JSONArray obj;  //建立json对象
        if (!jsonData.isEmpty()) {
            try {
                obj = new JSONArray(jsonData);
                for (int i = 0; i < obj.length(); i++) {
                    PepForDep pepForDep = new PepForDep();
                    JSONObject jsonObject = obj.getJSONObject(i);
                    pepForDep.setApplyid(jsonObject.getString("applydepid"));
                    pepForDep.setApplyreson(jsonObject.getString("reason"));
                    pepForDep.setApplyuser(jsonObject.getString("uid"));
                    pepForDep.setFlage(jsonObject.getString("flag"));
                    Log.d("Test",pepForDep.getApplyuser());
                    pepForDeps.add(pepForDep);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return pepForDeps;
    }
}

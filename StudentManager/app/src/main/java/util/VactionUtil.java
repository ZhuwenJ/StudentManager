package util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.Vacation;

public class VactionUtil {

    public static Vacation getVacationDetail (String jsonData){
        JSONArray obj;
        Vacation vacat=new Vacation();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonData);
            vacat.setLeavetime(jsonObject.getString("leavetime"));
            vacat.setBacktime(jsonObject.getString("backtime"));
            vacat.setVacatereason(jsonObject.getString("vacatereason"));
            vacat.setVacateperson(jsonObject.getString("vacateperson"));
            vacat.setVacateid(jsonObject.getString("vacateid"));
            vacat.setPersondepartment(jsonObject.getString("persondepartment"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vacat;
    }


}

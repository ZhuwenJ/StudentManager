package util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Activity;

public class ActUtil {


    public static Activity getActDetail(String jsonData){
        JSONArray obj;
        Activity act = new Activity();
        if (jsonData != null) {
            try {
                obj = new JSONArray(jsonData);
                for(int i=0;i<obj.length();i++) {
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
                    return act;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return act;
    }
}

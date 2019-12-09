package util;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DepUtil {

    /**
     * Description:这个是用来在json数据里面，获取部门id
     * @author zwj
     * @Time 2019-11-02 13:25:00
     * @param jsonData:json数据
     * @return String:返回的部门ID
     */
    public static String getDepId(final String jsonData){
        String result="null";
        JSONArray obj;  //建立json对象
        if (!jsonData.isEmpty())
        {
            try  //加上异常处理
            {
                obj = new JSONArray(jsonData);    //将字符串转为json对象
                for(int i=0;i<obj.length();i++) {
                    JSONObject jsonObject = obj.getJSONObject(i);
                    Log.d("Test", jsonObject.getString("departmentid"));
                    result=jsonObject.getString("departmentid");

                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }


        Log.d("Test", result);
        return result;
    }

}

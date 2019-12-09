package util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

    /**
     * Description:这个是用来异步处理返回的用户json数据
     * @Time 2019-11-02 13:25:00
     * @param jsonData:异步子进程传过来的的用户的所有数据
     * @return  String:json数据里面，真正有用的数据
     */
    public static String getdataObj(String jsonData){
        JSONObject obj = null;  //建立json对象
        String forResult;
        forResult = "not Found!"; //如果没有输出字符，则显示forResult
        if (!jsonData.isEmpty())
        {
            try  //加上异常处理
            {
                obj = new JSONObject(jsonData);    //将字符串转为json对象
                forResult = obj.optString("dataObj");//把result对象提取出来，并转化为字符串
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return forResult;
    }

}

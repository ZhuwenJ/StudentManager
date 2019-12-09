package util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import model.Department;
import model.User;
import okhttp3.Call;
import okhttp3.Response;

public class UserUtil {

    /**
     * Description:这个是处理json中用户信息，返回处理好后用户的数据
     * @author zwj
     * @Time 2019-11-15 15:00:00
     * @param jsonData:dataObj
     * @return 返回获得用户对象
     */
    public static User getUser(String jsonData){
        User user=new User();
        JSONObject obj;  //建立json对象
        if (!jsonData.isEmpty())
        {
            try  //加上异常处理
            {
                obj = new JSONObject(jsonData);    //将字符串转为json对象
                Log.d("MainActivity",jsonData);
                user.setUid( obj.getString("uid"));//把result对象提取出来，并转化为字符串
                user.setUsername( obj.optString("username"));
                user.setBranch( obj.optString("branch"));
                user.setClassId( obj.optString("classId"));
                user.setSex( obj.optString("sex"));
                user.setPhonenumber( obj.optString("phonenumber"));
                user.setEmail( obj.optString("email"));
                user.setRank( obj.optString("rank"));
                user.setDepartments( obj.optString("departments"));
                user.setDepId(obj.getString("depId"));

                String  leave;
                leave=obj.optString("leavetime");

                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                if(leave.equals("null"))Log.d("LoginActivity","还没有离任");
                else
                    user.setLeavetime( sdf.parse(obj.optString("leavetime")));
                Log.d("LoginActivity","ssss "+obj.optString("appointmentTime").equals("null"));
                if(obj.optString("appointmentTime").equals("null"))
                    Log.d("LoginActivity","zzzzzz"+sdf.parse(obj.optString("appointmentTime")).toString());
                else {
                    String string=obj.optString("appointmentTime");
                    String[] strs=string.split("T");
                    Log.d("LoginActivity","time"+sdf.parse(strs[0]).toString());
                    user.setAppointmentTime(sdf.parse(obj.optString("appointmentTime")));
                }
                user.setExtr( obj.optString("extr"));
            }
            catch (JSONException e) {
                Log.d("LoginActivity",e.toString());
                e.printStackTrace();
            } catch (ParseException e) {
                Log.d("LoginActivity",e.toString());
                e.printStackTrace();
            }
        }
        User.currentUser=user;
        return user;
    }

    /**
     * Description:这个是通过对应部门名字，获取部门id，获取当前部门的所有人员。
     * @author zwj
     * @Time 2019-12-3 20:23:00
     * @param depname:部门名字
     */
    public static void getUserList(final String depname) {
        // TODO: implement
        HttpUtil.getRequest("http://api.wangz.online/api/depart/searchBydepName?depName="+depname,new okhttp3.Callback(){
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resonseData =response.body().string();
                Log.d("LoginActivity","String"+resonseData);
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
                if(forResult.equals("success")){
                    String jsonData= JsonUtil.getdataObj(resonseData);
                    Log.d("Test",jsonData);
                    String id=DepUtil.getDepId(jsonData);
//                    setUserlist(id);

                }
            }
        });

    }

    /**
     * Description:这个是用来通过部门id，来获取当前部门所有的人员
     * @author zwj
     * @Time 2019-12-3 20:27:00
     * @param depid:部门编号
     */
    public  static  ArrayList<User> getUserlist(String depid){
        HttpUtil.getRequest("http://api.wangz.online/api/depart/getdepMenberById?depId="+depid, new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resonseData = response.body().string();
                JSONObject obj = null;  //建立json对象
                String forResult;
                forResult = "not Found!"; //如果没有输出字符，则显示forResult
                if (!resonseData.isEmpty()) {
                    try  //加上异常处理
                    {
                        obj = new JSONObject(resonseData);    //将字符串转为json对象
                        forResult = obj.optString("dataObj");//把result对象提取出来，并转化为字符串
                        Log.d("UserList",forResult);
                        User.users=getUsers(forResult);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return User.users;
    }

    /**
     * Description:这个是用来处理返回的json格式数据,变成用户数组
     * @author zwj
     * @Time 2019-12-3 20:40:00
     * @param jsonData:待处理的json数组
     * @return  ArrayList<User>:返回是处理好的用户数组
     */
    public static ArrayList<User> getUsers(String jsonData){
        ArrayList<User> users=new ArrayList<User>();
        JSONArray obj;  //建立json对象
        if (!jsonData.isEmpty()) {
            try {
                obj = new JSONArray(jsonData);
                for (int i = 0; i < obj.length(); i++) {
                    User user = new User();
                    JSONObject jsonObject = obj.getJSONObject(i);
                    user.setUid(jsonObject.getString("uid"));
                    user.setUsername(jsonObject.getString("username"));
                    if(user.getUsername().equals("root"))
                        continue;
                    user.setBranch(jsonObject.getString("branch"));
                    user.setClassId(jsonObject.getString("classId"));
                    user.setSex(jsonObject.getString("sex"));
                    user.setPhonenumber(jsonObject.getString("phonenumber"));
                    user.setEmail(jsonObject.getString("email"));
                    user.setDepartments(jsonObject.getString("departments"));
                    user.setRank(jsonObject.getString("rank"));
                    user.setDepId(User.currentUser.getDepId());

                    users.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return users;
    }


    public static  ArrayList<User> getFenguanUsers(String jsonData) {
        ArrayList<User> users = new ArrayList<User>();
        JSONArray obj;  //建立json对象
        JSONArray obj1;  //建立json对象
        if (!jsonData.isEmpty()) {
            try {
                obj1 = new JSONArray(jsonData);
                for (int i = 0; i < obj1.length(); i++) {
                    obj = new JSONArray(obj1.get(i).toString());
                    for (int j = 0; j < obj.length(); j++) {
                        User user = new User();
                        JSONObject jsonObject = obj.getJSONObject(j);
                        user.setUid(jsonObject.getString("uid"));
                        user.setUsername(jsonObject.getString("username"));
                        if (user.getUsername().equals("root"))
                            continue;
                        user.setBranch(jsonObject.getString("branch"));
                        user.setClassId(jsonObject.getString("classId"));
                        user.setSex(jsonObject.getString("sex"));
                        user.setPhonenumber(jsonObject.getString("phonenumber"));
                        user.setEmail(jsonObject.getString("email"));
                        user.setDepartments(jsonObject.getString("departments"));
                        user.setRank(jsonObject.getString("rank"));
                        user.setDepId(User.currentUser.getDepId());

                        users.add(user);
                    }
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
        return users;
    }

}

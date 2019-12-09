package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

public class Inform {
    private String informid;
    private String  informerid;
    private int informtype;
    private String  userid;
    private int checknum;
    private String depuid;
    private String noticetype;

    public static ArrayList<Inform> informm;
    public static  int flag=0;

    public int getInformtype() {
        return informtype;
    }

    public void setInformtype(int informtype) {
        this.informtype = informtype;
    }
    public String getInformid() {
        return informid;
    }

    public void setInformid(String informid) {
        this.informid = informid;
    }

    public String getInformerid() {
        return informerid;
    }

    public void setInformerid(String informerid) {
        this.informerid = informerid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getChecknum() {
        return checknum;
    }

    public void setChecknum(int checknum) {
        this.checknum = checknum;
    }

    public static  void getInformList(){
        // TODO: implement
    }

    public String getDepuid() {
        return depuid;
    }

    public void setDepuid(String depuid) {
        this.depuid = depuid;
    }

    public String getNoticetype() {
        return noticetype;
    }

    public void setNoticetype(String noticetype) {
        this.noticetype = noticetype;
    }

    public  static ArrayList<Inform> getInforms (String jsonData){
        ArrayList <Inform> informs = new ArrayList<Inform>();
        JSONArray obj;
        if(!jsonData.isEmpty()){
            try{
                obj = new JSONArray(jsonData);
                for(int i=0; i < obj.length();i++){
                    Inform inform = new Inform();
                    JSONObject jsonObject = obj.getJSONObject(i);
                    inform.setInformid(jsonObject.getString("informid"));
                    inform.setInformerid(jsonObject.getString("informerid"));
                    inform.setUserid(jsonObject.getString("userid"));
                    inform.setChecknum(jsonObject.getInt("checknum"));
                    if(jsonObject.getString("depuid").equals("系统通知"))
                        inform.setDepuid("活动通知");
                    else
                        inform.setDepuid(jsonObject.getString("depuid"));
                    inform.setNoticetype(jsonObject.getString("noticetype"));


                    Log.d("InformList",inform.getInformid());
                    informs.add(inform);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  informs;
    }
}

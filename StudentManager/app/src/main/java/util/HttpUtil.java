package util;

import android.util.Log;

import org.json.JSONObject;

import java.util.Map;
import java.util.Map.Entry;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    private static final MediaType JSON=MediaType.parse("application/json;charset=utf-8");

    /**
     * Description:这个是用post提交form表单的url的请求
     * @author zwj
     * @Time 2019-11-02 13:25:00
     * @param address:请求的url地址
     * @param forms:请求的form表单
     * @param callback:返回的操作
     */
    public static void postRequest(String address,Map<String,String> forms, okhttp3.Callback callback){
        OkHttpClient client =new OkHttpClient();
        FormBody.Builder requset=new FormBody.Builder();
        JSONObject jsonObject = new JSONObject(forms);
        for(Entry<String,String> entry : forms.entrySet()){
            requset.add(entry.getKey(),entry.getValue());
        }
        RequestBody body = requset.build();
        Request request=new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Description:这个是用post提交josn数据的url的请求
     * @author zwj
     * @Time 2019-11-02 13:25:00
     * @param address:请求的url地址
     * @param forms:请求的form表单
     * @param callback:返回的操作
     */

    public static void postJsonRequest(String address,Map<String,String> forms, okhttp3.Callback callback){
        OkHttpClient client =new OkHttpClient();
        JSONObject jsonObject = new JSONObject(forms);
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        FormBody.Builder requset=new FormBody.Builder();
        for(Entry<String,String> entry : forms.entrySet()){
            requset.add(entry.getKey(),entry.getValue());
        }
        Request request=new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Description:这个是用request的url的请求
     * @author zwj
     * @Time 2019-11-02 13:25:00
     * @param address:请求的url地址
     * @param callback:返回的操作
     */
    public static void getRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request=new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}

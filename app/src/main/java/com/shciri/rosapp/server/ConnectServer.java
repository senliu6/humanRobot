package com.shciri.rosapp.server;

import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.mydata.DBUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectServer {

    String URL = "http://zqn.ink/daimon/info.php";

    public void addInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                String url = URL + "?" + "operation="+"Insert" + "&task_exe_number="+"0" + "&task_exe_duration="+"0" + "&status_code="+"normal";
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String res = response.body().string();
                    System.out.println("response = " + res);
                    if (res != null && !res.trim().equals("")) {
                        JSONObject jsonObject = new JSONObject(res);
                        ServerInfoTab.id = jsonObject.getInt("info");
                        DBUtils.getInstance().DBInsertInfo(ServerInfoTab.id, RCApplication.rosIP);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    public void updateInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                String url = URL + "?" +
                        "operation="+"Update" +
                        "&task_exe_number="+ServerInfoTab.task_exe_number +
                        "&task_exe_duration="+ServerInfoTab.task_exe_duration +
                        "&status_code="+ServerInfoTab.status_code +
                        "&id="+ServerInfoTab.id;
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String res = response.body().string();
                    System.out.println("response = " + res);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void queryInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                String url = URL + "?" + "operation="+"Query" + "&id="+ServerInfoTab.id;
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String res = response.body().string();
                    System.out.println("response = " + res);
                    if (res != null && !res.trim().equals("")) {
                        JSONObject jsonObject = new JSONObject(res);
                        if(jsonObject.getInt("success") == 1) {
                            JSONObject item = jsonObject.getJSONObject("info");
                            ServerInfoTab.id = item.getInt("id");
                            ServerInfoTab.task_exe_number = item.getInt("task_exe_number");
                            ServerInfoTab.task_exe_duration = item.getInt("task_exe_duration");
                            ServerInfoTab.status_code = item.getString("status_code");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

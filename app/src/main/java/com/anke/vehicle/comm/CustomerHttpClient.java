package com.anke.vehicle.comm;

import android.util.Log;

import com.anke.vehicle.entity.BindAmbInfo;
import com.anke.vehicle.entity.NPadAccdintInfo;
import com.anke.vehicle.entity.NPadBookInfo;
import com.anke.vehicle.entity.NPadChangeStationInfo;
import com.anke.vehicle.entity.NPadShoufei;
import com.anke.vehicle.entity.SendNoticeInfo;
import com.anke.vehicle.entity.UpLoadInfo;
import com.anke.vehicle.utils.Utility;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerHttpClient {
    private static HttpClient customerHttpClient;
    private static CustomerHttpClient instance;
    private String serverUrl = "";


    private CustomerHttpClient() {
        customerHttpClient = new DefaultHttpClient();
        // 请求超时
        customerHttpClient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        // 读取超时
        customerHttpClient.getParams().setParameter(
                CoreConnectionPNames.SO_TIMEOUT, 20000);
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static CustomerHttpClient getInstance() {
        if (null == instance) {
            instance = new CustomerHttpClient();
        }
        return instance;
    }

    private String PostData(List<NameValuePair> datas) {
        String ret = "";
        if (serverUrl.equals(""))
//            serverUrl = "http://" + Utility.getServerIp() + ":" + Utility.getHttpport() + "/";
        serverUrl = "http://" + Utility.getServerIp() + ":" + Utility.getHttpport() + "/";
        HttpPost httppost = new HttpPost(serverUrl);

        try {
            httppost.setEntity(new UrlEncodedFormEntity(datas, HTTP.UTF_8));
            /* 取得HTTP response */
            HttpResponse httpResponse = customerHttpClient.execute(httppost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */
                String strResult = EntityUtils.toString(
                        httpResponse.getEntity(), "utf-8");
                ret = strResult;
            } else {
                ret = "Post异常：";
            }
        } catch (Exception e) {
            ret = "Post异常：";
        }
        return ret;
    }

    public String GetData(String uriAPI) {
        String ret = "";
		/* 建立HTTP Get联机 */
        HttpGet httpRequest = new HttpGet(uriAPI);
        try {
			/* 发出HTTP request */
            HttpResponse httpResponse = new DefaultHttpClient()
                    .execute(httpRequest);
			/* 若状态码为200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */
                String strResult = EntityUtils.toString(httpResponse
                        .getEntity());
                ret = strResult;
            } else {
                Log.d("GetData:", httpResponse.getStatusLine().toString());
            }
        } catch (Exception e) {
            Log.e("GetData:", e.toString());
        }
        return ret;
    }

    // 查询医院
    public String QueryHospital() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "QueryHospital"));
        return PostData(nameValuePairs);
    }

    // 修改绑定车辆 2016-06-06 肖明星
    public String QueryUnBindAmb() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "QueryUnBindAmb"));
        return PostData(nameValuePairs);
    }

    // 绑定车载 2016-5-5 xmx
    public String SetBindAmb(String content) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "BindAmbulance"));
        nameValuePairs.add(new BasicNameValuePair("Content", content));
        return PostData(nameValuePairs);
    }

    // Pad修改绑定车载 肖明星
    public String BindAmb(BindAmbInfo info) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "BindAmbulance"));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(info);// bean -> json
        nameValuePairs.add(new BasicNameValuePair("Content", jsonStr));
        return PostData(nameValuePairs);
    }

    // 发送通知
    public String SendNotice(SendNoticeInfo info) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "SaveNotice"));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(info);// bean -> json
        nameValuePairs.add(new BasicNameValuePair("Content", jsonStr));
        return PostData(nameValuePairs);
    }

    // 查询药品  主宿判断
    public String QueryYaoPin() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "QueryYaoPin"));

        return PostData(nameValuePairs);
    }

    // 查询专家
    public String QueryMaster() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "QueryMaster"));
        return PostData(nameValuePairs);
    }

    // 查询终止任务原因 2016-05-03 xmx
    public String QueryStopReason() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "QueryStop"));
        Log.e("taaaaaaa",PostData(nameValuePairs));
        return PostData(nameValuePairs);
    }

    // 查询暫停调用原因 2016-05-03 xmx
    public String QueryPauseReason() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "QueryPause"));
        return PostData(nameValuePairs);
    }

    // 告知急救费用
    public String SendCureCharge(UpLoadInfo info) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "SaveUpLoad"));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(info);// bean -> json
        nameValuePairs.add(new BasicNameValuePair("Content", jsonStr));
        return PostData(nameValuePairs);
    }

    // 保存急救费用  2016-05-11
    public String SaveCureCharge(NPadShoufei npsfinfo) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "SaveShoufei"));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(npsfinfo);// bean -> json
        nameValuePairs.add(new BasicNameValuePair("Content", jsonStr));
        return PostData(nameValuePairs);
    }

    // 上传事故  2016-05-30
    public String SaveAccdint(NPadAccdintInfo npAinfo) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "SaveShoufei"));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(npAinfo);// bean -> json
        nameValuePairs.add(new BasicNameValuePair("Content", jsonStr));
        return PostData(nameValuePairs);
    }

    // 新建任务 2016-05-05
    public String SendNewTask(NPadBookInfo npInfo) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "NewTask"));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(npInfo);// bean -> json
        nameValuePairs.add(new BasicNameValuePair("Content", jsonStr));
        return PostData(nameValuePairs);
    }

    // 获取分站 2016-05-05
    public String GetStation(String ambcode) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "QueryStation"));
        nameValuePairs.add(new BasicNameValuePair("Content", ambcode));
        return PostData(nameValuePairs);
    }

    // 修改分站 2016-05-05
    public String RecoverStation(NPadChangeStationInfo npcsInfo) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "ModifyStation"));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(npcsInfo);// bean -> json
        nameValuePairs.add(new BasicNameValuePair("Content", jsonStr));
        return PostData(nameValuePairs);
    }

    // 下载车辆 2016-05-05
    public String GetAmb() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "QueryUnBindAmb"));
        return PostData(nameValuePairs);
    }
}

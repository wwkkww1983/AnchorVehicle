package com.anke.vehicle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.anke.vehicle.R;
import com.anke.vehicle.comm.CustomerHttpClient;
import com.anke.vehicle.entity.NPadBookInfo;
import com.anke.vehicle.utils.Utility;

import org.json.JSONObject;

/**
 * 新建任务Activity
 */
public class NewTaskActivity extends Activity {
    private LinearLayout llyWaitAddr; // 接车地址 2016-05-05
    private EditText etWaitAddr;
    private Button btWaitAddr;
    private LinearLayout llySendAddr; // 送往地址 2016-05-05
    private EditText etSendAddr;
    private Button btSendAddr;
    private LinearLayout llyDescribe; // 病人情况 2016-05-05
    private EditText etDescribe;
    private LinearLayout llyRemark; // 备注 2016-05-05
    private EditText etRemark;
    private Button btSave; // 提交 2016-05-05
    private Button btNewTaskback; // 返回 2016-05-05
    public static Handler myHandler; // 线程 2016-05-10 xmx
    public String waitAddr;
    public String waitlatitude;
    public String waitlongitude;
    public String sendAddr;
    public String sendlatitude;
    public String sendlongitude;
    private String city = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtask);
        waitAddr = "";
        waitlatitude = "";
        waitlongitude = "";
        sendAddr = "";
        sendlatitude = "";
        sendlongitude = "";
        initUI();//初始化UI控件
        DealOnclick();//处理onclick事件


    }

    /**
     * 处理onclick事件
     */
    private void DealOnclick() {
        btWaitAddr.setOnClickListener(listener);
        btSendAddr.setOnClickListener(listener);
        btNewTaskback.setOnClickListener(listener);
        btSave.setOnClickListener(listener);
    }

    /**
     * 初始化UI控件
     */
    private void initUI() {
        //llyWaitAddr = (LinearLayout) findViewById(R.id.llyWaitAddr);
        etWaitAddr = (EditText) findViewById(R.id.etWaitAddr);
        btWaitAddr = (Button) findViewById(R.id.btWaitAddr);
        //llySendAddr = (LinearLayout) findViewById(R.id.llySendAddr);
        etSendAddr = (EditText) findViewById(R.id.etSendAddr);
        btSendAddr = (Button) findViewById(R.id.btSendAddr);
        //llyDescribe = (LinearLayout) findViewById(R.id.llyDescribe);
        etDescribe = (EditText) findViewById(R.id.etDescribe);
        //llyRemark = (LinearLayout) findViewById(R.id.llyRemark);
        etRemark = (EditText) findViewById(R.id.etRemark);
        btNewTaskback = (Button) findViewById(R.id.btNewTaskback);
        btSave = (Button) findViewById(R.id.btSave);
    }

    /**
     * 保存功能
     */
    private void saveFunction() {
        if (!etWaitAddr.getText().toString().equals("")) {
            if (!etSendAddr.getText().toString().equals(""))
                SendNewTaskThread();
            else
                Utility.ToastShow(NewTaskActivity.this, "请填写送往地址！");
        } else
            Utility.ToastShow(NewTaskActivity.this, "请填写接车地址！");
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Button bt = (Button) v;
            switch (bt.getId()) {
                case R.id.btWaitAddr://获取接车地址按键
                    Intent intent = new Intent();
                    intent.setClass(NewTaskActivity.this, MapActivity.class);
                    intent.putExtra("type", 1);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.btSendAddr://获取送往地址信息
                    Intent intent1 = new Intent();
                    intent1.setClass(NewTaskActivity.this, MapActivity.class);
                    intent1.putExtra("type", 2);
                    startActivityForResult(intent1, 1);
                    break;
                case R.id.btNewTaskback:
                    NewTaskActivity.this.finish();
                    break;
                case R.id.btSave:
                    saveFunction();//保存功能
                    break;
                default:
                    break;
            }
        }
    };

    // 提交新任务
    private void SendNewTaskThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NPadBookInfo npInfo = new NPadBookInfo();
                npInfo.setCity(city);
                npInfo.setCenter(etRemark.getText().toString());
                npInfo.setMeetaddr(etWaitAddr.getText().toString());
                npInfo.setMeetjd(waitlatitude);
                npInfo.setMeetwd(waitlongitude);
                npInfo.setMeettime(Utility.dinfo.getAmbulanceCode()); // 车辆编码
                npInfo.setTargetaddr(etSendAddr.getText().toString());
                npInfo.setTargetjd(sendlatitude);
                npInfo.setTargetwd(sendlongitude);
                npInfo.setTelphone(Utility.getPhone());
                npInfo.setDescribe(etDescribe.getText().toString());
                npInfo.setCenter(Utility.dinfo.getAmbDeskName()); // 车辆实际标识
                npInfo.setRemark(etRemark.getText().toString());
                SendNewTask(npInfo);
            }
        }).start();
    }

    // 上传新建任务 2016-05-05
    private void SendNewTask(NPadBookInfo npInfo) {
        Message m = new Message();
        String ret = CustomerHttpClient.getInstance().SendNewTask(npInfo);
        if (ret.equals("网络异常")) {
            m.what = 2;
            m.obj = "网络异常";// 上传失败
            handler.sendMessage(m);
            return;
        }

        if (ret.equals("success")) {
            m.what = 1; // 上传成功
        } else {
            m.what = 2; // 上传失败
            m.obj = ret;
        }
        handler.sendMessage(m);
    }

    // /如果key对应的值为null则赋值空
    public String GetJsonValue(String keyString, JSONObject objjson) {
        try {
            if (objjson.isNull(keyString)) {
                return "";
            } else {
                return objjson.getString(keyString);
            }
        } catch (Exception e) {
            return "";
        }
    }

    // 2016-05-05
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;

                case 1: // 上传成功
                    Utility.ToastShow(NewTaskActivity.this, "新建任务成功!");
                    NewTaskActivity.this.finish();
                    break;

                case 2: // 上传失败
                    Utility.ToastShow(NewTaskActivity.this,
                            "新建任务失败！原因：" + msg.obj.toString());
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 0 && resultCode == 1) {
            etWaitAddr.setText(data.getStringExtra("WaitAddr"));
            city = data.getStringExtra("city");
            waitlatitude = data.getStringExtra("WaitAddrlatitude");
            waitlongitude = data.getStringExtra("WaitAddrlongitude");
        }
        if (requestCode == 1 && resultCode == 1) {
            etSendAddr.setText(data.getStringExtra("WaitAddr"));
            city = data.getStringExtra("city");
            sendlatitude = data.getStringExtra("WaitAddrlatitude");
            sendlongitude = data.getStringExtra("WaitAddrlongitude");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

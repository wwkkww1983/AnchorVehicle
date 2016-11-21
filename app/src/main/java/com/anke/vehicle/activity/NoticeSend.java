package com.anke.vehicle.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.anke.vehicle.R;
import com.anke.vehicle.comm.CustomerHttpClient;
import com.anke.vehicle.entity.SendNoticeInfo;
import com.anke.vehicle.utils.CommonUtils;
import com.anke.vehicle.utils.Utility;

public class NoticeSend extends Activity {
    MyHandler mHandler = null;
    private EditText etContent;
    private Button btsend;
    private Button noticeback;
//    private ProgressDialog d;
private AlertDialog d;
    private Button bthisrecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendnotice);
        initFinViewById();//初始化UI控件
        DealBtnOnclick();//处理buttion点击事件
    }

    /**
     * 处理buttion点击事件
     */
    private void DealBtnOnclick() {
        btsend.setOnClickListener(btnListener);
        noticeback.setOnClickListener(btnListener);
        bthisrecord.setOnClickListener(btnListener);
    }

    /**
     * 处理发送
     */
    private void dealBtnSender() {
        if (!etContent.getText().toString().trim().equals("")) {
            // TODO Auto-generated method stub
            d = CommonUtils.createProgressDialog(this,"正在发送信息...");
            Thread thread2 = new Thread(new SendNotice());// 启动一个线程
            thread2.start();
        } else {
            Utility.ToastShow(NoticeSend.this, "发送内容不能为空");
        }
    }

    /**
     * 分别处理各自的点击事件
     */
    private OnClickListener btnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            switch (btn.getId()) {
                case R.id.ntsend:
                    dealBtnSender();//处理发送
                    break;
                case R.id.noticeback:
                    finish();
                    break;
                case R.id.bthisrecord:
                    Intent intenthis = new Intent();
                    intenthis.setClass(NoticeSend.this, HistoryActivity.class);
                    startActivity(intenthis);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化UI控件
     */
    private void initFinViewById() {
        etContent = (EditText) findViewById(R.id.ntcontent);
        btsend = (Button) findViewById(R.id.ntsend);
        noticeback = (Button) findViewById(R.id.noticeback);
        bthisrecord = (Button) findViewById(R.id.bthisrecord);
    }

    public class MyHandler extends Handler {

        public MyHandler(Looper looper) {

            super(looper);

        }

        public void handleMessage(Message msg) {
            // 处理消息
            if (msg.what == 1) {
                if (d != null && d.isShowing())
                    d.dismiss();
                Utility.ToastShow(NoticeSend.this, msg.obj.toString().trim());
            }
        }
    }

    ;

    class SendNotice implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            SendNoticeInfo info = new SendNoticeInfo();
            info.setTelCode(Utility.getPhone());
            info.setContent(etContent.getText().toString());
            String ret = CustomerHttpClient.getInstance().SendNotice(info);
            if ((!ret.trim().equals("")) && (!ret.trim().equals("null"))) {
                Looper curLooper = Looper.getMainLooper();
                mHandler = new MyHandler(curLooper);
                Message m = mHandler.obtainMessage(1, ret);
                mHandler.sendMessage(m);
            }
        }
    }
}

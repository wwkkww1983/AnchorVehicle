package com.anke.vehicle.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.anke.vehicle.R;
import com.anke.vehicle.application.MyApplication;
import com.anke.vehicle.status.WorkState;
import com.anke.vehicle.comm.CustomerHttpClient;
import com.anke.vehicle.database.DBHelper;
import com.anke.vehicle.entity.HistoryInfo;
import com.anke.vehicle.entity.NPadHospitalInfo;
import com.anke.vehicle.entity.NPadJudgeInfo;
import com.anke.vehicle.entity.UpLoadInfo;
import com.anke.vehicle.utils.CommonUtils;
import com.anke.vehicle.utils.Utility;
import com.google.gson.Gson;

import java.util.List;

/**
 * 告知
 */
public class UpLoad extends Activity {
    private DBHelper dbHelper;
    private Button btselect;
    private Button btupload;
    private Button btback;
    private EditText ethospital;
    private EditText etjudge;
    private EditText etill;
    // private EditText etshoufei;
    // private Button bt_save;
    private List<NPadHospitalInfo> kinfos;
    private List<NPadJudgeInfo> jinfos;
    private PopupWindow popView;
    private GJAdapter cja;
    private GJAdapter2 cja2;
    private ListView listView;
    // private String taskCode = "";
    // private String taskorder = "";
    private int workstate = -1;
    private String ambCode = "";
    private HistoryInfo hinfo = new HistoryInfo();

//    private ProgressDialog d;
private AlertDialog d;
    MyHandler mHandler = null;
    private String[] mArrayBingQing2 = {"危重", "中等", "轻", "其他"};
    private int[] tellCoclor = {Color.RED, Color.YELLOW, Color.GREEN, Color.GRAY};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gaozhinew);
        initFindViewById();//初始化UI控件
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        // taskCode = bundle.getString("taskcode");
        // taskorder = bundle.getString("taskorder");
        workstate = bundle.getInt("workstate");
        ambCode = bundle.getString("ambcode");
        initHistoryData(bundle);//初始化网络数据
        // 判断此命令单是否在任务中，在任务中才允许告知 2016-05-11
        dbHelper = new DBHelper(UpLoad.this);
        cja = new GJAdapter();
        cja2 = new GJAdapter2();
        MyApplication.addDestoryActivity(this,"UpLoad");
        dealListener();//处理所有的监听事件
    }

    /**
     * 处理所有的监听事件
     */
    private void dealListener() {
        etjudge.setOnKeyListener(onKey2);
        etjudge.addTextChangedListener(etjudgeChangeListener);
        ethospital.setOnKeyListener(onKey);
        ethospital.addTextChangedListener(ethospitalChangeListener);
        btback.setOnClickListener(listener);
        btselect.setOnClickListener(listener);
        btupload.setOnClickListener(listener);
        // bt_save.setOnClickListener(listener);
    }

    private TextWatcher ethospitalChangeListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            if (count > 0) {
                if (popView != null) {
                    popView.dismiss();
                    popView = null;
                    ShowHospital();
                } else {
                    ShowHospital();
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };
    private TextWatcher etjudgeChangeListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            if (count > 0) {
                if (popView != null) {
                    popView.dismiss();
                    popView = null;
                    ShowJudge();
                } else {
                    ShowJudge();
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    /**
     * 初始化历史数据
     *
     * @param bundle
     */
    private void initHistoryData(Bundle bundle) {
        Gson gson = new Gson();
        java.lang.reflect.Type type =
                new com.google.gson.reflect.TypeToken<HistoryInfo>() {
                }.getType();
        hinfo = gson.fromJson(bundle.getString("historyInfo"), type);

        if (hinfo.getIll() != null && !hinfo.getIll().toString().equals(""))
            etill.setText(hinfo.getIll().toString());
        if (hinfo.getHospital() != null && !hinfo.getHospital().toString().equals(""))
            ethospital.setText(hinfo.getHospital().toString());
        if (hinfo.getJudge() != null && !hinfo.getJudge().toString().equals(""))
            etjudge.setText(hinfo.getJudge().toString());
    }

    /**
     * 初始化UI控件
     */
    private void initFindViewById() {
        btselect = (Button) findViewById(R.id.btnSelect);
        btupload = (Button) findViewById(R.id.bt_upload);
        btback = (Button) findViewById(R.id.upback);
        // bt_save = (Button) findViewById(R.id.bt_save);

        ethospital = (EditText) findViewById(R.id.ethospital);
        etjudge = (EditText) findViewById(R.id.etjudge);
        etill = (EditText) findViewById(R.id.etBingqing);
        // etshoufei = (EditText)findViewById(R.id.etshoufei);
    }

    private void ShowHospital() {
        String temp = ethospital.getText().toString();
        kinfos = dbHelper.getHospital(temp);
        if (kinfos != null && !kinfos.isEmpty()) {
            initPopView(1);
        }
    }

    private void ShowJudge() {
        String temp = etjudge.getText().toString();
        jinfos = dbHelper.getJudge(temp);
        if (jinfos != null && !jinfos.isEmpty()) {
            initPopView(2);
        }
    }

    private void initPopView(int type) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.knowlist, null);
        listView = (ListView) view.findViewById(R.id.lvknow);
        if (type == 1)
            listView.setAdapter(cja);
        else
            listView.setAdapter(cja2);

        if (type == 1)
            popView = new PopupWindow(view, ethospital.getWidth(), LayoutParams.WRAP_CONTENT);
        else
            popView = new PopupWindow(view, etjudge.getWidth(), LayoutParams.WRAP_CONTENT);
        popView.setBackgroundDrawable(getResources().getDrawable(R.drawable.lhtopbar));
        popView.setFocusable(false);
        popView.setOutsideTouchable(true);
        popView.setAnimationStyle(android.R.style.Animation_Dialog);
        popView.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popView.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (type == 1)
            popView.showAsDropDown(ethospital);
        else {
            popView.showAsDropDown(etjudge);
        }
    }

    // 处理回车事件
    OnKeyListener onKey = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub

            if (keyCode == 67 && event.getAction() == KeyEvent.ACTION_UP
                    && ethospital.getText().toString().length() > 0) {
                if (popView != null) {
                    popView.dismiss();
                    popView = null;
                    ShowHospital();
                } else {
                    ShowHospital();
                }
            }
            return false;
        }
    };

    OnKeyListener onKey2 = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if (keyCode == 67 && event.getAction() == KeyEvent.ACTION_UP
                    && etjudge.getText().toString().length() > 0) {
                if (popView != null) {
                    popView.dismiss();
                    popView = null;
                    ShowJudge();
                } else {
                    ShowJudge();
                }
            }
            return false;
        }
    };

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Button bt = (Button) v;
            switch (bt.getId()) {
                case R.id.btnSelect:
                    new AlertDialog.Builder(UpLoad.this, AlertDialog.THEME_HOLO_LIGHT).setTitle("请选择")
                            .setItems(mArrayBingQing2, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichcountry) {
                                    etill.setText(mArrayBingQing2[whichcountry]);
                                    etill.setTextColor(tellCoclor[whichcountry]);
                                }
                            }).setNegativeButton("取消", null).show();
                    break;
                case R.id.upback:
                    onBackPressed();
                    break;

                case R.id.bt_upload:
                    if (workstate > WorkState.ASSIGNING_TASKS && workstate < WorkState.ROADWAITTING) {
                        if (!ethospital.getText().toString().equals("")) {
                            if (!etjudge.getText().toString().equals("")) {
                                if (!etill.getText().toString().equals("")) {
                                    UpLoading();
                                } else {
                                    Utility.ToastShow(UpLoad.this, "请填写病情！");
                                }
                            } else {
                                Utility.ToastShow(UpLoad.this, "请填写主诉判断！");
                            }
                        } else {
                            Utility.ToastShow(UpLoad.this, "请填写送往地址！");
                        }
                    } else {
                        Utility.ToastShow(UpLoad.this, "此命令单已经不在任务中不允许告知！");
                    }
                    break;

                // case R.id.bt_save: //保存 2016-05-10 xmx
                // UpLoading(2);
                // break;
                default:
                    break;
            }
        }
    };

    private void UpLoading() {
        d = CommonUtils.createProgressDialog(this, "正在上传信息...");
        Thread thread2 = new Thread(new UpdateDrug());// 启动一个线程
        thread2.start();
    }

    // 告知线程 2016-05-11
    class UpdateDrug implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            UpLoadInfo info = new UpLoadInfo();
            info.setAmbCoding(ambCode);
            info.setTaskCoding(hinfo.getTaskcode());
            info.setHospital(ethospital.getText().toString());
            info.setIll(etill.getText().toString());
            info.setJudge(etjudge.getText().toString());
            String ret = "";
            ret = CustomerHttpClient.getInstance().SendCureCharge(info);
            Looper curLooper = Looper.getMainLooper();

            mHandler = new MyHandler(curLooper);
            if (ret.contains("成功")) {
                hinfo.setHospital(ethospital.getText().toString());
                hinfo.setIll(etill.getText().toString());
                hinfo.setJudge(etjudge.getText().toString());
                dbHelper.SaveHistorydata(hinfo);
                Message m = mHandler.obtainMessage(1, 3, 1, ret);
                mHandler.sendMessage(m);
            } else {
                Message m = mHandler.obtainMessage(1, 3, 2, ret);
                mHandler.sendMessage(m);
            }
        }
    }

    public class MyHandler extends Handler {

        public MyHandler(Looper looper) {

            super(looper);

        }

        public void handleMessage(Message msg) {
            // Socket连接超时了
            if (msg.what == 1) {
                if (msg.arg1 == 3) {
                    switch (msg.arg2) {
                        case 1:
                            if (d != null && d.isShowing())
                                d.dismiss();
                            Utility.ToastShow(UpLoad.this, msg.obj.toString());
                            setResult(2);
                            UpLoad.this.finish();
                            break;

                        case 2:
                            if (d != null && d.isShowing())
                                d.dismiss();
                            Utility.ToastShow(UpLoad.this, msg.obj.toString());
                            break;

                        default:
                            break;
                    }
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    class GJAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return kinfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return kinfos.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) { // 这句就是重用的关键
                convertView = LayoutInflater.from(UpLoad.this).inflate(R.layout.knowitem, null);
            }
            final TextView tvtitle = (TextView) convertView.findViewById(R.id.tvknow);
            final String title = kinfos.get(position).getName();
            tvtitle.setText(title);
            convertView.setTag(position);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (title != null && title != "") {
                        ethospital.setText(title);
                        ethospital.setSelection(title.length());
                        if (popView != null)
                            popView.dismiss();
                    }
                }
            });
            return convertView;
        }
    }

    class GJAdapter2 extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return jinfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return jinfos.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) { // 这句就是重用的关键
                convertView = LayoutInflater.from(UpLoad.this).inflate(
                        R.layout.knowitem, null);
            }
            final TextView tvtitle = (TextView) convertView.findViewById(R.id.tvknow);
            final String title = jinfos.get(position).getName();
            tvtitle.setText(title);
            convertView.setTag(position);

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (title != null && title != "") {
                        etjudge.setText(title);
                        etjudge.setSelection(title.length());
                        if (popView != null)
                            popView.dismiss();
                    }
                }
            });
            return convertView;
        }
    }
}

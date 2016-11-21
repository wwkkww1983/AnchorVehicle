package com.anke.vehicle.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anke.vehicle.R;
import com.anke.vehicle.comm.CustomerHttpClient;
import com.anke.vehicle.database.DBHelper;
import com.anke.vehicle.entity.NPadChangeStationInfo;
import com.anke.vehicle.entity.NPadStationInfo;
import com.anke.vehicle.status.CreateDialogsLIst;
import com.anke.vehicle.status.FenZhanGetStatus;
import com.anke.vehicle.status.InvokingStaus;
import com.anke.vehicle.status.TrueOrFalseStatus;
import com.anke.vehicle.status.WorkState;
import com.anke.vehicle.utils.CommonUtils;
import com.anke.vehicle.utils.CustomDialogs;
import com.anke.vehicle.utils.UpDateDB;
import com.anke.vehicle.utils.Utility;
import com.anke.vehicle.utils.ZDYDialog;
import com.anke.vehicle.view.SetView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置入口
 * 右上角图标
 */
public class ParameterSetActivity extends Activity {
    private TextView tvFenzhan;//选中了那个分站
    private RelativeLayout rlPause; // 暂停调用 2016-05-04
    private TextView tvPause;//暂停(恢复)调用
    private TextView tvPauseReason;//暂停调用原因
    private PopupWindow popView; // 弹出下拉框 2016-05-04 xmx
    private int mWorkstate = -1; // 记录工作状态 2016-05-04 xmx
    public static Handler myHandler; // 线程 2016-05-03 xmx
    private Button btSetback; // 返回按钮
    private List<NPadStationInfo> listPadCSInfo; // 分站
    private String stationName; // 修改的分站名称
    private DBHelper dbHelper;
    private AlertDialog d;
    public static String code = "";
    private String title = "";
    private SetView mAddTask;//新增任务
    private SetView mFenZhan;//当前分站
    private SetView mPause;//暂停调用
    private SetView mDataUpdate;//数据更新
    private SetView mSetting;//参数设置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameterset);
        dbHelper = new DBHelper(this);
        code = "";
        stationName = ""; // 清空分站名称
        Intent intent = getIntent();
        mWorkstate = intent.getExtras().getInt("workstate"); // 工作状态
        initUI();//初始化UI控件
        showOrHidden();//是否显示新增分站 ，暂停（恢复）调用，当前分站
        ShowWorkstate();//暂停(恢复)调用
        mAddTask.setOnClickListener(svListener);
        mFenZhan.setOnClickListener(svListener);
        mPause.setOnClickListener(svListener);
        mDataUpdate.setOnClickListener(svListener);
        mSetting.setOnClickListener(svListener);
        btSetback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ParameterSetActivity.this.finish();
            }
        });
    }

    /**
     * 是否显示新增分站 ，暂停（恢复）调用，当前分站
     */
    private void showOrHidden() {
        if (dbHelper.GetParameter().getIsPauseCall() == TrueOrFalseStatus.TRUE) { // 根据设置判断是否显示暂停调用功能
            mPause.setVisibility(View.VISIBLE);
        } else {

            mPause.setVisibility(View.GONE);

        }
        if (dbHelper.GetParameter().getIsNewTask() == TrueOrFalseStatus.TRUE) { // 判断是否允许新建任务
            mAddTask.setVisibility(View.VISIBLE);
        } else {
            mAddTask.setVisibility(View.GONE);
        }
        if (dbHelper.GetParameter().getIsChangStation() == TrueOrFalseStatus.TRUE) { // 根据设置判断是否显示修改分站功能
            // 2016-05-17
            // xmx
            mFenZhan.setVisibility(View.VISIBLE);
        } else {
            mFenZhan.setVisibility(View.GONE);
        }
    }


    /**
     * 更新数据
     */
    private void updateDatas() {
        d = CommonUtils.createProgressDialog(this, "正在更新基础数据信息...");
          new UpDateDB(this,dbHelper,d);//更新数据库
    }

    /**
     * 待命状态下才可以新建任务
     * 未在待命状态下，不能修改分站
     */
    private void TaskStates() {
        if (mWorkstate == WorkState.ROADWAITTING ||
                mWorkstate == WorkState.STATION_AITTING) // 待命状态下才可以新建任务
        {
            GetStationThread();
        } else {
            Utility.ToastShow(ParameterSetActivity.this, "未在待命状态下，不能修改分站！");
        }
    }


    /**
     * 创建任务
     */
    private void NewTasks() {
        if (mWorkstate == WorkState.ROADWAITTING ||
                mWorkstate == WorkState.STATION_AITTING) // 待命状态下才可以新建任务
        {
            startActivity(new Intent(this, NewTaskActivity.class));
        } else {
            Utility.ToastShow(ParameterSetActivity.this, "未在待命状态下，不能新建任务！");
        }
    }


    /**
     * 设置操作
     */
    private void settingOperation() {
        ZDYDialog zdyDialog = new ZDYDialog(this, "请输入密码！！！", true);
        zdyDialog.setZdYinterface(new ZDYDialog.ZDYinterface() {
            @Override
            public void btnOkonClick(int type, String inpust) {
                if (type == 1) {
                    if (inpust.equals("a")) {

                        startActivity(new Intent(ParameterSetActivity.this,
                                PreferenceParamActivity.class));

                    } else {
                        Utility.ToastShow(ParameterSetActivity.this,
                                "密码错误！");
                    }
                }
            }
        });
    }

    /**
     * 判断是恢复调用 还是暂停操作
     */
    private void workStateIsOrNot() {
        if (tvPause.getText().toString().equals("恢复调用")) {
            if (mWorkstate == WorkState.SUSPEND_INVOKING) {
                myHandler.sendEmptyMessage(InvokingStaus.RECOVER);
                ParameterSetActivity.this.finish();
                return;
            } else {
                Utility.ToastShow(ParameterSetActivity.this,
                        "未在暂停调用状态下，不能进行恢复调用操作！");
                return;
            }
        }
        if (tvPause.getText().toString().equals("暂停调用")) {
            if (mWorkstate == WorkState.ROADWAITTING ||
                    mWorkstate == WorkState.STATION_AITTING) {
                // initPopView(); //显示下拉框 2016-05-03 xmx 暂时注释
                DealPauseReason();
                return;
            } else {
                Utility.ToastShow(ParameterSetActivity.this,
                        "未在待命状态下，不能进行暂停调用操作！");
                return;
            }
        }
    }
    /**
     * 新增任务 当前分站 暂停或回复调用 更新状态 参数设置的监听
     */
    private OnClickListener svListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            SetView setView = (SetView) v;
            switch (setView.getId()) {
                case R.id.svPause:
                    workStateIsOrNot();//是恢复调用操作 还是暂停操作
                    break;
                case R.id.svSetting:
                    settingOperation();//设置操作
                    break;
                case R.id.svAddTask:
                    NewTasks();//创建任务
                    break;
                case R.id.svFenZhan:
                    TaskStates();
                    break;
                case R.id.svDataUpdate:
                    updateDatas();//更新数据
                    break;
                default:
                    break;

            }
        }
    };
    /**
     * 根据设置判断暂停(恢复)调用
     */
    private void ShowWorkstate() {

        if (mWorkstate == WorkState.ROADWAITTING ||
                mWorkstate == WorkState.STATION_AITTING) // 判断状态 2016-05-05
        {
            tvPause.setText("暂停调用");
            tvPauseReason.setText("");
        }
        if (mWorkstate == WorkState.SUSPEND_INVOKING) {
            tvPause.setText("恢复调用");
            if (!dbHelper.GetPauseReson().equals(""))
                tvPauseReason.setText(dbHelper.GetPauseReson());
            else
                tvPauseReason.setText("");
        }
    }

    /**
     * 初始化UI控件
     */
    private void initUI() {
        mAddTask = (SetView) findViewById(R.id.svAddTask);//新建任务
        mFenZhan = (SetView) findViewById(R.id.svFenZhan);//当前分站
        tvFenzhan = mFenZhan.tvReason;//选中那个当前分站
        mPause = (SetView) findViewById(R.id.svPause);//暂停调用
        tvPause = mPause.tvInfor;//暂停(恢复)调用标签
        tvPauseReason = mPause.tvReason;//暂停原因
        mDataUpdate = (SetView) findViewById(R.id.svDataUpdate);//更新数据库
        mSetting = (SetView) findViewById(R.id.svSetting);//设置
          tvFenzhan.setText(dbHelper.GetStation());
        // 返回按钮
        btSetback = (Button) findViewById(R.id.btSetback);

    }

    // 初始化定义弹出下拉框 2016-05-04 肖明星
    private void initPopView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.radiogroup_gridview, null);
        popView = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        popView.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.lhtopbar));
        popView.setFocusable(false);
        popView.setOutsideTouchable(true);
        popView.setAnimationStyle(android.R.style.Animation_Dialog);
        popView.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popView.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // if (jilu == 0) {
        popView.showAsDropDown(mPause);
        // }
    }

    // 获取分站 2016-05-05
    private void GetStationThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetStation();
            }
        }).start();
    }

    // 获取分站 2016-05-05
    private void GetStation() {
        Message m = new Message();
        String ret = CustomerHttpClient.getInstance().GetStation(
                Utility.dinfo.getAmbulanceCode());
        if (ret.equals("网络异常")) {
            m.what = FenZhanGetStatus.GET_FAIL;
            m.obj = "网络异常";// 网络异常，获取失败
            handler.sendMessage(m);
            return;
        }
        if ((!ret.equals("")) && (!ret.equals("[]"))) {
            m.what = FenZhanGetStatus.GET_SUCCESS; // 获取成功
            m.obj = ret;
        } else {
            m.what = FenZhanGetStatus.GET_FAIL; // 获取失败
            m.obj = ret;
        }
        handler.sendMessage(m);
    }

    // 2016-05-05
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FenZhanGetStatus.GET_SUCCESS: // 获取分站成功
                    DealStationAmb(msg.obj.toString()); // 处理车辆信息 肖明星
                    break;
                case FenZhanGetStatus.GET_FAIL: // 获取分站失败
                    Utility.ToastShow(ParameterSetActivity.this,
                            "获取分站失败！原因：" + msg.obj.toString());
                    break;
                case FenZhanGetStatus.ALTER_SUCCESS: // 修改分站成功
                    Utility.ToastShow(ParameterSetActivity.this, "修改分站成功！");
                    stationName = ""; // 清空分站名称
                    listPadCSInfo.clear(); // 清空获取到的分站的实体
                    tvFenzhan.setText(msg.obj.toString());
                    break;
                case FenZhanGetStatus.ALTER_FAIL: // 修改分站失败
                    Utility.ToastShow(ParameterSetActivity.this, msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };

    // 处理分站信息 2015-05-31 肖明星
    public void DealStationAmb(String str) {
        try {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<NPadStationInfo>>() {
            }.getType();
            listPadCSInfo = gson.fromJson(str, type);

            List<String> ret = new ArrayList<String>();
            for (int i = 0; i < listPadCSInfo.size(); i++) {
                ret.add(listPadCSInfo.get(i).getName()); // 循环添加
            }
            final String[] str1 = CommonUtils.getDic(ret);
            if (listPadCSInfo != null) // 判断取出的车辆实体是否为空，如果不为空则弹出提示框
            {
                CreatDialig(str1, CreateDialogsLIst.ALTER_FENZHAN);
            } else {
                Utility.ToastShow(ParameterSetActivity.this, "未取到分站信息");
            }
        } catch (Exception ex) {
            Utility.ToastShow(ParameterSetActivity.this, "解析分站数据出错");
        }
    }

    // 分站弹出框 2015-06-04 肖明星
    private void CreatDialig(final String[] str1, final int type) {
        if (type == CreateDialogsLIst.ALTER_FENZHAN) {//修改当前分站列表
            title = "分站列表";
        }
        if (type == CreateDialogsLIst.PAUSE_REASON) {//暂停调用原因
             title = "请选择暂停调用原因";
        }
        CustomDialogs dialogs = new CustomDialogs(this, str1, type, title);
        dialogs.setDialogsInterface(new CustomDialogs.DialogsInterface() {
            @Override
            public void dealDIalogs(int type, String which) {
                stationName = which;
                if (type == CreateDialogsLIst.ALTER_FENZHAN) {//修改当前分站列表

                    if (!stationName.equals("")) {
                        Thread thread = new Thread(new ModifyStation()); // 重启一个线程处理车载的绑定
                        // 肖明星
                        thread.start();
                    } else {
                        Utility.ToastShow(ParameterSetActivity.this,
                                "请选择要修改的分站");
                    }
                }
                if (type == CreateDialogsLIst.PAUSE_REASON) {//暂停调用原因
                    if (!stationName.equals("")) {
                        // Looper curLooper = Looper.getMainLooper();
                        code = getcode(stationName, listPadCSInfo);
                        myHandler.sendEmptyMessage(InvokingStaus.PAUSE);
                        listPadCSInfo.clear(); // 清空获取到的分站的实体
                        ParameterSetActivity.this.finish();
                    } else {
                        Utility.ToastShow(ParameterSetActivity.this,
                                "请选择暂停调用原因");
                    }
                }
            }
        });

    }

    // 处理修改分站 2016-05-05 xmx
    class ModifyStation implements Runnable {
        @Override
        public void run() {
            Message m = new Message();
            NPadChangeStationInfo info = getStationInfo(stationName,
                    listPadCSInfo);
            // TODO Auto-generated method stub
            String ret = CustomerHttpClient.getInstance().RecoverStation(info);
            if (ret.equals("success")) // 判断收到的回复是否是"success"，如果是则车辆获取失败
            {
                m.what = FenZhanGetStatus.ALTER_SUCCESS;
                m.obj = stationName;
                dbHelper.SaveStation(info.getStationCode(), stationName);
            } else {
                m.what = FenZhanGetStatus.ALTER_FAIL;
                m.obj = "修改分站失败：" + ret;
            }
            handler.sendMessage(m);
        }
    }

    // 通过分站名称找到分站编码 2015-06-01 肖明星
    public NPadChangeStationInfo getStationInfo(String ret,
                                                List<NPadStationInfo> list) {
        NPadChangeStationInfo info = new NPadChangeStationInfo();
        for (NPadStationInfo npsInfo : list) {
            if (npsInfo.getName().equals(ret)) {
                info.setStationCode(npsInfo.getCode().toString());
                info.setAmbCode(Utility.dinfo.getAmbulanceCode());
            }
        }
        return info;
    }

    public String getcode(String name, List<NPadStationInfo> list) {
        String code = "";
        for (NPadStationInfo npsInfo : list) {
            if (npsInfo.getName().equals(name)) {
                code = npsInfo.getCode().toString();
            }
        }
        return code;
    }

    // 处理暂停调用 2016-05-09 xmx
    public void DealPauseReason() {
        listPadCSInfo = dbHelper.GetPauseReason();
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < listPadCSInfo.size(); i++) {
            ret.add(listPadCSInfo.get(i).getName()); // 循环添加
        }
        final String[] str1 = CommonUtils.getDic(ret);
        if (listPadCSInfo != null) // 判断取出的车辆实体是否为空，如果不为空则弹出提示框
        {
            CreatDialig(str1, CreateDialogsLIst.PAUSE_REASON);
        } else {
            Utility.ToastShow(ParameterSetActivity.this, "未取到暂停调用原因信息");
        }
    }


}

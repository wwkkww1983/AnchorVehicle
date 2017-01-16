package com.anke.vehicle.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anke.vehicle.R;
import com.anke.vehicle.application.MyApplication;
import com.anke.vehicle.comm.CustomerHttpClient;
import com.anke.vehicle.comm.TestClient;
import com.anke.vehicle.comm.TestClientIntHandler;
import com.anke.vehicle.comm.TestClientIntHandler.OnMessageListener;
import com.anke.vehicle.database.DBHelper;
import com.anke.vehicle.entity.AmbPauseReasonInfo;
import com.anke.vehicle.entity.AmbStopTaskInfo;
import com.anke.vehicle.entity.BaiduToGpsInfo;
import com.anke.vehicle.entity.BindAmbInfo;
import com.anke.vehicle.entity.CommandInfo;
import com.anke.vehicle.entity.DictionaryInfo;
import com.anke.vehicle.entity.DisplayInfo;
import com.anke.vehicle.entity.DutyInfo;
import com.anke.vehicle.entity.GPSInfo;
import com.anke.vehicle.entity.HistoryInfo;
import com.anke.vehicle.entity.NPadAccdintInfo;
import com.anke.vehicle.entity.NPadAmbPerInfo;
import com.anke.vehicle.entity.NPadIntervalInfo;
import com.anke.vehicle.entity.NPadModifyBandAmbInfo;
import com.anke.vehicle.entity.NPadPerInfo;
import com.anke.vehicle.entity.NPadStationInfo;
import com.anke.vehicle.entity.NoticeInfo;
import com.anke.vehicle.entity.PADInfo;
import com.anke.vehicle.entity.PadAmbInfo;
import com.anke.vehicle.entity.ParameterInfo;
import com.anke.vehicle.entity.UpdateManager;
import com.anke.vehicle.receiver.GrayService;
import com.anke.vehicle.receiver.MyJobService;
import com.anke.vehicle.receiver.MyReceiver;
import com.anke.vehicle.status.ConnectStatus;
import com.anke.vehicle.status.CreateDialogsLIst;
import com.anke.vehicle.status.InvokingStaus;
import com.anke.vehicle.status.ServerStatus;
import com.anke.vehicle.status.TrueOrFalseStatus;
import com.anke.vehicle.status.UpdataBDList;
import com.anke.vehicle.status.WorkState;
import com.anke.vehicle.status.msgProcessList;
import com.anke.vehicle.utils.CommonUtils;
import com.anke.vehicle.utils.CustomDialogs;
import com.anke.vehicle.utils.UpDateDB;
import com.anke.vehicle.utils.Utility;
import com.anke.vehicle.utils.ZDYDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;

/**
 * 程序入口
 */
public class MainActivity extends Activity {
    private Button mapButton;// 地图按钮
    private TextView tvAlarmtel;// 呼救电话
    private TextView tvLinktel;// 联系电话
    private Button btState;// 连接状态
    private TextView tvState;// 车辆状态命令单界面
    private TextView tvStatemain; // 车辆状态主界面
    private RelativeLayout mStatelly;// 改变状态按钮
    private RelativeLayout mStatellynew;// 改变状态按钮
    private TextView tvContent;// 命令单内容
    private Button btSetting;// 设置按钮
    private Button btonoffduty;// 上下班按钮
    private Button bthistory;// 历史记录
    private LinearLayout llycenter;
    private RelativeLayout rlycommand;
    private TextView tvcentername;//默认北京120急救中心
    private TextView tvambname;//默认指挥1
    private TextView tvvison;//默认V3.0
    private Button bthome;
    private TextView tvNext;
    private TextView tv_tishi;
    private ImageView imgnext;
    private Button btStop; // 暂停按钮 2016-05-03 xmx
    private Button btTell;
    private RelativeLayout rltitle; // 2016-05-03 xmx
    private RelativeLayout rlState; // 状态模块 2016-05-04
    private LinearLayout rlAmbPer; // 随车人员模块 2016-05-04
    private TextView tvSiji; // 司机
    private TextView tvYisheng; // 医生
    private TextView tvHushi; // 护士
    private LinearLayout llmain; // 整个主界面模块 2016-05-04 xmx
    private Button btaccdint; // 上传事故 2016-05-30 xmx
    private EditText etaccdint;
    private String accdint; // 事故 2016-05-31 xmx
    private boolean m_isline = false;
    private String m_TaskCode = "";
    private String m_AmbCode = "";
    public static int mWorkstateID = WorkState.CANNOT_INVOKING;// 工作状态
    public static int mTaskOrder = 0;// 任务序号
    private Thread thread = null;// 线程
    private double m_X = 0;// 经度
    private double m_Y = 0;// 纬度
    private String m_Addr = "";// 现场地址
    private LocationClient mLocationClient;
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    MyHandler mHandler = null;
    private NotificationManager mNotificationManager;
    private Notification notification;
    private CharSequence contentTitle = "通知栏标题";
    private CharSequence contentText = "通知栏内容";
    private CharSequence tickerText;
    private Intent notificationIntent;
    private Context context;
    private int icon;
    private long when;
    private PendingIntent contentIntent;
    private List<NPadPerInfo> ppInfo = new ArrayList<NPadPerInfo>();
    private Dialog dl;
    private DBHelper dbHelper;
    //    private ProgressDialog d;
    private AlertDialog d;
    private DisplayInfo dInfo;
    private NPadIntervalInfo ninfo = new NPadIntervalInfo();
    double latitude = 0;//维度
    double longitude = 0;//经度
    // 是否是按下"修改绑定车辆"按钮
    boolean IsAlterAmb = false; // 用来判断是要上下班还要修改绑定的车辆 肖明星 2015-06-10
    // 肖明星
    // 2015-06-10
    String shijibiaoshiString; // 实际标识
    private List<PadAmbInfo> listPadAmbInfo;
    private BroadcastReceiver mNetworkStateReceiver;
    boolean success;//判断是否有网
    BroadcastReceiver receiver;
    PowerManager pm;
    PowerManager.WakeLock wakeLock = null;
    private ChannelHandlerContext ctx = null;
    private byte[] result2 = new byte[2048]; // 缓存服务端传过来的数据
    private int length = 0; // 记录缓存包的长度
    public int mactive = 0; // 心跳指数 2015-11-06 肖明星
    private Boolean isSuccess = false; // 是否转实体成功
    private AlarmManager am; // 时钟
    private PendingIntent pi;
    public static final int ELAPSED_TIME = 180 * 1000;// 时钟频率
    private boolean Is_connect = false;// 已经连接过
    private String bindAmb = ""; // 记录绑定的车辆 2016-5-6 xmx
    private EditText etcheliang;
    private GJAdapter gja; // 自定义适配器 2016-5-06 xmx
    private List<DictionaryInfo> Dinfos; // 2016-5-06 xmx
    private PopupWindow popView; // 弹出下拉框 2016-5-06 xmx
    private ListView listView; // 车牌号list 2016-5-06 xmx
    private List<NPadStationInfo> listPadCSInfo;
    private String stopReasonName = ""; // 终止任务原因名称
    private int gettype = -1;
    private String taskorder = ""; // 记录最新命令单的任务流水号 2016-05-11
    private Boolean is_lianjie = false; // 判断程序是否连接过
    private List<HistoryInfo> hinfos;
    private static final int ALLOW_BIND = 2;
    private static final int HEARTS = 2;
    private static final int UPDATE_DB = 6;//更新数据库信息
    public static final int SEND2SERVER = 3;//向服务器发消息
    private CustomDialogs dialogs;
    private AlertDialog alertDialog;
    private MyApplication myApplication;
    private Intent grayIntent;
    public static boolean isreConnc = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();//初始化UI控件
        is_lianjie = false;
           GrayService.GrayInnerService.isLive = true;
        CommonUtils.ShowDBExist(this);//确定手机中有数据库，不存在就把raw里的数据库写入手机
        dbHelper = new DBHelper(this);
        ParameterSetActivity.myHandler = myHandlerPause; // 2016-05-05 xmx
        TestClient.myHandler = myHandlerConnect; // 2016-06-21 xmx
        PreferenceParamActivity.handler = myHandlerConnect;//设置完ip端口号尝试重连
        CommonUtils.LoadSound(this);//加载声音
        // 绑定监听函数 2015-05-15 肖明星
        myApplication = (MyApplication) getApplication();
        mLocationClient = myApplication.mLocationClient;
        myApplication.setCallBack(new BDListener());//设置绑定监听事件
        btnAllPressed();//所有buttn按钮设置点击事件
        Utility.getParamAndVersionInfo(this);//得到版本名 版本号
        InitServerConfig();//得到服务器配置信息
        NoticeInit();//初始化notification基本信息

        InitAmbState();//初始化状态，默认不能用
        Register();//注册网络状态广播
        mStatelly.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                StatellyFormation();//改变按钮状态
            }
        });

        // 暂时注释，启动程序时不连服务端 2016-05-06 xmx
        // 是否允许绑定车辆，允许绑定车辆的话就直接连服务端
        if (dbHelper.GetParameter().getIsBindCar() == TrueOrFalseStatus.TRUE) {
            Looper curLooper = Looper.getMainLooper();
            mHandler = new MyHandler(curLooper);
            Message m = mHandler.obtainMessage(ServerStatus.RECONNRCTING, ALLOW_BIND, 0, "");
            mHandler.sendMessage(m); // 连接服务端
        }
        RegisterAlarm();//注册时钟
        //高于android5.0用下面进程保活
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            grayIntent = new Intent(getApplicationContext(), MyJobService.class);
            startService(grayIntent);
        }else{//地狱Android5.0用以下保活
            Intent intent = new Intent(getApplicationContext(), GrayService.class);
            startService(intent);
        }

    }

    /**
     * 所有按钮点击事件
     */
    private void btnAllPressed() {
        btonoffduty.setOnClickListener(listener);
        bthistory.setOnClickListener(listener);
        btSetting.setOnClickListener(listener);
        mapButton.setOnClickListener(listener);
        bthome.setOnClickListener(listener);
        btStop.setOnClickListener(listener);
        btaccdint.setOnClickListener(listener);
        tvAlarmtel.setOnClickListener(tvlistener);
        tvLinktel.setOnClickListener(tvlistener);
    }

    /**
     * 改变按钮状态
     */
    private void StatellyFormation() {
        if (mWorkstateID > WorkState.ASSIGNING_TASKS && mWorkstateID < WorkState.STATION_AITTING) {
            if (Utility.isFastClick()) {//状态按钮5秒内不能重复按
                Utility.ToastShow(this, "5秒内请不要连续按键");
            } else if (success) {
                mWorkstateID++;
                CommonUtils.ringSound(1);//声音提示
                sendMsg(Utility.GetDateTime(), msgProcessList.STATE_SYNCH, mTaskOrder, mWorkstateID);
            } else {
                Utility.ToastShow(this, "客户端断网，请检查网络!");
            }
        }
    }

    /**
     * 是否显示 终止 告知 事故按钮
     */
    public void showOrHiden() {
        //用于隐藏事故和终止两项功能
        if (mWorkstateID > WorkState.ARRIVED) {
            btaccdint.setVisibility(View.GONE);
            if (alertDialog != null) {
                alertDialog.dismiss();
                alertDialog = null;
            }
            btStop.setVisibility(View.GONE);
            if (dialogs != null) {
                dialogs.alertDialog.dismiss();
                dialogs = null;
            }
        } else {
            if (dbHelper.GetParameter().getIsShiGu() == TrueOrFalseStatus.TRUE) {
                btaccdint.setVisibility(View.VISIBLE);
            } else {
                btaccdint.setVisibility(View.GONE);
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
            }
            if (dbHelper.GetParameter().getIsStopTask() == TrueOrFalseStatus.TRUE) {
                btStop.setVisibility(View.VISIBLE);
            } else {
                btStop.setVisibility(View.GONE);
                if (dialogs != null) {
                    dialogs.alertDialog.dismiss();
                    dialogs = null;
                }
            }
        }
        //告知功能
        hinfos = dbHelper.GetHistory();//获取刷新数据
        if (dbHelper.GetParameter().getIsGaoZhi() == TrueOrFalseStatus.TRUE) {  //根据配置是否显示告知
            if (mWorkstateID == WorkState.GETON) {
                btTell.setVisibility(View.VISIBLE);
                btTell.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Gson gson = new Gson();
                        String htInfo = gson.toJson(hinfos.get(0));
                        Intent intentup = new Intent();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("historyInfo", htInfo);
                        bundle1.putString("taskcode", hinfos.get(0).getTaskcode());
                        bundle1.putInt("workstate", mWorkstateID);
                        bundle1.putString("taskorder", taskorder);
                        bundle1.putString("ambcode", hinfos.get(0).getAmbcode());
                        intentup.putExtras(bundle1);
                        intentup.setClass(MainActivity.this, UpLoad.class);
                        startActivityForResult(intentup, 1);
                    }
                });

                if (!hinfos.get(0).getHospital().equals("")
                        || !hinfos.get(0).getIll().equals("")
                        || !hinfos.get(0).getJudge().equals("")) {
                    btTell.setBackgroundResource(R.drawable.ys_style_1);
                } else {
                    btTell.setBackgroundResource(R.drawable.ws_style_1);
                }
            } else {
                btTell.setVisibility(View.GONE);
                MyApplication.destoryActivity("UpLoad");
            }
        } else {
            btTell.setVisibility(View.GONE);
            MyApplication.destoryActivity("UpLoad");
        }
    }

    /**
     * 处理事故
     */
    private void btnAccdint() {
        // 弹出框的确认按钮 2016-05-03 xmx
        if (mWorkstateID > WorkState.ASSIGNING_TASKS && mWorkstateID < WorkState.ROADWAITTING) {
            final View view;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            builder.setTitle("请填写事故内容！"); // 标题
            view = getLayoutInflater().inflate(R.layout.accdint, null);
            builder.setPositiveButton("上传",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            if (!etaccdint.getText().toString().equals("")) {
                                accdint = etaccdint.getText().toString();
                                NPadAccdintInfo npainfo = new NPadAccdintInfo();
                                npainfo.setContent(etaccdint.getText().toString());
                                npainfo.setTaskCode(taskorder);
                                Gson gson = new Gson();
                                String jsonStr = gson.toJson(npainfo);
                                sendMsg(jsonStr, 8, mTaskOrder, mWorkstateID); // 发送
                                CommonUtils.dialogSty(arg0, true);
                            } else {
                                CommonUtils.dialogSty(arg0, false);
                                Utility.ToastShow(MainActivity.this, "请填写事故内容！");
                            }
                        }
                    });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CommonUtils.dialogSty(dialog, true);
                }
            });
            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);//点击dialog以外的区域没反应
            alertDialog.show();
//            dl = builder.show();
            etaccdint = (EditText) view.findViewById(R.id.etAccdint);
//            List<HistoryInfo> lhinfo = new ArrayList<HistoryInfo>();
            List<HistoryInfo> lhinfo = dbHelper.GetHistory();
            for (int i = 0; i < lhinfo.size(); i++) // 给事故赋值 2016-05-31
            {
                if (lhinfo.get(i).getTaskcode().toString()
                        .equals(taskorder)) {
                    if (!lhinfo.get(i).getShiGu().toString().equals(""))
                        etaccdint.setText(lhinfo.get(i).getShiGu());
                }
            }
        } else
            Utility.ToastShow(MainActivity.this, "未在任务中，不能进行上传事故操作");
    }

    /**
     * 初始化各种UI控件
     */
    private void initUI() {
        tvSiji = (TextView) findViewById(R.id.tvSiji);
        tvYisheng = (TextView) findViewById(R.id.tvYisheng);
        tvHushi = (TextView) findViewById(R.id.tvHushi);
        mapButton = (Button) findViewById(R.id.gpsbutton);//百度地图客户端按钮
        // pttButton = (Button) findViewById(R.id.pttbutton);
        btState = (Button) findViewById(R.id.btState);//在线 掉线
        btSetting = (Button) findViewById(R.id.btSetting);//右上角设置
        tvState = (TextView) findViewById(R.id.tvState);//收到命令单
        tvStatemain = (TextView) findViewById(R.id.tvStatemain); // 当前状态主界面
        tvAlarmtel = (TextView) findViewById(R.id.tvAlarmtel);//呼救电话
        tvLinktel = (TextView) findViewById(R.id.tvLinktel);//联系电话
        mStatelly = (RelativeLayout) findViewById(R.id.llystatebt);
        mStatellynew = (RelativeLayout) findViewById(R.id.llystate);
        tvContent = (TextView) findViewById(R.id.textView1);
        btonoffduty = (Button) findViewById(R.id.btonoffduty);
        bthistory = (Button) findViewById(R.id.bthistory);
        bthome = (Button) findViewById(R.id.bthome);
        rltitle = (RelativeLayout) findViewById(R.id.rltitle); // 2016-05-04
        llmain = (LinearLayout) findViewById(R.id.llmain); // 2016-05-04 xmx
        rlycommand = (RelativeLayout) findViewById(R.id.rlycommand); // 2016-05-04
        rlState = (RelativeLayout) findViewById(R.id.rlState); // 2016-05-04
        rlAmbPer = (LinearLayout) findViewById(R.id.rlAmbPer); // 2016-05-04
        llycenter = (LinearLayout) findViewById(R.id.llycenter);
        tvcentername = (TextView) findViewById(R.id.tvcentername);
        tvambname = (TextView) findViewById(R.id.tvambname);
        tvvison = (TextView) findViewById(R.id.tvvison);
        tvNext = (TextView) findViewById(R.id.tvNext);
        tv_tishi = (TextView) findViewById(R.id.tv_tishi);
        imgnext = (ImageView) findViewById(R.id.imgnext);
        btTell = (Button) findViewById(R.id.bt_tell);
        btStop = (Button) findViewById(R.id.btstop); // 终止任务功能 2016-05-04 xmx
        btaccdint = (Button) findViewById(R.id.btaccdint);
    }


    /**
     * 得到服务器配置 ip 端口等信息
     */
    private void InitServerConfig() {
        ParameterInfo info = dbHelper.GetParameter();
        if (info != null) {
            Utility.setPhone(info.getTelphone());
            Utility.setServerIp(info.getIpaddress());
            Utility.setPort(info.getPort());
            Utility.setHttpport(info.getHttpport());
            Utility.setVersionUrl(info.getVersionUrl());
            Utility.setApkUrl(info.getApkUrl());
            Utility.setIslocation(info.getIslocation()); // 暂时注释 2016-04-18
            Utility.setIsBindCar(info.getIsBindCar()); // 2016-05-11
            Utility.setIsStopTask(info.getIsStopTask());
            Utility.setIsChangStation(info.getIsChangStation());
            Utility.setIsNewTask(info.getIsNewTask());
            Utility.setIsPauseCall(info.getIsPauseCall());
            Utility.setIsGaoZhi(info.getIsGaoZhi()); // 是否告知 2016-05-17 xmx
            Utility.setIsShouFei(info.getIsShouFei()); // 是否收费
            Utility.setIsShiGu(info.getIsShiGu()); // 是否上传事故 2016-05-30
        }

        dInfo = dbHelper.GetCenter();
        if (dInfo != null) {
            Utility.dinfo.setAmbDeskName(dInfo.getAmbDeskName());
            Utility.dinfo.setAmbulanceCode(dInfo.getAmbulanceCode());
            Utility.dinfo.setCenterName(dInfo.getCenterName());
        }

        ShowCenterName(1);
        ninfo = dbHelper.GetGpsParam();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();
        // 监听黑屏亮屏,定位重连操作。 费晓波 2015 9-14
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Do your action here
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    if (mWorkstateID < WorkState.ROADWAITTING)
                        ChangeDingWei(ninfo.TaskInterval);
                    else
                        ChangeDingWei(ninfo.StationInterval);
                }
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

                    if (mWorkstateID < WorkState.ROADWAITTING)
                        ChangeDingWei(ninfo.TaskInterval);
                    else
                        ChangeDingWei(ninfo.StationInterval);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.setPriority(2147483647);
        registerReceiver(receiver, filter);
        ChangeDingWei(ninfo.StationInterval); // 判断定位
    }

    // 初始化百度定位 设置参数 2015-05-15 肖明星
    private void InitLocation(int time) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);// 设置定位模式
        option.setCoorType("bd09ll");// 设置定位坐标返回方式
        option.setScanSpan(time * 1000);// 定位时间间隔为10秒
        option.setIsNeedAddress(false);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * 显示或者隐藏收到通知单等一些信息
     *
     * @param sign
     */
    private void ShowCenterName(int sign) {
        if (sign == 1) {
            tvcentername.setText(Utility.dinfo.getCenterName());
            String s1 = Utility.dinfo.getAmbDeskName();
            tvambname.setText(Utility.dinfo.getAmbDeskName());
            tvvison.setText("V" + Utility.versionName);
            rlycommand.setVisibility(View.GONE);
            llycenter.setVisibility(View.VISIBLE);
            rlState.setVisibility(View.VISIBLE); // 2016-05-04
            rlAmbPer.setVisibility(View.VISIBLE);
            rltitle.setBackgroundResource(R.drawable.title);
            tvState.setVisibility(View.GONE);
            llmain.setBackgroundResource(R.drawable.backgroud);
            mStatelly.setVisibility(View.GONE);
            mStatellynew.setVisibility(View.VISIBLE);
            btStop.setVisibility(View.GONE);
            btaccdint.setVisibility(View.GONE);
            m_Addr = "";
            m_X = 0;
            m_Y = 0;
        } else {

            rlycommand.setVisibility(View.VISIBLE);
            mStatelly.setVisibility(View.VISIBLE);
            mStatellynew.setVisibility(View.GONE);
            tvState.setVisibility(View.VISIBLE);
            rlState.setVisibility(View.GONE); // 2016-05-04
            rlAmbPer.setVisibility(View.GONE);
            llycenter.setVisibility(View.GONE);
            llmain.setBackgroundColor(Color.WHITE); // 背景设置成白色
            rltitle.setBackgroundResource(R.drawable.topnew);
        }
    }

    /**
     * 初始化状态，默认不能用
     */
    private void InitAmbState() {
        mWorkstateID = WorkState.CANNOT_INVOKING;
        mTaskOrder = 0;
        tvState.setText("不能调用");
        tvState.setTextColor(Color.RED);
        tvStatemain.setText("不能调用");
        tvStatemain.setTextColor(Color.GRAY);
    }

    /**
     * 初始化notification基本信息
     */
    private void NoticeInit() {
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        icon = R.drawable.msg; // 通知图标
        when = System.currentTimeMillis(); // 通知产生的时间，会在通知信息里显示
        context = getApplicationContext(); // 上下文
        notificationIntent = new Intent(MainActivity.this, MainActivity.class); // 点击该通知后要跳转的Activity
        contentIntent = PendingIntent.getActivity(MainActivity.this, 0,
                notificationIntent, 0);
    }

    private void ShowNotice(String title, String msg) {
        if (!Utility.isTopActivity(MainActivity.this))// 当前页面不处于激活状态
        {
            tickerText = title;
            contentTitle = tickerText;
            contentText = msg;
            if (tickerText != null) {
                notification = new Notification(icon, tickerText, when);

                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.setLatestEventInfo(context, contentTitle,
                        contentText, contentIntent);
                mNotificationManager.notify(0, notification);
            }
        }
    }


    public class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        /**
         * 处理心跳包
         *
         * @param msg
         */
        public void handleMessage(Message msg) {

            if (msg.what == HEARTS) {
                if (Is_connect == true || is_lianjie == true) {
                    if (mactive < 1) { // 判断心跳次数是否小于1 2016-01-18 xmx
                        sendMsg("*#01", msgProcessList.HEARTBEAT, mTaskOrder, mWorkstateID); // 发心跳
                        mactive++;
                    } else {
                        OutLine(); // 掉线操作
                        if (dbHelper.GetParameter().getIsBindCar() == TrueOrFalseStatus.TRUE) {
                            ConnectSever(); // 重连
                            mactive = 0; // 重连心跳清零
                        } else {
                            if (!bindAmb.equals("")) {
                                ConnectSever(); // 重连
                                mactive = 0; // 重连心跳清零
                            }
                        }
                    }
                }
            }

            // 向通服发消息 2016-05-05 xmx
            if (msg.what == SEND2SERVER) {
                if (msg.arg1 == InvokingStaus.PAUSE) {
                    AmbPauseReasonInfo apsinfo = new AmbPauseReasonInfo();
                    apsinfo.setPauseReasonId(Integer.valueOf(msg.obj.toString()).intValue());
                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(apsinfo);
                    sendMsg(jsonStr, msgProcessList.ABMINF, mTaskOrder, mWorkstateID); // 发送
                }
                if (msg.arg1 == InvokingStaus.RECOVER) {
                    sendMsg("", 7, mTaskOrder, mWorkstateID); // 发送
                }
            }

            if (msg.what == ServerStatus.SERVER_OR_Client_EXCEPTION) {//后台服务器异常或者客户端网络异常走这一步
                if (ctx != null){
                    ctx.close();

                }

                if (dbHelper.GetParameter().getIsBindCar() == TrueOrFalseStatus.FAlSE) { // 判断是否绑定车辆
                    // 2016-06-22
                    // xmx
                    if (!bindAmb.equals("") && success) { // 只有车辆绑定了才能连接
                        ConnectSever();
                    }
                } else {
                    if (success)
                        ConnectSever();
                }

            }

            // 发送失败重连
            if (msg.what == ServerStatus.RECONNRCTING) {
                if (msg.arg1 == 1) {
                    if (msg.arg2 == ConnectStatus.CONNECT_FAIL) {//连接失败
                        Is_connect = true;
                        OutlineDeal(); // 设置断线状态
                    }
                    if (msg.arg2 == ConnectStatus.RECEIVER) // 处理消息
                    {
                        // serverLines = 0;//清空服务器异常连接次数
                        LineDeal();
                        msgProcess(msg.obj.toString()); // 处理数据
                    }
                    if (msg.arg2 == ConnectStatus.CONNECT_SUCCESS) // 连接成功
                    {
                        Is_connect = true;
                        sendMsg(Utility.GetDateTime(), msgProcessList.STATE_SYNCH, mTaskOrder, mWorkstateID);//同步状态
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendMsg("*#01", msgProcessList.HEARTBEAT, mTaskOrder, mWorkstateID); // 发心跳
                    }
                }
                if (msg.arg1 == ALLOW_BIND) {   // 是否允许绑定车辆，允许绑定车辆的话就直接连服务端
                    ConnectSever(); // 连接服务器
                }
                if (msg.arg1 == UPDATE_DB)// 更新数据库信息
                {
                    switch (msg.arg2) {
                        case UpdataBDList.GET_AMB_FAIL:
                            dealGetAmbFail(msg);//处理获取失败
                            break;
                        case UpdataBDList.DEAL_BIND_AMB: // 处理绑定车辆
                            dealBindAmb(msg);
                            break;
                        case UpdataBDList.BIND_AMB_FAIL: // 绑定车辆失败
                            Utility.ToastShow(MainActivity.this, msg.obj.toString());
                            break;
                        default:
                            if (d != null && d.isShowing())
                                d.dismiss();
                            break;
                    }
                }
            }
        }
    }

    /**
     * 处理获取失败
     *
     * @param msg
     */
    private void dealGetAmbFail(Message msg) {
        if (msg.obj.toString().equals("获取车辆信息失败")) {
            Utility.ToastShow(MainActivity.this, "获取车辆信息失败,请检查网络");
        }
        if (msg.obj.toString().equals("[]")) {
            Utility.ToastShow(MainActivity.this, "没有未上班的车辆！");
        } else {
            DealDutyAmb(msg.obj.toString()); // 处理车辆信息 肖明星
        }
        d.dismiss();
    }

    /**
     * 处理绑定车辆
     *
     * @param msg
     */
    private void dealBindAmb(Message msg) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(msg.obj.toString());
            String AmbCodeString = Utility.GetJsonValue("AmbCode",
                    jsonObject);
            String AmbRealSignString = Utility.GetJsonValue(
                    "AmbRealSign", jsonObject);
            String StationCodeString = Utility.GetJsonValue(
                    "StationCode", jsonObject);
            String StationNameString = Utility.GetJsonValue(
                    "StationName", jsonObject);
            Utility.dinfo.setAmbulanceCode(AmbCodeString);
            Utility.dinfo.setAmbDeskName(AmbRealSignString);
            Utility.dinfo.setStationCode(StationCodeString);
            Utility.dinfo.setStationName(StationNameString);
            dbHelper.SaveCenter(AmbRealSignString, AmbCodeString,
                    Utility.dinfo.getCenterName(),
                    StationCodeString, StationNameString);
            tvambname.setText(AmbRealSignString); // 设置界面修改
            bindAmb = AmbRealSignString;
            //Toast.makeText(MainActivity.this, "绑定车辆成功！", Toast.LENGTH_LONG).show();
            Utility.ToastShow(MainActivity.this, "绑定车辆成功！");
            // 绑定车辆成功后开始连服务端 2016-05-06
            if (!bindAmb.equals("")) {
                ConnectSever();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 上下班及返回人员信息
     *
     * @param info
     */
    private void OnOffDuty(final NPadAmbPerInfo info) {
        DutyAdapter daAdapter = new DutyAdapter();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        if (info.getPPInfo() != null) {
            View view = getLayoutInflater().inflate(R.layout.onduty, null);
            final ListView lvlhlist = (ListView) view.findViewById(R.id.lvlhlist);
            final EditText etfasong = (EditText) view.findViewById(R.id.etfasong);
            final Button btfasong = (Button) view.findViewById(R.id.btfasong);
            final Button btrefrash = (Button) view.findViewById(R.id.btrefrash);
            ppInfo = info.getPPInfo();
            if (!ppInfo.isEmpty())
                lvlhlist.setAdapter(daAdapter);
            btfasong.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {//上班按钮
                    // TODO Auto-generated method stub
                    if (ctx != null) {
                        if (!"".equals(etfasong.getText().toString().trim())) {
                            DutyInfo info = new DutyInfo();
                            info.setIsOnDuty(true);
                            info.setM_GpsTime(Utility.GetDateTime());
                            info.setM_PersonCode(etfasong.getText().toString());
                            Gson gson = new Gson();
                            String jsonStr = gson.toJson(info);
                           // SPUtils.putSP(MainActivity.this,"onWork","1");//兼容苏州
                            sendMsg(jsonStr, msgProcessList.NOTICE_LIST, mTaskOrder, mWorkstateID);
                            etfasong.setText("");
                            dl.dismiss();
                        } else {
//                            Toast.makeText(MainActivity.this, "工号不能为空!",
//                                    Toast.LENGTH_LONG).show();
                            Utility.ToastShow(MainActivity.this, "工号不能为空!");
                        }
                    }
                }
            });

            btrefrash.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    dl.dismiss();
                    gettype = 1;
                    IsAlterAmb = true;
                    sendMsg("", msgProcessList.PERSONINF, mTaskOrder, mWorkstateID);
                    d = CommonUtils.createProgressDialog(MainActivity.this, "Loading...");
                }
            });

            builder.setNegativeButton("关闭",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface d, int arg1) {
                            // TODO Auto-generated method stub
                            d.dismiss();
                        }
                    });
            builder.setView(view);
            dl = builder.show();
        }
    }

    /**
     * 处理车辆信息 2015-05-31 肖明星
     *
     * @param str
     */
    public void DealDutyAmb(String str) {
        try {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<PadAmbInfo>>() {
            }.getType();
            listPadAmbInfo = gson.fromJson(str, type);

            List<String> ret = new ArrayList<String>();
            for (int i = 0; i < listPadAmbInfo.size(); i++) {
                ret.add(listPadAmbInfo.get(i).GetRealSign()); // 循环添加
            }
            final String[] str1 = CommonUtils.getDic(ret);
            if (listPadAmbInfo != null) // 判断取出的车辆实体是否为空，如果不为空则弹出提示框
            {
                CreatDialig(str1);
            } else {
                Utility.ToastShow(MainActivity.this, "未取到车辆信息");
            }
        } catch (Exception ex) {
            Utility.ToastShow(MainActivity.this, "解析车辆数据出错");
        }
    }

    /**
     * 关于没有绑定Pad的弹出框 2015-06-04 肖明星
     *
     * @param str1
     */
    private void CreatDialig(final String[] str1) {
        AlertDialog.Builder creatDialog = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        creatDialog.setTitle("请选择需要上班的车辆");
        creatDialog.setSingleChoiceItems(str1, -1,
                new DialogInterface.OnClickListener() // 把循环添加的实体作为选项
                {
                    public void onClick(DialogInterface dialog, int whichcountry) {
                        shijibiaoshiString = str1[whichcountry]; // 获取实际标识
                    }
                });
        creatDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        d.dismiss();
                    }
                });

        creatDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialoginterface, int i) {
                        // TODO Auto-generated method stub
                        thread = new Thread(new BindAmb()); // 重启一个线程处理车载的绑定
                        // 肖明星
                        thread.start();
                    }
                });
        creatDialog.create().show();
    }


    class DutyAdapter extends BaseAdapter {
        public int getCount() {
            // TODO Auto-generated method stub
            return ppInfo.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return ppInfo.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        // 弹出上下班菜单 2015-05-20 肖明星
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) { // 这句就是重用的关键
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.ondutyitemnew, null);
            }
            final TextView tvgonghao = (TextView) convertView.findViewById(R.id.tvgonghao);
            final TextView tvtype = (TextView) convertView.findViewById(R.id.tvtype);
            final TextView tvname = (TextView) convertView.findViewById(R.id.tvname);
            final Button btoffduty = (Button) convertView.findViewById(R.id.btoffduty);
            tvgonghao.setText(ppInfo.get(position).getWorkCode());
            tvtype.setText(ppInfo.get(position).getType());
            tvname.setText(ppInfo.get(position).getName());
            btoffduty.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {//下班按钮
                    // TODO Auto-generated method stub
                    if (ctx != null) {
                        if (!"".equals(ppInfo.get(position).getWorkCode())) {
                            DutyInfo info = new DutyInfo();
                            info.setIsOnDuty(false);
                            info.setM_GpsTime(Utility.GetDateTime());
                            info.setM_PersonCode(ppInfo.get(position).getWorkCode());
                            Gson gson = new Gson();
                            String jsonStr = gson.toJson(info);
//                            SPUtils.putSP(MainActivity.this,"outwork","2");//兼容苏州
                            sendMsg(jsonStr, msgProcessList.NOTICE_LIST, mTaskOrder, mWorkstateID);
                            dl.dismiss();
                        }
                    } else {
//                        Toast.makeText(MainActivity.this, "未登陆服务器,发送失败!",
//                                Toast.LENGTH_LONG).show();
                        Utility.ToastShow(MainActivity.this, "未登陆服务器,发送失败!");
                    }
                }
            });
            convertView.setTag(position);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                }
            });
            return convertView;
        }
    }

    // 适配器 2015-11-06 肖明星
    class GJAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return Dinfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return Dinfos.get(arg0);
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
                convertView = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.knowitem, null);
            }
            final TextView tvtitle = (TextView) convertView.findViewById(R.id.tvknow);
            final String title = Dinfos.get(position).getName();
            tvtitle.setText(title);
            convertView.setTag(position);

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (title != null && title != "") {
                        etcheliang.setText(title);
                        etcheliang.setSelection(title.length());
                        if (popView != null)
                            popView.dismiss();
                    }
                }
            });
            return convertView;
        }
    }

    /**
     * 修改了关于提醒下一状态的提示 肖明星
     *
     * @param workstateid
     * @param taskOrder
     */
    public void StateChange(int workstateid, int taskOrder) {//改变车载状态
        // 定位时间判断 2016-6-22 xmx
        if (mWorkstateID < WorkState.ROADWAITTING) // 判断当前状态
        {
            if (workstateid > WorkState.ARRIVED_HOSPITAL) // 判断要同步的状态
                ChangeDingWei(ninfo.StationInterval);
        }
        if (mWorkstateID > WorkState.ARRIVED_HOSPITAL) {
            if (workstateid < WorkState.ROADWAITTING)
                ChangeDingWei(ninfo.TaskInterval);
        }
        //以下代码是为了兼容苏州，苏州上下班返回值有时正常，有时异常
//        String result1 = SPUtils.getSP(MainActivity.this,"onWork");
//        String result2 = SPUtils.getSP(MainActivity.this,"outwork");
//                    if (!TextUtils.isEmpty(result1)){
//                        workstateid = 7;
//                        SPUtils.remove(MainActivity.this,"onWork");
//
//                    }
//                    if (!TextUtils.isEmpty(result2)){
//                        workstateid = 8;
//                        SPUtils.remove(MainActivity.this,"outwork");
//                    }
        //以上兼容苏州
        mWorkstateID = workstateid;
        mTaskOrder = taskOrder;
        switch (mWorkstateID) {

            case WorkState.RECEIVER_INVOKING:
                tvState.setText("收到指令");
                tvStatemain.setText("收到指令");
                tvState.setTextColor(Color.RED);
                tvNext.setText("驶向现场");
                tvNext.setTextColor(getResources().getColor(R.color.yellownew));
                ShowCenterName(0);
                showOrHiden();
                sendMsg(Utility.GetDateTime(), msgProcessList.STATE_SYNCH, mTaskOrder, mWorkstateID);
                break;

            case WorkState.GOING:
                tvState.setText("驶向现场");
                tvStatemain.setText("驶向现场");
                tvState.setTextColor(getResources().getColor(R.color.yellownew));
                ShowCenterName(0);
                showOrHiden();
                tvNext.setText("到达现场");
//                tvNext.setText("抢救转送");
                tvNext.setTextColor(getResources().getColor(R.color.pink));
                break;

            case WorkState.ARRIVED:
                tvState.setText("到达现场");
                tvStatemain.setText("到达现场");
//                tvState.setText("抢救转送");
//                tvStatemain.setText("抢救转送");
                tvState.setTextColor(getResources().getColor(R.color.pink));
                ShowCenterName(0);
                showOrHiden();

                tvNext.setText("病人上车");
//                tvNext.setText("离开现场"); //无锡病人上车 改成 离开现场
                tvNext.setTextColor(Color.MAGENTA);
                // mStatelly.setBackgroundDrawable(getResources().getDrawable(
                // R.drawable.state_style));
                break;

            case WorkState.GETON:
                tvState.setText("病人上车");
                tvStatemain.setText("病人上车");//无锡病人上车 改成 离开现场
//                tvState.setText("离开现场");
//                tvStatemain.setText("离开现场");
                tvState.setTextColor(Color.MAGENTA);
                tvNext.setText("到达医院");
                tvNext.setTextColor(getResources().getColor(R.color.bluegreen));
                ShowCenterName(0);
                showOrHiden();
                break;

            case WorkState.ARRIVED_HOSPITAL:
                tvState.setText("到达医院");
                tvStatemain.setText("到达医院");
                tvState.setTextColor(getResources().getColor(R.color.bluegreen));
                ShowCenterName(0);
                showOrHiden();
                tvNext.setText("途中待命");
                tvNext.setTextColor(Color.BLUE);
                break;

            case WorkState.ROADWAITTING:
                tvState.setText("途中待命");
                tvState.setTextColor(Color.BLUE);
                tvStatemain.setText("途中待命");
                tvStatemain.setTextColor(Color.BLUE);
                ShowCenterName(1);
                tvNext.setText("站内待命");
                tvNext.setTextColor(getResources().getColor(R.color.green));
                showOrHiden();
                mStatelly.setVisibility(View.VISIBLE);
                mStatellynew.setVisibility(View.GONE);
                break;

            case WorkState.STATION_AITTING:
                tvState.setText("站内待命");
                tvStatemain.setText("站内待命");
                tvStatemain.setTextColor(getResources().getColor(R.color.green));
                ShowCenterName(1);
                showOrHiden();
                tvState.setTextColor(Color.GREEN);
                break;

            case WorkState.CANNOT_INVOKING:
                tvState.setText("不能调用");
                tvStatemain.setText("不能调用");
                tvStatemain.setTextColor(Color.GRAY);
                ShowCenterName(1);
                showOrHiden();
                tvState.setTextColor(Color.RED);
                break;

            case WorkState.SUSPEND_INVOKING:
                tvState.setText("暂停调用");
                tvStatemain.setText("暂停调用");
                tvStatemain.setTextColor(Color.RED);
                ShowCenterName(1);
                showOrHiden();
                tvState.setTextColor(Color.GRAY);
                break;

            default:
                break;
        }
    }

    // 离线情况处理
    public void OutlineDeal() {
        btState.setBackgroundResource(R.drawable.land_offline_style);
        m_isline = false;

    }

    // 在线情况处理
    public void LineDeal() {
        btState.setBackgroundResource(R.drawable.land_online_style);
        m_isline = true;
    }

    /**
     * 处理联系电话 和呼叫电话拨打
     */
    private OnClickListener tvlistener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            TextView tv = (TextView) v;
            switch (tv.getId()) {
                case R.id.tvAlarmtel:
                    String telalarm = tvAlarmtel.getText().toString();
                    Intent intentalarm = new Intent();
                    intentalarm.setAction(Intent.ACTION_CALL);
                    telalarm = "tel:" + telalarm;
                    intentalarm.setData(Uri.parse(telalarm));
                    startActivity(intentalarm);
                    break;

                case R.id.tvLinktel:
                    String tellink = tvLinktel.getText().toString();
                    Intent intentlink = new Intent();
                    intentlink.setAction(Intent.ACTION_CALL);
                    tellink = "tel:" + tellink;
                    intentlink.setData(Uri.parse(tellink));
                    startActivity(intentlink);
                    break;

                default:
                    break;
            }
        }
    };
    /**
     * 所有的button按钮点击事件的处理
     */
    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Button bt = (Button) v;
            switch (bt.getId()) {
                case R.id.gpsbutton:// 转到地图页面 //2014-04-28
                    if (mWorkstateID < WorkState.ROADWAITTING) {
                        LatLng pt1 = new LatLng(latitude, longitude);
                        LatLng pt2 = Utility.Gps84ToBaidu(m_X, m_Y);
                        // 初始化导航
                        NaviParaOption para = new NaviParaOption();
                        para.startPoint(pt1);
                        para.startName("开始地点");
                        para.endPoint(pt2);
                        para.endName("结束地点");
                        try {
                            if (isInstallByread("com.baidu.BaiduMap"))
                                BaiduMapNavigation.openBaiduMapNavi(para,
                                        v.getContext());
                            else {
                                String str = "您尚未安装百度地图app或app版本过低，" +
                                        "请升级或安装百度地图后再进行导航！";
                                // Dialog(str);
                                CommonUtils.Dialog(MainActivity.this, str);
                            }
                        } catch (BaiduMapAppNotSupportNaviException e) {
                            e.printStackTrace();
                            String str = "跳转百度地图出错！原因：" + e.toString();
//                            Dialog(str);
                            CommonUtils.Dialog(MainActivity.this, str);
                        }
                    } else {
                        // 调起百度地图客户端
                        try {
                            Intent intent = Intent
                                    .getIntent("intent://map/#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                            if (isInstallByread("com.baidu.BaiduMap")) {
                                startActivity(intent); // 启动调用
                            } else {
                                String str = "您尚未安装百度地图app或app版本过低，" +
                                        "请升级或安装百度地图后再进行导航！";
//                                Dialog(str);
                                CommonUtils.Dialog(MainActivity.this, str);
                            }
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                            String str = "跳转百度地图出错！原因：" + e.toString();
                            CommonUtils.Dialog(MainActivity.this, str);
                        }
                    }
                    break;

                case R.id.btSetting:// 设置按钮
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ParameterSetActivity.class);
                    intent.putExtra("workstate", mWorkstateID); // 跳转界面时传工作状态
                    // 2016-05-04 xmx
                    startActivity(intent);
                    break;

                case R.id.btonoffduty:// 返回随车人员信息 //2014-04-28

                    if (dbHelper.GetParameter().getIsBindCar() == TrueOrFalseStatus.FAlSE) {
                        IsAlterAmb = true;
                        gettype = 1;
                        if (bindAmb.equals("")) {
                            SelectOndutyAmb();//选择上班车辆
                        } else {
                            if (m_isline) {//在线状态
                                if (ctx != null) {
                                    d = CommonUtils.createProgressDialog(MainActivity.this, "Loading...");
                                    sendMsg("", msgProcessList.PERSONINF, mTaskOrder, mWorkstateID);
                                }
                            } else {//离线状态
                                if (d != null) {
                                    d.dismiss();
                                }
                                Utility.ToastShow(MainActivity.this, "未连接服务器，请重连服务器！");
                            }
                        }
                    } else {
                        if (m_isline) {
                            if (ctx != null) {
                                IsAlterAmb = true;
                                gettype = 1;
                                d = CommonUtils.createProgressDialog(MainActivity.this, "Loading...");
                                sendMsg("", msgProcessList.PERSONINF, mTaskOrder, mWorkstateID);
                            }
                        } else {
                            if (d != null) {
                                d.dismiss();
                            }
                            Utility.ToastShow(MainActivity.this, "未连接服务器，请重连服务器！");
                        }
                    }

                    break;

                case R.id.bthistory:// 查看历史记录 //2014-04-28
                    Intent intenthis = new Intent();
                    intenthis.setClass(MainActivity.this, HistoryActivity.class);
                    intenthis.putExtra("taskorder", taskorder);
                    intenthis.putExtra("workstate", mWorkstateID);
                    startActivity(intenthis);
                    break;

                case R.id.bthome: // 2014-04-28
                    Intent intentkl = new Intent();
                    intentkl.setClass(MainActivity.this, KnowledgeActivity.class);
                    startActivity(intentkl);
                    break;
                case R.id.btstop://终止任务
                    DealStopTask();
                    break;
                case R.id.btaccdint://上传事故
                    btnAccdint();
                    break;
                default:
                    break;
            }
        }
    };

    public void Exit() {
        ZDYDialog zdyDialog = new ZDYDialog(this, "您确定要退出吗?", false);
        zdyDialog.setZdYinterface(new ZDYDialog.ZDYinterface() {
            @Override
            public void btnOkonClick(int type, String inpust) {
                if (type == 2) {
                    setResult(RESULT_OK);
                    if (mNotificationManager != null)
                        mNotificationManager.cancelAll();
                    MainActivity.this.finish();
                }

            }
        });
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWorkstateID < WorkState.ROADWAITTING && m_isline) { // 判断是否在任务中 2016-05-10 肖明星
                Utility.ToastShow(MainActivity.this, "任务中不能退出！");
                return false;
            }
            if (dbHelper.GetParameter().getIsBindCar() == TrueOrFalseStatus.FAlSE) {
                if (mWorkstateID > WorkState.ARRIVED_HOSPITAL &&
                        mWorkstateID < WorkState.CANNOT_INVOKING ) { // 判断是否有人员未退出2016-05-10
                    // xmx
                    Utility.ToastShow(MainActivity.this, "请将所有的人员下班后再退出程序！");
                    return false;
                }
            }
//            Exit(); // 退出
            moveTaskToBack(false);

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mNetworkStateReceiver != null)
            unregisterReceiver(mNetworkStateReceiver); // 取消监听
        if (receiver != null)
            unregisterReceiver(receiver); // 取消监听
        thread = null;
        if (wakeLock != null)
            wakeLock.release();// 释放电源锁
        if (am != null && pi != null)
            am.cancel(pi);
        if (mLocationClient.isStarted())
            mLocationClient.stop();
        super.onDestroy();
        System.exit(0);
    }


    // 创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "系统更新").setIcon(
                android.R.drawable.stat_sys_download);
        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "重新连接").setIcon(
                android.R.drawable.stat_notify_sync);
//        menu.add(Menu.NONE, Menu.FIRST + 3, 3, "更新数据").setIcon(
//                android.R.drawable.stat_sys_download_done);
//        menu.add(Menu.NONE, Menu.FIRST + 4, 4, "修改绑定车辆").setIcon(
//                android.R.drawable.ic_menu_myplaces); // 德州暂时隐藏 //青岛不隐藏
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 点击显示是否在线的按钮可弹出菜单
     * 此点击事件是在activity_main.xml文件中设置的
     *
     * @param v
     */
    public void menuButtun(View v) {
        if (v.getId() == R.id.btState) {
            openOptionsMenu();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                if (mWorkstateID < WorkState.ROADWAITTING) {
                    Utility.ToastShow(MainActivity.this, "任务中不能更新程序！");
                } else {
                    UpdateManager um = new UpdateManager(MainActivity.this);
                    um.checkUpdateInfo();
                }
                break;

            case Menu.FIRST + 2:
                if (success) { // 判断是否有网络 2016-06-03 xmx
                    if (!m_isline && ctx == null) {
                        if (dbHelper.GetParameter().getIsBindCar() == TrueOrFalseStatus.FAlSE) { // 判断是否绑定车辆
                            // 2016-05-02
                            // xmx
                            if (!bindAmb.equals("")) { // 只有车辆绑定了才能连接
                                ConnectSever();
                            } else {
                                Utility.ToastShow(MainActivity.this, "未绑定车辆，请绑定车辆后再进行连接！");
                            }
                        } else
                            ConnectSever();
                    } else {
                        Utility.ToastShow(MainActivity.this, "已经连接，不用重连");
                    }
                } else {
                    Utility.ToastShow(MainActivity.this, "连接失败！没有网络！");
                }
                break;

            case Menu.FIRST + 3:
                d = CommonUtils.createProgressDialog(this, "正在更新基础数据信息...");
                new UpDateDB(MainActivity.this, dbHelper, d);
//                Thread thread1 = new Thread(new UpdateHospital()); // 启动一个线程
//                thread1.start();
                break;

            case Menu.FIRST + 4:
                sendMsg("", msgProcessList.PERSONINF, mTaskOrder, mWorkstateID); // 因为要去判断当前的Pad上是否还有上班的人员
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 1) {
            if (resultCode == 2) {
                /**
                 * 告知状态显示
                 */
                hinfos = dbHelper.GetHistory();
                if (mWorkstateID == WorkState.GETON && (
                        !hinfos.get(0).getHospital().equals("")
                                || !hinfos.get(0).getIll().equals("")
                                || !hinfos.get(0).getJudge().equals("")
                )
                        ) {
                    btTell.setBackgroundResource(R.drawable.ys_style_1);
                } else {
                    btTell.setBackgroundResource(R.drawable.ws_style_1);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // 往调度台发送车载位置信息
    private void SendGps(BDLocation location) {
        GPSInfo info = new GPSInfo();
        try {
            BaiduToGpsInfo binfo = Utility.BaiduToGps84(
                    location.getLongitude(), location.getLatitude());
            info.setDir(0);
            info.setY(binfo.getY());
            info.setX(binfo.getX());
            SimpleDateFormat tempDate = new SimpleDateFormat("HH:mm:ss");
            String datetime = tempDate.format(new java.util.Date());
            info.setGpsTime(datetime);
            info.setHeight(0);
            info.setSpeed(new Double(String.valueOf(location.getSpeed())));
            Gson gson = new Gson();
            String jsonStr = gson.toJson(info);
            sendMsg(jsonStr, msgProcessList.COMMAND_LIST, mTaskOrder, mWorkstateID);
        } catch (Exception e) {
            // TODO: handle exception
            //          Log.d("转换GPS信息出错", e.toString());
        }
    }

    // 处理查询没有绑定车载的Pad 2015-06-03 肖明星
    class QueryUnBindAmb implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            String str = "车辆获取失败！";
            String ret = CustomerHttpClient.getInstance().QueryUnBindAmb();
            if ((!ret.trim().equals("")) && (!ret.trim().equals("null"))
                    && (!ret.trim().contains("Post"))
                    && (!ret.trim().contains("[]"))) {
                str = ret;
            }
            Looper curLooper = Looper.getMainLooper();
            mHandler = new MyHandler(curLooper);
            Message m = mHandler.obtainMessage(ServerStatus.RECONNRCTING, UPDATE_DB,
                    UpdataBDList.GET_AMB_FAIL, str);//11
            mHandler.sendMessage(m);
        }
    }

    // 处理Pad绑定车载 2015-06-09 肖明星
    class BindAmb implements Runnable {
        @Override
        public void run() {
            BindAmbInfo info = CommonUtils.getAmbInfo(shijibiaoshiString, listPadAmbInfo);
            // TODO Auto-generated method stub
            String str = "车辆获取失败";
            String ret = CustomerHttpClient.getInstance().BindAmb(info);
            if (!(ret.trim().equals("")) == true) // 判断收到的回复是否是“”，如果是则车辆获取失败
            {
                str = ret;
            }
            Looper curLooper = Looper.getMainLooper();
            mHandler = new MyHandler(curLooper);
            Message m = mHandler.obtainMessage(ServerStatus.RECONNRCTING, UPDATE_DB,
                    UpdataBDList.DEAL_BIND_AMB, str);//
            mHandler.sendMessage(m);
            shijibiaoshiString = ""; // 清空实际标识
            listPadAmbInfo.clear(); // 清空获取到的没有绑定Pad的车载的实体
        }
    }


    // 根据工作状态不同判断定位时间
    public void ChangeDingWei(int Interval) {
        if (ninfo != null) {
            if (mLocationClient != null && mLocationClient.isStarted()) {
                mLocationClient.stop();
            }
            InitLocation(Interval);
        }
    }

    // 状态可点击，更新此时状态
    public void onClickUpdate(View v) {
        if (v.getId() == R.id.tvState) {
            if (!sendMsg(Utility.GetDateTime(), msgProcessList.STATE_SYNCH, MainActivity.mTaskOrder, MainActivity.mWorkstateID)) {
                Looper curLooper = Looper.getMainLooper();
                mHandler = new MyHandler(curLooper);
                Message m = mHandler.obtainMessage(ServerStatus.RECONNRCTING, 1, ConnectStatus.CONNECT_FAIL, "");
                mHandler.sendMessage(m);
            }
        }
    }


    public void msgProcess(Object s) { // 处理消息

        try {
            SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datetime = tempDate.format(new java.util.Date());
            String contentString = s.toString().trim();
            PADInfo info = CommonUtils.getbBaotou(contentString);//
//            Log.e("Pad信息",info.toString());
            Utility.setTrue(true);
            switch (info.getType()) {
                case msgProcessList.HEARTBEAT:// 收到心跳
                    mactive = 0;
                    //StateChange(info.getWorkStateID(), info.getTaskOrder());
                    if (isreConnc){
                        isreConnc = false;
                        IsAlterAmb = true;
                        gettype = 2;
                        sendMsg("", msgProcessList.PERSONINF, mTaskOrder, mWorkstateID);

                    }

                    break;

                case msgProcessList.STATE_SYNCH:// 状态同步
                    StateChange(info.getWorkStateID(), info.getTaskOrder());
                    gettype = 2;
                    IsAlterAmb = true;
                    sendMsg("", msgProcessList.PERSONINF, mTaskOrder, mWorkstateID);
                    break;

                case msgProcessList.COMMAND_LIST:// 命令单
                    Gson gson = new Gson();
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<CommandInfo>() {
                    }.getType();
                    CommandInfo cinfo = gson.fromJson(info.getContent().toString(), type);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("发单时间：" + cinfo.getSendTime() + "\n");
                    stringBuilder.append(" " + cinfo.getContent());
                    if (cinfo.isIsLabeled())// 是否标注
                    {
                        m_X = cinfo.getX();
                        m_Y = cinfo.getY();
                    } else {
                        m_X = 0;
                        m_Y = 0;
                    }
                    m_AmbCode = cinfo.getAmbCode();
                    m_TaskCode = cinfo.getTaskCode();
                    m_Addr = cinfo.getExtension();// 分机号没有用实际是现场地址
                    tvAlarmtel.setText(cinfo.getAlarmTel());
                    tvLinktel.setText(cinfo.getLinkTel());
                    tvContent.setText(stringBuilder);
                    ShowNotice("收到命令单", "呼救电话：" + cinfo.getAlarmTel()); // 界面显示
                    CommonUtils.ringSound(3);
                    taskorder = cinfo.getTaskCode();
                    HistoryInfo hinfo = new HistoryInfo();
                    hinfo.setHistype("命令单");
                    hinfo.setHistime(cinfo.getSendTime());
                    hinfo.setHiscontent(cinfo.getContent());
                    hinfo.setAlarmtel(cinfo.getAlarmTel());
                    hinfo.setLinktel(cinfo.getLinkTel());
                    hinfo.setAmbcode(cinfo.getAmbCode());
                    hinfo.setTaskcode(cinfo.getTaskCode());
                    dbHelper.SaveHistory(hinfo);
                    StateChange(info.getWorkStateID(), info.getTaskOrder());
                    // 判断终止任务按钮是否可用 2016-06-08 xmx

                    List<HistoryInfo> lhinfo = dbHelper.GetHistory();
                    for (int i = 0; i < lhinfo.size(); i++) // 给事故赋值 2016-05-31
                    {
                        if (lhinfo.get(i).getTaskcode().toString()
                                .equals(taskorder)) {
                            if (!lhinfo.get(i).getShiGu().toString().equals("")) {
                                btStop.setEnabled(false);
                                btStop.setBackgroundResource(R.drawable.stopenable);
                            } else {
                                btStop.setEnabled(true);
                                btStop.setBackgroundResource(R.drawable.stop_style);
                            }
                        }
                    }
                    break;

                case msgProcessList.NOTICE_LIST:// 通知单
//                    StateChange(info.getWorkStateID(), info.getTaskOrder());

                    ShowNotice("收到通知单", "通知内容：" + info.getContent().toString());
                    if (info.getContent().equals("你所在的车辆已经暂停调用")) {
                        dbHelper.SetPauseReson(
                                ParameterSetActivity.code,
                                getCodeByCode(ParameterSetActivity.code,
                                        dbHelper.GetPauseReason()));
                    }
                    if (info.getContent().equals("上报成功")) {
                        dbHelper.SaveShiGu(accdint, taskorder);
                        accdint = "";
                        btStop.setEnabled(false);
                        btStop.setBackgroundResource(R.drawable.stopenable);
                    } else {
                        btStop.setEnabled(true);
                        btStop.setBackgroundResource(R.drawable.stop_style);
                    }
                    CommonUtils.ringSound(2);
                    NoticeInfo ninfos = new NoticeInfo();
                    ninfos.setHistype("通知单");
                    ninfos.setHistime(datetime);
                    ninfos.setHiscontent(info.getContent().toString());
                    dbHelper.SaveHisNotice(ninfos);
                    View view = getLayoutInflater().inflate(R.layout.notice, null);
                    final TextView tvnotice = (TextView) view.findViewById(R.id.tvnotice);
                    final TextView nttime = (TextView) view.findViewById(R.id.nttime);
                    tvnotice.setText("    " + info.getContent().toString());
                    nttime.setText(datetime);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    builder.setTitle("收到通知单");
                    builder.setNegativeButton("取消", null);
                    builder.setView(view);
                    builder.show();
                    gettype = 2;
                    IsAlterAmb = true;
//                    Log.e("通知单：" ,mWorkstateID+"");


                    sendMsg("", msgProcessList.PERSONINF, mTaskOrder, mWorkstateID);
                    break;

                case msgProcessList.PERSONINF:// 请求返回人员信息
                    StateChange(info.getWorkStateID(), info.getTaskOrder());
                    Gson gson1 = new Gson();
                    java.lang.reflect.Type type1 = new com.google.gson.reflect.TypeToken<NPadAmbPerInfo>() {
                    }.getType();
                    NPadAmbPerInfo ainfo = gson1.fromJson(info.getContent()
                            .toString(), type1);
                    if (IsAlterAmb) {
                        if (gettype == 1) { // 判断获取人员信息的方式 2016-05-10
                            if (ainfo != null) {
                                if (ainfo.getSuccess()) {
                                    OnOffDuty(ainfo);
                                } else {
                                    Utility.ToastShow(MainActivity.this, ainfo.getRemark());
                                }
                            }
                        }
                        if (gettype == 2) {//处理界面上的人员信息
                            if (ainfo != null) {
                                if (ainfo.getSuccess()) {
                                    DealPerson(ainfo);
                                } else {
                                    Utility.ToastShow(MainActivity.this, ainfo.getRemark());
                                }
                            }
                        }
                    } else {
                        if (!(ainfo.getRemark().toString().equals("没有人员在本车上班"))) {
                            AlertDialog.Builder bd = new AlertDialog.Builder(
                                    MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                            bd.setTitle("提示");
                            bd.setMessage("人员有未下班的，请在人员全部下班后在进行修改绑定车!");
                            bd.setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method
                                            // stub
                                            dialog.dismiss();
                                        }
                                    });
                            bd.create().show();
                        } else {
                            d = CommonUtils.createProgressDialog(this, "正在获取车辆信息...");
                            thread = new Thread(new QueryUnBindAmb());// 启动一个线程
                            // 肖明星
                            thread.start();
                        }
                    }
                    if (d != null)
                        d.dismiss();
                    IsAlterAmb = false;
                    gettype = -1;
                    break;

                case msgProcessList.SHOW_NAME:// 显示名称

                    Gson gson2 = new Gson();
                    java.lang.reflect.Type type2 = new com.google.gson.reflect.TypeToken<DisplayInfo>() {
                    }.getType();
                    DisplayInfo dinfo = gson2.fromJson(info.getContent().toString(), type2);
                    if (dinfo != null) {
                        Utility.dinfo.setAmbDeskName(dinfo.getAmbDeskName());
                        Utility.dinfo.setAmbulanceCode(dinfo.getAmbulanceCode());
                        Utility.dinfo.setCenterName(dinfo.getCenterName());
                        Utility.dinfo.setStationCode(dinfo.getStationCode());
                        Utility.dinfo.setStationName(dinfo.getStationName());
                        dbHelper.SaveCenter(dinfo.getAmbDeskName(),
                                dinfo.getAmbulanceCode(), dinfo.getCenterName(),
                                dinfo.getStationCode(), dinfo.getStationName());
                        tvcentername.setText(dinfo.getCenterName());
                        tvambname.setText(dinfo.getAmbDeskName());
                    }
                    break;

                case msgProcessList.ABMINF://得到车载信息
                    Gson gson3 = new Gson();
                    java.lang.reflect.Type type3 = new com.google.gson.reflect.TypeToken<NPadIntervalInfo>() {
                    }.getType();
                    NPadIntervalInfo nPIinfo = gson3.fromJson(info.getContent().toString(), type3);
                    if (nPIinfo != null) {
                        int gpsnum = 0;
                        if (nPIinfo.IsGPS)
                            gpsnum = 1;
                        dbHelper.SaveGpsParam(gpsnum, nPIinfo.TaskInterval,
                                nPIinfo.StationInterval, nPIinfo.OffInterval, nPIinfo.AmbCode);
                        ninfo = nPIinfo;
                        if (ninfo.IsGPS) {
                            dbHelper.SaveParameter(Utility.getServerIp(),
                                    Utility.getPort(), Utility.getHttpport(),
                                    Utility.getPhone(), Utility.getVersionUrl(),
                                    Utility.getApkUrl(), 1, Utility.getIsBindCar(),
                                    Utility.getIsStopTask(),
                                    Utility.getIsChangStation(),
                                    Utility.getIsPauseCall(),
                                    Utility.getIsNewTask(), Utility.getIsGaoZhi(),
                                    Utility.getIsShouFei(), Utility.getIsShiGu());
                        } else {
                            dbHelper.SaveParameter(Utility.getServerIp(),
                                    Utility.getPort(), Utility.getHttpport(),
                                    Utility.getPhone(), Utility.getVersionUrl(),
                                    Utility.getApkUrl(), 0, Utility.getIsBindCar(),
                                    Utility.getIsStopTask(),
                                    Utility.getIsChangStation(),
                                    Utility.getIsPauseCall(),
                                    Utility.getIsNewTask(), Utility.getIsGaoZhi(),
                                    Utility.getIsShouFei(), Utility.getIsShiGu());
                        }
                        if (mWorkstateID < WorkState.ROADWAITTING)
                            ChangeDingWei(ninfo.TaskInterval);
                        else
                            ChangeDingWei(ninfo.StationInterval);
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            if (d != null && d.isShowing())
                d.dismiss();
            //           Log.d("收到通服信息解析出错", e.getMessage());
        }
    }


    // Netty连接线程
    class ClientConnect implements Runnable {
        // @Override
        public void run() {
            final Message m = new Message();
            try {
                TestClient hc = new TestClient();
                TestClientIntHandler hello = new TestClientIntHandler();
                hello.setOnMessageListener(new OnMessageListener() {
                    @Override
                    public void onMessage(ChannelHandlerContext type, int sign,
                                          byte[] result) {
                        // TODO Auto-generated method stub
                        Looper curLooper = Looper.getMainLooper();
                        mHandler = new MyHandler(curLooper);
                        ctx = type;

                        switch (sign) {
                            case ConnectStatus.CONNECT_FAIL: // Netty抛出异常

                                Message m = mHandler.obtainMessage(ServerStatus.RECONNRCTING, 1, ConnectStatus.CONNECT_FAIL, "");
                                mHandler.sendMessage(m);
                                break;

                            case ConnectStatus.RECEIVER: // 接收数据处理
                                mactive = 0;

                                String commandInfo = CommonUtils.TcommandInfo(result);
                                if (!commandInfo.equals("")) {
                                    Message m1 = mHandler.obtainMessage(ServerStatus.RECONNRCTING, 1, ConnectStatus.RECEIVER,
                                            commandInfo);
                                    mHandler.sendMessage(m1);
                                }
                                break;

                            case ConnectStatus.CONNECT_SUCCESS: // 连接成功
                                mactive = 0;
                                Message m2 = mHandler.obtainMessage(ServerStatus.RECONNRCTING, 1, ConnectStatus.CONNECT_SUCCESS, "");
                                mHandler.sendMessage(m2);
                                break;

                            default:
                                break;
                        }
                    }
                });
                hc.connect(Utility.getServerIp(),
                        Integer.valueOf(Utility.getPort()), hello);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                is_lianjie = true;
            } finally {
                ctx = null;
            }
        }
    }

    // 连接服务端
    private void ConnectSever() {
        if (ctx == null) {
            Thread thread = new Thread(new ClientConnect());
            thread.start();
        }
    }

    /**
     * 注册网络状态广播
     */
    private void Register() {
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                success = false;
                // 获得网络连接服务
                ConnectivityManager connManager = (ConnectivityManager)
                        getSystemService(CONNECTIVITY_SERVICE);
                State state = connManager.getNetworkInfo(
                        ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态
                if (State.CONNECTED == state) { // 判断是否正在使用WIFI网络
                    success = true;
                }
                state = connManager.getNetworkInfo(
                        ConnectivityManager.TYPE_MOBILE).getState(); // 获取网络连接状态
                if (State.CONNECTED == state) { // 判断是否正在使用GPRS网络
                    success = true;
                }
                if (success) {
                    if (Is_connect == true || is_lianjie == true) {
                        if (ctx == null) {
                            Is_connect = false;
                            if (dbHelper.GetParameter().getIsBindCar() == TrueOrFalseStatus.TRUE) {
                                ConnectSever(); // 重连
                            } else {
                                if (!bindAmb.equals("")) {
                                    ConnectSever(); // 重连
                                }
                            }
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (mNetworkStateReceiver != null)
            registerReceiver(mNetworkStateReceiver, filter);
    }

    public boolean sendMsg(Object str, int sign, int TaskOrder, int WorkStateID) {
        boolean ret = false;
        try {
            PADInfo info = new PADInfo();
            info.setTelCode(Utility.getPhone());//手机号码发到服务器
            info.setContent(str);
            info.setType(sign);
            info.setTaskOrder(TaskOrder);
            info.setWorkStateID(WorkStateID);
            String contentString = CommonUtils.GetContent(info);
            TestClientIntHandler.sendMsg(ctx, contentString.getBytes());
            ret = true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * 注册时钟 2016-01-18 xmx
     */
    private void RegisterAlarm() {
        // TODO Auto-generated method stub
        Intent intent = new Intent("ELITOR_CLOCK");
        intent.putExtra("msg", "ok");
        pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ELAPSED_TIME, pi);
        MyReceiver.myHandler = myHandler;
    }


    // 通知给主线程
    public Handler myHandler = new Handler() {
        public void handleMessage(final Message msg) {
            if (msg.what == ServerStatus.RECONNRCTING) {
                Looper curLooper = Looper.getMainLooper();
                mHandler = new MyHandler(curLooper);
                Message m = mHandler.obtainMessage(HEARTS, "");
                mHandler.sendMessage(m);
            }
        }
    };

    // 暂停调用发送给主线程 暂时注释 2016-05-04 xmx
    public Handler myHandlerPause = new Handler() {


        public void handleMessage(final Message msg) {
            if (msg.what == InvokingStaus.PAUSE) { // 暂停调用
                Looper curLooper = Looper.getMainLooper();
                mHandler = new MyHandler(curLooper);
                Message m = mHandler.obtainMessage(SEND2SERVER, InvokingStaus.PAUSE, 0, ParameterSetActivity.code);
                mHandler.sendMessage(m);
            }
            if (msg.what == InvokingStaus.RECOVER) { // 恢复调用
                Looper curLooper = Looper.getMainLooper();
                mHandler = new MyHandler(curLooper);
                Message m = mHandler.obtainMessage(SEND2SERVER, InvokingStaus.RECOVER, 0, "");
                mHandler.sendMessage(m);
            }
        }
    };

    // 2016-06-21 xmx
    public Handler myHandlerConnect = new Handler() {
        public void handleMessage(final Message msg) {
            if (msg.what == ServerStatus.RECONNRCTING) { // 重新连接服务端
                Looper curLooper = Looper.getMainLooper();
                mHandler = new MyHandler(curLooper);
                Message m = mHandler.obtainMessage(ServerStatus.SERVER_OR_Client_EXCEPTION, ParameterSetActivity.code
                );

                mHandler.sendMessage(m);
            }
            if (msg.what == 123) {

                Looper curLooper = Looper.getMainLooper();
                mHandler = new MyHandler(curLooper);
                Message m = mHandler.obtainMessage(ServerStatus.SERVER_OR_Client_EXCEPTION);
                mHandler.sendMessage(m);
            }
        }
    };

    // 客户端断开 2016-01-18 xmx
    private void OutLine() {
        if (ctx != null) { // 判断连接是否断开 2016-01-18
            ctx.close();

        }
        OutlineDeal();
    }

    /**
     * 选择上班车辆 2016-5-6 肖明星
     */
    private void SelectOndutyAmb() {
        final View view;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("请选择上班车辆！"); // 标题
        view = getLayoutInflater().inflate(R.layout.bindamb, null);
        builder.setNegativeButton("关闭", null);
        builder.setView(view);
        dl = builder.show();
        etcheliang = (EditText) view.findViewById(R.id.etcheliang);
        final Button btSetBindAmb = (Button) view.findViewById(R.id.btSetBindAmb);
        btSetBindAmb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//
                // TODO Auto-generated method stub
                if (!etcheliang.getText().toString().equals("")) {
                    dl.dismiss();
                    Thread thread = new Thread(new SetBindAmb());
                    thread.start();
                } else {
                    Utility.ToastShow(MainActivity.this, "请填写车牌号！");
                }
            }
        });

        gja = new GJAdapter(); // 给适配器传参，用于下拉框的点击事件 2015-11-05 肖明星
        // 添加编辑框内容改变事件 2015-11-06 肖明星
        etcheliang.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (count > 0) {
                    if (popView != null) {
                        popView.dismiss();
                        popView = null;
                        String temp = etcheliang.getText().toString();
                        Dinfos = dbHelper.getAmbNumber(temp);

                    } else {
                        String temp = etcheliang.getText().toString();
                        Dinfos = dbHelper.getAmbNumber(temp);
                    }

                    if (Dinfos != null && !Dinfos.isEmpty()) {
                        initPopView(etcheliang);
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
        });
    }

    // 处理Pad绑定车载 2016-01-14 肖明星
    class SetBindAmb implements Runnable {
        public void run() {
            // TODO Auto-generated method stub
            Looper curLooper = Looper.getMainLooper();
            mHandler = new MyHandler(curLooper);
            Message m = new Message();
            String str = "车辆获取失败";
            NPadModifyBandAmbInfo npmbaInfo = new NPadModifyBandAmbInfo();
            npmbaInfo.setAmbCode(etcheliang.getText().toString());
            npmbaInfo.setTelCode(Utility.getPhone());
            Gson gson = new Gson();
            String jsonStr = gson.toJson(npmbaInfo);
            String ret = CustomerHttpClient.getInstance().SetBindAmb(jsonStr);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(ret);
                String resultString = Utility.GetJsonValue("Result", jsonObject);
                if (ret.contains("success")) {
                    str = ret;
                    m = mHandler.obtainMessage(ServerStatus.RECONNRCTING, UPDATE_DB,
                            UpdataBDList.DEAL_BIND_AMB, str);//11
                } else {
                    str = "绑定车辆失败！原因：" + ret;
                    m = mHandler.obtainMessage(ServerStatus.RECONNRCTING, UPDATE_DB,
                            UpdataBDList.BIND_AMB_FAIL, str);
                }
                mHandler.sendMessage(m);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // 初始化定义弹出下拉框 2015-11-05 肖明星
    private void initPopView(EditText editText) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.knowlist, null);
        listView = (ListView) view.findViewById(R.id.lvknow);
        listView.setAdapter(gja); // 实例化适配器 2015-08-18 肖明星
        popView = new PopupWindow(view, editText.getWidth(), LayoutParams.MATCH_PARENT);
        popView.setBackgroundDrawable(getResources().getDrawable(R.drawable.lhtopbar));
        popView.setFocusable(false);
        popView.setOutsideTouchable(true);
        popView.setAnimationStyle(android.R.style.Animation_Dialog);
        popView.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popView.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popView.showAsDropDown(editText);
    }

    // 终止按钮 2016-05-09 xmx
    public void DealStopTask() {
        listPadCSInfo = dbHelper.GetStopReason();
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < listPadCSInfo.size(); i++) {
            ret.add(listPadCSInfo.get(i).getName()); // 循环添加
        }
        if (ret.size() != 0) {
            final String[] str1 = CommonUtils.getDic(ret);
            CreatDialig1(str1);

        } else {//数据库中没数据
            Utility.ToastShow(this, "没获取到值，请点击右上角设置按钮，选择数据更新");
            return;
        }


    }

    /**
     * 关于没有绑定Pad的弹出框 2015-06-04 肖明星
     * 生成当前分站或者暂停(恢复)调用列表
     */

    private void CreatDialig1(final String[] str1) {
        dialogs = new CustomDialogs(this, str1, CreateDialogsLIst.PAUSE_TASK, "请选择终止任务原因");
        dialogs.setDialogsInterface(new CustomDialogs.DialogsInterface() {
            @Override
            public void dealDIalogs(int type, String which) {
                if (type == CreateDialogsLIst.PAUSE_TASK) {
                    stopReasonName = which;
                    if (!stopReasonName.equals("")) {
                        String code = getCodeByName(stopReasonName, listPadCSInfo);
                        AmbStopTaskInfo asinfo = new AmbStopTaskInfo();
                        asinfo.setSelectLocal(Integer.valueOf(code).intValue());
                        Gson gson = new Gson();
                        String jsonStr = gson.toJson(asinfo);
                        sendMsg(jsonStr, msgProcessList.SHOW_NAME, mTaskOrder, mWorkstateID); // 发送给命令单
                        stopReasonName = "";
                        listPadCSInfo.clear();
                    } else {
                        Utility.ToastShow(MainActivity.this, "请选择终止任务原因");
                    }
                }
            }
        });
    }

    // 处理界面上的人员信息 2016-05-10 xmx
    private void DealPerson(NPadAmbPerInfo ainfo) {
        String siji = "";
        String yisheng = "";
        String hushi = "";
        for (int i = 0; i < ainfo.getPPInfo().size(); i++) {
            if (ainfo.getPPInfo().get(i).getType().equals("司机")) {
                siji = ainfo.getPPInfo().get(i).getName() + "、";
            }
            if (ainfo.getPPInfo().get(i).getType().equals("医生")) {
                yisheng = ainfo.getPPInfo().get(i).getName() + "、";
            }
            if (ainfo.getPPInfo().get(i).getType().equals("护士")) {
                hushi = ainfo.getPPInfo().get(i).getName() + "、";
            }
        }
        if (!siji.equals("")) {
            siji = siji.substring(0, siji.length() - 1);
        }
        if (!yisheng.equals("")) {
            yisheng = yisheng.substring(0, yisheng.length() - 1);
        }
        if (!hushi.equals("")) {
            hushi = hushi.substring(0, hushi.length() - 1);
        }
        tvSiji.setText(siji);
        tvYisheng.setText(yisheng);
        tvHushi.setText(hushi);
    }

    public String getCodeByName(String name, List<NPadStationInfo> list) {
        String code = "";
        for (NPadStationInfo npsInfo : list) {
            if (npsInfo.getName().equals(name)) {
                code = npsInfo.getCode().toString();
            }
        }
        return code;
    }

    public String getCodeByCode(String code, List<NPadStationInfo> list) {
        String name = "";
        for (NPadStationInfo npsInfo : list) {
            if (npsInfo.getCode().equals(code)) {
                name = npsInfo.getName().toString();
            }
        }
        return name;
    }

    /**
     * 绑定监听
     */
    class BDListener implements MyApplication.ICallBack {
        @Override
        public void onSuccess(BDLocation location) {
            // TODO Auto-generated method stub
            // 不用发GPS暂时注释
            // 通过配置文件来判断是否实时定位
            if (location.getLocType() == 61
                    || location.getLocType() == 161) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                // 往调度台发送位置信息 2015-05-18 肖明星
                if (dbHelper.GetParameter().getIslocation() == TrueOrFalseStatus.TRUE) { // 暂时注释
                    if (success)
                        SendGps(location);
                }
            }
        }
    }
}

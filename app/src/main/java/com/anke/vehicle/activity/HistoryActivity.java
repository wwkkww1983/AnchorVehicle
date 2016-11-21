package com.anke.vehicle.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anke.vehicle.R;
import com.anke.vehicle.comm.CustomerHttpClient;
import com.anke.vehicle.database.DBHelper;
import com.anke.vehicle.entity.HistoryInfo;
import com.anke.vehicle.entity.NPadShoufei;
import com.anke.vehicle.entity.NoticeInfo;
import com.anke.vehicle.utils.CommonUtils;
import com.anke.vehicle.utils.Utility;
import com.anke.vehicle.utils.ZDYDialog;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 历史记录
 */
public class HistoryActivity extends Activity {
    private DBHelper dbHelper;
    private ListView lvhis;
    private Button btmingling;
    private Button bttongzhi;
    private Button hisback;
    private Button hisrefresh; // 刷新 2016-05-09
    private List<HistoryInfo> hinfos;
    private List<NoticeInfo> ninfos;
    private CommandListAdapter ha;
    private NoticeListAdapter na;
    private boolean iscommand = true;
    private int mWorkstate = -1; // 记录工作状态 2016-05-04 xmx
    private String taskorder = ""; // 记录工作状态 2016-05-04 xmx
    private String taskcode = "";  //2016-05-31
    private String shoufei = "";
    private EditText etshoufei;
    MyHandler mHandler = null;
    private AlertDialog d;
//    private ProgressDialog d;
    private static final int REFRESH_COMMANDLIST = 1;//命令单
    private static final int REFRESH_NOTICELIST = 2;//通知单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        dbHelper = new DBHelper(this);
        Intent intent = getIntent();
        mWorkstate = intent.getExtras().getInt("workstate"); // 工作状态
        taskorder = intent.getExtras().getString("taskorder"); // 任务流水号
        initUI();//初始化UI控件
        ha = new CommandListAdapter();//命令单
        na = new NoticeListAdapter();//通知单
        hinfos = dbHelper.GetHistory();
        ninfos = dbHelper.GetHisNotice();
        if (hinfos != null && !hinfos.isEmpty())//默认显示命令单
            lvhis.setAdapter(ha);

        btnAllPressed();//处理点击事件
    }

    /**
     * 处理点击事件
     */
    private void btnAllPressed() {
        btmingling.setOnClickListener(listener);
        bttongzhi.setOnClickListener(listener);
        hisback.setOnClickListener(listener);
        hisrefresh.setOnClickListener(listener);
    }

    /**
     * 初始化UI控件
     */
    private void initUI() {
        lvhis = (ListView) findViewById(R.id.hislist);
        btmingling = (Button) findViewById(R.id.btmingling);
        bttongzhi = (Button) findViewById(R.id.bttongzhi);
        hisback = (Button) findViewById(R.id.hisback);
        hisrefresh = (Button) findViewById(R.id.hisrefresh);
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Button bt = (Button) v;
            switch (bt.getId()) {
                case R.id.btmingling:// 命令单
                    hinfos = dbHelper.GetHistory();
                    if (hinfos != null)
                        lvhis.setAdapter(ha);
                    iscommand = true;
                    bttongzhi.setBackgroundResource(R.drawable.tongzhidan);
                    btmingling.setBackgroundResource(R.drawable.minglingdan1);
                    break;

                case R.id.bttongzhi:// 通知单
                    ninfos = dbHelper.GetHisNotice();
                    if (ninfos != null)
                        lvhis.setAdapter(na);
                    iscommand = false;
                    bttongzhi.setBackgroundResource(R.drawable.tongzhidan1);
                    btmingling.setBackgroundResource(R.drawable.minglingdan);
                    break;

                case R.id.hisback:// 返回
                    onBackPressed();
                    break;

                case R.id.hisrefresh:
                    if (iscommand)
                        refash(REFRESH_COMMANDLIST);//刷新命令单
                    else
                        refash(REFRESH_NOTICELIST);//刷新通知单
                    break;

                // case R.id.btdelete:
                // new AlertDialog.Builder(HistoryActivity.this)
                // .setTitle("是否清除所有历史记录")
                // .setPositiveButton("确定",
                // new DialogInterface.OnClickListener()
                // {
                // @Override
                // public void onClick(DialogInterface dialog,
                // int which)
                // {
                // dbHelper.DeleteHisNotice();
                // if (iscommand)
                // refash(1);
                // else
                // refash(2);
                // }
                // }).setNegativeButton("取消", null).show();
                // break;
                default:
                    break;
            }

        }
    };

    private void refash(int type) {
        if (type == 1) {
            hinfos = dbHelper.GetHistory();
            if (hinfos != null)
                lvhis.setAdapter(ha);
        } else {
            ninfos = dbHelper.GetHisNotice();
            if (ninfos != null)
                lvhis.setAdapter(na);
        }
    }

    class CommandListAdapter extends BaseAdapter {



        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return hinfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return hinfos.get(arg0);
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
                convertView = LayoutInflater.from(HistoryActivity.this)
                        .inflate(R.layout.historyitem, null);
            }
            final TextView tvhistype = (TextView) convertView
                    .findViewById(R.id.tvhisType);
            final TextView tvtime = (TextView) convertView
                    .findViewById(R.id.tvhistime);
            final TextView tvcontent = (TextView) convertView
                    .findViewById(R.id.tvhiscontent);
            final Button btshoufei = (Button) convertView
                    .findViewById(R.id.bt_shoufei);

            if (dbHelper.GetParameter().getIsShouFei() == 1)  //根据配置是否显示收费  2016-05-17 xmx
            { btshoufei.setVisibility(View.VISIBLE);}
            else
            { btshoufei.setVisibility(View.GONE);}

            tvhistype.setText(hinfos.get(position).getHistype().toString());
            tvtime.setText(hinfos.get(position).getHistime().toString());
            tvcontent.setText(hinfos.get(position).getHiscontent().replaceAll("\n", "  "));
            convertView.setTag(position);

            if (!hinfos.get(position).getShoufei().equals("")) {
                btshoufei.setText("已 收");
                btshoufei.setBackgroundResource(R.drawable.ys_style);
            } else {
                btshoufei.setText("未 收");
                btshoufei.setBackgroundResource(R.drawable.ws_style);
            }

            btshoufei.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    // 收费上传 2016-05-11 xmx
                    final View view;
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    builder.setTitle("请填写收费！"); // 标题
                    view = getLayoutInflater().inflate(R.layout.shoufei, null);
                    builder.setPositiveButton("上传",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int arg1) {
                                    // TODO Auto-generated method stub
                                    if (!etshoufei.getText().toString().equals("")) {
                                        Thread thread = new Thread(
                                                new Uploadshoufei(etshoufei.getText().toString(),
                                                        hinfos.get(position).getAmbcode(),
                                                        hinfos.get(position).getTaskcode()));
                                        thread.start();
                                        taskcode = hinfos.get(position).getTaskcode();
                                        shoufei = etshoufei.getText().toString();
                                        d = CommonUtils.createProgressDialog(HistoryActivity.this, "正在上传请耐心等待......");
                                        CommonUtils.dialogSty(dialog,true);//dialog谈话框消失
                                    } else {
                                        CommonUtils.dialogSty(dialog,false);//dialog谈话框不消失
                                        Utility.ToastShow(HistoryActivity.this,
                                                "请填写收费！");

                                    }



                                }
                            });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CommonUtils.dialogSty(dialog,true);//dialog谈话框消失
                        }
                    });
                    builder.setView(view);
                    AlertDialog alertDialog = builder.create();
//                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);//点击dialog以外的区域没反应
                    alertDialog.show();

                    etshoufei = (EditText) view.findViewById(R.id.etshoufei);
                    if (!hinfos.get(position).getShoufei().equals("")) {
                        etshoufei.setText(hinfos.get(position).getShoufei());
                    }
                }
            });

            convertView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    ZDYDialog zdyDialog = new ZDYDialog(HistoryActivity.this, "是否删除此命令单", false);
                    zdyDialog.setZdYinterface(new ZDYDialog.ZDYinterface() {
                        @Override
                        public void btnOkonClick(int type, String inpust) {
                            if (type == 2){
                                dbHelper.DeleteCommand(hinfos.get(
                                                 position).getTaskcode());
                                           refash(1);
                            }
                        }
                    });
     // TODO Auto-generated method stub
                    return false;
                }
            });

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    View view = getLayoutInflater().inflate(R.layout.command, null);
                    final TextView tvalarmtel = (TextView) view
                            .findViewById(R.id.hisAlarmtel);
                    final TextView tvlinktel = (TextView) view
                            .findViewById(R.id.hisLinktel);
                    final TextView tvcontent = (TextView) view
                            .findViewById(R.id.textView2);
                    final TextView tvhisShiGu = (TextView) view
                            .findViewById(R.id.hisShiGu);
                    tvalarmtel.setText(hinfos.get(position).getAlarmtel());
                    tvlinktel.setText(hinfos.get(position).getLinktel());
                    tvcontent.setText("发单时间："
                            + hinfos.get(position).getHistime() + "\n"
                            + hinfos.get(position).getHiscontent());

                    if (!hinfos.get(position).getShiGu().toString().equals(""))
                        tvhisShiGu.setText("事故：" + hinfos.get(position).getShiGu().toString());
                    else
                        tvhisShiGu.setText("事故：无");

                    tvalarmtel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            String telalarm = tvalarmtel.getText().toString();
                            Intent intentalarm = new Intent();
                            intentalarm.setAction(Intent.ACTION_CALL);
                            telalarm = "tel:" + telalarm;
                            intentalarm.setData(Uri.parse(telalarm));
                            startActivity(intentalarm);
                        }
                    });

                    tvlinktel.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            String telalarm = tvlinktel.getText().toString();
                            Intent intentalarm = new Intent();
                            intentalarm.setAction(Intent.ACTION_CALL);
                            telalarm = "tel:" + telalarm;
                            intentalarm.setData(Uri.parse(telalarm));
                            startActivity(intentalarm);
                        }
                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    builder.setTitle("历史命令单");
                    builder.setNegativeButton("关闭", null);
                    builder.setView(view);
                    builder.show();
                }
            });
            return convertView;
        }
    }

    class NoticeListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return ninfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return ninfos.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) { // 这句就是重用的关键
                convertView = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.historyitem, null);
            }

            final TextView tvhistype = (TextView) convertView
                    .findViewById(R.id.tvhisType);
            final TextView tvtime = (TextView) convertView
                    .findViewById(R.id.tvhistime);
            final TextView tvcontent = (TextView) convertView
                    .findViewById(R.id.tvhiscontent);
            final RelativeLayout rlshoufei = (RelativeLayout) convertView
                    .findViewById(R.id.rlshoufei);

            rlshoufei.setVisibility(View.GONE);
            tvhistype.setText(ninfos.get(position).getHistype().toString());
            tvtime.setText(ninfos.get(position).getHistime().toString());
            tvcontent.setText(ninfos.get(position).getHiscontent().toString());
            convertView.setTag(position);

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    View view = getLayoutInflater().inflate(R.layout.notice,
                            null);
                    final TextView tvnotice = (TextView) view
                            .findViewById(R.id.tvnotice);
                    final TextView nttime = (TextView) view
                            .findViewById(R.id.nttime);
                    tvnotice.setText("    "
                            + ninfos.get(position).getHiscontent());
                    nttime.setText(ninfos.get(position).getHistime());
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    builder.setTitle("历史通知单");
                    builder.setNegativeButton("关闭", null);
                    builder.setView(view);
                    builder.show();
                }
            });
            return convertView;
        }
    }

    // 处理Pad绑定车载 2016-01-14 肖明星
    class Uploadshoufei implements Runnable {
        String ambcode = "";
        String taskcode = "";
        String shoufei = "";

        public Uploadshoufei(String shoufei, String ambcode, String taskcode) {
            this.ambcode = ambcode;
            this.taskcode = taskcode;
            this.shoufei = shoufei;
        }

        public void run() {
            // TODO Auto-generated method stub
            Looper curLooper = Looper.getMainLooper();
            mHandler = new MyHandler(curLooper);
            Message m = new Message();
            String str = "上传收费失败";
            NPadShoufei npsfInfo = new NPadShoufei();
            npsfInfo.setAmbCoding(ambcode);
            npsfInfo.setTaskCoding(taskcode);
            npsfInfo.setShoufei(shoufei);
            String ret = CustomerHttpClient.getInstance().SaveCureCharge(npsfInfo);
            if (ret.contains("成功")) {
                m = mHandler.obtainMessage(1, 1, 1, ret);
            } else
                m = mHandler.obtainMessage(1, 1, 2, ret);
            mHandler.sendMessage(m);
        }

    }

    public class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            // Socket连接超时了
            if (msg.what == 1) {
                if (msg.arg1 == 1) {
                    if (d != null){
                        d.dismiss();
                    }
                    switch (msg.arg2) {
                        case 1: // 上传成功 2016-05-11
                            dbHelper.SaveHistoryShoufei(shoufei, taskcode);
                            if (iscommand)
                                refash(REFRESH_COMMANDLIST);
                            else
                                refash(REFRESH_NOTICELIST);
                            Utility.ToastShow(HistoryActivity.this, msg.obj.toString());
                            shoufei = "";
                            taskcode = "";
                            break;
                        case 2: // 上传失败 2016-05-11
                            Utility.ToastShow(HistoryActivity.this, msg.obj.toString());
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 1) {
            if (resultCode == 2) {
                if (iscommand)
                    refash(REFRESH_COMMANDLIST);
                else
                    refash(REFRESH_NOTICELIST);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

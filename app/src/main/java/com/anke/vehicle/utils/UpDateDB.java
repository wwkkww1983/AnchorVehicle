package com.anke.vehicle.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.anke.vehicle.database.DBHelper;
import com.anke.vehicle.status.TrueOrFalseStatus;
import com.anke.vehicle.status.UpdataBDList;

/**
 * 创建作者： 张蔡奇
 * 创建时间： 2016/10/17
 * 创建公司： 珠海市安克电子技术有限公司合肥分公司
 * 处理数据库更新
 */
public class UpDateDB {
    private DBHelper dbHelper;
    private Context mContext;
    private AlertDialog d;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UpdataBDList.UPDATE_HOSPITAL:
                    updateHospital();//更新医院数据库
                    break;
                case UpdataBDList.UPDATE_DRUG:
                    if (msg.obj.toString().equals("")) {
                        updateDrug();//更新药品数据库
                    } else {
                        if (d != null)
                            d.dismiss();
                        Utility.ToastShow(mContext, "UPDATE_DRUG"+msg.obj.toString());
                    }

                    break;
                case UpdataBDList.UPDATE_STOPREASON:
                    if (msg.obj.toString().equals("")) {
                        if (dbHelper.GetParameter().getIsStopTask() == TrueOrFalseStatus.TRUE) {
                            updateStopReason();//更新暂停原因
                        } else {
                            Message m = new Message();
                            m.what = UpdataBDList.UPDATE_PAUSEREASON;
                            m.obj = msg.obj.toString();
                            handler.sendMessage(m);
                        }
                    } else {
                        if (d != null)
                            d.dismiss();
                        Utility.ToastShow(mContext, "UPDATE_STOPREASON"+msg.obj.toString());
                    }
                    break;
                case UpdataBDList.UPDATE_PAUSEREASON:

                    if (msg.obj.toString().equals("")) {
                        if (dbHelper.GetParameter().getIsPauseCall() == TrueOrFalseStatus.TRUE) {
                            updatePauseReason();//更新终止原因
                        } else {
                            Message m = new Message();
                            m.what = UpdataBDList.UPDATE_AMB;
                            m.obj = msg.obj.toString();
                            handler.sendMessage(m);
                        }
                    } else {
                        if (d != null)
                            d.dismiss();
                        Utility.ToastShow(mContext, "UPDATE_PAUSEREASON1111"+msg.obj.toString());
                    }
                    break;
                case UpdataBDList.UPDATE_AMB:

                    if (msg.obj.toString().equals("")) {
                        if (dbHelper.GetParameter().getIsBindCar() == TrueOrFalseStatus.FAlSE) {
                            updateAmb();//更新车载信息
                        } else {
                            Message m = new Message();
                            m.what = UpdataBDList.UPDATE_SUCCESS;
                            m.obj = msg.obj.toString();
                            handler.sendMessage(m);
                        }
                    } else {
                        if (d != null)
                            d.dismiss();
                        Utility.ToastShow(mContext,"UPDATE_AMB"+msg.obj.toString());
                    }
                    break;
                case UpdataBDList.UPDATE_SUCCESS://更新成功
                    if (d != null && d.isShowing())
                        d.dismiss();
                    Utility.ToastShow(mContext, "更新成功！");
                    break;
                default:
                    break;

            }
        }
    };

    /**
     * 构造方法传参数
     * @param context
     * @param dbHelper
     * @param d
     */
    public UpDateDB(Context context, DBHelper dbHelper, AlertDialog d) {
        mContext = context;
        this.dbHelper = dbHelper;
        this.d = d;
        new Thread(){
            @Override
            public void run() {
                handler.sendEmptyMessage(UpdataBDList.UPDATE_HOSPITAL);
            }
        }.start();


    }

    /**
     * 更新车载信息
     */
    private void updateAmb() {
        new Thread() {
            public void run() {
                String str = CommonUtils.dealAMB(dbHelper);
                Message m = new Message();
                m.what = UpdataBDList.UPDATE_SUCCESS;
                m.obj = str;
                handler.sendMessage(m);
            }
        }.start();
    }

    /**
     * 更新终止原因
     */
    private void updatePauseReason() {
        new Thread() {
            public void run() {
                String str = CommonUtils.dealPauseReason(dbHelper);
                Message m = new Message();
                m.what = UpdataBDList.UPDATE_AMB;
                m.obj = str;
                handler.sendMessage(m);
            }
        }.start();
    }

    /**
     * 更新暂停原因
     */
    private void updateStopReason() {
        new Thread() {
            @Override
            public void run() {
                String str = CommonUtils.dealStopReason(dbHelper);
                Message m = new Message();
                m.what = UpdataBDList.UPDATE_PAUSEREASON;
                m.obj = str;
                handler.sendMessage(m);
            }
        }.start();
    }

    /**
     * 更新药品数据库
     */
    private void updateDrug() {
        new Thread() {
            @Override
            public void run() {
                String str = CommonUtils.dealDrug(dbHelper);
                Message m = new Message();
                m.what = UpdataBDList.UPDATE_STOPREASON;
                m.obj = str;
                handler.sendMessage(m);
            }
        }.start();
    }

    /**
     * 更新医院数据库
     */
    public void updateHospital() {
        new Thread() {
            @Override
            public void run() {
                String str = CommonUtils.dealHospital(dbHelper);
                Message m = new Message();
                m.what = UpdataBDList.UPDATE_DRUG;
                m.obj = str;
                handler.sendMessage(m);
            }
        }.start();
    }
}

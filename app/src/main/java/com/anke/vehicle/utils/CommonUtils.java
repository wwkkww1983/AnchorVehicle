package com.anke.vehicle.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anke.vehicle.R;
import com.anke.vehicle.activity.HistoryActivity;
import com.anke.vehicle.activity.MainActivity;
import com.anke.vehicle.activity.ParameterSetActivity;
import com.anke.vehicle.comm.CustomerHttpClient;
import com.anke.vehicle.database.DBHelper;
import com.anke.vehicle.entity.BindAmbInfo;
import com.anke.vehicle.entity.NPadHospitalInfo;
import com.anke.vehicle.entity.NPadJudgeInfo;
import com.anke.vehicle.entity.NPadMasterInfo;
import com.anke.vehicle.entity.PADInfo;
import com.anke.vehicle.entity.PadAmbInfo;
import com.anke.vehicle.entity.PauseReasonInfo;
import com.anke.vehicle.entity.StopReasonInfo;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * 创建作者： 张蔡奇
 * 创建时间： 2016/9/27
 * 创建公司： 珠海市安克电子技术有限公司合肥分公司
 */
public class CommonUtils {
    private static byte[] result2 = new byte[2048]; // 缓存服务端传过来的数据
    private static int length = 0; // 记录缓存包的长度
    public static String dbName = "anchorvehicle.db";// 数据库的名字
    public static String DATABASE_PATH = "/data/data/com.anke.vehicle/databases/";// 数据库在手机里的路径
    public static SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5); // 播放音乐初始化 肖明星 2015-06-10
    public static HashMap<Integer, Integer> musicId = new HashMap<Integer, Integer>(); // 保存音乐的键值对

    /**
     * 显示ProgressDialog
     */

    public static AlertDialog  createProgressDialog(Context context, String title) {
//        ProgressDialog p = new ProgressDialog(context,ProgressDialog.THEME_HOLO_LIGHT);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context,
                AlertDialog.THEME_HOLO_LIGHT);
        AlertDialog d = builder1.create();
        View v = View.inflate(context,R.layout.custom_process,null);
        TextView tv = (TextView) v.findViewById(R.id.pb_tv);
        tv.setText(title);
        d.setView(v,0,0,0,0);// 设置边距为0,保证在2.x的版本上运行没问题
        d.show();
        return d;

    }

    /**
     * 当dialog内容没填，点击确定按钮时，dialog谈话框是否消失 false dialog谈话框不消失 true 消失
     */
    public static void dialogSty(DialogInterface dialog,boolean isGone){
        try
        {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(dialog, isGone);  //需要关闭的时候 将这个参数设置为true 他就会自动关闭了
        }catch(Exception e) {
            e.printStackTrace();
        }
}
    // 创建一个对话框 2015-05-30 肖明星
    public static void Dialog(Context context, String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * // 通过实际标识找到车辆编码 2015-06-01 肖明星
     *
     * @param ret
     * @param list
     * @return
     */
    public static BindAmbInfo getAmbInfo(String ret, List<PadAmbInfo> list) {
        BindAmbInfo info = new BindAmbInfo();
        for (PadAmbInfo iterable_element : list) {
            if (iterable_element.GetRealSign().equals(ret)) {
                info.SetAmbCode(iterable_element.GetAmbCode().toString());
                info.SetTelCode(Utility.getPhone());
            }
        }
        return info;
    }

    /**
     * json解析
     * @param str
     * @return
     */
    public static PADInfo getbBaotou(String str) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<PADInfo>() {
        }.getType();
        PADInfo ret = gson.fromJson(str, type);
        return ret;
    }

    /**
     * 处理医院数据库更新
     *
     * @param dbHelper
     * @return
     */
    public static String dealHospital(DBHelper dbHelper) {
        String str = "";
        String ret = CustomerHttpClient.getInstance().QueryHospital();
        if ((!ret.trim().equals("")) && (!ret.trim().equals("null"))
                && (!ret.trim().equals("Post异常："))) {
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<NPadHospitalInfo>>() {
                }.getType();
                List<NPadHospitalInfo> listHospital = gson.fromJson(ret,
                        type);
                if (listHospital != null) {
                    str = dbHelper.SaveHospital(listHospital);
                } else {
                    str = "没有获取到医院信息！";
                }
            } catch (Exception ex) {

                str = ex.getMessage();
            }
        } else {
            str = ret;
        }
        return str;
    }

    /**
     * 处理毒品数据库
     *
     * @param dbHelper
     * @return
     */
    public static String dealDrug(DBHelper dbHelper) {
        String str = "";
        String ret = CustomerHttpClient.getInstance().QueryYaoPin();
        if ((!ret.trim().equals("")) && (!ret.trim().equals("null"))
                && (!ret.trim().equals("Post异常："))) {
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<NPadJudgeInfo>>() {
                }.getType();
                List<NPadJudgeInfo> jinfos = gson.fromJson(ret, type);
                if (jinfos != null) {
                    str = dbHelper.SaveJudge(jinfos);
                } else {
                    str = "没有获取到判断信息！";
                }
            } catch (Exception ex) {

                str = ex.getMessage();
            }
        } else {
            str = ret;
        }
        return str;
    }

    /**
     * 处理专家列表数据库
     *
     * @param dbHelper
     * @return
     */
    public static String dealMaster(DBHelper dbHelper) {
        String str = "";
        String ret = CustomerHttpClient.getInstance().QueryMaster();
        if ((!ret.trim().equals("")) && (!ret.trim().equals("null"))
                && (!ret.trim().equals("Post异常："))) {
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<NPadMasterInfo>>() {
                }.getType();
                List<NPadMasterInfo> minfos = gson.fromJson(ret, type);
                if (minfos != null) {
                    str = dbHelper.SaveMaster(minfos);
                } else {
                    str = "没有获取到专家列表！";
                }
            } catch (Exception ex) {

                str = ex.getMessage();
            }
        } else {
            str = ret;
        }
        return str;
    }

    /**
     * 获取任务终止原因字典表
     *
     * @param dbHelper
     * @return
     */
    public static String dealStopReason(DBHelper dbHelper) {
        String str = "";
        String ret = CustomerHttpClient.getInstance().QueryStopReason();
        if ((!ret.trim().equals("")) && (!ret.trim().equals("null"))
                && (!ret.trim().equals("Post异常："))) {
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<StopReasonInfo>>() {
                }.getType();
                List<StopReasonInfo> minfos = gson.fromJson(ret, type);
                if (minfos != null) {
                    str = dbHelper.SaveStopReason(minfos);
                } else {
                    str = "没有获取到任务终止原因列表！";
                }
            } catch (Exception ex) {

                str = ex.getMessage();
            }
        } else {
            str = ret;
        }
        return str;
    }

    /**
     * 收到服务器的数据转String
     *
     * @param result
     * @return
     */
    public static String TcommandInfo(byte[] result) {
        String resultstring = "";
        String string = new String(result);
        if (Utility.isTrue()) {
            length = 0;
            result2 = new byte[2048];
        }
        if (string.substring(string.length() - 1, string.length()).equals("}")) {
            for (int i = 0; i < result.length; i++)
                result2[length++] = result[i];
            resultstring = new String(result2);
        } else {
            Utility.setTrue(false);
            for (int i = 0; i < result.length; i++)
                result2[length++] = result[i];
        }
        return resultstring;
    }

    /**
     * 获取暂停调用原因字典表
     *
     * @param dbHelper
     * @return
     */
    public static String dealPauseReason(DBHelper dbHelper) {
        String str = "";
        String ret = CustomerHttpClient.getInstance().QueryPauseReason();
        if ((!ret.trim().equals("")) && (!ret.trim().equals("null"))
                && (!ret.trim().equals("Post异常："))) {
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<PauseReasonInfo>>() {
                }.getType();
                List<PauseReasonInfo> minfos = gson.fromJson(ret, type);
                if (minfos != null) {
                    str = dbHelper.SavePauseReason(minfos);
                } else {
                    str = "没有获取到暂停调用原因列表！";
                }
            } catch (Exception ex) {

                str = ex.getMessage();
            }
        } else {
            str = ret;
        }
        return str;
    }

    /**
     * 处理车载数据库
     *
     * @param dbHelper
     * @return
     */
    public static String dealAMB(DBHelper dbHelper) {
        String str = "";
        String ret = CustomerHttpClient.getInstance().QueryUnBindAmb();
        if ((!ret.trim().equals("")) && (!ret.trim().equals("null"))
                && (!ret.trim().equals("Post异常："))) {
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<PadAmbInfo>>() {
                }.getType();
                List<PadAmbInfo> minfos = gson.fromJson(ret, type);
                if (minfos != null) {
                    str = dbHelper.SaveAmb(minfos);
                } else {
                    str = "没有获取到车辆列表！";
                }
            } catch (Exception ex) {

                str = ex.getMessage();
            }
        } else {
            str = ret;
        }
        return str;
    }

    /**
     * 实体转成String
     *
     * @param info
     * @return
     */
    public static String GetContent(PADInfo info) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(info);
        return jsonStr;
    }

    /**
     * List转字符串数组
     *
     * @param ret
     * @return
     */
    public static String[] getDic(List<String> ret) {
        return (String[]) ret.toArray(new String[ret.size()]);
    }

    /**
     * 确定手机中有数据库，不存在就把raw里的数据库写入手机
     */
    public static void ShowDBExist(Context context) {
        boolean dbExist = checkDataBase();

        if (!dbExist) {// 不存在就把raw里的数据库写入手机
            try {
                copyDataBase(context);
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * 检查数据库是否存在
     *
     * @return
     */
    public static boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String databaseFilename = DATABASE_PATH + dbName;
            checkDB = SQLiteDatabase.openDatabase(databaseFilename, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * 复制数据库到手机指定文件夹下
     *
     * @param context
     * @throws IOException
     */
    public static void copyDataBase(Context context) throws IOException {
        String databaseFilenames = DATABASE_PATH + dbName;
        File dir = new File(DATABASE_PATH);
        if (!dir.exists())// 判断文件夹是否存在，不存在就新建一个
            dir.mkdir();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(databaseFilenames);// 得到数据库文件的写入流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStream is = context.getResources().openRawResource(
                R.raw.anchorvehicle);// 得到数据库文件的数据流
        byte[] buffer = new byte[8192];
        int count = 0;
        try {
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
                // os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载声音
     */
    public static void LoadSound(Context context) {
        musicId.put(1, soundPool.load(context, R.raw.anxia, 1)); // 添加音频文件 肖明星
        musicId.put(2, soundPool.load(context, R.raw.notice, 1));
        musicId.put(3, soundPool.load(context, R.raw.sendcar, 1));
    }

    public static void ringSound(int i) {
        soundPool.play(musicId.get(i), 1, 1, 1, 0, 1); // 响铃
    }
}

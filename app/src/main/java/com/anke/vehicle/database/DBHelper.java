package com.anke.vehicle.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anke.vehicle.entity.DictionaryInfo;
import com.anke.vehicle.entity.DisplayInfo;
import com.anke.vehicle.entity.HistoryInfo;
import com.anke.vehicle.entity.KnowLedgeInfo;
import com.anke.vehicle.entity.NPadHospitalInfo;
import com.anke.vehicle.entity.NPadIntervalInfo;
import com.anke.vehicle.entity.NPadJudgeInfo;
import com.anke.vehicle.entity.NPadMasterInfo;
import com.anke.vehicle.entity.NPadStationInfo;
import com.anke.vehicle.entity.NoticeInfo;
import com.anke.vehicle.entity.PadAmbInfo;
import com.anke.vehicle.entity.ParameterInfo;
import com.anke.vehicle.entity.PauseReasonInfo;
import com.anke.vehicle.entity.StopReasonInfo;
import com.anke.vehicle.entity.UpLoadInfo;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "anchorvehicle.db";
    private final static int DATABASE_VERSION = 2;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //添加车辆表  2016-07-18  xmx
        db.execSQL("DROP TABLE IF EXISTS TAmblauce");
        db.execSQL("CREATE TABLE IF NOT EXISTS TAmblauce "
                + " ([AmbCode] NVARCHAR(20), [RealSign] VARCHAR(20))");

        //添加暂停调用原因表  2016-07-18  xmx
        db.execSQL("DROP TABLE IF EXISTS TPauseReason");
        db.execSQL("CREATE TABLE IF NOT EXISTS TPauseReason "
                + " ([Code] INTEGER, [Name] VARCHAR(20))");

        //添加中止任务原因表  2016-07-18  xmx
        db.execSQL("DROP TABLE IF EXISTS TStopReason");
        db.execSQL("CREATE TABLE IF NOT EXISTS TStopReason "
                + " ([Code] INTEGER, [Name] VARCHAR(20))");

        //centername表添加字段并且赋值  2016-07-18  xmx
        db.execSQL("alter table centername add StationCode VARCHAR(20) default '001'");
        db.execSQL("alter table centername add StationName VARCHAR(20) default '急救指挥'");
        db.execSQL("alter table centername add PauseReasonName VARCHAR(20) default '车辆故障'");
        db.execSQL("alter table centername add PauseReasonCode VARCHAR(20) default '101'");

        //history表添加字段  2016-07-18  xmx
        db.execSQL("alter table history add Shoufei VARCHAR(20)");
        db.execSQL("alter table history add Hospital VARCHAR(20)");
        db.execSQL("alter table history add Ill VARCHAR(20)");
        db.execSQL("alter table history add Judge VARCHAR(20)");
        db.execSQL("alter table history add ShiGu VARCHAR(20)");

        //parameters表添加字段  2016-07-18  xmx
        db.execSQL("alter table parameters add isbindamb INTEGER(2) default '1'");
        db.execSQL("alter table parameters add isstoptask INTEGER(2) default '0'");
//        db.execSQL("alter table parameters add islocation INTEGER(2) default '0'");//青岛库加这个
        db.execSQL("alter table parameters add ismodifystation INTEGER(2) default '0'");
        db.execSQL("alter table parameters add ispause INTEGER(2) default '0'");
        db.execSQL("alter table parameters add isGaoZhi INTEGER(2) default '0'");
        db.execSQL("alter table parameters add isShouFei INTEGER(2) default '0'");
        db.execSQL("alter table parameters add isnewtask INTEGER(2) default '0'");
        db.execSQL("alter table parameters add isShiGu INTEGER(2) default '0'");
        db.execSQL("alter table parameters add httpport VARCHAR(20) default '8810'");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // db.execSQL("CREATE TABLE IF NOT EXISTS YiYuan "
        // +
        // " ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [code] NVARCHAR(20), [name] NVARCHAR(100))");

    }

    // 得到历史记录
    public List<HistoryInfo> GetHistory() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select histype,histime,hiscontent,alarmtel,linktel,taskcode,ambcode,Shoufei,Hospital,Ill,Judge,ShiGu from history order by histime desc ";
        List<HistoryInfo> infosList = new ArrayList<HistoryInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                HistoryInfo info = new HistoryInfo();
                String histype = cursor.getString(0);
                info.setHistype(histype);
                String histime = cursor.getString(1);
                info.setHistime(histime);
                String hiscontent = cursor.getString(2);
                info.setHiscontent(hiscontent);
                String alarmtel = cursor.getString(3);
                info.setAlarmtel(alarmtel);
                String linktel = cursor.getString(4);
                info.setLinktel(linktel);
                String Taskcode = cursor.getString(5);
                info.setTaskcode(Taskcode);
                String Ambcode = cursor.getString(6);
                info.setAmbcode(Ambcode);

                String shoufei = (cursor.getString(7) == null ? "" : cursor.getString(7));
                info.setShoufei(shoufei);
                String hospital = (cursor.getString(8) == null ? "" : cursor.getString(8));
                info.setHospital(hospital);
                String ill = (cursor.getString(9) == null ? "" : cursor.getString(9));
                info.setIll(ill);
                String judge = (cursor.getString(10) == null ? "" : cursor.getString(10));
                info.setJudge(judge);
                String shigu = (cursor.getString(11) == null ? "" : cursor.getString(11));
                info.setShiGu(shigu);
                infosList.add(info);
            }
        } catch (Exception e) {
            Log.d("得到历史记录", e.toString());
            // TODO: handle exception
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return infosList;
    }

    // 得到参数信息
    public ParameterInfo GetParameter() {
        ParameterInfo info = null;
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select ipaddress,port,telphone,versionUrl,apkUrl,islocation,isbindamb,isstoptask,ismodifystation,ispause,isnewtask,isGaoZhi,isShouFei,isShiGu,httpport from parameters";
        Cursor cursor = db.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            info = new ParameterInfo();
            String ip = cursor.getString(0);
            info.setIpaddress(ip);
            String port = cursor.getString(1);
            info.setPort(port);
            String telphone = cursor.getString(2);
            info.setTelphone(telphone);
            String versionUrl = cursor.getString(3);
            info.setVersionUrl(versionUrl);
            String apkUrl = cursor.getString(4);
            info.setApkUrl(apkUrl);
            int islocation = cursor.getInt(5);
            info.setIslocation(islocation);
            int isbind = cursor.getInt(6);
            info.setIsBindCar(isbind);
            int isstoptask = cursor.getInt(7);
            info.setIsStopTask(isstoptask);
            int ischangestation = cursor.getInt(8);
            info.setIsChangStation(ischangestation);
            int ispause = cursor.getInt(9);
            info.setIsPauseCall(ispause);
            int isnewtask = cursor.getInt(10);
            info.setIsNewTask(isnewtask);
            int isgaozhi = cursor.getInt(11);
            info.setIsGaoZhi(isgaozhi);
            int isshoufei = cursor.getInt(12);
            info.setIsShouFei(isshoufei);
            int isshigu = cursor.getInt(13);
            info.setIsShiGu(isshigu);
            String httpport = cursor.getString(14);
            info.setHttpport(httpport);
        }
        cursor.close();
        db.close();
        return info;
    }

    // 保存参数信息
    public void SaveParameter(String ip, String port, String httpport, String tel,
                              String verurl, String apkurl, int islocation, int isBindCar,
                              int isStopTask, int isChangStation, int isPauseCall,
                              int isNewTask, int isGaoZhi, int isShouFei, int isShiGu) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select id from parameters";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String where = "id = ?";
            String[] whereValue = {Integer.toString(id)};
            /* 将修改的值放入ContentValues */
            ContentValues cv1 = new ContentValues();
            cv1.put("ipaddress", ip);
            cv1.put("port", port);
            cv1.put("httpport", httpport);
            cv1.put("telphone", tel);
            cv1.put("versionUrl", verurl);
            cv1.put("apkUrl", apkurl);
            cv1.put("islocation", islocation);
            cv1.put("isbindamb", isBindCar);
            cv1.put("isstoptask", isStopTask);
            cv1.put("ismodifystation", isChangStation);
            cv1.put("ispause", isPauseCall);
            cv1.put("isnewtask", isNewTask);
            cv1.put("isGaoZhi", isGaoZhi);
            cv1.put("isShouFei", isShouFei);
            cv1.put("isShiGu", isShiGu);
            db.update("parameters", cv1, where, whereValue);
        } else {
            ContentValues cv2 = new ContentValues();
            cv2.put("ipaddress", ip);
            cv2.put("port", port);
            cv2.put("httpport", httpport);
            cv2.put("telphone", tel);
            cv2.put("versionUrl", verurl);
            cv2.put("apkUrl", apkurl);
            cv2.put("islocation", islocation);
            cv2.put("islocation", islocation);
            cv2.put("isbindamb", isBindCar);
            cv2.put("isstoptask", isStopTask);
            cv2.put("ismodifystation", isChangStation);
            cv2.put("ispause", isPauseCall);
            cv2.put("isnewtask", isNewTask);
            cv2.put("isGaoZhi", isGaoZhi);
            cv2.put("isShouFei", isShouFei);
            cv2.put("isShiGu", isShiGu);
            db.insert("parameters", null, cv2);
        }
        cursor.close();
        db.close();
    }

    // 拼音头查询救治措施
    public List<KnowLedgeInfo> getCure(String pinyintou) {
        List<KnowLedgeInfo> infos = new ArrayList<KnowLedgeInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 序列号,急救名称,拼音头 from TCureRule where 拼音头 like '%"
                    + pinyintou + "%' ";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                KnowLedgeInfo info = new KnowLedgeInfo();
                info.setId(cursor.getInt(0));
                info.setName(cursor.getString(1));
                info.setPyt(cursor.getString(2));
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return infos;
    }

    // 查询救治措施内容
    public String getCureString(String name) {
        String ret = "";
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 急救名称,病情评估,救治原则,现场_转运注意事项,急救流程 from TCureRule where 急救名称=?";
            Cursor cursor = db.rawQuery(sql, new String[]{name});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); ) {
                ret += "名称:  " + cursor.getString(0) + "\n\n";
                ret += "[病情评估]\n" + cursor.getString(1) + "\n\n";
                ret += "[救治原则]\n" + cursor.getString(2) + "\n\n";
                ret += "[现场_转运注意事项]\n" + cursor.getString(2) + "\n\n";
                ret += "[急救流程]\n" + cursor.getString(2) + "\n";
                break;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return ret;
    }

    // 保存显示名称
    public void SaveCenter(String AmbDeskName, String AmbulanceCode,
                           String CenterName, String Stationcode, String Stationname) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select id from centername";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String where = "id = ?";
            String[] whereValue = {Integer.toString(id)};
            /* 将修改的值放入ContentValues */
            ContentValues cv1 = new ContentValues();
            cv1.put("AmbDeskName", AmbDeskName);
            cv1.put("AmbulanceCode", AmbulanceCode);
            cv1.put("CenterName", CenterName);
            cv1.put("StationCode", Stationcode);
            cv1.put("StationName", Stationname);
            db.update("centername", cv1, where, whereValue);
        } else {
            ContentValues cv2 = new ContentValues();
            cv2.put("AmbDeskName", AmbDeskName);
            cv2.put("AmbulanceCode", AmbulanceCode);
            cv2.put("CenterName", CenterName);
            cv2.put("StationCode", Stationcode);
            cv2.put("StationName", Stationname);
            db.insert("centername", null, cv2);
        }
        cursor.close();
        db.close();
    }

    // 得到显示名称
    public DisplayInfo GetCenter() {
        DisplayInfo info = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select AmbDeskName,AmbulanceCode,CenterName from centername";
        Cursor cursor = db.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            info = new DisplayInfo();
            String ambname = cursor.getString(0);
            info.setAmbDeskName(ambname);
            String ambcode = cursor.getString(1);
            info.setAmbulanceCode(ambcode);
            String centername = cursor.getString(2);
            info.setCenterName(centername);
        }
        cursor.close();
        db.close();
        return info;
    }
    // 保存历史记录
    public String SaveHistory(HistoryInfo info) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select taskcode from history where taskcode=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{info.getTaskcode()});
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                String taskcode = cursor.getString(0);
                String where = "taskcode = ?";
                String[] whereValue = {taskcode};
                /* 将修改的值放入ContentValues */
                ContentValues cv1 = new ContentValues();
                cv1.put("histype", info.getHistype());
                cv1.put("histime", info.getHistime());
                cv1.put("hiscontent", info.getHiscontent());
                cv1.put("alarmtel", info.getAlarmtel());
                cv1.put("linktel", info.getLinktel());
                cv1.put("ambcode", info.getAmbcode());
                db.update("history", cv1, where, whereValue);
            } else {

                ContentValues cv = new ContentValues();
                cv.put("histype", info.getHistype());
                cv.put("histime", info.getHistime());
                cv.put("hiscontent", info.getHiscontent());
                cv.put("alarmtel", info.getAlarmtel());
                cv.put("linktel", info.getLinktel());
                cv.put("taskcode", info.getTaskcode());
                cv.put("ambcode", info.getAmbcode());
                db.insert("history", null, cv);
            }
        } catch (Exception e) {
            ret = e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return ret;
    }

    // 保存历史收费记录
    public String SaveHistoryShoufei(String shoufei, String taskcode) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select taskcode from history where taskcode=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{taskcode});
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                String taskcodenew = cursor.getString(0);
                String where = "taskcode = ?";
                String[] whereValue = {taskcodenew};
                /* 将修改的值放入ContentValues */
                ContentValues cv1 = new ContentValues();
                cv1.put("Shoufei", shoufei);
                db.update("history", cv1, where, whereValue);
            } else {
                ContentValues cv = new ContentValues();
                cv.put("Shoufei", shoufei);
                db.insert("history", null, cv);
            }
        } catch (Exception e) {
            ret = e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return ret;
    }

    // 保存历史告知记录
    public String SaveHistorydata(HistoryInfo info) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select taskcode from history where taskcode=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{info.getTaskcode()});
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                String taskcode = cursor.getString(0);
                String where = "taskcode = ?";
                String[] whereValue = {taskcode};
				/* 将修改的值放入ContentValues */
                ContentValues cv1 = new ContentValues();
                cv1.put("Hospital", info.getHospital());
                cv1.put("Ill", info.getIll());
                cv1.put("Judge", info.getJudge());
                db.update("history", cv1, where, whereValue);
            } else {
                ContentValues cv = new ContentValues();
                cv.put("Hospital", info.getHospital());
                cv.put("Ill", info.getIll());
                cv.put("Judge", info.getJudge());
                db.insert("history", null, cv);
            }
        } catch (Exception e) {
            ret = e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return ret;
    }

    // 保存历史事故记录
    public String SaveShiGu(String shigu, String taskcode) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select taskcode from history where taskcode=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{taskcode});
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                String taskcodenew = cursor.getString(0);
                String where = "taskcode = ?";
                String[] whereValue = {taskcodenew};
					/* 将修改的值放入ContentValues */
                ContentValues cv1 = new ContentValues();
                cv1.put("ShiGu", shigu);
                db.update("history", cv1, where, whereValue);
            } else {
                ContentValues cv = new ContentValues();
                cv.put("ShiGu", shigu);
                db.insert("history", null, cv);
            }
        } catch (Exception e) {
            ret = e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return ret;
    }

    // 得到历史记录
    public List<NoticeInfo> GetHisNotice() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select histype,histime,hiscontent from hisnotice order by histime desc ";
        List<NoticeInfo> infosList = new ArrayList<NoticeInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NoticeInfo info = new NoticeInfo();
                String histype = cursor.getString(0);
                info.setHistype(histype);
                String histime = cursor.getString(1);
                info.setHistime(histime);
                String hiscontent = cursor.getString(2);
                info.setHiscontent(hiscontent);
                infosList.add(info);
            }
        } catch (Exception e) {
            Log.d("得到历史记录", e.toString());
            // TODO: handle exception
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return infosList;
    }

    // 保存历史记录
    public String SaveHisNotice(NoticeInfo info) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        try {
            // db.delete("country", null, null);
            ContentValues cv = new ContentValues();
            cv.put("histype", info.getHistype());
            cv.put("histime", info.getHistime());
            cv.put("hiscontent", info.getHiscontent());
            db.insert("hisnotice", null, cv);

        } catch (Exception e) {
            ret = e.getMessage();
        } finally {
            db.close();
        }
        return ret;
    }

    public UpLoadInfo GetUpLoad(String taskcode) {
        UpLoadInfo info = new UpLoadInfo();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select taskcode,ambcode,hospital,judge,ill from TUpLoad where taskcode=?";
            Cursor cursor = db.rawQuery(sql, new String[]{taskcode});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); ) {
                info.setTaskCoding(cursor.getString(0));
                info.setAmbCoding(cursor.getString(1));
                info.setHospital(cursor.getString(2));
                info.setJudge(cursor.getString(3));
                info.setIll(cursor.getString(4));
                break;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return info;
    }

    // 保存上传医院信息
    public String SaveUpLoad(UpLoadInfo info) {
        SQLiteDatabase db = getReadableDatabase();
        String ret = "";
        String sql = "select taskcode,ambcode,hospital,judge,ill from TUpLoad where taskcode=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{info.getTaskCoding()});
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                String taskcode = cursor.getString(0);
                String where = "taskcode = ?";
                String[] whereValue = {taskcode};
				/* 将修改的值放入ContentValues */
                ContentValues cv1 = new ContentValues();
                cv1.put("ambcode", info.getAmbCoding());
                cv1.put("hospital", info.getHospital());
                cv1.put("judge", info.getJudge());
                cv1.put("ill", info.getIll());
                db.update("TUpLoad", cv1, where, whereValue);
            } else {
                ContentValues cv2 = new ContentValues();
                cv2.put("taskcode", info.getTaskCoding());
                cv2.put("ambcode", info.getAmbCoding());
                cv2.put("hospital", info.getHospital());
                cv2.put("judge", info.getJudge());
                cv2.put("ill", info.getIll());
                db.insert("TUpLoad", null, cv2);
            }
        } catch (Exception e) {
            Log.d("得到历史记录", e.toString());
            ret = e.toString();
            // TODO: handle exception
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return ret;
    }

    // 删除命令单
    public String DeleteCommand(String taskCode) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        try {
            String where = "taskcode = ?";
            String[] whereValue = {taskCode};
            db.delete("history", where, whereValue);
        } catch (Exception e) {
            ret = e.getMessage();
        } finally {
            db.close();
        }
        return ret;
    }

    // 删除历史记录
    public String DeleteHisNotice() {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete("hisnotice", null, null);
            db.delete("history", null, null);
        } catch (Exception e) {
            ret = e.getMessage();
        } finally {
            db.close();
        }
        return ret;
    }

    // 拼音头查询化学品
    public List<KnowLedgeInfo> getDanger(String pinyintou) {
        List<KnowLedgeInfo> infos = new ArrayList<KnowLedgeInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 序号,中文名称,拼音头 from TDanger where 拼音头 like '%"
                    + pinyintou + "%' ";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                KnowLedgeInfo info = new KnowLedgeInfo();
                info.setId(cursor.getInt(0));
                info.setName(cursor.getString(1));
                info.setPyt(cursor.getString(2));
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return infos;
    }

    // 查询毒品内容
    public String getDangerString(String name) {
        String ret = "";
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 中文名称,外观及性状,禁忌,危险性,急救措施,防护措施,泄露处理,附注 from TDanger where 中文名称=?";
            Cursor cursor = db.rawQuery(sql, new String[]{name});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); ) {
                ret += "中文名称:  " + cursor.getString(0) + "\n\n";
                ret += "[外观及性状]\n" + cursor.getString(1) + "\n\n";
                ret += "[禁忌]\n" + cursor.getString(2) + "\n\n";
                ret += "[危险性]\n" + cursor.getString(3) + "\n\n";
                ret += "[急救措施]\n" + cursor.getString(4) + "\n\n";
                ret += "[防护措施]\n" + cursor.getString(5) + "\n\n";
                ret += "[泄露处理]\n" + cursor.getString(6) + "\n\n";
                ret += "[附注]\n" + cursor.getString(7) + "\n";
                break;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return ret;
    }

    // 拼音头查询毒品
    public List<KnowLedgeInfo> getHxp(String pinyintou) {
        List<KnowLedgeInfo> infos = new ArrayList<KnowLedgeInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 序号,中文名称,拼音头 from TPoison where 拼音头 like '%"
                    + pinyintou + "%' ";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                KnowLedgeInfo info = new KnowLedgeInfo();
                info.setId(cursor.getInt(0));
                info.setName(cursor.getString(1));
                info.setPyt(cursor.getString(2));
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return infos;
    }

    // 查询毒品内容
    public String getHxpString(String name) {
        String ret = "";
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 中文名称,性质,毒性,诊断要点,救治要点,备注 from TPoison where 中文名称=?";
            Cursor cursor = db.rawQuery(sql, new String[]{name});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); ) {
                ret += "中文名称:  " + cursor.getString(0) + "\n\n";
                ret += "[性质]\n" + cursor.getString(1) + "\n\n";
                ret += "[毒性]\n" + cursor.getString(2) + "\n\n";
                ret += "[诊断要点]\n" + cursor.getString(3) + "\n\n";
                ret += "[救治要点]\n" + cursor.getString(4) + "\n\n";
                ret += "[备注]\n" + cursor.getString(5) + "\n\n";
                break;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return ret;
    }

    // 拼音头查询医院
    public List<NPadHospitalInfo> getHospital(String pinyintou) {
        List<NPadHospitalInfo> infos = new ArrayList<NPadHospitalInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 序号,名称,拼音头,地址,联系电话,备注,医院等级,医院专长,医疗设备,X坐标,Y坐标,联系信息 from THospital where 拼音头 like '%"
                    + pinyintou + "%' ";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadHospitalInfo info = new NPadHospitalInfo();
                info.setName(cursor.getString(1));
                info.setPinyin(cursor.getString(2));
                info.setDizhi(cursor.getString(3));
                info.setLinkTel(cursor.getString(4));
                info.setRemark(cursor.getString(5));
                info.setLevel(cursor.getString(6));
                info.setZhuanChang(cursor.getString(7));
                info.setSheBei(cursor.getString(8));
                info.setX(cursor.getDouble(9));
                info.setY(cursor.getDouble(10));
                info.setLink(cursor.getString(11));
                info.setDistance(0);
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return infos;
    }

    public List<NPadHospitalInfo> getHospitalbytype(String zc) {
        List<NPadHospitalInfo> infos = new ArrayList<NPadHospitalInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 序号,名称,拼音头,地址,联系电话,备注,医院等级,医院专长,医疗设备,X坐标,Y坐标,联系信息 from THospital where 医院专长 like '%"
                    + zc + "%' or 医疗设备 like '%" + zc + "%'";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadHospitalInfo info = new NPadHospitalInfo();
                info.setName(cursor.getString(1));
                info.setPinyin(cursor.getString(2));
                info.setDizhi(cursor.getString(3));
                info.setLinkTel(cursor.getString(4));
                info.setRemark(cursor.getString(5));
                info.setLevel(cursor.getString(6));
                info.setZhuanChang(cursor.getString(7));
                info.setSheBei(cursor.getString(8));
                info.setX(cursor.getDouble(9));
                info.setY(cursor.getDouble(10));
                info.setLink(cursor.getString(11));
                info.setDistance(0);
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return infos;
    }

    public List<NPadHospitalInfo> getHospitalbytype2(String zc, String level) {
        List<NPadHospitalInfo> infos = new ArrayList<NPadHospitalInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 序号,名称,拼音头,地址,联系电话,备注,医院等级,医院专长,医疗设备,X坐标,Y坐标,联系信息 from THospital where 医院等级='"
                    + level
                    + "'"
                    + " and (医院专长 like '%"
                    + zc
                    + "%' or 医疗设备 like '%" + zc + "%')";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadHospitalInfo info = new NPadHospitalInfo();
                info.setName(cursor.getString(1));
                info.setPinyin(cursor.getString(2));
                info.setDizhi(cursor.getString(3));
                info.setLinkTel(cursor.getString(4));
                info.setRemark(cursor.getString(5));
                info.setLevel(cursor.getString(6));
                info.setZhuanChang(cursor.getString(7));
                info.setSheBei(cursor.getString(8));
                info.setX(cursor.getDouble(9));
                info.setY(cursor.getDouble(10));
                info.setLink(cursor.getString(11));
                info.setDistance(0);
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return infos;
    }

    // 得到所有医院列表
    public List<NPadHospitalInfo> getAllHospital() {
        List<NPadHospitalInfo> infos = new ArrayList<NPadHospitalInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 序号,名称,拼音头,地址,联系电话,备注,医院等级,医院专长,医疗设备,X坐标,Y坐标,联系信息  from THospital ";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadHospitalInfo info = new NPadHospitalInfo();
                info.setName(cursor.getString(1));
                info.setPinyin(cursor.getString(2));
                info.setDizhi(cursor.getString(3));
                info.setLinkTel(cursor.getString(4));
                info.setRemark(cursor.getString(5));
                info.setLevel(cursor.getString(6));
                info.setZhuanChang(cursor.getString(7));
                info.setSheBei(cursor.getString(8));
                info.setX(cursor.getDouble(9));
                info.setY(cursor.getDouble(10));
                info.setLink(cursor.getString(11));
                info.setDistance(0);
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            String ret = e.getMessage();
            Log.d("得到所有医院列表", ret);
        }
        return infos;
    }

    // 根据医院等级得到医院
    public List<NPadHospitalInfo> getHospitalbyLevel(String level) {
        List<NPadHospitalInfo> infos = new ArrayList<NPadHospitalInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 序号,名称,拼音头,地址,联系电话,备注,医院等级,医院专长,医疗设备,X坐标,Y坐标,联系信息  from THospital where 医院等级='"
                    + level + "'";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadHospitalInfo info = new NPadHospitalInfo();
                info.setName(cursor.getString(1));
                info.setPinyin(cursor.getString(2));
                info.setDizhi(cursor.getString(3));
                info.setLinkTel(cursor.getString(4));
                info.setRemark(cursor.getString(5));
                info.setLevel(cursor.getString(6));
                info.setZhuanChang(cursor.getString(7));
                info.setSheBei(cursor.getString(8));
                info.setX(cursor.getDouble(9));
                info.setY(cursor.getDouble(10));
                info.setLink(cursor.getString(11));
                info.setDistance(0);
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            String ret = e.getMessage();
            Log.d("得到所有医院列表", ret);
        }
        return infos;
    }

    // 得到所有专家列表
    public List<NPadMasterInfo> getAllMaster() {
        List<NPadMasterInfo> infos = new ArrayList<NPadMasterInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 姓名,联系电话,备注,专长,职称  from TMaster ";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadMasterInfo info = new NPadMasterInfo();
                info.setName(cursor.getString(0));
                info.setLinkTel(cursor.getString(1));
                info.setRemark(cursor.getString(2));
                info.setZhuanChang(cursor.getString(3));
                info.setZhiCheng(cursor.getString(4));
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            String ret = e.getMessage();
            Log.d("得到所有专家列表", ret);
        }
        return infos;
    }

    public List<NPadMasterInfo> getMasterbyname(String nameString) {
        List<NPadMasterInfo> infos = new ArrayList<NPadMasterInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 姓名,联系电话,备注,专长,职称  from TMaster where 姓名 like '%"
                    + nameString + "%'";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadMasterInfo info = new NPadMasterInfo();
                info.setName(cursor.getString(0));
                info.setLinkTel(cursor.getString(1));
                info.setRemark(cursor.getString(2));
                info.setZhuanChang(cursor.getString(3));
                info.setZhiCheng(cursor.getString(4));
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            String ret = e.getMessage();
            Log.d("得到所有专家列表", ret);
        }
        return infos;
    }

    public List<NPadMasterInfo> getMasterbyzc(String zc) {
        List<NPadMasterInfo> infos = new ArrayList<NPadMasterInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 姓名,联系电话,备注,专长,职称  from TMaster where 专长 like '%"
                    + zc + "%'";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadMasterInfo info = new NPadMasterInfo();
                info.setName(cursor.getString(0));
                info.setLinkTel(cursor.getString(1));
                info.setRemark(cursor.getString(2));
                info.setZhuanChang(cursor.getString(3));
                info.setZhiCheng(cursor.getString(4));
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            String ret = e.getMessage();
            Log.d("得到所有专家列表", ret);
        }
        return infos;
    }

    // 拼音头查询判断
    public List<NPadJudgeInfo> getJudge(String pinyintou) {
        List<NPadJudgeInfo> infos = new ArrayList<NPadJudgeInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select 序号,名称,拼音头 from TJudgeInfo where 名称 like '%"
                    + pinyintou + "%' ";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadJudgeInfo info = new NPadJudgeInfo();
                info.setName(cursor.getString(1));
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            String ret = e.getMessage();
            Log.d("保存医院出错", ret);
        }
        return infos;
    }

    // 得到GPS配置
    // 添加非任务时间间隔，车辆编码字段 2015-07-06 肖明星 2015-7-16 修改 费晓波
    public NPadIntervalInfo GetGpsParam() {
        NPadIntervalInfo info = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select 是否定位,任务间隔,非任务间隔 ,下班间隔  from TGPSParam ";
        Cursor cursor = db.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            info = new NPadIntervalInfo();
            int gps = cursor.getInt(0);
            if (gps == 0)
                info.setIsGPS(false);
            else
                info.setIsGPS(true);
            int gpstask = cursor.getInt(1);
            info.setTaskInterval(gpstask);
            int gpsnotask = cursor.getInt(2);
            info.setStationInterval(gpsnotask);
            int gpsOffInterval = cursor.getInt(3);
            info.setOffInterval(gpsOffInterval);

        }
        cursor.close();
        db.close();
        return info;
    }

    // 保存Gps配置
    // 添加非任务时间间隔，车辆编码字段 2015-07-06 肖明星
    public void SaveGpsParam(int gps, int tasktime, int stationtime,
                             int offdutyTime, String ambCode) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select 序号 from TGPSParam";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String where = "序号 = ?";
            String[] whereValue = {Integer.toString(id)};
			/* 将修改的值放入ContentValues */
            ContentValues cv1 = new ContentValues();
            cv1.put("是否定位", gps);
            cv1.put("任务间隔", tasktime);
            cv1.put("非任务间隔", stationtime);
            cv1.put("下班间隔", offdutyTime);
            cv1.put("车辆编码", ambCode);
            db.update("TGPSParam", cv1, where, whereValue);
        } else {
            ContentValues cv2 = new ContentValues();
            cv2.put("是否定位", gps);
            cv2.put("任务间隔", tasktime);
            cv2.put("非任务间隔", stationtime);
            cv2.put("下班间隔", offdutyTime);
            cv2.put("车辆编码", ambCode);
            db.insert("TGPSParam", null, cv2);
        }
        cursor.close();
        db.close();
    }

    // 保存医院信息
    public String SaveHospital(List<NPadHospitalInfo> hinfos) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("THospital", null, null);
            for (NPadHospitalInfo info : hinfos) {
                ContentValues cv = new ContentValues();
                cv.put("名称", info.getName());
                cv.put("拼音头", info.getPinyin());
                cv.put("地址", info.getDizhi());
                cv.put("联系电话", info.getLinkTel());
                cv.put("备注", info.getRemark());
                cv.put("医院等级", info.getLevel());
                cv.put("医院专长", info.getZhuanChang());
                cv.put("医疗设备", info.getSheBei());
                cv.put("X坐标", info.getX());
                cv.put("Y坐标", info.getY());
                cv.put("联系信息", info.getLink());
                db.insert("THospital", null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ret = e.getMessage();
            Log.d("保存医院出错", ret);
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    // 保存专家
    public String SaveMaster(List<NPadMasterInfo> minfos) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("TMaster", null, null);
            for (NPadMasterInfo info : minfos) {
                ContentValues cv = new ContentValues();
                cv.put("姓名", info.getName());
                cv.put("联系电话", info.getLinkTel());
                cv.put("专长", info.getZhuanChang());
                cv.put("职称", info.getZhiCheng());
                cv.put("备注", info.getRemark());
                db.insert("TMaster", null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ret = e.getMessage();
            Log.d("保存专家出错", ret);
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    // 保存終止任務原因 2016-05-03 xmx
    public String SaveStopReason(List<StopReasonInfo> Srinfos) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("TStopReason", null, null);
            for (StopReasonInfo info : Srinfos) {
                ContentValues cv = new ContentValues();
                cv.put("Code", info.getCode());
                cv.put("Name", info.getName());
                db.insert("TStopReason", null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ret = e.getMessage();
            Log.d("保存终止任务原因出错！", ret);
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    // 获取暂停原因 2016-05-09
    public List<NPadStationInfo> GetStopReason() {
        List<NPadStationInfo> npInfo = new ArrayList<NPadStationInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select Code,Name from TStopReason";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadStationInfo info = new NPadStationInfo();
                info.setCode(cursor.getString(0));
                info.setName(cursor.getString(1));
                npInfo.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return npInfo;
    }

    // 保存暂停调用原因 2016-05-03 xmx
    public String SavePauseReason(List<PauseReasonInfo> Prinfos) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("TPauseReason", null, null);
            for (PauseReasonInfo info : Prinfos) {
                ContentValues cv = new ContentValues();
                cv.put("Code", info.getCode());
                cv.put("Name", info.getName());
                db.insert("TPauseReason", null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ret = e.getMessage();
            Log.d("保存暂停调用原因出错！", ret);
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    // 获取暂停原因 2016-05-09
    public List<NPadStationInfo> GetPauseReason() {
        List<NPadStationInfo> npInfo = new ArrayList<NPadStationInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select Code,Name from TPauseReason";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NPadStationInfo info = new NPadStationInfo();
                info.setCode(cursor.getString(0));
                info.setName(cursor.getString(1));
                npInfo.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return npInfo;
    }

    // 保存暂停调用原因 2016-05-03 xmx
    public String SaveAmb(List<PadAmbInfo> Painfos) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("TAmblauce", null, null);
            for (PadAmbInfo info : Painfos) {
                ContentValues cv = new ContentValues();
                cv.put("AmbCode", info.GetAmbCode());
                cv.put("RealSign", info.GetRealSign());
                db.insert("TAmblauce", null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ret = e.getMessage();
            Log.d("保存暂停调用原因出错！", ret);
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    // 保存判断
    public String SaveJudge(List<NPadJudgeInfo> jinfos) {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("TJudgeInfo", null, null);
            for (NPadJudgeInfo info : jinfos) {
                ContentValues cv = new ContentValues();
                cv.put("名称", info.getName());
                cv.put("拼音头", "");
                cv.put("上级编码", "");
                db.insert("TJudgeInfo", null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ret = e.getMessage();
            Log.d("保存最新出错", ret);
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    // 根据开头查车车牌号 2015-08-13 肖明星
    public List<DictionaryInfo> getAmbNumber(String kaitou) {
        List<DictionaryInfo> infos = new ArrayList<DictionaryInfo>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select [RealSign] from TAmblauce where RealSign like '%"
                    + kaitou + "%' ";
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                DictionaryInfo info = new DictionaryInfo();
                info.setName(cursor.getString(0));
                infos.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return infos;
    }

    // 得到分站名称 2016-05-05 肖明星
    public String GetStation() {
        String station = "";
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select StationName from centername";
        Cursor cursor = db.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            station = (cursor.getString(0));
        }
        cursor.close();
        db.close();
        return station;
    }

    // 得到暂停调用原因 2016-05-05 肖明星
    public String GetPauseReson() {
        String pauseReason = "";
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select PauseReasonName from centername";
        Cursor cursor = db.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            pauseReason = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return pauseReason;
    }

    // 保存分站名称 2015-11-06 肖明星
    public void SaveStation(String stationcode, String stationname) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select id from centername";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        String where = "id = ?";
        String[] whereValue = {Integer.toString(id)};
		/* 将修改的值放入ContentValues */
        ContentValues cv1 = new ContentValues();
        cv1.put("StationCode", stationcode);
        cv1.put("StationName", stationname);
        db.update("centername", cv1, where, whereValue);
        cursor.close();
        db.close();
    }

    // 得到分站名称 2016-05-05 肖明星
    public void SetPauseReson(String pausecode, String pausename) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select id from centername";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        String where = "id = ?";
        String[] whereValue = {Integer.toString(id)};
		/* 将修改的值放入ContentValues */
        ContentValues cv1 = new ContentValues();
        cv1.put("PauseReasonCode", pausecode);
        cv1.put("PauseReasonName", pausename);
        db.update("centername", cv1, where, whereValue);
        cursor.close();
        db.close();
    }
}



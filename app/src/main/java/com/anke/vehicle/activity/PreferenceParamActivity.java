package com.anke.vehicle.activity;

import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.anke.vehicle.R;
import com.anke.vehicle.database.DBHelper;
import com.anke.vehicle.status.TrueOrFalseStatus;
import com.anke.vehicle.utils.Utility;

public class PreferenceParamActivity extends PreferenceActivity {
    private DBHelper dbHelper;
    public  static Handler handler;
    boolean isChange = false;//只要IP地址 端口号 服务器端口号 电话号码 是否绑定车辆变化，任意值为true，重新连接服务器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        addPreferencesFromResource(R.xml.preferences);
        Preference ipPreference = findPreference("ServerIp");
        ipPreference.setOnPreferenceChangeListener(listener);
        ipPreference.setDefaultValue(Utility.getServerIp());

        Preference portPreference = findPreference("Port");
        portPreference.setOnPreferenceChangeListener(listener);
        portPreference.setDefaultValue(Utility.getPort());

        Preference hportPreference = findPreference("HttpPort");
        hportPreference.setOnPreferenceChangeListener(listener);
        hportPreference.setDefaultValue(Utility.getHttpport());

        Preference vuPreference = findPreference("phone");
        vuPreference.setOnPreferenceChangeListener(listener);
        vuPreference.setDefaultValue(Utility.getPhone());

        Preference loPreference = findPreference("islocation");
        loPreference.setOnPreferenceChangeListener(listener);
        loPreference.setDefaultValue(Utility.getIslocation());

        Preference bindtPreference = findPreference("isBindCar");
        bindtPreference.setOnPreferenceChangeListener(listener);
        bindtPreference.setDefaultValue(Utility.getIsBindCar());

        Preference stopPreference = findPreference("isStopTask");
        stopPreference.setOnPreferenceChangeListener(listener);
        stopPreference.setDefaultValue(Utility.getIsStopTask());

        Preference stationPreference = findPreference("isChangStation");
        stationPreference.setOnPreferenceChangeListener(listener);
        stationPreference.setDefaultValue(Utility.getIsChangStation());

        Preference pausePreference = findPreference("isPauseCall");
        pausePreference.setOnPreferenceChangeListener(listener);
        pausePreference.setDefaultValue(Utility.getIsPauseCall());

        Preference taskPreference = findPreference("isNewTask");
        taskPreference.setOnPreferenceChangeListener(listener);
        taskPreference.setDefaultValue(Utility.getIsNewTask());

        Preference gzPreference = findPreference("isGaoZhi");
        gzPreference.setOnPreferenceChangeListener(listener);
        gzPreference.setDefaultValue(Utility.getIsGaoZhi());

        Preference sfPreference = findPreference("isShouFei");
        sfPreference.setOnPreferenceChangeListener(listener);
        sfPreference.setDefaultValue(Utility.getIsShouFei());

        Preference sgPreference = findPreference("isAccdint");
        sgPreference.setOnPreferenceChangeListener(listener);
        sgPreference.setDefaultValue(Utility.getIsShiGu());

        Preference vsPreference = findPreference("VersionUrl");
        vsPreference.setOnPreferenceChangeListener(listener);
        vsPreference.setDefaultValue(Utility.getVersionUrl());

        Preference auPreference = findPreference("ApkUrl");
        auPreference.setOnPreferenceChangeListener(listener);
        auPreference.setDefaultValue(Utility.getApkUrl());
    }

    Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // TODO Auto-generated method stub
            if (preference.getKey().equals("ServerIp")) {
                Utility.setServerIp(newValue.toString());
                isChange = true;
            } else if (preference.getKey().equals("Port")) {
                Utility.setPort(newValue.toString());
                isChange = true;
            } else if (preference.getKey().equals("HttpPort")) {
                Utility.setHttpport(newValue.toString());
                isChange = true;
            } else if (preference.getKey().equals("phone")) {
                Utility.setPhone(newValue.toString());
                isChange = true;
            } else if (preference.getKey().equals("VersionUrl")) {
                Utility.setVersionUrl(newValue.toString());
            } else if (preference.getKey().equals("ApkUrl")) {
                Utility.setApkUrl(newValue.toString());
            }

            //是否上传GPS
            else if (preference.getKey().equals("islocation")) {
                if (newValue.toString().equals("true"))
                    Utility.setIslocation(TrueOrFalseStatus.TRUE);
                else
                    Utility.setIslocation(TrueOrFalseStatus.FAlSE);
            }

            //是否允许绑定车辆  2016-05-11
            else if (preference.getKey().equals("isBindCar")) {
                if (newValue.toString().equals("true"))
                    Utility.setIsBindCar(TrueOrFalseStatus.TRUE);
                else
                    Utility.setIsBindCar(TrueOrFalseStatus.FAlSE);
                isChange = true;
            }

            //是否允许终止任务
            else if (preference.getKey().equals("isStopTask")) {
                if (newValue.toString().equals("true"))
                    Utility.setIsStopTask(TrueOrFalseStatus.TRUE);
                else
                    Utility.setIsStopTask(TrueOrFalseStatus.FAlSE);
            }

            //是否允许修改分站
            else if (preference.getKey().equals("isChangStation")) {
                if (newValue.toString().equals("true"))
                    Utility.setIsChangStation(TrueOrFalseStatus.TRUE);
                else
                    Utility.setIsChangStation(TrueOrFalseStatus.FAlSE);
            }

            //是否允许暂停调用
            else if (preference.getKey().equals("isPauseCall")) {
                if (newValue.toString().equals("true"))
                    Utility.setIsPauseCall(TrueOrFalseStatus.TRUE);
                else
                    Utility.setIsPauseCall(TrueOrFalseStatus.FAlSE);
            }

            //是否允许新建任务
            else if (preference.getKey().equals("isNewTask")) {
                if (newValue.toString().equals("true"))
                    Utility.setIsNewTask(TrueOrFalseStatus.TRUE);
                else
                    Utility.setIsNewTask(TrueOrFalseStatus.FAlSE);
            }

            //是否允许暂停调用
            else if (preference.getKey().equals("isGaoZhi")) {
                if (newValue.toString().equals("true"))
                    Utility.setIsGaoZhi(TrueOrFalseStatus.TRUE);
                else
                    Utility.setIsGaoZhi(TrueOrFalseStatus.FAlSE);
            }

            //是否允许新建任务
            else if (preference.getKey().equals("isShouFei")) {
                if (newValue.toString().equals("true"))
                    Utility.setIsShouFei(TrueOrFalseStatus.TRUE);
                else
                    Utility.setIsShouFei(TrueOrFalseStatus.FAlSE);
            }

            //是否允许上传事故
            else if (preference.getKey().equals("isAccdint")) {
                if (newValue.toString().equals("true"))
                    Utility.setIsShiGu(TrueOrFalseStatus.TRUE);
                else
                    Utility.setIsShiGu(TrueOrFalseStatus.FAlSE);
            }
            dbHelper.SaveParameter(Utility.getServerIp(),
                    Utility.getPort(), Utility.getHttpport(),
                    Utility.getPhone(), Utility.getVersionUrl(),
                    Utility.getApkUrl(), Utility.getIslocation(),
                    Utility.getIsBindCar(), Utility.getIsStopTask(),
                    Utility.getIsChangStation(), Utility.getIsPauseCall(),
                    Utility.getIsNewTask(), Utility.getIsGaoZhi(),
                    Utility.getIsShouFei(), Utility.getIsShiGu());

            return true;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isChange){
            new Thread(){
                @Override
                public void run() {
                    handler.sendEmptyMessage(123);
                    isChange = false;
                }
            }.start();


        }

    }
}

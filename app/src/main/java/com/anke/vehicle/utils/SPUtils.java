package com.anke.vehicle.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/11/22 0022.
 *
 */
public class SPUtils {
    public static final String NAME = "config";

    /**
     * 向里面放入值
     * @param context
     * @param key
     * @param name
     */
    public static void putSP(Context context, String key,String name) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key,name).commit();
    }

    /**
     * 获取值
     * @param context
     * @param key
     * @return
     */
    public static String getSP(Context context,String key) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
       return sp.getString(key,"");
    }
    /**
    移除某个key值已经对应的值
     */
    public static void remove(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }
}

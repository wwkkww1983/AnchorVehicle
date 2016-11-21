package com.anke.vehicle.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anke.vehicle.R;
import com.anke.vehicle.entity.BaiduToGpsInfo;
import com.anke.vehicle.entity.DisplayInfo;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class Utility {
	private static String serverIp = "180.9.0.8";
	private static String port = "4567";
	private static String httpport = "8810";
	private static String phone = "18600000001";
	private static int versionCode = -1;
	public static String versionName = "";
	private static String versionUrl = "";
	private static String apkUrl = "";
	private static int islocation = 0;
	private static int isBindCar = 0;  //2016-05-11
	private static int isStopTask = 0;
	private static int isChangStation = 0;
	private static int isPauseCall = 0;
	private static int isNewTask = 0;
	private static int isGaoZhi = 0;
	private static int isShouFei = 0;
	private static int isShiGu = 0;
	private static boolean isTrue = false;	
	
	public static boolean isTrue() {
		return isTrue;
	}

	public static void setTrue(boolean isTrue) {
		Utility.isTrue = isTrue;
	}

	public static int getIslocation() {
		return islocation;
	}

	public static void setIslocation(int islocation) {
		Utility.islocation = islocation;
	}

	public static DisplayInfo dinfo = new DisplayInfo();
	public static long startTime;
	public static long endTime;

	/**
	 * 得到版本名 版本号
	 * @param context
     */
	public static void getParamAndVersionInfo(Context context) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			versionName = info.versionName; // 版本名，versionCode同理
			versionCode = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String GetDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String datetimeString = formatter.format(cal.getTime());
		return datetimeString;
	}

	public static void ToastShow(Context context, String str) {
//		Toast toast = Toast.makeText(context, Str, Toast.LENGTH_SHORT);
//		toast.show();
		Toast toast = new Toast(context);
		View v = View.inflate(context, R.layout.custometoast,null);
		TextView tv_toast = (TextView) v.findViewById(R.id.tv_toast);
		tv_toast.setText(str);
		toast.setView(v);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public static String getServerIp() {
		return serverIp;
	}

	public static void setServerIp(String ip) {
		serverIp = ip;
	}

	public static String getPort() {
		return port;
	}

	public static void setPort(String pt) {
		port = pt;
	}

	public static String getPhone() {
		return phone;
	}

	public static void setPhone(String phone) {
		Utility.phone = phone;
	}

	private static long lastClickTime;

	/**
	 * 状态按钮5秒内不能重复按
	 * @return
     */
	public static boolean isFastClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 5000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static boolean isTopActivity(Activity activity) {
		String packageName = "com.anke.vehicle.activity.MainActivity";
		ActivityManager activityManager = (ActivityManager) activity
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private static double EARTH_RADIUS = 6372.797;// 地球平均半径

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2,
			double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.ceil(s * 100 + .5) / 100;
		return s;
	}

	public static int getVersionCode() {
		return versionCode;
	}

	public static void setVersionCode(int versionCode) {
		Utility.versionCode = versionCode;
	}

	public static String getVersionUrl() {
		return versionUrl;
	}

	public static void setVersionUrl(String versionUrl) {
		Utility.versionUrl = versionUrl;
	}

	public static String getApkUrl() {
		return apkUrl;
	}

	public static void setApkUrl(String apkUrl) {
		Utility.apkUrl = apkUrl;
	}

	// 百度坐标转Gps84坐标  2015-05-18 xmx
	public static BaiduToGpsInfo BaiduToGps84(Double m_X, Double m_Y) {
		BaiduToGpsInfo lonlatList = new BaiduToGpsInfo();
		LatLng desLatLng = Gps84ToBaidu(m_X, m_Y);
		lonlatList.setX((m_X * 2 - (desLatLng.longitude)));
		lonlatList.setY((m_Y * 2 - (desLatLng.latitude)));
		return lonlatList;
	}

	// Gps84转百度坐标 2015-05-18 xmx
	public static LatLng Gps84ToBaidu(Double m_X, Double m_Y) {
		// 定位坐标转百度坐标
		LatLng pt_start = new LatLng(m_Y, m_X);
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);
		// 待转换坐标
		converter.coord(pt_start);
		LatLng desLatLng = converter.convert();
		return desLatLng;
	}
	
	// /如果key对应的值为null则赋值空
		public static String GetJsonValue(String keyString, JSONObject objjson) {
			try {
				if (objjson.isNull(keyString)) {
					return "";
				} else {
					return objjson.getString(keyString);
				}
			} catch (Exception e) {
				return "";
			}
		}

		public static int getIsBindCar() {
			return isBindCar;
		}

		public static void setIsBindCar(int isBindCar) {
			Utility.isBindCar = isBindCar;
		}

		public static int getIsStopTask() {
			return isStopTask;
		}

		public static void setIsStopTask(int isStopTask) {
			Utility.isStopTask = isStopTask;
		}

		public static int getIsChangStation() {
			return isChangStation;
		}

		public static void setIsChangStation(int isChangStation) {
			Utility.isChangStation = isChangStation;
		}

		public static int getIsPauseCall() {
			return isPauseCall;
		}

		public static void setIsPauseCall(int isPauseCall) {
			Utility.isPauseCall = isPauseCall;
		}

		public static int getIsNewTask() {
			return isNewTask;
		}

		public static void setIsNewTask(int isNewTask) {
			Utility.isNewTask = isNewTask;
		}

		public static int getIsGaoZhi() {
			return isGaoZhi;
		}

		public static void setIsGaoZhi(int isGaoZhi) {
			Utility.isGaoZhi = isGaoZhi;
		}

		public static int getIsShouFei() {
			return isShouFei;
		}

		public static void setIsShouFei(int isShouFei) {
			Utility.isShouFei = isShouFei;
		}

		public static int getIsShiGu() {
			return isShiGu;
		}

		public static void setIsShiGu(int isShiGu) {
			Utility.isShiGu = isShiGu;
		}

		public static String getHttpport() {
			return httpport;
		}

		public static void setHttpport(String httpport) {
			Utility.httpport = httpport;
		}	
}

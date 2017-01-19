package com.anke.vehicle.application;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyApplication extends Application
{
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	public Vibrator mVibrator;
	public int i = 0;
	//接口
	public interface ICallBack {
	    public void onSuccess(BDLocation mLocation);	    
	}	
	private ICallBack mCallBack;
	
	public void setCallBack(ICallBack iCallBack)
	{
		mCallBack = iCallBack;
	}
	
	//public TextView tView;
	@Override
	public void onCreate() {
		super.onCreate();
		
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(getApplicationContext());
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(getApplicationContext());		
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
	}
	
	/**
	 * 监听函数   2015-04-18 XMX
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 	
			i = location.getLocType();
			if(mCallBack!=null)
				mCallBack.onSuccess(location);
		}
	}

	private static Map<String,Activity> destoryMap = new HashMap<>();
	/**
	 * 添加到销毁队列
	 * @param activity 要销毁的activity
	 */

	public static void addDestoryActivity(Activity activity,String activityName) {
		destoryMap.put(activityName,activity);
	}
	/**
	 *销毁指定Activity
	 */
	public static void destoryActivity(String activityName) {
		Set<String> keySet=destoryMap.keySet();
		for (String key:keySet){
			if (activityName == key)
			destoryMap.get(key).finish();
		}
	}
}

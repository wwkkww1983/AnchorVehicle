package com.anke.vehicle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.anke.vehicle.activity.MainActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {
    //当Android启动时，会发出一个系统广播，内容为ACTION_BOOT_COMPLETED，它的字
	//符串常量表示为 android.intent.action.BOOT_COMPLETED
	//2015-07-06 肖明星
	//接受广播消息
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//判断接收到的广播 2015-07-06 肖明星
		 if (intent.getAction().equals(ACTION)){
			   Intent Mainintent = new Intent(context,MainActivity.class);
			 //区别于默认优先启动在activity栈中已经存在的activity（如果之前启动过，
			  //并还没有被destroy的话）而是无论是否存在，都重新启动新的activity
			   Mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			   context.startActivity(Mainintent);  //启动
			  }
	}
}

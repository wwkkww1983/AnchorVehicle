package com.anke.vehicle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.anke.vehicle.status.ServerStatus;

public class MyReceiver extends BroadcastReceiver {
	public static Handler myHandler;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// 该发送心跳了  2016-01-18  xmx
		String msg = intent.getStringExtra("msg");
		if (null != msg && msg.equals("ok") && myHandler != null) {
			myHandler.sendEmptyMessage(ServerStatus.RECONNRCTING);
		}
	}
}

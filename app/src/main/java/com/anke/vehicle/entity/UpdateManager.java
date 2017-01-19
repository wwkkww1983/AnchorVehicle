package com.anke.vehicle.entity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.anke.vehicle.R;
import com.anke.vehicle.utils.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateManager
{
	private Context mContext;
	// 提示语
	private String updateMsg = "有最新的软件包哦，亲快下载吧~";
	// version url
	// private String versionUrl =
	// 返回的安装包url
	// private String apkUrl =
	// "http://180.9.0.42/BJPatientRecord/PatientRecord.apk";
	private Dialog noticeDialog;
	// "http://180.9.0.42/BJPatientRecord/version.txt";
	private Dialog downloadDialog;
	/* 下载包安装路径 */
	private static final String savePath = Environment.getExternalStorageDirectory() + "/";
	private static final String saveFileName = savePath + "UpdateVehicle.apk";
	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private static final int DOWN_NEWVERSION = 3;// 有新版本
	private static final int DOWN_NONE = 4;// 没有新版本
	private static final int DOWN_ERROR = 5;// 检验更新出错
	private int progress;
	private Thread downLoadThread;
	private boolean interceptFlag = false;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;
			case DOWN_NEWVERSION:
				showNoticeDialog();
				break;
			case DOWN_NONE:
				Utility.ToastShow(mContext, "没有新的版本！");
				break;
			case DOWN_ERROR:
				Utility.ToastShow(mContext, "检查更新出错,可能没有网络！");
				break;
			default:
				break;
			}
		}
	};

	public UpdateManager(Context context)
	{
		this.mContext = context;
	}

	// 外部接口让主Activity调用
	public void checkUpdateInfo()
	{
		// showNoticeDialog();
		Thread getVersionThread = new Thread(mgetVersionRunnable);
		getVersionThread.start();
	}

	private void showNoticeDialog()
	{
		AlertDialog.Builder builder = new Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("软件版本更新");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("下载", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showDownloadDialog()
	{
		AlertDialog.Builder builder = new Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("软件版本更新");

		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.downloadprogress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);

		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();

		downloadApk();
	}

	private Runnable mgetVersionRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			StringBuilder sb = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			// 设置网络超时参数
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);
			try
			{
				HttpResponse response = client.execute(new HttpGet(Utility
						.getVersionUrl()));
				HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(entity.getContent(), "UTF-8"),
							8192);
					String line = null;
					while ((line = reader.readLine()) != null)
					{
						sb.append(line + "\n");
					}
					reader.close();
				}
				JSONObject obj = new JSONObject(sb.toString());
				int newVersionCode = obj.getInt("VersionCode");
				if (newVersionCode > Utility.getVersionCode())
				{
					mHandler.sendEmptyMessage(DOWN_NEWVERSION); // 有新版本
				} else
				{
					mHandler.sendEmptyMessage(DOWN_NONE); // 没有新版本
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();
				mHandler.sendEmptyMessage(DOWN_ERROR); // 出错
			}
		}
	};

	private Runnable mdownApkRunnable = new Runnable()
	{
		@Override
		public void run()
		{

			try
			{
				URL url = new URL(Utility.getApkUrl());
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists())
				{
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				int count = 0;
				byte buf[] = new byte[1024];

				do
				{
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0)
					{
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.
				is.close();
				fos.close();
				conn.disconnect();
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	};

	/**
	 * 下载apk
	 * 
	 * @param
	 */
	private void downloadApk()
	{
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param
	 */
	private void installApk()
	{
		File apkfile = new File(saveFileName);
		if (!apkfile.exists())
		{
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
	
	
}

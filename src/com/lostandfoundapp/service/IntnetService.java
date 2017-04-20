package com.lostandfoundapp.service;

import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.utils.ProgressStateListener;
import com.lostandfoundapp.utils.ToastUtils;
import com.lostandfoundapp.view.ProgressToast;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 网络相关服务
 * 
 * @author lee
 *
 */
public class IntnetService extends Service {
	public static boolean INTNET;
	private ProgressToast toast;
	public static ProgressStateListener listener;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what==MyValues.TestWHAT) {
				String result = (String) msg.obj;
				if (result.equals("Testing")) {
					INTNET = true;
					Log.i("LAG", "网络连接测试通过");
				}else {
					INTNET = false;
					ToastUtils.textToast(IntnetService.this, result);
				}
			}
		};
	};
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		toast = ProgressToast.makeToast(this);
		listener = new ProgressStateListener() {
			
			@Override
			public void stateChange(int state) {
				// TODO Auto-generated method stub
				switch (state) {
				case 1://状态为1，认为开始网络访问
					toast.show();
					break;

				default://状态为0，认为网络访问结束
					toast.cancle();
					break;
				}
			}
		};
		isIntnet();
		return super.onStartCommand(intent, flags, startId);
	}

	private void isIntnet() {
		// 判断本机网络是否可用
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			// 当前无可用网络
			INTNET = false;
			ToastUtils.textToast(this, "当前网络不可用");
		} else {
			// 当前有可用网络
			OpenVolley.strin(MyValues.TestURL, MyValues.TestTAG, null, new ResponseListener() {
				
				@Override
				public void onSuccess(String msg) {
					// TODO Auto-generated method stub
					onError(msg);
				}
				
				@Override
				public void onError(String msg) {
					// TODO Auto-generated method stub
					Message m = Message.obtain();
					m.obj = msg;
					m.what = MyValues.TestWHAT;
					handler.sendMessage(m);
				}
			});
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}

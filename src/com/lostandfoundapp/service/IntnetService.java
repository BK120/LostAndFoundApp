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
 * ������ط���
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
					Log.i("LAG", "�������Ӳ���ͨ��");
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
				case 1://״̬Ϊ1����Ϊ��ʼ�������
					toast.show();
					break;

				default://״̬Ϊ0����Ϊ������ʽ���
					toast.cancle();
					break;
				}
			}
		};
		isIntnet();
		return super.onStartCommand(intent, flags, startId);
	}

	private void isIntnet() {
		// �жϱ��������Ƿ����
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			// ��ǰ�޿�������
			INTNET = false;
			ToastUtils.textToast(this, "��ǰ���粻����");
		} else {
			// ��ǰ�п�������
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

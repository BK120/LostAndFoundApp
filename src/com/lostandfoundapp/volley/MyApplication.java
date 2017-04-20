package com.lostandfoundapp.volley;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Application;

/**
 * 创建全局的请求队列
 * 
 * @author Administrator
 *
 */
public class MyApplication extends Application {
	private static RequestQueue queue;

	@Override
	public void onCreate() {
		super.onCreate();
		queue = Volley.newRequestQueue(getApplicationContext());
	}

	public static RequestQueue getHttpQueue() {
		return queue;
	}

}

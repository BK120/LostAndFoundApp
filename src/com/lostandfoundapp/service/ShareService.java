package com.lostandfoundapp.service;

import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.share.ShareToQQListener;
import com.tencent.tauth.Tencent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ShareService extends Service {
	public static Tencent mtencent;
	public static ShareToQQListener mlistener;
	//public static IWXAPI api;

	@Override
	public IBinder onBind(Intent intent) {
		// 绑定时初始化
		mtencent = Tencent.createInstance(MyValues.TencentAPPID,
				this.getApplicationContext());
		mlistener = new ShareToQQListener();
		//由于微信开发无法通过审核，放弃该功能
//		api = WXAPIFactory.createWXAPI(this, MyValues.W_APPID, true);
//		api.registerApp(MyValues.W_APPID);
//		api.handleIntent(new Intent(), new ShareToXinListener());
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// 解绑时释放资源
		mtencent.releaseResource();
		//api.detach();
		return super.onUnbind(intent);
	}
}

package com.lostandfoundapp.share;

import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
/**
 * 消息分享到QQ结果回调接口
 * @author lee
 *
 */
public class ShareToQQListener implements IUiListener{

	@Override
	public void onCancel() {
		// 取消分享
		Log.i("LAF", "取消分享");
	}

	@Override
	public void onComplete(Object arg0) {
		// 分享成功
		Log.i("LAF", "分享成功");
	}

	@Override
	public void onError(UiError arg0) {
		// 分享出错
		Log.i("LAF", "分享错误");
	}

}

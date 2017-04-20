package com.lostandfoundapp.net;

import com.lostandfoundapp.volley.ResponseListener;

import android.graphics.Bitmap;
import android.widget.ImageView;
/**
 * HttpUrlConnetion异步任务开启工具
 * @author lee
 *
 */
public class OpenHttpUCTask {
	/**
	 * 开启异步加载图片
	 * @param urlPath
	 * @param fileName
	 * @param view
	 */
	public static void downloadPic(String urlPath, String fileName,
			ImageView view) {
		DownloadPicTask task = new DownloadPicTask(fileName, view);
		task.execute(urlPath);
	}
	/**
	 * 开启异步上传图片
	 * @param url
	 * @param bitmap
	 * @param listener
	 */
	public static void savaPic(String url, Bitmap bitmap,
			ResponseListener listener) {
		SavaPicTask task = new SavaPicTask(bitmap, listener);
		task.execute(url);
	}
}

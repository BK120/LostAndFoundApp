package com.lostandfoundapp.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lostandfoundapp.utils.SystemUtils;
import com.lostandfoundapp.volley.ResponseListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * HttpUrlConnection网络连接工具
 * 
 * @author lee
 *
 */
public class HttpURLConnectionUtils {
	/**
	 * 上传图片到服务器
	 * 
	 * @param url
	 * @param bitmap
	 * @param response
	 */
	public static void savaPic(String url, Bitmap bitmap,
			ResponseListener response) {
		try {
			URL u = new URL(url);// 获取URL
			HttpURLConnection connection = (HttpURLConnection) u
					.openConnection();// 获取连接对象
			connection.setRequestMethod("POST");
			OutputStream outputStream = connection.getOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
			outputStream.flush();
			outputStream.close();
			InputStream inputStream = connection.getInputStream();
			byte[] bytes = new byte[1024];
			StringBuffer buffer = new StringBuffer();
			while ((inputStream.read(bytes)) != -1) {
				buffer.append(new String(bytes, "GBK"));
			}
			if (response != null) {
				response.onSuccess(buffer.toString());
			}
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从服务器下载图片，保存至本地并为图片控件设置
	 * 
	 * @param urlPath
	 * @param fileName
	 * @param view
	 */
	public static void downloadPic(String urlPath, String fileName,
			ImageView view) {
		try {
			URL url = new URL(urlPath + fileName + ".jpg");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			InputStream stream = connection.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(stream);
			if (bitmap != null) {
				view.setImageBitmap(bitmap);
				SystemUtils.savaPic(bitmap, fileName);
			}
			stream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

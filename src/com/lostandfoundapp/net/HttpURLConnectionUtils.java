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
 * HttpUrlConnection�������ӹ���
 * 
 * @author lee
 *
 */
public class HttpURLConnectionUtils {
	/**
	 * �ϴ�ͼƬ��������
	 * 
	 * @param url
	 * @param bitmap
	 * @param response
	 */
	public static void savaPic(String url, Bitmap bitmap,
			ResponseListener response) {
		try {
			URL u = new URL(url);// ��ȡURL
			HttpURLConnection connection = (HttpURLConnection) u
					.openConnection();// ��ȡ���Ӷ���
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
	 * �ӷ���������ͼƬ�����������ز�ΪͼƬ�ؼ�����
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

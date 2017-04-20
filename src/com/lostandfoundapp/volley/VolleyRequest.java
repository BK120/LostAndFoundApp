package com.lostandfoundapp.volley;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import com.lostandfoundapp.utils.SystemUtils;

/**
 * Volley�������ʽ
 * 
 * @author Administrator
 *
 */
public class VolleyRequest {
	private static String str;

	/**
	 * ����Volley-StringRequest����
	 * 
	 * @param url
	 * @param tag
	 * @param map
	 * @return
	 */
	public static String stringRequest(String url, String tag,
			final Map<String, String> map) {
		str = "";
		MyApplication.getHttpQueue().cancelAll(tag);
		StringRequest request = new StringRequest(Method.POST, url,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// �������ӳɹ�������String
						try {
							// Volley��������صķ�����Ĭ����ΪISO-8859-1���룬���������ΪJava������Ĭ�ϱ���ΪGBK
							// ��������Ҫ����������ΪGBK������ȷ��ʾ
							str = new String(response.getBytes("ISO-8859-1"),
									"GBK");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// ��������ʧ�ܣ��׳���ʱ�쳣
						str = "��������ʧ��";
						System.out.println(error.toString());
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				return map;
			}
		};
		request.setTag(tag);
		MyApplication.getHttpQueue().add(request);
		MyApplication.getHttpQueue().start();
		SystemUtils.sleep(500);
		if (TextUtils.isEmpty(str)) {
			SystemUtils.sleep(7500);
		}
		Log.i("LAF", str);
		return str;
	}

	/**
	 * Volley-JsonObjectRequest����
	 * 
	 * @param url
	 * @param tag
	 * @param json
	 * @return
	 */
	public static String jsonObjectRequest(String url, String tag,
			JSONObject json) {
		str = "";
		MyApplication.getHttpQueue().cancelAll(tag);
		JsonObjectRequest request = new JsonObjectRequest(Method.POST, url,
				json, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// ����ɹ����ӣ�����JSON
						String string = response.toString();
						try {
							str = new String(string.getBytes("ISO-8859-1"),
									"GBK");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// ��������ʧ�ܣ��׳��쳣
						str = "{\"result\":\"��������ʧ��\"}";
						if (error != null) {
							System.out.println("VolleyError:"
									+ error.toString());
						}
					}
				});
		request.setTag(tag);
		MyApplication.getHttpQueue().add(request);
		MyApplication.getHttpQueue().start();
		SystemUtils.sleep(500);
		if (TextUtils.isEmpty(str)) {
			SystemUtils.sleep(7500);
		}
		Log.i("LAF", str);
		return str;
	}

	/**
	 * Volley-ImageRequest ���������ͼƬΪImageView����
	 * 
	 * @param image
	 *            ͼƬ�ؼ�
	 * @param defaultImage
	 *            Ĭ����ʾͼƬ
	 * @param errorImage
	 *            �޷�����ͼƬʱ��ʾ
	 * @param url
	 *            ͼƬ��ַ
	 * @param tag
	 *            ��ǩ
	 */
	public static void imageRequest(final ImageView image, int defaultImage,
			int errorImage, String url, String tag) {
		MyApplication.getHttpQueue().cancelAll(tag);
		ImageRequest request = new ImageRequest(url, new Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap response) {
				// TODO Auto-generated method stub
				image.setImageBitmap(response);
			}
		}, 100, 100, Config.RGB_565, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				System.out.println(error.toString());
				error.printStackTrace();
			}
		});
		request.setTag(tag);
		MyApplication.getHttpQueue().add(request);
		MyApplication.getHttpQueue().start();
	}

	/**
	 * ͨ��Volley-ImageLoader �������ȡͼƬΪImageView����
	 * 
	 * @param image
	 * @param defaultImage
	 * @param errorImage
	 * @param url
	 */
	public static void imageLoadSetImg(ImageView image, int defaultImage,
			int errorImage, String url) {
		ImageLoader il = new ImageLoader(MyApplication.getHttpQueue(),
				new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(image,
				defaultImage, errorImage);
		il.get(url, listener);
	}

	/**
	 * ͨ��Volley-ImageLoader �������ȡͼƬΪNetworkImageView����
	 * 
	 * @param ntiv
	 *            NetworkImageViewΪVolley�����һ��ͼƬ�ؼ�
	 * @param defaultImage
	 * @param errorImage
	 * @param url
	 */
	public static void imageLoadSetImg(NetworkImageView ntiv, int defaultImage,
			int errorImage, String url) {
		ImageLoader il = new ImageLoader(MyApplication.getHttpQueue(),
				new BitmapCache());
		ntiv.setDefaultImageResId(defaultImage);
		ntiv.setErrorImageResId(errorImage);
		ntiv.setImageUrl(url, il);
	}
}

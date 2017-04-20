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
 * Volley框架请求方式
 * 
 * @author Administrator
 *
 */
public class VolleyRequest {
	private static String str;

	/**
	 * 发送Volley-StringRequest请求
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
						// 网络连接成功，返回String
						try {
							// Volley框架所传回的反馈被默认设为ISO-8859-1编码，而服务端作为Java服务器默认编码为GBK
							// 故这里需要将编码设置为GBK才能正确显示
							str = new String(response.getBytes("ISO-8859-1"),
									"GBK");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// 网络连接失败，抛出超时异常
						str = "网络连接失败";
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
	 * Volley-JsonObjectRequest请求
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
						// 网络成功连接，返回JSON
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
						// 网络连接失败，抛出异常
						str = "{\"result\":\"网络连接失败\"}";
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
	 * Volley-ImageRequest 从网络加载图片为ImageView设置
	 * 
	 * @param image
	 *            图片控件
	 * @param defaultImage
	 *            默认显示图片
	 * @param errorImage
	 *            无法加载图片时显示
	 * @param url
	 *            图片地址
	 * @param tag
	 *            标签
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
	 * 通过Volley-ImageLoader 从网络获取图片为ImageView设置
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
	 * 通过Volley-ImageLoader 从网络获取图片为NetworkImageView设置
	 * 
	 * @param ntiv
	 *            NetworkImageView为Volley框架中一种图片控件
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

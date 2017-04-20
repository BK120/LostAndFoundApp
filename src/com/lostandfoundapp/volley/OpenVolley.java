package com.lostandfoundapp.volley;

import java.util.Map;

import org.json.JSONObject;

/**
 * 开启异步加载工具
 * 
 * @author lee
 *
 */
public class OpenVolley {
	/**
	 * 开启JSON请求
	 * 
	 * @param url
	 * @param tag
	 * @param json
	 * @param listener
	 */
	public static void json(String url, String tag, JSONObject json,
			ResponseListener listener) {
		JsonVolleyTask task = new JsonVolleyTask(tag, json, listener);
		task.execute(url);
	}

	/**
	 * 开启String请求
	 * 
	 * @param url
	 * @param tag
	 * @param map
	 * @param listener
	 */
	public static void strin(String url, String tag, Map<String, String> map,
			ResponseListener listener) {
		StringVolleyTask task = new StringVolleyTask(tag, map, listener);
		task.execute(url);
	}

}

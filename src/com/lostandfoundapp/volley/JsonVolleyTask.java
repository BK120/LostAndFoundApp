package com.lostandfoundapp.volley;

import org.json.JSONObject;

import com.lostandfoundapp.service.IntnetService;

import android.os.AsyncTask;
/**
 * JSON请求的异步任务封装
 * @author lee
 *
 */
public class JsonVolleyTask extends AsyncTask<String, Integer, String>{
	private String tag;
	private JSONObject json;
	private ResponseListener listener;

	public JsonVolleyTask(String tag, JSONObject json, ResponseListener listener) {
		super();
		this.tag = tag;
		this.json = json;
		this.listener = listener;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		IntnetService.listener.stateChange(1);
		return VolleyRequest.jsonObjectRequest(params[0], tag, json);
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		IntnetService.listener.stateChange(0);
		if (listener!=null) {
			listener.onSuccess(result);
		}
	}

}

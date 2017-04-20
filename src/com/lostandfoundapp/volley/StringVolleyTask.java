package com.lostandfoundapp.volley;

import java.util.Map;

import com.lostandfoundapp.service.IntnetService;

import android.os.AsyncTask;
/**
 * String请求的一部任务封装
 * @author lee
 *
 */
public class StringVolleyTask extends AsyncTask<String, Integer, String>{
	private String tag;
	private Map<String, String> map;
	private ResponseListener listener;
	public StringVolleyTask(String tag, Map<String, String> map,
			ResponseListener listener) {
		super();
		this.tag = tag;
		this.map = map;
		this.listener = listener;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		IntnetService.listener.stateChange(1);
		String str = VolleyRequest.stringRequest(params[0], tag, map);
		return str;
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

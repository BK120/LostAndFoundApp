package com.lostandfoundapp.net;

import com.lostandfoundapp.volley.ResponseListener;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * �ϴ�ͼƬ�첽�����װ
 * 
 * @author Administrator
 *
 */
public class SavaPicTask extends AsyncTask<String, Integer, String> {
	private Bitmap bitmap;
	private ResponseListener listener;

	public SavaPicTask(Bitmap bitmap, ResponseListener listener) {
		super();
		this.bitmap = bitmap;
		this.listener = listener;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		HttpURLConnectionUtils.savaPic(params[0], bitmap, listener);
		return null;
	}

}

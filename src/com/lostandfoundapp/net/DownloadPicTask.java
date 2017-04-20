package com.lostandfoundapp.net;

import android.os.AsyncTask;
import android.widget.ImageView;
/**
 * “Ï≤Ωº”‘ÿÕº∆¨
 * @author lee
 *
 */
public class DownloadPicTask extends AsyncTask<String, Integer, Void>{
	private String fileName;
	private ImageView view;
	
	
	public DownloadPicTask(String fileName, ImageView view) {
		super();
		this.fileName = fileName;
		this.view = view;
	}

	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		HttpURLConnectionUtils.downloadPic(params[0], fileName, view);
		return null;
	}

}

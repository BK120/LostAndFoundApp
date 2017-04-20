package com.lostandfoundapp.view;

import com.lostandfoundapp.activity.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
/**
 * 自定义Toast，其中有一个圆形进度条
 * 包括创建Toast、显示和取消方法
 * @author lee
 *
 */
public class ProgressToast {
	private Toast pToast;
	/**
	 * 构造器
	 * @param context
	 */
	@SuppressLint("InflateParams")
	private ProgressToast(Context context) {
		pToast = new Toast(context);
		View view = LayoutInflater.from(context).inflate(
				R.layout.progress_toast_view, null);
		pToast.setView(view);
		pToast.setDuration(8000);
	}
	/**
	 * 创建Toast
	 * @param context
	 * @return
	 */
	public static ProgressToast makeToast(Context context) {
		return new ProgressToast(context);
	}
	/**
	 * 显示Toast
	 */
	public void show() {
		if (pToast != null) {
			pToast.show();
		}
	}
	/**
	 * 取消Toast
	 */
	public void cancle() {
		if (pToast != null) {
			pToast.cancel();
		}
	}
}

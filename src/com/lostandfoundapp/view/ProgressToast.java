package com.lostandfoundapp.view;

import com.lostandfoundapp.activity.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
/**
 * �Զ���Toast��������һ��Բ�ν�����
 * ��������Toast����ʾ��ȡ������
 * @author lee
 *
 */
public class ProgressToast {
	private Toast pToast;
	/**
	 * ������
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
	 * ����Toast
	 * @param context
	 * @return
	 */
	public static ProgressToast makeToast(Context context) {
		return new ProgressToast(context);
	}
	/**
	 * ��ʾToast
	 */
	public void show() {
		if (pToast != null) {
			pToast.show();
		}
	}
	/**
	 * ȡ��Toast
	 */
	public void cancle() {
		if (pToast != null) {
			pToast.cancel();
		}
	}
}

package com.lostandfoundapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * ��˿������
 * @author lee
 *
 */
public class ToastUtils {
	/**
	 * ��ʾ�ı���˿
	 * @param context
	 * @param text
	 */
	public static void textToast(Context context,String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}

package com.lostandfoundapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐丝工具类
 * @author lee
 *
 */
public class ToastUtils {
	/**
	 * 显示文本吐丝
	 * @param context
	 * @param text
	 */
	public static void textToast(Context context,String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}

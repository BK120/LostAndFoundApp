package com.lostandfoundapp.utils;

import com.lostandfoundapp.data.MyValues;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences������ ������Ҫ�Ƿ�װ��SharedPreferences�洢String��boolean�����������������ݵķ���
 * 
 * @author lee
 *
 */
public class SharedPrefUtils {
	private static SharedPreferences sharedPreferences;
	private static Editor editor;

	/**
	 * ����boolean��������
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putBoolean(Context context, String key, boolean value) {
		sharedPreferences = context.getSharedPreferences(MyValues.LAF,
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * ȡ��boolean��������
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key) {
		sharedPreferences = context.getSharedPreferences(MyValues.LAF,
				Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, false);
	}

	/**
	 * ����String��������
	 * 
	 * @param context
	 * @param key
	 */
	public static void putString(Context context, String key, String value) {
		sharedPreferences = context.getSharedPreferences(MyValues.LAF,
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * ȡ��String��������
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		sharedPreferences = context.getSharedPreferences(MyValues.LAF,
				Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, defValue);
	}

	/**
	 * ������������
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putInt(Context context, String key, int value) {
		sharedPreferences = context.getSharedPreferences(MyValues.LAF,
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * ȡ����������
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context, String key, int defValue) {
		sharedPreferences = context.getSharedPreferences(MyValues.LAF,
				Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, defValue);
	}

}

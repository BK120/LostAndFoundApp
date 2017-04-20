package com.lostandfoundapp.utils;

import com.lostandfoundapp.data.MyValues;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences工具类 该类主要是封装了SharedPreferences存储String、boolean、整型三种类型数据的方法
 * 
 * @author lee
 *
 */
public class SharedPrefUtils {
	private static SharedPreferences sharedPreferences;
	private static Editor editor;

	/**
	 * 存入boolean类型数据
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
	 * 取出boolean类型数据
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
	 * 存入String类型数据
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
	 * 取出String类型数据
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
	 * 存入整型数据
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
	 * 取出整型数据
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

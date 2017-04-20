package com.lostandfoundapp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import com.lostandfoundapp.listener.IntFeedBack;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * 自定义系统工具类 该类主要封装了获取屏幕宽高、控件宽高、外边距、系统时间，字符串是否包含非法字符判断等方法
 * 设置字符编码、获取手机指定路径图片、保存图片到指定路径、判断SD卡是否可用、线程睡眠
 * 
 * @author lee
 *
 */
public class SystemUtils {
	/**
	 * 获取DisplayMetrics类以获取屏幕宽高
	 * 
	 * @param context
	 * @return
	 */
	private static DisplayMetrics getDM(Context context) {
		Resources resources = context.getResources();
		return resources.getDisplayMetrics();
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param dm
	 * @return
	 */
	public static int getSysWidth(Context context) {
		return getDM(context).widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param dm
	 * @return
	 */
	public static int getSysHeight(Context context) {
		return getDM(context).heightPixels;
	}

	/**
	 * 通过日历获取当前年月日时分秒
	 * 
	 * @return
	 */
	public static int[] getDate() {
		int[] dates = new int[6];
		Calendar calendar = Calendar.getInstance();
		dates[0] = calendar.get(Calendar.YEAR);
		dates[1] = calendar.get(Calendar.MONTH)+1;
		dates[2] = calendar.get(Calendar.DAY_OF_MONTH);
		dates[3] = calendar.get(Calendar.HOUR_OF_DAY);
		dates[4] = calendar.get(Calendar.MINUTE);
		dates[5] = calendar.get(Calendar.SECOND);
		return dates;
	}

	/**
	 * 获取控件宽高、外边距
	 * 
	 * @param view
	 * @return
	 */
	public static void getViewWH(final View view, final IntFeedBack listener) {
		view.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						MarginLayoutParams mp = (MarginLayoutParams) view
								.getLayoutParams();
						listener.feedBack(view.getWidth(), view.getHeight(),
								mp.leftMargin, mp.rightMargin, mp.topMargin,
								mp.bottomMargin);
					}
				});
	}
	/**
	 * 判断字符串中是否包含非法字符\\W
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isCotain(String str) {
		boolean flag = false;
		char[] a_c = str.toCharArray();
		String[] a_s = new String[a_c.length];
		for (int i = 0; i < a_c.length; i++)
			a_s[i] = String.valueOf(a_c[i]);
		for (String s : a_s)
			if (s.matches("\\W")) {
				flag = true;
				break;
			}
		return flag;
	}
	/**
	 * 为字符串设置指定编码类型
	 * @param str
	 * @param chara
	 * @return
	 */
	public static String setChar(String str,String chara){
		String result = "";
		try {
			result = new String(str.getBytes(),chara);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 获取用户头像图片
	 * @return
	 */
	public static Bitmap getUserPic(String fileName){
		if (isSDNormal()) {
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			String picPath = path + "/" + fileName;
			return BitmapFactory.decodeFile(picPath);
		}else {
			return null;
		}
	}
	/**
	 * 保存用户头像图片
	 * @param bitmap
	 */
	public static void savaPic(Bitmap bitmap,String fileName){
		if (isSDNormal()) {
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			File tempFile = new File(path, fileName);
			FileOutputStream stream = null;
			try {
				stream = new FileOutputStream(tempFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
				stream.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (stream!=null) {
						stream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 删除用户头像
	 * @param fileName
	 */
	public static void deletePic(String fileName){
		if (isSDNormal()) {
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			File tempFile = new File(path, fileName);
			tempFile.delete();
		}
	}
	/**
	 * 判断SD是否可用
	 * @return
	 */
	public static boolean isSDNormal(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	/**
	 * 睡眠
	 * @param m
	 */
	public static void sleep(long m){
		try {
			Thread.sleep(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

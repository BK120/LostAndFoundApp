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
 * �Զ���ϵͳ������ ������Ҫ��װ�˻�ȡ��Ļ��ߡ��ؼ���ߡ���߾ࡢϵͳʱ�䣬�ַ����Ƿ�����Ƿ��ַ��жϵȷ���
 * �����ַ����롢��ȡ�ֻ�ָ��·��ͼƬ������ͼƬ��ָ��·�����ж�SD���Ƿ���á��߳�˯��
 * 
 * @author lee
 *
 */
public class SystemUtils {
	/**
	 * ��ȡDisplayMetrics���Ի�ȡ��Ļ���
	 * 
	 * @param context
	 * @return
	 */
	private static DisplayMetrics getDM(Context context) {
		Resources resources = context.getResources();
		return resources.getDisplayMetrics();
	}

	/**
	 * ��ȡ��Ļ���
	 * 
	 * @param dm
	 * @return
	 */
	public static int getSysWidth(Context context) {
		return getDM(context).widthPixels;
	}

	/**
	 * ��ȡ��Ļ�߶�
	 * 
	 * @param dm
	 * @return
	 */
	public static int getSysHeight(Context context) {
		return getDM(context).heightPixels;
	}

	/**
	 * ͨ��������ȡ��ǰ������ʱ����
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
	 * ��ȡ�ؼ���ߡ���߾�
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
	 * �ж��ַ������Ƿ�����Ƿ��ַ�\\W
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
	 * Ϊ�ַ�������ָ����������
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
	 * ��ȡ�û�ͷ��ͼƬ
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
	 * �����û�ͷ��ͼƬ
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
	 * ɾ���û�ͷ��
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
	 * �ж�SD�Ƿ����
	 * @return
	 */
	public static boolean isSDNormal(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	/**
	 * ˯��
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

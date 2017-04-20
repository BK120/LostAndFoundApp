package com.lostandfoundapp.utils;

import com.lostandfoundapp.activity.R;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

/**
 * 短信验证工具 该APP短信验证的作用主要是确定手机号码存在有效，故省去麻烦不使用集成
 * 分两步，首先确定是否是本机号码，如果是则使用本机短信功能提供验证码，如果不是则发送短信至提供号码
 * 
 * @author lee
 *
 */
public class SMSVerifyUtil {
	private static String verify;
	/**
	 * 发送验证码
	 * @param context
	 * @param phoneString
	 */
	public static String verify(Context context, String phoneString) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phone = manager.getLine1Number();// 获取本机号码
		if (phone.equals(phoneString)) {
			sendToMyself(context);
		} else {
			sendToOther(phoneString, context);
		}
		return verify;
	}
	/**
	 * 短信发给自己
	 * @param context
	 */
	private static void sendToMyself(Context context) {
		// 将短信插入手机
		ContentResolver contentResolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("address", "523277");
		values.put("date", System.currentTimeMillis());
		values.put("type", 2);
		values.put("read", 0);
		values.put("body", getSMSBody(0));
		contentResolver.insert(Uri.parse("content://sms"), values);
		// 发送短信提示
		Notification.Builder builder = new Builder(context);
		builder.setSmallIcon(R.drawable.sms);
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sms);
		builder.setLargeIcon(bitmap);// 设置大图标，必须是位图
		builder.setTicker("验证码："+verify);
		builder.setContentText(getSMSBody(0));
		builder.setContentTitle("523277");
		builder.setDefaults(Notification.DEFAULT_ALL);
		// 设置点击提示跳转任务
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("vnd.android.cursor.dir/mms");

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(pendingIntent);
		builder.setAutoCancel(true);

		Notification notification = builder.build();

		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(1, notification);
	}
	/**
	 * 短信发给别人
	 * @param phoneString
	 * @param context
	 */
	private static void sendToOther(String phoneString, Context context) {
//		Uri uri = Uri.parse("smsto:" + phoneString);
//		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//		intent.putExtra("sms_body", getSMSBody(1));
//		context.startActivity(intent);
		SmsManager.getDefault().sendTextMessage(phoneString, null, getSMSBody(1), null, null);
	}
	/**
	 * 获取短信内容
	 * @param i
	 * @return
	 */
	private static String getSMSBody(int i) {
		verify = getVerify();
		String smsBody = "";
		if (i==0) {
			smsBody = "【失而复得】"+verify+"是您本次验证码，您正在使用该手机进行注册，切勿告知他人";
		}else {
			smsBody = "【失而复得】我正在使用您手机进行注册，验证码为："+verify;
		}
		return smsBody;
	}
	/**
	 * 获取四位随机数
	 * @return
	 */
	private static String getVerify() {
		StringBuffer verify = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			verify.append(NumerationUtils.random());
		}
		return verify.toString();
	}
}

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
 * ������֤���� ��APP������֤��������Ҫ��ȷ���ֻ����������Ч����ʡȥ�鷳��ʹ�ü���
 * ������������ȷ���Ƿ��Ǳ������룬�������ʹ�ñ������Ź����ṩ��֤�룬����������Ͷ������ṩ����
 * 
 * @author lee
 *
 */
public class SMSVerifyUtil {
	private static String verify;
	/**
	 * ������֤��
	 * @param context
	 * @param phoneString
	 */
	public static String verify(Context context, String phoneString) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phone = manager.getLine1Number();// ��ȡ��������
		if (phone.equals(phoneString)) {
			sendToMyself(context);
		} else {
			sendToOther(phoneString, context);
		}
		return verify;
	}
	/**
	 * ���ŷ����Լ�
	 * @param context
	 */
	private static void sendToMyself(Context context) {
		// �����Ų����ֻ�
		ContentResolver contentResolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("address", "523277");
		values.put("date", System.currentTimeMillis());
		values.put("type", 2);
		values.put("read", 0);
		values.put("body", getSMSBody(0));
		contentResolver.insert(Uri.parse("content://sms"), values);
		// ���Ͷ�����ʾ
		Notification.Builder builder = new Builder(context);
		builder.setSmallIcon(R.drawable.sms);
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sms);
		builder.setLargeIcon(bitmap);// ���ô�ͼ�꣬������λͼ
		builder.setTicker("��֤�룺"+verify);
		builder.setContentText(getSMSBody(0));
		builder.setContentTitle("523277");
		builder.setDefaults(Notification.DEFAULT_ALL);
		// ���õ����ʾ��ת����
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
	 * ���ŷ�������
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
	 * ��ȡ��������
	 * @param i
	 * @return
	 */
	private static String getSMSBody(int i) {
		verify = getVerify();
		String smsBody = "";
		if (i==0) {
			smsBody = "��ʧ�����á�"+verify+"����������֤�룬������ʹ�ø��ֻ�����ע�ᣬ�����֪����";
		}else {
			smsBody = "��ʧ�����á�������ʹ�����ֻ�����ע�ᣬ��֤��Ϊ��"+verify;
		}
		return smsBody;
	}
	/**
	 * ��ȡ��λ�����
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

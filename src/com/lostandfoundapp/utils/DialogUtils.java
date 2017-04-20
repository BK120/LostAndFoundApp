package com.lostandfoundapp.utils;

import com.lostandfoundapp.activity.R;
import com.lostandfoundapp.bean.User;
import com.lostandfoundapp.data.MyValues;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * ��ʾ�򹤾���
 * 
 * @author lee
 *
 */
public class DialogUtils {
	/**
	 * ���ı���ʾ��
	 * 
	 * @param context
	 * @param text
	 * @param listener
	 */
	public static void textDialog(Context context, String text,
			final OnClickListener listener) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		builder.setMessage(text);
		builder.setPositiveButton("ȷ��", listener);
		builder.setNegativeButton("ȡ��", null);
		builder.create().show();
	}

	/**
	 * �û���Ϣ������ʾ��
	 * 
	 * @param context
	 * @param type
	 * @param hint
	 * @param up
	 */
	public static void updataDialog(final Context context, final int type,
			final String hint, final UpdataBack up) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		View v = View.inflate(context, R.layout.dialog_updata_view, null);
		final EditText text1 = (EditText) v
				.findViewById(R.id.dialog_updata_text1);
		final EditText text2 = (EditText) v
				.findViewById(R.id.dialog_updata_text2);
		RadioGroup sex = (RadioGroup) v.findViewById(R.id.dialog_updata_sex);
		final RadioButton male = (RadioButton) v
				.findViewById(R.id.dialog_updata_male);
		Button commit = (Button) v.findViewById(R.id.dialog_updata_commit);
		Button cancle = (Button) v.findViewById(R.id.dialog_updata_cancle);
		switch (type) {
		case 1:// �޸�����
			text2.setVisibility(View.GONE);
			sex.setVisibility(View.GONE);
			text1.setText(hint);
			break;
		case 2:// �޸�����
			sex.setVisibility(View.GONE);
			break;
		case 3:// �޸��Ա�
			text1.setVisibility(View.GONE);
			text2.setVisibility(View.GONE);
			RadioButton female = (RadioButton) v
					.findViewById(R.id.dialog_updata_female);
			if (hint.equals("��")) {
				male.setChecked(true);
			} else {
				female.setChecked(true);
			}
			break;
		}
		builder.setView(v);
		builder.setCancelable(false);
		final AlertDialog dialog = builder.create();
		dialog.show();
		commit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ȷ����ť����¼�
				switch (type) {
				case 1:// �޸�����
					String name = text1.getText().toString();
					if (TextUtils.isEmpty(name) || SystemUtils.isCotain(name)) {
						ToastUtils.textToast(context, "����д��ȷ������");
					} else {
						ToastUtils.textToast(context, "�޸ĳɹ�");
						up.back(name);
						dialog.dismiss();
					}
					break;

				case 2:// �޸�����
					String pass1 = text1.getText().toString();
					String pass2 = text2.getText().toString();
					if (TextUtils.isEmpty(pass1)) {
						ToastUtils.textToast(context, "��������������");
					} else if (SystemUtils.isCotain(pass1)) {
						ToastUtils.textToast(context, "��������Ƿ��ַ�");
					} else if (!pass1.equals(pass2)) {
						ToastUtils.textToast(context, "�������벻��ͬ");
					} else {
						ToastUtils.textToast(context, "�޸ĳɹ�");
						up.back(pass1);
						dialog.dismiss();
					}
					break;
				case 3:// �޸��Ա�
					String sex = male.isChecked() ? "��" : "Ů";
					if (!sex.equals(hint)) {
						ToastUtils.textToast(context, "�޸ĳɹ�");
						up.back(sex);
					}
					dialog.dismiss();
					break;
				}
			}
		});
		cancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ȡ����ť����¼�
				dialog.cancel();
			}
		});
	}

	/**
	 * ����ѡ��Ի���
	 * 
	 * @param context
	 * @param date
	 * @param updata
	 */
	public static void dateDialog(final Context context, final int[] date,
			final UpdataBack updata) {
		int year = date[0];// �õ���
		int month = date[1];// �õ���
		int day = date[2];// �õ���
		DatePickerDialog dpd = new DatePickerDialog(context,
				new OnDateSetListener() {
					// ����Dialogֻ��һ����ť�����ֽС���ɡ�
					@Override
					public void onDateSet(DatePicker view, int year, int month,
							int dayOfMonth) {
						// �����õ���������Ҫ��1����Ϊÿ����·��Ǵ�0-11
						final String dateString = year + "-" + (month + 1)
								+ "-" + dayOfMonth;
						timeDialog(context, date, new UpdataBack() {

							@Override
							public void back(String back) {
								// TODO Auto-generated method stub
								StringBuilder sb = new StringBuilder();
								sb.append(dateString);
								sb.append(" " + back);
								updata.back(sb.toString());
							}
						});
					}
				}, year, month - 1, day);// �����յĸ�ʽ�����ֻ����ã���-��-�ջ���-��-�꣩
		dpd.show();
	}

	/**
	 * ʱ��ѡ��Ի���
	 * 
	 * @param context
	 * @param date
	 * @param back
	 */
	public static void timeDialog(Context context, int[] date,
			final UpdataBack back) {
		int hour = date[3];// �õ�Сʱ
		int minute = date[4];// �õ�����
		TimePickerDialog time = new TimePickerDialog(context,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						back.back(NumerationUtils.setTime(hourOfDay) + ":"
								+ NumerationUtils.setTime(minute));
					}
				}, hour, minute, true);
		time.show();
	}

	/**
	 * �û��޸���Ϣ�Ի���
	 * 
	 * @param context
	 * @param click
	 */
	public static void updateDialog(Context context,
			android.view.View.OnClickListener... click) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		View v = View.inflate(context, R.layout.pop_pic_choice_view, null);
		Button update = (Button) v.findViewById(R.id.pop_pic_phone);
		update.setText("�޸���Ϣ");
		Button complete = (Button) v.findViewById(R.id.pop_pic_pic);
		complete.setText("�����¼�Ϊ���");
		Button delete = (Button) v.findViewById(R.id.pop_pic_cancle);
		delete.setText("ɾ��������¼");
		builder.setView(v);
		builder.setNegativeButton("ȡ��", null);
		builder.create().show();
		update.setOnClickListener(click[0]);
		complete.setOnClickListener(click[1]);
		delete.setOnClickListener(click[2]);
	}

	/**
	 * �鿴�û���Ϣ
	 * 
	 * @param context
	 * @param img
	 * @param user
	 */
	public static void checkUserDialog(Context context, Bitmap img, User user) {
		if (user!=null) {
			AlertDialog.Builder builder = new Builder(context);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle(R.string.app_name);
			View v = View.inflate(context, R.layout.check_user_view, null);
			ImageView pic = (ImageView) v.findViewById(R.id.check_pic);
			TextView name = (TextView) v.findViewById(R.id.check_name);
			TextView phone = (TextView) v.findViewById(R.id.check_phone);
			TextView address = (TextView) v.findViewById(R.id.check_address);
			if (img == null)
				pic.setImageResource(R.drawable.default_pic);
			else
				pic.setImageBitmap(img);
			name.setText(user.getName());
			Drawable sex = context.getResources().getDrawable(
					user.getSex().trim().equals("��") ? R.drawable.male
							: R.drawable.female);
			sex.setBounds(0, 0, 45, 45);
			name.setCompoundDrawables(null, null, sex, null);
			phone.setText(user.getPhone());
			address.setText(user.getAddress());
			builder.setView(v);
			builder.setPositiveButton("ȷ��", null);
			builder.create().show();
		}
	}
	/**
	 * ����IP�Ի���
	 * @param context
	 * @param back
	 */
	public static void setIPDialog(final Context context,final UpdataBack back){
		AlertDialog.Builder builder = new Builder(context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		View v = View.inflate(context, R.layout.set_ip_view, null);
		final RadioButton input = (RadioButton) v.findViewById(R.id.dialog_setid_inputip);
		final RadioButton home = (RadioButton) v.findViewById(R.id.dialog_setid_homeip);
		final RadioButton phone = (RadioButton) v.findViewById(R.id.dialog_setid_phoneip);
		final EditText inputIp = (EditText) v.findViewById(R.id.dialog_setid_input);
		inputIp.setHint(MyValues.IP);
		input.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					inputIp.setVisibility(View.VISIBLE);
				}else{
					inputIp.setVisibility(View.GONE);
				}
			}
		});
		builder.setView(v);
		builder.setPositiveButton("ȷ��", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (input.isChecked()) {
					String ip = inputIp.getText().toString().trim();
					if (TextUtils.isEmpty(ip)) {
						ToastUtils.textToast(context, "����û������IP");
					}
					MyValues.IP = ip;
				}else if (home.isChecked()) {
					MyValues.IP = MyValues.IP1;
				}else if (phone.isChecked()) {
					MyValues.IP = MyValues.IP2;
				}
				MyValues.URL = "http://" + MyValues.IP + ":8088/LostAndFoundServers/";
				back.back("");
			}
		});
		builder.setNegativeButton("ȡ��", null);
		builder.create().show();
	}
}

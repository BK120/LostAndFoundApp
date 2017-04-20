package com.lostandfoundapp.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.bean.User;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.utils.GPSUtil;
import com.lostandfoundapp.utils.JsonUtils;
import com.lostandfoundapp.utils.Md5Utils;
import com.lostandfoundapp.utils.SMSVerifyUtil;
import com.lostandfoundapp.utils.SharedPrefUtils;
import com.lostandfoundapp.utils.SystemUtils;
import com.lostandfoundapp.utils.ToastUtils;
import com.lostandfoundapp.view.MyEditText;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * ע�����
 * 
 * @author lee
 *
 */
public class SignActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.sign_back)
	private ImageView back;//���ذ�ť
	@ViewInject(R.id.sign_phone)
	private MyEditText phone;//�绰���������
	@ViewInject(R.id.sign_phone_verify)
	private Button phoneVerify;//�绰��֤��ť
	@ViewInject(R.id.sign_verify)
	private MyEditText verify;//��֤�������
	@ViewInject(R.id.sign_verify_btn)
	private Button verifyBtn;//��֤��ť
	@ViewInject(R.id.sign_next)
	private LinearLayout next;//���ڿ����°벿�ֿؼ�������
	@ViewInject(R.id.sign_name)
	private MyEditText name;//���������
	@ViewInject(R.id.sign_password)
	private MyEditText pass;//���������
	@ViewInject(R.id.sign_password_again)
	private MyEditText passAgain;//�ٴ��������������
	@ViewInject(R.id.sign_male)
	private RadioButton male;//�Ա�ѡ����
	@ViewInject(R.id.sign_address)
	private TextView address;//������л�����ַѡ�����
	@ViewInject(R.id.sign_sgin)
	private Button sign;//ע�ᰴť
	@ViewInject(R.id.sign_clear)
	private Button clear;//���ð�ť

	private String verifyString;//���ص���֤��
	private JSONObject userJSON;//�����û���Ϣ��JSON����

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {//���߳����ע�ᣬ��Ҫ���߳�����ʾ
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == MyValues.SignWHAT) {
				String result = (String) msg.obj;
				JSONObject json = null;
				try {
					json = new JSONObject(result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				result = JsonUtils.jsonToStr(json, "result");
				if (result.equals("ע��ɹ�")){
					SharedPrefUtils.putString(SignActivity.this, MyValues.USERINFO, userJSON.toString());
					finish();
				}
				ToastUtils.textToast(SignActivity.this, result);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		initView();
	}

	private void initView() {
		// ��ʼ���ؼ�
		ViewUtils.inject(this);
		userJSON = new JSONObject();
		back.setOnClickListener(this);
		phoneVerify.setOnClickListener(this);
		verifyBtn.setOnClickListener(this);
		address.setOnClickListener(this);
		sign.setOnClickListener(this);
		clear.setOnClickListener(this);
		address.setText(GPSUtil.getAddress(this).trim());
	}

	@Override
	public void onClick(View v) {
		// �ؼ�����¼�
		switch (v.getId()) {
		case R.id.sign_back:// ���ذ�ť
			finish();
			break;
		case R.id.sign_phone_verify:// �ֻ���֤
			String phoneString = phone.getText();
			if (TextUtils.isEmpty(phoneString)) {
				ToastUtils.textToast(this, "����д�����ֻ�����");
			} else if (phoneString.trim().length()!=11) {
				ToastUtils.textToast(this, "��������ȷ���ֻ�����");
			}else {
				verifyString = SMSVerifyUtil.verify(this, phoneString);
			}
			break;
		case R.id.sign_verify_btn:// ��֤��ť
			String verifyStr = verify.getText();
			if (TextUtils.isEmpty(verifyStr)) {
				ToastUtils.textToast(this, "����д��֤��");
			} else if (verifyStr.equals(verifyString)) {
				next.setVisibility(View.VISIBLE);
				ToastUtils.textToast(this, "��֤�ɹ����������д");
			} else {
				ToastUtils.textToast(this, "��֤�������������д��������֤");
			}
			break;
		case R.id.sign_address:// ��ַ
			Intent intent = new Intent(SignActivity.this, AdressActivity.class);
			intent.putExtra("address", address.getText());
			startActivityForResult(intent, MyValues.SIGN_REQUEST_CODE_FOR_ADDRESS);
			break;

		case R.id.sign_sgin:// ע��
			String idPhone = phone.getText();
			String nameString = name.getText();
			String pass1 = pass.getText();
			String pass2 = passAgain.getText();
			String malesString = male.isChecked() ? "��" : "Ů";
			String addreString = address.getText().toString();
			if (TextUtils.isEmpty(nameString)) {
				ToastUtils.textToast(this, "��������Ϊ��");
			} else if (SystemUtils.isCotain(nameString)) {
				ToastUtils.textToast(this, "���������Ƿ��ַ�");
			} else if (TextUtils.isEmpty(pass1)) {
				ToastUtils.textToast(this, "����������");
			} else if (SystemUtils.isCotain(pass1)) {
				ToastUtils.textToast(this, "��������Ƿ��ַ�");
			} else if (!pass1.equals(pass2)) {
				ToastUtils.textToast(this, "�����������벻ͬ");
			} else {
				User user = new User(idPhone, Md5Utils.md5Password(pass1), nameString, malesString,
						addreString);
				try {
					userJSON = JsonUtils.userToJson(user);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what =MyValues.SignWHAT;
					msg.obj = e.getMessage();
					handler.sendMessage(msg);
				}
				OpenVolley.json(MyValues.SignURL, MyValues.SignTAG, userJSON,
						new ResponseListener() {

							@Override
							public void onSuccess(String msg) {
								// TODO Auto-generated method stub
								Message m = Message.obtain();
								m.what =MyValues.SignWHAT;
								m.obj = msg;
								handler.sendMessage(m);
							}

							@Override
							public void onError(String msg) {
								// TODO Auto-generated method stub
								onSuccess(msg);
							}
						});
			}
			break;
		case R.id.sign_clear:// ����
			name.setText("");
			pass.setText("");
			passAgain.setText("");
			male.setChecked(true);
			address.setText("��ѡ��פ��ַ");
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ��ַѡ�񷵻�
		if (resultCode == MyValues.ADDRSS_RESULT_CODE && requestCode == MyValues.SIGN_REQUEST_CODE_FOR_ADDRESS) {
			address.setText(data.getStringExtra("address"));
		}
	}
}

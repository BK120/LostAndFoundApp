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
 * 注册界面
 * 
 * @author lee
 *
 */
public class SignActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.sign_back)
	private ImageView back;//返回按钮
	@ViewInject(R.id.sign_phone)
	private MyEditText phone;//电话号码输入框
	@ViewInject(R.id.sign_phone_verify)
	private Button phoneVerify;//电话验证按钮
	@ViewInject(R.id.sign_verify)
	private MyEditText verify;//验证码输入框
	@ViewInject(R.id.sign_verify_btn)
	private Button verifyBtn;//验证按钮
	@ViewInject(R.id.sign_next)
	private LinearLayout next;//用于控制下半部分控件的显隐
	@ViewInject(R.id.sign_name)
	private MyEditText name;//姓名输入框
	@ViewInject(R.id.sign_password)
	private MyEditText pass;//密码输入框
	@ViewInject(R.id.sign_password_again)
	private MyEditText passAgain;//再次输入密码输入框
	@ViewInject(R.id.sign_male)
	private RadioButton male;//性别选择：男
	@ViewInject(R.id.sign_address)
	private TextView address;//点击可切换到地址选择界面
	@ViewInject(R.id.sign_sgin)
	private Button sign;//注册按钮
	@ViewInject(R.id.sign_clear)
	private Button clear;//重置按钮

	private String verifyString;//返回的验证码
	private JSONObject userJSON;//保存用户信息的JSON对象

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {//子线程完成注册，需要主线程中显示
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
				if (result.equals("注册成功")){
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
		// 初始化控件
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
		// 控件点击事件
		switch (v.getId()) {
		case R.id.sign_back:// 返回按钮
			finish();
			break;
		case R.id.sign_phone_verify:// 手机验证
			String phoneString = phone.getText();
			if (TextUtils.isEmpty(phoneString)) {
				ToastUtils.textToast(this, "请填写您的手机号码");
			} else if (phoneString.trim().length()!=11) {
				ToastUtils.textToast(this, "请输入正确的手机号码");
			}else {
				verifyString = SMSVerifyUtil.verify(this, phoneString);
			}
			break;
		case R.id.sign_verify_btn:// 验证按钮
			String verifyStr = verify.getText();
			if (TextUtils.isEmpty(verifyStr)) {
				ToastUtils.textToast(this, "请填写验证码");
			} else if (verifyStr.equals(verifyString)) {
				next.setVisibility(View.VISIBLE);
				ToastUtils.textToast(this, "验证成功，请继续填写");
			} else {
				ToastUtils.textToast(this, "验证码错误，请重新填写或重新验证");
			}
			break;
		case R.id.sign_address:// 地址
			Intent intent = new Intent(SignActivity.this, AdressActivity.class);
			intent.putExtra("address", address.getText());
			startActivityForResult(intent, MyValues.SIGN_REQUEST_CODE_FOR_ADDRESS);
			break;

		case R.id.sign_sgin:// 注册
			String idPhone = phone.getText();
			String nameString = name.getText();
			String pass1 = pass.getText();
			String pass2 = passAgain.getText();
			String malesString = male.isChecked() ? "男" : "女";
			String addreString = address.getText().toString();
			if (TextUtils.isEmpty(nameString)) {
				ToastUtils.textToast(this, "姓名不能为空");
			} else if (SystemUtils.isCotain(nameString)) {
				ToastUtils.textToast(this, "姓名包含非法字符");
			} else if (TextUtils.isEmpty(pass1)) {
				ToastUtils.textToast(this, "请输入密码");
			} else if (SystemUtils.isCotain(pass1)) {
				ToastUtils.textToast(this, "密码包含非法字符");
			} else if (!pass1.equals(pass2)) {
				ToastUtils.textToast(this, "两次输入密码不同");
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
		case R.id.sign_clear:// 重置
			name.setText("");
			pass.setText("");
			passAgain.setText("");
			male.setChecked(true);
			address.setText("请选择常驻地址");
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 地址选择返回
		if (resultCode == MyValues.ADDRSS_RESULT_CODE && requestCode == MyValues.SIGN_REQUEST_CODE_FOR_ADDRESS) {
			address.setText(data.getStringExtra("address"));
		}
	}
}

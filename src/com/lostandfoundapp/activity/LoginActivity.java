package com.lostandfoundapp.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.bean.User;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.utils.JsonUtils;
import com.lostandfoundapp.utils.Md5Utils;
import com.lostandfoundapp.utils.SharedPrefUtils;
import com.lostandfoundapp.utils.SystemUtils;
import com.lostandfoundapp.utils.ToastUtils;
import com.lostandfoundapp.view.MyEditText;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用户登录界面
 * 
 * @author lee
 *
 */
public class LoginActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.login_pic)
	private ImageView pic;// 如果用户登录过则显示用户头像，否则显示默认头像
	@ViewInject(R.id.login_name)
	private MyEditText phone;// 登录使用的手机号码输入框，即帐号
	@ViewInject(R.id.login_pass)
	private MyEditText password;// 登录密码输入框
	@ViewInject(R.id.login_login)
	private Button login;// 登录按钮
	@ViewInject(R.id.login_cancle)
	private Button cancle;// 取消按钮
	@ViewInject(R.id.login_ck)
	private CheckBox check;// 选中服务条款的复选框
	@ViewInject(R.id.login_text)
	private TextView clause;// 点击可弹出服务条款
	@ViewInject(R.id.login_sign)
	private TextView sign;// 点击可转到注册

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what==MyValues.LoginWHAT) {
				String result = (String) msg.obj;
				if (result.startsWith("{")) {
					SharedPrefUtils.putBoolean(LoginActivity.this, MyValues.ISLOGIN, true);
					SharedPrefUtils.putString(LoginActivity.this, MyValues.USERINFO, result);
					result = "登录成功";
					finish();
				}
				ToastUtils.textToast(LoginActivity.this, result);
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
	}

	private void initView() {
		// 初始化控件
		ViewUtils.inject(this);// xUtils
		login.setOnClickListener(this);
		cancle.setOnClickListener(this);
		clause.setOnClickListener(this);
		sign.setOnClickListener(this);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 同意设置登录字体为黑色，可点击，否则反之
				login.setEnabled(isChecked);
				if (isChecked) {
					login.setTextColor(Color.BLACK);
				} else {
					login.setTextColor(Color.WHITE);
				}
			}
		});
		
		String userInfo = SharedPrefUtils.getString(this, MyValues.USERINFO, "");
		User user = null;
		if (!TextUtils.isEmpty(userInfo)) {
			try {
				user = JsonUtils.jsonToUser(JsonUtils.newJson(userInfo));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (user!=null) {
			phone.setText(user.getPhone());
			Bitmap bitmap = SystemUtils.getUserPic(user.getPhone()+".jpg");
			if (bitmap!=null) {
				pic.setImageBitmap(bitmap);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// 控件点击事件
		switch (v.getId()) {
		case R.id.login_login:// 登录
			String phoneString = phone.getText();
			String pasString = password.getText();
			if (TextUtils.isEmpty(phoneString)) {
				ToastUtils.textToast(this, "请输入您的电话号码，作为帐号");
			}else if (TextUtils.isEmpty(pasString)) {
				ToastUtils.textToast(this, "请输入密码");
			}else {
				Map<String, String> map = new HashMap<String, String>();
				map.put("phone", phoneString);
				map.put("password", Md5Utils.md5Password(pasString));
				OpenVolley.strin(MyValues.LoginURL, MyValues.LoginTAG, map, new ResponseListener() {
					
					@Override
					public void onSuccess(String msg) {
						// TODO Auto-generated method stub
						Message m = Message.obtain();
						m.what = MyValues.LoginWHAT;
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

		case R.id.login_cancle:// 取消
			finish();
			break;
		case R.id.login_text:// 服务条款
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LoginActivity.this);
			View view = View.inflate(LoginActivity.this,
					R.layout.dialog_login_view, null);
			builder.setView(view);
			builder.setPositiveButton("我同意",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							check.setChecked(true);// 点击我同意就设置复选框为选中
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create().show();
			break;
		case R.id.login_sign:// 注册
			startActivity(new Intent(this, SignActivity.class));
			break;
		}
	}

}

package com.lostandfoundapp.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.bean.User;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.service.IntnetService;
import com.lostandfoundapp.utils.DialogUtils;
import com.lostandfoundapp.utils.JsonUtils;
import com.lostandfoundapp.utils.Md5Utils;
import com.lostandfoundapp.utils.SharedPrefUtils;
import com.lostandfoundapp.utils.ToastUtils;
import com.lostandfoundapp.utils.UpdataBack;
import com.lostandfoundapp.view.MyView;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * 修改个人信息界面
 * 
 * @author lee
 *
 */
public class UpdataActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.updata_back)
	private ImageView back;
	@ViewInject(R.id.updata_name)
	private MyView name;
	@ViewInject(R.id.updata_sex)
	private MyView sex;
	@ViewInject(R.id.updata_address)
	private MyView address;
	@ViewInject(R.id.updata_password)
	private MyView password;
	private User user;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MyValues.UpdataUserWHAT) {
				ToastUtils.textToast(UpdataActivity.this, (String) msg.obj);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updata);
		initView();
	}

	private void initView() {
		// 初始化控件
		ViewUtils.inject(this);
		String userInfo = SharedPrefUtils
				.getString(this, MyValues.USERINFO, "");
		try {
			user = JsonUtils.jsonToUser(new JSONObject(userInfo));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		updataInfo();
		back.setOnClickListener(this);
		name.setOnClickListener(this);
		sex.setOnClickListener(this);
		address.setOnClickListener(this);
		password.setOnClickListener(this);
	}

	private void updataInfo() {
		name.setText(user.getName());
		sex.setText(user.getSex().trim());
		address.setText(user.getAddress());
	}

	@Override
	public void onClick(View v) {
		// 控件监听
		switch (v.getId()) {
		case R.id.updata_back:// 返回
			finish();
			break;
		case R.id.updata_name:// 修改姓名
			DialogUtils.updataDialog(this, 1, name.getText(), new UpdataBack() {

				@Override
				public void back(String back) {
					// TODO Auto-generated method stub
					name.setText(back);
					user.setName(back);
					SharedPrefUtils.putBoolean(UpdataActivity.this,
							MyValues.ISUPDATA, true);
				}
			});
			break;
		case R.id.updata_sex:// 修改性别
			DialogUtils.updataDialog(this, 3, sex.getText(), new UpdataBack() {

				@Override
				public void back(String back) {
					// TODO Auto-generated method stub
					sex.setText(back);
					user.setSex(back);
					SharedPrefUtils.putBoolean(UpdataActivity.this,
							MyValues.ISUPDATA, true);
				}
			});
			break;
		case R.id.updata_address:// 修改常驻地址
			Intent intent = new Intent(this, AdressActivity.class);
			intent.putExtra("address", address.getText());
			startActivityForResult(intent, 1001);
			break;
		case R.id.updata_password:// 修改密码
			DialogUtils.updataDialog(this, 2, null, new UpdataBack() {

				@Override
				public void back(String back) {
					// TODO Auto-generated method stub
					user.setPassword(Md5Utils.md5Password(back));
					SharedPrefUtils.putBoolean(UpdataActivity.this,
							MyValues.ISUPDATA, true);
				}
			});
			break;
		}
	}

	private JSONObject userJson = new JSONObject();

	@Override
	protected void onPause() {
		// 为了保证退出更新界面时，我的界面可以刷新数据，暂停即保存本地数据，停止时再更新网络数据
		super.onPause();
		if (SharedPrefUtils.getBoolean(this, MyValues.ISUPDATA)
				&& IntnetService.INTNET) {
			try {
				userJson = JsonUtils.userToJson(user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			SharedPrefUtils.putString(this, MyValues.USERINFO,
					userJson.toString());
		}
	}

	@Override
	protected void onDestroy() {
		// 退出时保存所有修改
		super.onDestroy();
		if (!IntnetService.INTNET) {
			ToastUtils.textToast(this, "网络连接失败");
		} else if (SharedPrefUtils.getBoolean(this, MyValues.ISUPDATA)) {
			OpenVolley.json(MyValues.UpdataUserURL, MyValues.UpdataUserTAG,
					userJson, new ResponseListener() {

						@Override
						public void onSuccess(String msg) {
							// TODO Auto-generated method stub
							JSONObject jsonResult = null;
							try {
								jsonResult = new JSONObject(msg);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Message m = Message.obtain();
							m.what = MyValues.UpdataUserWHAT;
							m.obj = JsonUtils.jsonToStr(jsonResult, "result");
							handler.sendMessage(m);
						}

						@Override
						public void onError(String msg) {
							// TODO Auto-generated method stub
							onSuccess(msg);
						}
					});
		}
		SharedPrefUtils.putBoolean(this, MyValues.ISUPDATA, false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 地址返回选择
		if (requestCode == 1001 && resultCode == 1002) {
			String addres = data.getStringExtra("address");
			address.setText(addres);
			user.setAddress(addres);
			SharedPrefUtils.putBoolean(UpdataActivity.this, MyValues.ISUPDATA,
					true);
		}
	}
}

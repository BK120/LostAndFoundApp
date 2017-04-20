package com.lostandfoundapp.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.lostandfoundapp.activity.CameraActivity;
import com.lostandfoundapp.activity.LoginActivity;
import com.lostandfoundapp.activity.PublishActivity;
import com.lostandfoundapp.activity.R;
import com.lostandfoundapp.activity.SignActivity;
import com.lostandfoundapp.activity.UpdataActivity;
import com.lostandfoundapp.bean.User;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.net.OpenHttpUCTask;
import com.lostandfoundapp.service.IntnetService;
import com.lostandfoundapp.utils.DialogUtils;
import com.lostandfoundapp.utils.JsonUtils;
import com.lostandfoundapp.utils.SharedPrefUtils;
import com.lostandfoundapp.utils.SystemUtils;
import com.lostandfoundapp.utils.ToastUtils;
import com.lostandfoundapp.view.MyView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 个人中心界面 显示我的信息及其他功能
 * 
 * @author lee
 *
 */
public class MineFragment extends Fragment implements OnClickListener {
	private Context context;
	private Button login, sign;
	private LinearLayout noLogin, hadLogin;
	private ImageView pic;
	private TextView name, phone, address;
	private MyView lost, found, people, updata, out,updata_pic;

	public MineFragment() {
		// TODO Auto-generated constructor stub
	}
	public MineFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 界面构造
		View view = inflater.inflate(R.layout.fragment_mine, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		// 初始化控件
		noLogin = (LinearLayout) view.findViewById(R.id.mine_nologin);
		hadLogin = (LinearLayout) view.findViewById(R.id.mine_hadlogin);
		pic = (ImageView) view.findViewById(R.id.mine_pic);
		name = (TextView) view.findViewById(R.id.mine_name);
		phone = (TextView) view.findViewById(R.id.mine_phone);
		address = (TextView) view.findViewById(R.id.mine_address);
		lost = (MyView) view.findViewById(R.id.mine_lost);
		found = (MyView) view.findViewById(R.id.mine_found);
		people = (MyView) view.findViewById(R.id.mine_people);
		updata = (MyView) view.findViewById(R.id.mine_updata);
		out = (MyView) view.findViewById(R.id.mine_out);
		updata_pic = (MyView) view.findViewById(R.id.mine_updata_pic);
		login = (Button) view.findViewById(R.id.mine_login);
		sign = (Button) view.findViewById(R.id.mine_sign);
		login.setOnClickListener(this);
		sign.setOnClickListener(this);
		lost.setOnClickListener(this);
		found.setOnClickListener(this);
		people.setOnClickListener(this);
		updata.setOnClickListener(this);
		out.setOnClickListener(this);
		updata_pic.setOnClickListener(this);
		changeView();
	}

	private void changeView() {
		if (SharedPrefUtils.getBoolean(context, MyValues.ISLOGIN)) {
			noLogin.setVisibility(View.GONE);
			hadLogin.setVisibility(View.VISIBLE);
			String userString = SharedPrefUtils.getString(context,
					MyValues.USERINFO, "");
			User user = null;
			try {
				user = JsonUtils.jsonToUser(new JSONObject(userString));
				name.setText(user.getName());
				phone.setText(user.getPhone());
				address.setText(user.getAddress());
				Drawable sex = context.getResources().getDrawable(
						user.getSex().trim().equals("男") ? R.drawable.male
								: R.drawable.female);
				sex.setBounds(0, 0, 45, 45);
				name.setCompoundDrawables(null, null, sex, null);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Bitmap userPic = SystemUtils.getUserPic(user.getPhone()+".jpg");
			if (userPic!=null) {
				pic.setImageBitmap(userPic);
				updata_pic.setImageBitmap(userPic);
			}else {
				OpenHttpUCTask.downloadPic(MyValues.UserPicURL, user.getPhone(), pic);
				OpenHttpUCTask.downloadPic(MyValues.UserPicURL, user.getPhone(), updata_pic.getImageView());
			}
		} else {
			noLogin.setVisibility(View.VISIBLE);
			hadLogin.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// 控件监听事件
		switch (v.getId()) {
		case R.id.mine_login://登录按钮
			startActivity(new Intent(context, LoginActivity.class));
			break;

		case R.id.mine_sign://注册
			startActivity(new Intent(context, SignActivity.class));
			break;
		case R.id.mine_lost://发布丢失
			if (IntnetService.INTNET) {
				Intent intentLost = new Intent(context,PublishActivity.class);
				intentLost.putExtra("type", 0);
				startActivity(intentLost);
			}else {
				ToastUtils.textToast(context, "网络连接失败，请检查网络");
			}
			break;
		case R.id.mine_found://发布招领
			if (IntnetService.INTNET) {
				Intent intentFound = new Intent(context,PublishActivity.class);
				intentFound.putExtra("type", 1);
				startActivity(intentFound);
			}else {
				ToastUtils.textToast(context, "网络连接失败，请检查网络");
			}
			break;
		case R.id.mine_people://发布寻人启示
			if (IntnetService.INTNET) {
				Intent intentFound = new Intent(context,PublishActivity.class);
				intentFound.putExtra("type", 2);
				startActivity(intentFound);
			}else {
				ToastUtils.textToast(context, "网络连接失败，请检查网络");
			}
			break;
		case R.id.mine_updata://更新个人信息
			startActivity(new Intent(context,UpdataActivity.class));
			break;
		case R.id.mine_updata_pic://修改头像
			Intent picIntent = new Intent(context,CameraActivity.class);
			picIntent.putExtra("userOrdata", 0);
			startActivity(picIntent);
			break;
		case R.id.mine_out://注销登录
			DialogUtils.textDialog(context, "确定要注销当前用户吗？",
					new android.content.DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 确定按钮响应事件，注销用户
							SharedPrefUtils.putBoolean(context, MyValues.ISLOGIN, false);
							changeView();
							//SystemUtils.deletePic(phone.getText().toString().trim()+".jpg");
						}

					});
			break;
		}
	}
	
	@Override
	public void onResume() {
		// 我的界面重新显示时改变界面结构
		super.onResume();
		changeView();
	}

}

package com.lostandfoundapp.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.bean.User;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.net.OpenHttpUCTask;
import com.lostandfoundapp.utils.JsonUtils;
import com.lostandfoundapp.utils.SharedPrefUtils;
import com.lostandfoundapp.utils.SystemUtils;
import com.lostandfoundapp.utils.ToastUtils;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

/**
 * 获取图片 可通过拍摄或者从手机获取 选择完毕后需要进行裁剪，最后才能被选中
 * 
 * @author lee
 *
 */
public class CameraActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.camera_back)
	private ImageView back;// 返回按钮
	@ViewInject(R.id.camera_pic)
	private ImageView pic;// 图片预览
	@ViewInject(R.id.camera_commit)
	private Button commit;// 提交按钮
	private PopupWindow pop;// PopWindow

	private int userOrData;// 用户修改头像或数据图片
	private Bitmap photo;// 最后将得到的图片格式
	private String fileName;// 文件名

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {// 保存图片需要访问服务端，返回消息处理
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MyValues.InsertPicWHAT) {
				String result = (String) msg.obj;
				if (result.equals("get")) {// 将图片文件名保存后服务器返回"get"字符
					savaPic();// 接收到"get"再将图片以流的形式传到服务器
				} else if (result.startsWith("图片上传成功")) {// 如果收到此消息证明图片上传成功，退出并提示
					finish();
					ToastUtils.textToast(CameraActivity.this, result);
				} else {// 否则就是错误信息，直接输出即可
					ToastUtils.textToast(CameraActivity.this, result);
				}
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		initView();
	}

	private void initView() {
		// 初始化控件
		userOrData = getIntent().getIntExtra("userOrdata", 0);// 由启动该Activity的Activity传整型数，以表明图片做何用
		if (userOrData == 0) {// 0-表明是用户在修改图片
			String userInfo = SharedPrefUtils.getString(CameraActivity.this,
					MyValues.USERINFO, "");// 从文件中获取用户信息
			try {
				User user = JsonUtils.jsonToUser(new JSONObject(userInfo));// 文件中的用户信息是直接以JSON格式存储的，所以需要解析
				fileName = user.getPhone() + ".jpg";// 以用户电话为文件名
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {// 1-表明是在发布消息
			int[] date = SystemUtils.getDate();// 获取当时时间
			fileName = date[0] + "" + date[1] + "" + date[2] + "" + date[3]
					+ "" + date[4] + "" + date[5] + ".jpg";// 以时间为文件名
		}
		ViewUtils.inject(this);// xUtils初始化控件
		back.setOnClickListener(this);
		pic.setOnClickListener(this);
		commit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// 控件点击监听
		switch (v.getId()) {
		case R.id.camera_back:// 返回
			finish();// 直接结束当前界面即可
			break;
		case R.id.camera_pic:// 显示PopWindow
			ShowPopWindow(back);// 显示参照为退出按钮，令其显示在界面上部分
			break;
		case R.id.camera_commit:// 确定,用户头像则保存照片并上传到服务端；消息配图则只保存照片并返回文件名
			if (photo == null) {// 如果还没有获取到图片，抛出提示
				ToastUtils.textToast(this, "请选择一张图片");
			} else {// 有图则先保存图片到手机，方便离开此界面加载图片
				SystemUtils.savaPic(photo, fileName);// 保存图片到手机
				if (userOrData == 0 && SystemUtils.isSDNormal()) {// 用户头像
					sendToServlet();// 先保存文件名到到服务器，得到消息后再上传图片到服务器
				} else if (userOrData == 1) {// 消息配图
					Intent picIntent = new Intent();
					picIntent.putExtra("picName", fileName);// 返回文件名，方便发布操作和获取图片
					setResult(MyValues.PIC_RESULT_CODE, picIntent);
					finish();
				} else {
					ToastUtils.textToast(CameraActivity.this, "无效SD卡");
				}
			}
			break;
		case R.id.pop_pic_cancle:// 隐藏PopWindow
			pop.dismiss();
			break;
		case R.id.pop_pic_phone:// 相机拍摄照片
			Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (SystemUtils.isSDNormal()) {// 判断存储卡是否可以用，可用进行存储
				// 拍照操作
				File path = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
				File file = new File(path, fileName);
				photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(file));
				startActivityForResult(photoIntent, 10);
				pop.dismiss();
			} else {
				ToastUtils.textToast(this, "无效SD卡");
			}
			break;
		case R.id.pop_pic_pic:// 选择本地照片
			Intent intentFromGallery = new Intent();
			intentFromGallery.setType("image/*"); // 设置文件类型
			intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentFromGallery, 11);
			pop.dismiss();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 返回结果
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 10:// 相机拍摄得到的照片
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			File tempFile = new File(path, fileName);
			startPhotoZoom(Uri.fromFile(tempFile));//拍摄完成后去裁剪
			break;
		case 11:// 从本地选取的照片
			if (data != null) {
				startPhotoZoom(data.getData());//选择本地照片后去裁剪
			}
			break;
		case 12:// 裁剪完毕的照片
			if (data != null) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					photo = extras.getParcelable("data");
					pic.setImageBitmap(photo);
				}
			}
			break;
		}
	}

	public void startPhotoZoom(Uri uri) {
		// 照片裁剪
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 12);
	}

	private void ShowPopWindow(View v) {
		// 显示PopWindow
		getPopWindow();
		pop.showAsDropDown(v);
	}

	private void getPopWindow() {
		// 获取PopWindow
		if (pop != null) {
			pop.dismiss();
			return;
		} else {
			initPopWindow();
		}
	}

	private void initPopWindow() {
		// 初始化PopWindow
		View contentView = View.inflate(this, R.layout.pop_pic_choice_view,
				null);
		Button phone = (Button) contentView.findViewById(R.id.pop_pic_phone);
		Button pic = (Button) contentView.findViewById(R.id.pop_pic_pic);
		Button cancle = (Button) contentView.findViewById(R.id.pop_pic_cancle);
		phone.setOnClickListener(this);
		pic.setOnClickListener(this);
		cancle.setOnClickListener(this);
		pop = new PopupWindow(contentView,// 弹出界面的引用
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,// 界面的宽高属性
				true);// 是否可点击
		contentView.setOnTouchListener(new OnTouchListener() {
			// 设定点击收回效果
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (pop != null && pop.isShowing()) {// 如果pop已存在并且在显示
					pop.dismiss();
				}
				return false;
			}
		});
	}

	private void sendToServlet() {
		// 像服务器发送消息表明将要上传图片
		// 只在用户修改头像时上传到网络，发布消息时另行上传
		if (userOrData == 0) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("userOrdata", "user");
			map.put("fileName", fileName);
			OpenVolley.strin(MyValues.InsertPicURL, MyValues.InsertPicTAG, map,
					new ResponseListener() {

						@Override
						public void onSuccess(String msg) {
							// TODO Auto-generated method stub
							Message m = Message.obtain();
							m.what = MyValues.InsertPicWHAT;
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
	}

	private void savaPic() {
		// 上传图片
		OpenHttpUCTask.savaPic(MyValues.SavaPicURL, photo, new ResponseListener() {
			
			@Override
			public void onSuccess(String msg) {
				// TODO Auto-generated method stub
				Message m = Message.obtain();
				m.what = MyValues.InsertPicWHAT;
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
}

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
 * ��ȡͼƬ ��ͨ��������ߴ��ֻ���ȡ ѡ����Ϻ���Ҫ���вü��������ܱ�ѡ��
 * 
 * @author lee
 *
 */
public class CameraActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.camera_back)
	private ImageView back;// ���ذ�ť
	@ViewInject(R.id.camera_pic)
	private ImageView pic;// ͼƬԤ��
	@ViewInject(R.id.camera_commit)
	private Button commit;// �ύ��ť
	private PopupWindow pop;// PopWindow

	private int userOrData;// �û��޸�ͷ�������ͼƬ
	private Bitmap photo;// ��󽫵õ���ͼƬ��ʽ
	private String fileName;// �ļ���

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {// ����ͼƬ��Ҫ���ʷ���ˣ�������Ϣ����
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MyValues.InsertPicWHAT) {
				String result = (String) msg.obj;
				if (result.equals("get")) {// ��ͼƬ�ļ�����������������"get"�ַ�
					savaPic();// ���յ�"get"�ٽ�ͼƬ��������ʽ����������
				} else if (result.startsWith("ͼƬ�ϴ��ɹ�")) {// ����յ�����Ϣ֤��ͼƬ�ϴ��ɹ����˳�����ʾ
					finish();
					ToastUtils.textToast(CameraActivity.this, result);
				} else {// ������Ǵ�����Ϣ��ֱ���������
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
		// ��ʼ���ؼ�
		userOrData = getIntent().getIntExtra("userOrdata", 0);// ��������Activity��Activity�����������Ա���ͼƬ������
		if (userOrData == 0) {// 0-�������û����޸�ͼƬ
			String userInfo = SharedPrefUtils.getString(CameraActivity.this,
					MyValues.USERINFO, "");// ���ļ��л�ȡ�û���Ϣ
			try {
				User user = JsonUtils.jsonToUser(new JSONObject(userInfo));// �ļ��е��û���Ϣ��ֱ����JSON��ʽ�洢�ģ�������Ҫ����
				fileName = user.getPhone() + ".jpg";// ���û��绰Ϊ�ļ���
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {// 1-�������ڷ�����Ϣ
			int[] date = SystemUtils.getDate();// ��ȡ��ʱʱ��
			fileName = date[0] + "" + date[1] + "" + date[2] + "" + date[3]
					+ "" + date[4] + "" + date[5] + ".jpg";// ��ʱ��Ϊ�ļ���
		}
		ViewUtils.inject(this);// xUtils��ʼ���ؼ�
		back.setOnClickListener(this);
		pic.setOnClickListener(this);
		commit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// �ؼ��������
		switch (v.getId()) {
		case R.id.camera_back:// ����
			finish();// ֱ�ӽ�����ǰ���漴��
			break;
		case R.id.camera_pic:// ��ʾPopWindow
			ShowPopWindow(back);// ��ʾ����Ϊ�˳���ť��������ʾ�ڽ����ϲ���
			break;
		case R.id.camera_commit:// ȷ��,�û�ͷ���򱣴���Ƭ���ϴ�������ˣ���Ϣ��ͼ��ֻ������Ƭ�������ļ���
			if (photo == null) {// �����û�л�ȡ��ͼƬ���׳���ʾ
				ToastUtils.textToast(this, "��ѡ��һ��ͼƬ");
			} else {// ��ͼ���ȱ���ͼƬ���ֻ��������뿪�˽������ͼƬ
				SystemUtils.savaPic(photo, fileName);// ����ͼƬ���ֻ�
				if (userOrData == 0 && SystemUtils.isSDNormal()) {// �û�ͷ��
					sendToServlet();// �ȱ����ļ����������������õ���Ϣ�����ϴ�ͼƬ��������
				} else if (userOrData == 1) {// ��Ϣ��ͼ
					Intent picIntent = new Intent();
					picIntent.putExtra("picName", fileName);// �����ļ��������㷢�������ͻ�ȡͼƬ
					setResult(MyValues.PIC_RESULT_CODE, picIntent);
					finish();
				} else {
					ToastUtils.textToast(CameraActivity.this, "��ЧSD��");
				}
			}
			break;
		case R.id.pop_pic_cancle:// ����PopWindow
			pop.dismiss();
			break;
		case R.id.pop_pic_phone:// ���������Ƭ
			Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (SystemUtils.isSDNormal()) {// �жϴ洢���Ƿ�����ã����ý��д洢
				// ���ղ���
				File path = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
				File file = new File(path, fileName);
				photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(file));
				startActivityForResult(photoIntent, 10);
				pop.dismiss();
			} else {
				ToastUtils.textToast(this, "��ЧSD��");
			}
			break;
		case R.id.pop_pic_pic:// ѡ�񱾵���Ƭ
			Intent intentFromGallery = new Intent();
			intentFromGallery.setType("image/*"); // �����ļ�����
			intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentFromGallery, 11);
			pop.dismiss();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ���ؽ��
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 10:// �������õ�����Ƭ
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			File tempFile = new File(path, fileName);
			startPhotoZoom(Uri.fromFile(tempFile));//������ɺ�ȥ�ü�
			break;
		case 11:// �ӱ���ѡȡ����Ƭ
			if (data != null) {
				startPhotoZoom(data.getData());//ѡ�񱾵���Ƭ��ȥ�ü�
			}
			break;
		case 12:// �ü���ϵ���Ƭ
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
		// ��Ƭ�ü�
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// ���òü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 12);
	}

	private void ShowPopWindow(View v) {
		// ��ʾPopWindow
		getPopWindow();
		pop.showAsDropDown(v);
	}

	private void getPopWindow() {
		// ��ȡPopWindow
		if (pop != null) {
			pop.dismiss();
			return;
		} else {
			initPopWindow();
		}
	}

	private void initPopWindow() {
		// ��ʼ��PopWindow
		View contentView = View.inflate(this, R.layout.pop_pic_choice_view,
				null);
		Button phone = (Button) contentView.findViewById(R.id.pop_pic_phone);
		Button pic = (Button) contentView.findViewById(R.id.pop_pic_pic);
		Button cancle = (Button) contentView.findViewById(R.id.pop_pic_cancle);
		phone.setOnClickListener(this);
		pic.setOnClickListener(this);
		cancle.setOnClickListener(this);
		pop = new PopupWindow(contentView,// �������������
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,// ����Ŀ������
				true);// �Ƿ�ɵ��
		contentView.setOnTouchListener(new OnTouchListener() {
			// �趨����ջ�Ч��
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (pop != null && pop.isShowing()) {// ���pop�Ѵ��ڲ�������ʾ
					pop.dismiss();
				}
				return false;
			}
		});
	}

	private void sendToServlet() {
		// �������������Ϣ������Ҫ�ϴ�ͼƬ
		// ֻ���û��޸�ͷ��ʱ�ϴ������磬������Ϣʱ�����ϴ�
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
		// �ϴ�ͼƬ
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

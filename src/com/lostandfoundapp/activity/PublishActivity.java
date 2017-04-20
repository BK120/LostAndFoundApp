package com.lostandfoundapp.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.bean.Data;
import com.lostandfoundapp.bean.User;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.net.OpenHttpUCTask;
import com.lostandfoundapp.utils.DialogUtils;
import com.lostandfoundapp.utils.GPSUtil;
import com.lostandfoundapp.utils.JsonUtils;
import com.lostandfoundapp.utils.NumerationUtils;
import com.lostandfoundapp.utils.SharedPrefUtils;
import com.lostandfoundapp.utils.SystemUtils;
import com.lostandfoundapp.utils.ToastUtils;
import com.lostandfoundapp.utils.UpdataBack;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ���������ʧ
 * 
 * @author lee
 *
 */
public class PublishActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.publish_back)
	private ImageView back;
	@ViewInject(R.id.publish_title)
	private TextView title;
	@ViewInject(R.id.publish_name)
	private EditText name;
	@ViewInject(R.id.publish_detail)
	private EditText detail;
	@ViewInject(R.id.publish_remark)
	private EditText remark;
	@ViewInject(R.id.publish_address)
	private TextView address;
	@ViewInject(R.id.publish_address_detail)
	private TextView addressDetail;
	@ViewInject(R.id.publish_date1)
	private TextView date1;
	@ViewInject(R.id.publish_date2)
	private TextView date2;
	@ViewInject(R.id.publish_pic)
	private ImageView pic;
	@ViewInject(R.id.publish_commit)
	private Button commit;

	private int type;
	private String picName = "noPic";

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String result = (String) msg.obj;
			if (msg.what == MyValues.PublishWHAT) {
				try {
					result = JsonUtils.jsonToStr(new JSONObject(result),
							"result");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ToastUtils.textToast(PublishActivity.this, result);
			} else if (msg.what == MyValues.InsertPicWHAT
					&& result.equals("get")) {
				savaPic();
			} else {
				ToastUtils.textToast(PublishActivity.this, result);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		initView();
	}

	private void initView() {
		// ��ʼ���ؼ�
		ViewUtils.inject(this);
		chageView();
		address.setText(GPSUtil.getAddress(this).trim());
		int[] date = SystemUtils.getDate();
		date1.setText(setDate(date));
		date2.setText(setDate(date));
		back.setOnClickListener(this);
		address.setOnClickListener(this);
		date1.setOnClickListener(this);
		date2.setOnClickListener(this);
		pic.setOnClickListener(this);
		commit.setOnClickListener(this);
	}

	private void chageView() {
		// ���ݲ�����ɽ��������ʧ��Ӧ�Ĳ���
		type = getIntent().getIntExtra("type", 0);
		switch (type) {
		case 0:// ������ʧ
			title.setText("������ʧ");
			break;
		case 1:// ��������
			title.setText("��������");
			break;
		case 2:// ����Ѱ��
			title.setText("Ѱ����ʾ");
			break;
		}
	}

	private String setDate(int[] date) {
		// �������ڸ�ʽ
		return date[0] + "-" + date[1] + "-" + date[2] + " " + NumerationUtils.setTime(date[3]) + ":"
				+ NumerationUtils.setTime(date[4]);
	}

	private String[] getTime(String time) {
		// �������е������պ�ʱ�ַֿ�
		return time.split(" ");
	}

	@Override
	public void onClick(View v) {
		// ����¼���Ӧ
		switch (v.getId()) {
		case R.id.publish_back:// �˳�
			finish();
			break;

		case R.id.publish_address:// ��ȡ��ַ
			Intent intent = new Intent(this, AdressActivity.class);
			intent.putExtra("address", address.getText());
			startActivityForResult(intent,
					MyValues.PUBLISH_REQUEST_CODE_FOR_ADDRESS);
			break;
		case R.id.publish_date1:// �¼�����ʱ��1
			DialogUtils.dateDialog(this, SystemUtils.getDate(),
					new UpdataBack() {

						@Override
						public void back(String back) {
							// TODO Auto-generated method stub
							date1.setText(back);
						}
					});
			break;
		case R.id.publish_date2:// �¼�����ʱ��2
			DialogUtils.dateDialog(this, SystemUtils.getDate(),
					new UpdataBack() {

						@Override
						public void back(String back) {
							// TODO Auto-generated method stub
							date2.setText(back);
						}
					});
			break;
		case R.id.publish_pic:// ͼƬ
			Intent pIntent = new Intent(this, CameraActivity.class);
			pIntent.putExtra("userOrdata", 1);
			startActivityForResult(pIntent,
					MyValues.PUBLISH_REQUEST_CODE_FOR_PIC);
			break;
		case R.id.publish_commit:// �ύ
			if (TextUtils.isEmpty(name.getText().toString())) {
				ToastUtils.textToast(this, "����д��Ʒ��");
			} else if (TextUtils.isEmpty(addressDetail.getText().toString())) {
				ToastUtils.textToast(this, "����д��ϸ�·��ص�");
			} else if (date1.getText().toString()
					.equals(date2.getText().toString())) {
				ToastUtils.textToast(this, "��˶��·�ʱ��");
			} else {
				publish();
				finish();
			}
			break;
		}
	}

	private void publish() {
		// ������Ϣ
		JSONObject json = null;
		try {
			json = JsonUtils.dataToJson(getData());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OpenVolley.json(MyValues.PublishURL, MyValues.PublishTAG, json,
				new ResponseListener() {

					@Override
					public void onSuccess(String msg) {
						// TODO Auto-generated method stub
						Message m = Message.obtain();
						m.what = MyValues.PublishWHAT;
						m.obj = msg;
						handler.sendMessage(m);
					}

					@Override
					public void onError(String msg) {
						// TODO Auto-generated method stub
						onSuccess(msg);
					}
				});
		if (!picName.equals("noPic")) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("userOrdata", "data");
			map.put("fileName", picName);
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
		Bitmap bitmap = SystemUtils.getUserPic(picName);
		OpenHttpUCTask.savaPic(MyValues.SavaPicURL, bitmap,
				new ResponseListener() {

					@Override
					public void onSuccess(String msg) {
						// TODO Auto-generated method stub
						Log.i("LAF", msg);
					}

					@Override
					public void onError(String msg) {
						// TODO Auto-generated method stub
						onSuccess(msg);
					}
				});
	}

	private Data getData() {
		// ��ȡ������Ϣ�ֶ�ֵ
		Data data = new Data();
		String userInfo = SharedPrefUtils
				.getString(this, MyValues.USERINFO, "");
		data.setId(0);
		String dataType = "";
		switch (type) {
		case 0:// ��ʧ
			dataType = "lost";
			break;
		case 1:// ����
			dataType = "find";
			break;
		case 2:// Ѱ��
			dataType = "peop";
			break;
		}
		data.setType(dataType);
		data.setName(name.getText().toString());
		String detailsString = detail.getText().toString();
		data.setDetail(TextUtils.isEmpty(detailsString) ? "noDetail"
				: detailsString);
		String remarkString = remark.getText().toString();
		data.setRemark(TextUtils.isEmpty(remarkString) ? "noRemark"
				: remarkString);
		data.setPlace(address.getText().toString() + " "
				+ addressDetail.getText().toString());
		String time1 = date1.getText().toString();
		data.setIncidentDate1(JsonUtils.strToSql(getTime(time1)[0]));
		data.setIncidentTime1(getTime(time1)[1]);
		String time2 = date2.getText().toString();
		data.setIncidentDate2(JsonUtils.strToSql(getTime(time2)[0]));
		data.setIncidentTime2(getTime(time2)[1]);
		int[] pd = SystemUtils.getDate();
		data.setPublishDate(JsonUtils.strToSql(pd[0] + "-" + pd[1] + "-"
				+ pd[2]));
		data.setPublishTime(NumerationUtils.setTime(pd[3]) + ":" + NumerationUtils.setTime(pd[4]));
		data.setPicName(picName);
		data.setIsFinish("n");

		try {
			User user = JsonUtils.jsonToUser(new JSONObject(userInfo));
			data.setUserPhone(user.getPhone());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == MyValues.ADDRSS_RESULT_CODE
				&& requestCode == MyValues.PUBLISH_REQUEST_CODE_FOR_ADDRESS) {// ���ص�ַ
			address.setText(data.getStringExtra("address"));
		} else if (resultCode == MyValues.PIC_RESULT_CODE
				&& requestCode == MyValues.PUBLISH_REQUEST_CODE_FOR_PIC) {
			picName = data.getStringExtra("picName");
			Bitmap bitmap = SystemUtils.getUserPic(picName);
			if (bitmap != null) {
				pic.setImageBitmap(bitmap);
			}
		}
	}
}

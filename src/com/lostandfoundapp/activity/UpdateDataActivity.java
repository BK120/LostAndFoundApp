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
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 修改消息内容界面
 * 
 * @author lee
 *
 */
public class UpdateDataActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.update_data_back)
	private ImageView back;
	@ViewInject(R.id.update_data_type0)
	private RadioButton type_lost;
	@ViewInject(R.id.update_data_type1)
	private RadioButton type_found;
	@ViewInject(R.id.update_data_type2)
	private RadioButton type_people;
	@ViewInject(R.id.update_data_name)
	private EditText name;
	@ViewInject(R.id.update_data_detail)
	private EditText detail;
	@ViewInject(R.id.update_data_remark)
	private EditText remark;
	@ViewInject(R.id.update_data_address_detail)
	private EditText addressDetail;
	@ViewInject(R.id.update_data_address)
	private TextView address;
	@ViewInject(R.id.update_data_date1)
	private TextView date1;
	@ViewInject(R.id.update_data_date2)
	private TextView date2;
	@ViewInject(R.id.update_data_pic)
	private ImageView pic;
	@ViewInject(R.id.update_data_commit)
	private Button commit;

	private Data data;
	private String picName = "noPic";

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String result = (String) msg.obj;
			if (msg.what == MyValues.UpdateDataWHAT) {
				try {
					result = JsonUtils.jsonToStr(new JSONObject(result),
							"result");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ToastUtils.textToast(UpdateDataActivity.this, result);
			} else if (msg.what == MyValues.InsertPicWHAT
					&& result.equals("get")) {
				savaPic();
			} else {
				ToastUtils.textToast(UpdateDataActivity.this, result);
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_data);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		ViewUtils.inject(this);
		data = (Data) getIntent().getSerializableExtra("data");
		name.setText(data.getName());
		detail.setText(data.getDetail().equals("noDetail")?"":data.getDetail());
		remark.setText(data.getRemark().equals("noRemark")?"":data.getRemark());
		address.setText(data.getPlace());
		date1.setText(data.getIncidentDate1() + " " + data.getIncidentTime1());
		date2.setText(data.getIncidentDate2() + " " + data.getIncidentTime2());
		Bitmap bitmap = SystemUtils.getUserPic(data.getPicName());
		if (bitmap!=null) {
			pic.setImageBitmap(bitmap);
		}else {
			pic.setBackgroundResource(R.drawable.camera);
		}
		type_lost.setChecked(data.getType().equals("lost"));
		type_found.setChecked(data.getType().equals("find"));
		type_people.setChecked(data.getType().equals("peop"));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.update_data_back:
			finish();
			break;
		case R.id.updata_address:
			Intent aintent = new Intent(this, AdressActivity.class);
			aintent.putExtra("address", address.getText());
			startActivityForResult(aintent,
					MyValues.UPDATA_DATA_REQUEST_CODE_FOR_ADDRESS);
			break;
		case R.id.update_data_date1:
			DialogUtils.dateDialog(this, SystemUtils.getDate(),
					new UpdataBack() {

						@Override
						public void back(String back) {
							// TODO Auto-generated method stub
							date1.setText(back);
						}
					});
			break;
		case R.id.update_data_date2:
			DialogUtils.dateDialog(this, SystemUtils.getDate(),
					new UpdataBack() {

						@Override
						public void back(String back) {
							// TODO Auto-generated method stub
							date2.setText(back);
						}
					});
			break;
		case R.id.update_data_pic:
			Intent pIntent = new Intent(this, CameraActivity.class);
			pIntent.putExtra("userOrdata", 1);
			startActivityForResult(pIntent,
					MyValues.UPDATA_DATA_REQUEST_CODE_FOR_PIC);
			break;
		case R.id.update_data_commit:
			if (TextUtils.isEmpty(name.getText().toString())) {
				ToastUtils.textToast(this, "请填写物品名");
			} else if (TextUtils.isEmpty(addressDetail.getText().toString())) {
				ToastUtils.textToast(this, "请填写详细事发地点");
			} else if (date1.getText().toString()
					.equals(date2.getText().toString())) {
				ToastUtils.textToast(this, "请核对事发时段");
			} else {
				update();
				finish();
			}
			break;
		}
	}
	//修改信息
	private void update(){
		JSONObject json = null;
		try {
			json = JsonUtils.dataToJson(getData());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OpenVolley.json(MyValues.UpdateDataURL, MyValues.UpdateDataTAG, json,
				new ResponseListener() {

					@Override
					public void onSuccess(String msg) {
						// TODO Auto-generated method stub
						Message m = Message.obtain();
						m.what = MyValues.UpdateDataWHAT;
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
		// 上传图片
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
		// 获取所有消息字段值
		String userInfo = SharedPrefUtils
				.getString(this, MyValues.USERINFO, "");
		data.setId(0);
		String dataType = "";
		if (type_lost.isChecked()) 
			dataType = "lost";
		if(type_found.isChecked())
			dataType = "find";
		if(type_people.isChecked())
			dataType = "peop";
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

		try {
			User user = JsonUtils.jsonToUser(new JSONObject(userInfo));
			data.setUserPhone(user.getPhone());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	private String[] getTime(String time) {
		// 将日期中的年月日和时分分开
		return time.split(" ");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == MyValues.ADDRSS_RESULT_CODE
				&& requestCode == MyValues.UPDATA_DATA_REQUEST_CODE_FOR_ADDRESS) {// 返回地址
			address.setText(data.getStringExtra("address"));
		} else if (resultCode == MyValues.PIC_RESULT_CODE
				&& requestCode == MyValues.UPDATA_DATA_REQUEST_CODE_FOR_PIC) {
			picName = data.getStringExtra("picName");
			Bitmap bitmap = SystemUtils.getUserPic(picName);
			if (bitmap != null) {
				pic.setImageBitmap(bitmap);
			}
		}
	}

}

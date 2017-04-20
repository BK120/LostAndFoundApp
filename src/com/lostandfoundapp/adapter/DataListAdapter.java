package com.lostandfoundapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lostandfoundapp.activity.CheckPicActivity;
import com.lostandfoundapp.activity.R;
import com.lostandfoundapp.activity.UpdateDataActivity;
import com.lostandfoundapp.bean.Data;
import com.lostandfoundapp.bean.User;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.net.OpenHttpUCTask;
import com.lostandfoundapp.service.ShareService;
import com.lostandfoundapp.utils.DialogUtils;
import com.lostandfoundapp.utils.JsonUtils;
import com.lostandfoundapp.utils.SystemUtils;
import com.lostandfoundapp.utils.ToastUtils;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;


/**
 * �����б�������
 * 
 * @author lee
 *
 */
public class DataListAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	private List<Data> datas;
	private int home;// ���������ʾ�����ҵķ�����ʾ��1������Ҫ��æ��Ϊ�޸�

	private PopupWindow pop;
	private Data tag;//��������������Ŀ��������
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			String result = (String) msg.obj;
			switch (msg.what) {
			case MyValues.DeleteDataWHAT:
			case MyValues.UpdateDataWHAT:
				ToastUtils.textToast(context, result);
				notifyDataSetChanged();
				break;
			case MyValues.SelectUserWHAT:
				User user = null;
				try {
					user = JsonUtils.jsonToUser(JsonUtils.newJson(result));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap bitmap = SystemUtils.getUserPic(user.getPhone()+".jpg");
				DialogUtils.checkUserDialog(context, bitmap, user);
				break;
			}
		};
	};

	public DataListAdapter(Context context, List<Data> datas, int home) {
		super();
		this.context = context;
		this.datas = datas;
		this.home = home;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas != null ? datas.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(context, R.layout.data_list_item_view,
					null);
			initView(viewHolder, convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setValues(viewHolder, position);
		return convertView;
	}

	private void initView(ViewHolder v, View convertView) {
		// ��ʼ����Ŀ�ؼ�
		v.allView = (LinearLayout) convertView.findViewById(R.id.data_item_view);
		v.userPic = (ImageView) convertView
				.findViewById(R.id.data_item_user_pic);
		v.userPicBack = (ImageView) convertView.findViewById(R.id.data_item_user_pic_back);
		v.title = (TextView) convertView.findViewById(R.id.data_item_title);
		v.type = (ImageView) convertView.findViewById(R.id.data_item_type);
		v.detailLL = (LinearLayout) convertView
				.findViewById(R.id.data_item_detail_ll);
		v.detail = (TextView) convertView.findViewById(R.id.data_item_detail);
		v.place = (TextView) convertView.findViewById(R.id.data_item_place);
		v.address = (TextView) convertView.findViewById(R.id.data_item_address);
		v.date1 = (TextView) convertView.findViewById(R.id.data_item_date1);
		v.date2 = (TextView) convertView.findViewById(R.id.data_item_date2);
		v.remarkLL = (LinearLayout) convertView
				.findViewById(R.id.data_item_remark_ll);
		v.remark = (TextView) convertView.findViewById(R.id.data_item_remark);
		v.pic = (ImageView) convertView.findViewById(R.id.data_item_pic);
		v.phone = (TextView) convertView.findViewById(R.id.data_item_phone);
		v.publish = (TextView) convertView.findViewById(R.id.data_item_publish);
		v.share = (TextView) convertView.findViewById(R.id.data_item_share);

		v.pic.setOnClickListener(this);
		v.share.setOnClickListener(this);
	}

	private void setValues(ViewHolder v, int position) {
		// ������Ŀ�и����ؼ���ֵ
		Data data = datas.get(position);
		String type = data.getType();
		
		if (data.getIsFinish().trim().equals("y")) {//����ɽ���Ŀ�û�
			v.allView.setBackgroundColor(Color.GRAY);
			v.userPicBack.setBackgroundResource(R.drawable.pic_back_gray);
		}else {
			v.allView.setBackgroundColor(Color.WHITE);
			v.userPicBack.setBackgroundResource(R.drawable.pic_back);
		}

		setPic(v.userPic, data.getUserPhone());
		v.userPic.setTag(data.getUserPhone());

		v.share.setTag(data);
		if (home == 1) {
			v.share.setText("�޸���Ϣ");
		}else {
			v.userPic.setOnClickListener(this);
		}

		v.title.setText(data.getName());
		if (type.equals("lost")) {
			v.type.setBackgroundResource(R.drawable.type_lost);
			v.place.setText("��ʧ�ص㣺");
		} else if (type.equals("peop")) {
			v.type.setBackgroundResource(R.drawable.type_lost_peple);
			v.place.setText("��ʧ�ص㣺");
		}else {
			v.type.setBackgroundResource(R.drawable.type_found);
			v.place.setText("�ҵ��ص㣺");
		}
		String detail = data.getDetail();
		if (detail.equals("noDetail")) {
			v.detailLL.setVisibility(View.GONE);
		} else {
			v.detailLL.setVisibility(View.VISIBLE);
			v.detail.setText(detail);
		}
		v.address.setText(data.getPlace());
		v.date1.setText(data.getIncidentDate1() + " " + data.getIncidentTime1());
		v.date2.setText(data.getIncidentDate2() + " " + data.getIncidentTime2());
		String remark = data.getRemark();
		if (remark.equals("noRemark")) {
			v.remarkLL.setVisibility(View.GONE);
		} else {
			v.remarkLL.setVisibility(View.VISIBLE);
			v.remark.setText(remark);
		}
		if (data.getPicName().equals("noPic")) {
			v.pic.setVisibility(View.GONE);
		} else {
			v.pic.setVisibility(View.VISIBLE);
			v.pic.setTag(data.getPicName());
			setPic(v.pic, data.getPicName());
		}
		v.phone.setText(data.getUserPhone());
		v.publish.setText(data.getPublishDate() + " " + data.getPublishTime());
	}

	static class ViewHolder {
		LinearLayout detailLL, remarkLL ,allView;
		TextView title, detail, place, address, date1, date2, remark, phone,
				publish, share;
		ImageView type, pic, userPic ,userPicBack;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.data_item_pic:
			Intent bigIntent = new Intent(context, CheckPicActivity.class);
			bigIntent.putExtra("pic", (String) v.getTag());
			context.startActivity(bigIntent);
			break;
		case R.id.data_item_share:
			tag = (Data) v.getTag();
			if (home == 1) {
				updateDate();
			} else {
				showPopWindow(v);
			}
			break;
		case R.id.data_item_user_pic:
			showUserInfo((String) v.getTag());
			break;
		}
	}

	/**
	 * ��ʾ�û���Ϣ
	 */
	private void showUserInfo(String userPhone) {
		// TODO Auto-generated method stub
		JSONObject json = JsonUtils.strToJson(userPhone, "condition");
		OpenVolley.json(MyValues.SelectUserURL, MyValues.SelectUserTAG, json, new ResponseListener() {
			
			@Override
			public void onSuccess(String msg) {
				// TODO Auto-generated method stub
				Message m = Message.obtain();
				m.what = MyValues.SelectUserWHAT;
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
	
	/**
	 * �޸��û���Ϣ
	 */
	private void updateDate() {
		// TODO Auto-generated method stub
		OnClickListener[] clicks = { new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �޸���Ϣ
				Intent update = new Intent(context,UpdateDataActivity.class);
				update.putExtra("data", tag);
				context.startActivity(update);
			}
		}, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �޸�״̬
				final String isFinish = tag.getIsFinish();
				String text = "";
				if (isFinish.trim().equals("y")) {
					text = "ȷ��Ҫ�Ѹ�����Ϣ����Ϊδ�����";
				}else {
					text = "ȷ��Ҫ�Ѹ�����Ϣ����Ϊ�������";
				}
				DialogUtils.textDialog(context, text, new android.content.DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (isFinish.trim().equals("y")) {
						tag.setIsFinish("n");
						}else {
							tag.setIsFinish("y");
						}
						JSONObject json = null;
						try {
							json = JsonUtils.dataToJson(tag);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						OpenVolley.json(MyValues.UpdateDataURL, MyValues.UpdateDataTAG, json, new ResponseListener() {
							
							@Override
							public void onSuccess(String msg) {
								// TODO Auto-generated method stub
								Message m = Message.obtain();
								m.what = MyValues.UpdateDataWHAT;
								m.obj = JsonUtils.jsonToStr(JsonUtils.newJson(msg), "result");
								handler.sendMessage(m);
							}
							
							@Override
							public void onError(String msg) {
								// TODO Auto-generated method stub
								onSuccess(msg);
							}
						});
						dialog.dismiss();
					}
					
				});
			}
		}, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ɾ����Ϣ
				DialogUtils.textDialog(context, "ȷ��Ҫɾ��������¼��", new  android.content.DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Map<String, String> map = new HashMap<String, String>();
						map.put("phone", tag.getId()+"");
						OpenVolley.strin(MyValues.DeleteDataURL, MyValues.DeleteDataTAG, map, new ResponseListener() {
							
							@Override
							public void onSuccess(String msg) {
								// TODO Auto-generated method stub
								Message m = Message.obtain();
								m.what = MyValues.DeleteDataWHAT;
								m.obj = msg;
								handler.sendMessage(m);
							}
							
							@Override
							public void onError(String msg) {
								// TODO Auto-generated method stub
								onSuccess(msg);
							}
						});
						dialog.dismiss();
					}
				});
			}
		} };
		DialogUtils.updateDialog(context, clicks);
	}

	/**
	 * �첽����ͼƬ
	 * 
	 * @param view
	 * @param fileName
	 */
	public void setPic(ImageView view, String fileName) {
		Bitmap bitmap = SystemUtils.getUserPic(fileName);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
		} else {
			OpenHttpUCTask.downloadPic(MyValues.DataPicURL, fileName, view);
		}
	}

	private void shareToQQ() {
		// ����QQ
		if (ShareService.mtencent.isSessionValid()
				&& ShareService.mtencent.getOpenId() == null) {
			ToastUtils.textToast(context, "����δ��װQQ");
		} else {
			Bundle bundle = new Bundle();
			bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
					QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
			String type1 = tag.getType().equals("lost") ? "��ʧ��" : "���죺";
			String type2 = tag.getType().equals("lost") ? "��ʧ" : "��";
			bundle.putString(QQShare.SHARE_TO_QQ_TITLE, type1 + tag.getName());// ����ı���
			String detail = "������" + tag.getPlace() + type2 + tag.getName();
			bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, detail);// ����ժҪ
			bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, MyValues.CheckURL
					+ "?id=" + tag.getId());// �������ת����ҳ��ַ
			bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, MyValues.DataPicURL
					+ tag.getPicName());// ����ͼƬ
			ShareService.mtencent.shareToQQ((Activity) context, bundle,
					ShareService.mlistener);
		}
	}

	private void shareToQzone() {
		// �����ռ�
		Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
				QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		String type1 = tag.getType().equals("lost") ? "��ʧ��" : "���죺";
		String type2 = tag.getType().equals("lost") ? "��ʧ" : "��";
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, type1 + tag.getName());// ����
		String detail = "������" + tag.getPlace() + type2 + tag.getName();
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, detail);// ѡ��
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, MyValues.CheckURL
				+ "?id=" + tag.getId());// ����
		ArrayList<String> imgUrl = new ArrayList<String>();
		imgUrl.add(MyValues.DataPicURL + tag.getPicName());
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
		ShareService.mtencent.shareToQzone((Activity) context, params,
				ShareService.mlistener);
	}

	// 0-��������� 1-��������Ȧ
//	private void shareToXin(int flag) {
//		if (!ShareService.api.isWXAppInstalled()) {
//			ToastUtils.textToast(context, "����δ��װ΢��");
//			return;
//		} else {
//			// ����һ��WXWebPageObject�������ڷ�װҪ���͵�URL
//			WXWebpageObject webpage = new WXWebpageObject();
//			webpage.webpageUrl = MyValues.CheckURL + "?id=" + tag.getId();
//			// ����һ��WXMediaMessage����
//			WXMediaMessage msg = new WXMediaMessage(webpage);
//			String type1 = tag.getType().equals("lost") ? "��ʧ��" : "���죺";
//			String type2 = tag.getType().equals("lost") ? "��ʧ" : "��";
//			msg.title = type1 + tag.getName();
//			String detail = "������" + tag.getPlace() + type2 + tag.getName();
//			msg.description = detail;
//
//			SendMessageToWX.Req req = new SendMessageToWX.Req();
//			req.transaction = String.valueOf(System.currentTimeMillis());// transaction�ֶ�����Ψһ��ʶһ��������������У���������
//			req.message = msg;
//
//			// ��ʾ���͸�����Ȧ WXSceneTimeline ��ʾ���͸����� WXSceneSession
//			req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
//					: SendMessageToWX.Req.WXSceneTimeline;
//
//			ShareService.api.sendReq(req);
//		}
//	}

	/**
	 * ��ʾPopWindow
	 * 
	 * @param v
	 */
	private void showPopWindow(View v) {
		if (tag.getIsFinish().equals("y")) {//����ɵ����񲻿�ѡ����
			ToastUtils.textToast(context, "�Ѿ��ҵ�����лл");
		}else {
			getPopWindow();
			pop.showAsDropDown(v, 0, -230);
		}
	}

	/**
	 * ��ȡPopWindow
	 */
	private void getPopWindow() {
		if (pop != null) {
			pop.dismiss();
			return;
		} else {
			initPopWindow();
		}
	}

	/**
	 * ��ʼ��PopWindow
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void initPopWindow() {
		View contentView = View.inflate(context, R.layout.pop_share_view, null);// ��ȡPopWindow�Ľ��沼��
		GridView shareTo = (GridView) contentView.findViewById(R.id.share_gv);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < MyValues.item.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("icon", MyValues.item_icon[i]);
			map.put("name", MyValues.item[i]);
			list.add(map);

		}
		SimpleAdapter adapter = new SimpleAdapter(context, list,
				R.layout.share_item_view, new String[] { "icon", "name" },
				new int[] { R.id.share_icon, R.id.share_name });
		shareTo.setAdapter(adapter);
		shareTo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					shareToQQ();
					break;
				case 1:
					shareToQzone();
					break;
					//����΢�ſ���ƽ̨�޷�ͨ����ˣ���������΢�Ź���
//				case 1:
//					shareToXin(0);
//					break;
//				case 3:
//					shareToXin(1);
//					break;
				}
			}
		});
		pop = new PopupWindow(contentView,// �������������
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,// ����Ŀ������
				true);// �Ƿ�ɵ��
		contentView.setOnTouchListener(new OnTouchListener() {
			// �趨����ջ�Ч��
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (pop != null && pop.isShowing()) {// ���pop�Ѵ��ڲ�������ʾ
					pop.dismiss();
				}
				return false;// �¼�����󷵻�false
			}
		});

	}
}

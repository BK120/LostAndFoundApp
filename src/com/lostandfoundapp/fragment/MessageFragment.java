package com.lostandfoundapp.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lostandfoundapp.activity.LoginActivity;
import com.lostandfoundapp.activity.R;
import com.lostandfoundapp.activity.SignActivity;
import com.lostandfoundapp.adapter.DataListAdapter;
import com.lostandfoundapp.bean.Data;
import com.lostandfoundapp.bean.User;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.utils.JsonUtils;
import com.lostandfoundapp.utils.SharedPrefUtils;
import com.lostandfoundapp.view.pull.PullToRefreshView;
import com.lostandfoundapp.view.pull.PullToRefreshView.OnRefreshListener;
import com.lostandfoundapp.view.pull.PullableListView;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 我的发布界面 显示我发布的招领及丢失
 * 
 * @author lee
 *
 */
public class MessageFragment extends Fragment implements OnClickListener,
		OnItemSelectedListener {
	private Context context;//上下文
	private CheckBox showDone;//是否显示已完成的消息
	private Spinner type;//用来选择查看类型的下拉框
	private Button login, sign;//登录和注册按钮
	private LinearLayout noLogin;//没有登录时的界面
	private RelativeLayout hadLogin;//登录过的界面
	private TextView noFile, title;
	private ListView listView;//列表
	
	private PullToRefreshView pullView;//可下拉界面

	private int howMuch;//用于选择请求数据的类型

	private DataListAdapter adapter;//列表适配器
	private List<Data> datas;//数据

	public MessageFragment() {
		// TODO Auto-generated constructor stub
	}

	public MessageFragment(Context context) {
		this.context = context;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MyValues.SelectMyDataWHAT) {
				String result = (String) msg.obj;
				if (result.startsWith("{\"result")) {
					datas = null;
					result = JsonUtils.jsonToStr(JsonUtils.newJson(result),
							"result");
					pullView.refreshFinish(PullToRefreshView.FAIL);
					// ToastUtils.textToast(context, result);
				} else {
					try {
						datas = JsonUtils
								.jsonTodatas(JsonUtils.newJson(result));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					pullView.refreshFinish(PullToRefreshView.SUCCEED);
				}
				changeShow();
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 构造界面
		View view = inflater.inflate(R.layout.fragment_message, container,
				false);
		initDatas();
		initView(view);
		return view;
	}

//	@Override
//	public void onAttach(Context context) {
//		// TODO Auto-generated method stub
//		super.onAttach(context);
//		if (SharedPrefUtils.getBoolean(context, MyValues.ISLOGIN)) {
//			initDatas();
//		}
//	}


	private void initView(View view) {
		// 初始化控件
		type = (Spinner) view.findViewById(R.id.message_type);
		showDone = (CheckBox) view.findViewById(R.id.message_show_done);
		login = (Button) view.findViewById(R.id.message_login);
		sign = (Button) view.findViewById(R.id.message_sign);
		noLogin = (LinearLayout) view.findViewById(R.id.message_nologin);
		hadLogin = (RelativeLayout) view.findViewById(R.id.message_hadlogin);
		noFile = (TextView) view.findViewById(R.id.message_nofile);
		listView = (PullableListView) view.findViewById(R.id.message_list);
		title = (TextView) view.findViewById(R.id.message_title);
		pullView = (PullToRefreshView) view.findViewById(R.id.message_refresh_view);
		pullView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh(PullToRefreshView pullToRefreshLayout) {
				// TODO Auto-generated method stub
				initDatas();
			}
			
			@Override
			public void onLoadMore(PullToRefreshView pullToRefreshLayout) {
				// TODO Auto-generated method stub
				initDatas();
			}
		});

		type.setAdapter(new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, MyValues.TYPES));

		login.setOnClickListener(this);
		sign.setOnClickListener(this);
		type.setOnItemSelectedListener(this);
		noFile.setOnClickListener(this);
		title.setOnClickListener(this);
	}

	private void changeView() {
		if (SharedPrefUtils.getBoolean(context, MyValues.ISLOGIN)) {
			noLogin.setVisibility(View.GONE);
			hadLogin.setVisibility(View.VISIBLE);
			type.setVisibility(View.VISIBLE);
			showDone.setVisibility(View.VISIBLE);
		} else {
			noLogin.setVisibility(View.VISIBLE);
			hadLogin.setVisibility(View.GONE);
			type.setVisibility(View.GONE);
			showDone.setVisibility(View.GONE);
		}
	}

	private void initDatas() {
//		String userInfo = SharedPrefUtils.getString(context, MyValues.USERINFO,
//				"");
//		User user = null;
//		if (TextUtils.isEmpty(userInfo)) {
//			try {
//				user = JsonUtils.jsonToUser(JsonUtils.newJson(userInfo));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		if (user != null) {
			JSONObject json = JsonUtils.intToJson("howMuch", howMuch);
			try {
				json.put("phone", "18862006760");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			OpenVolley.json(MyValues.SelectDataURL, MyValues.SelectMyDataTAG,
					json, new ResponseListener() {

						@Override
						public void onSuccess(String msg) {
							// TODO Auto-generated method stub
							Message m = Message.obtain();
							m.what = MyValues.SelectMyDataWHAT;
							m.obj = msg;
							handler.sendMessage(m);
						}

						@Override
						public void onError(String msg) {
							// TODO Auto-generated method stub
							onSuccess(msg);
						}
					});
//		}
	}
	
	private void changeShow() {
		if (datas != null) {
			noFile.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			listShow();
		} else {
			noFile.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
	}
	
	private void listShow(){
//		List<Data> showList = new ArrayList<Data>();
//		List<Data> datas1 = new ArrayList<Data>();
//		List<Data> datas2 = new ArrayList<Data>();
//		for(Data d:datas){
//			if (d.getIsFinish().equals("n")) {//未完成
//				datas1.add(d);
//			}else {
//				datas2.add(d);
//			}
//		}
//		if (showDone.isChecked()) {//显示已完成
//			showList = datas1;
//		}else{
//			showList = datas2;
//		}
		if (adapter==null) {
			adapter = new DataListAdapter(context, datas,1);
			listView.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		// 控件监听
		switch (v.getId()) {
		case R.id.message_login:
			startActivity(new Intent(context, LoginActivity.class));
			break;

		case R.id.message_sign:
			startActivity(new Intent(context, SignActivity.class));
			break;
		case R.id.message_nofile:
			initDatas();
			break;
		case R.id.message_title:
			initDatas();
			break;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeView();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		howMuch = position + 3;
		initDatas();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}

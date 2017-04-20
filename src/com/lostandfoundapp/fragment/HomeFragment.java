package com.lostandfoundapp.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lostandfoundapp.activity.R;
import com.lostandfoundapp.adapter.DataListAdapter;
import com.lostandfoundapp.bean.Data;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.utils.JsonUtils;
import com.lostandfoundapp.view.pull.PullToRefreshView;
import com.lostandfoundapp.view.pull.PullToRefreshView.OnRefreshListener;
import com.lostandfoundapp.view.pull.PullableListView;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 主界面 显示所有发布
 * 
 * @author lee
 *
 */
public class HomeFragment extends Fragment implements OnClickListener,OnItemSelectedListener {
	private Context context;
	private ListView listView;
	private TextView nodata,title;
	private Spinner type;

	private DataListAdapter adapter;
	private List<Data> datas;
	
	private int howMuch;
	
	private PullToRefreshView pullView;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MyValues.SelectDataWHAT) {
				String result = (String) msg.obj;
				if (result.startsWith("{\"result")) {
					datas = null;
					result = JsonUtils.jsonToStr(JsonUtils.newJson(result),
							"result");
					pullView.refreshFinish(PullToRefreshView.FAIL);
//					ToastUtils.textToast(context, result);
				} else {
					try {
						datas = JsonUtils
								.jsonTodatas(JsonUtils.newJson(result));
						pullView.refreshFinish(PullToRefreshView.SUCCEED);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				changeView();
			}
		};
	};

	public HomeFragment() {
		// TODO Auto-generated constructor stub
	}

	public HomeFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 界面构造
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		initPullView(view);
		initDatas();
		init(view);
		return view;
	}

	private void initPullView(View view) {
		// TODO Auto-generated method stub
		pullView = (PullToRefreshView) view.findViewById(R.id.home_refresh_view);
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
	}

	private void initDatas() {
		// TODO Auto-generated method stub
		JSONObject json = JsonUtils.intToJson("howMuch", howMuch);
		OpenVolley.json(MyValues.SelectDataURL, MyValues.SelectDataTAG, json,
				new ResponseListener() {

					@Override
					public void onSuccess(String msg) {
						// TODO Auto-generated method stub
						Message m = Message.obtain();
						m.what = MyValues.SelectDataWHAT;
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

	private void init(View view) {
		// 初始化控件
		nodata = (TextView) view.findViewById(R.id.home_nodata);
		title = (TextView) view.findViewById(R.id.home_title_text);
		listView = (PullableListView) view.findViewById(R.id.home_list);
		type = (Spinner) view.findViewById(R.id.home_type);
		
		type.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,MyValues.TYPES));
		
		title.setOnClickListener(this);
		type.setOnItemSelectedListener(this);
		changeView();
	}

	private void changeView() {
		if (datas != null) {
			nodata.setVisibility(View.GONE);
			pullView.setVisibility(View.VISIBLE);
			type.setVisibility(View.VISIBLE);
			listShow();
		} else {
			nodata.setVisibility(View.VISIBLE);
			pullView.setVisibility(View.GONE);
			type.setVisibility(View.VISIBLE);
			nodata.setOnClickListener(this);
		}
	}
	
	private void listShow(){
		if (adapter==null) {
			adapter = new DataListAdapter(context, datas,0);
			listView.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.home_nodata:
			initDatas();
			break;

		case R.id.home_title_text:
			initDatas();
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		howMuch = position;
		initDatas();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

}

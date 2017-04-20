package com.lostandfoundapp.activity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.utils.GPSUtil;
import com.lostandfoundapp.utils.SystemUtils;
import com.lostandfoundapp.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

/**
 * 获取地址 通过该界面选择三级地址
 * 
 * @author lee
 *
 */
public class AdressActivity extends Activity {
	@ViewInject(R.id.address_back)
	private ImageView back;//退出按钮
	@ViewInject(R.id.address_province)
	private Spinner province;//省下拉菜单
	@ViewInject(R.id.address_city)
	private Spinner city;//市下拉菜单
	@ViewInject(R.id.address_area)
	private Spinner area;//区下拉菜单
	@ViewInject(R.id.address_detail)
	private EditText detail;//详细地址输入框
	@ViewInject(R.id.address_commit)
	private Button commit;//提交按钮
	@ViewInject(R.id.address_gps)
	private Button gps;//选择定位按钮
	private JSONObject jsonObject;// 把全国的省市区的信息以JSON的格式保存
	private String[] allProv;// 所有的省

	private ArrayAdapter<String> provinceAdapter;// 省份数据适配器
	private ArrayAdapter<String> cityAdapter;// 城市数据适配器
	private ArrayAdapter<String> areaAdapter;// 区县数据适配器

	private String[] allSpinList;// 在Spinner中选出来的地址，后面需要用空格隔开省市区

	private String address;// 用来接收Intent的参数

	private String provinceName;// 省的名字
	private String cityName;// 市的名字
	private String areaName;// 区的名字
	
	// 省市区的集合
	private Map<String, String[]> cityMap = new HashMap<String, String[]>();// key:省p---value:市n
																			// value是一个集合
	private Map<String, String[]> areaMap = new HashMap<String, String[]>();// key:市n---value:区s
																			// 区也是一个集合
	private boolean choseByUser;//是否由用户手动选择
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adress);
		initView();//初始化控件
		initJsonData();//初始化JSON数据
		initDatas();//初始化数据
		setSpinner();//设置Spinner
		setSpinnerData();//设置Spinner默认显示
		setListener();// 设置spinner的点击监听
		initClick();// 设置监听
	}

	/**
	 * spinner初始化
	 */
	private void setSpinner() {
		// 省
		provinceAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		provinceAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		province.setAdapter(provinceAdapter);
		// 市
		cityAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		cityAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		city.setAdapter(cityAdapter);

		// 区
		areaAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		areaAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		area.setAdapter(areaAdapter);
	}

	/**
	 * 设置Spinner默认值
	 */
	private void setSpinnerData() {
		if (address != null && !address.equals("")) {
			allSpinList = address.split(" ");// 区分传入数据，分为省、市、区、详细地址
			if (allSpinList.length == 4) {//普通省
				detail.setText(allSpinList[3].trim());
				updateProvince(allSpinList[0].trim(), allSpinList[1].trim(), allSpinList[2].trim());
			}else if (allSpinList.length==3) {//直辖市
				detail.setText(allSpinList[2].trim());
				updateProvince(allSpinList[0].trim(), allSpinList[0].trim(), allSpinList[1].trim());
			}
			choseByUser = false;
			
		} else {
			choseByUser = true;
			updateCity(null, null, null);
		}
	}

	/**
	 * 设置Spinner的点击监听,实现三级联动
	 */
	private void setListener() {
		// 省
		province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				provinceName = parent.getSelectedItem() + "";// 获取省名字
				if (choseByUser) {
					updateCity(provinceName, null, null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		// 市
		city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				cityName = parent.getSelectedItem() + "";// 获取市的名字
				if (choseByUser) {
					updateArea(parent.getSelectedItem() + "", null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// 区
		area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				areaName = parent.getSelectedItem() + "";// 获取区的名字
				choseByUser = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	/**
	 * 更新省，连带跟新市
	 * 
	 * @param province
	 * @param city
	 * @param area
	 */
	private void updateProvince(Object province, Object city, Object area) {
		int selectPosition = 0;// 设置默认显示第一个
		provinceAdapter.clear();
		for (int i = 0; i < allProv.length; i++) {
			// 给Spinner省赋值,设置默认值
			if (province != null && province.toString().equals(allProv[i])) {
				selectPosition = i;// 找到传入数据省的位置
			}
			provinceAdapter.add(allProv[i]);// 添加每一个省
		}
		provinceAdapter.notifyDataSetChanged();
		if (province == null) {
			updateCity(allProv[selectPosition], null, null);
		} else {
			this.province.setSelection(selectPosition);// 设置当前显示传入的省
			updateCity(allProv[selectPosition], city, area);
		}

	}

	/**
	 * 根据当前的省，更新市和区的信息
	 * 
	 * @param object
	 * @param city
	 * @param area
	 */
	private void updateCity(Object object, Object city, Object area) {
		int selectPosition = 0;// 有数据时，进行匹配城市，默认选中
		String[] cities = cityMap.get(object);
		cityAdapter.clear();// 清空adapter的数据
		for (int i = 0; i < cities.length; i++) {
			if (city != null && city.toString().equals(cities[i])) {// 判断传入的市在集合中匹配
				selectPosition = i;
			}
			cityAdapter.add(cities[i]);// 将这个列表“市”添加到adapter中
		}
		cityAdapter.notifyDataSetChanged();// 刷新
		if (city == null) {
			updateArea(cities[0], null);// 更新区,没有市则默认第一个给它
		} else {
			this.city.setSelection(selectPosition);
			updateArea(city, area);// 传入的区去集合中匹配
		}
	}

	/**
	 * 根据当前的市，更新区的信息
	 * 
	 * @param object
	 * @param myArea
	 */
	private void updateArea(Object object, Object myArea) {
		int selectPosition = 0;// 当有数据时，进行匹配地区，默认选中
		String[] area = areaMap.get(object);
		areaAdapter.clear();// 清空
		if (area != null) {
			for (int i = 0; i < area.length; i++) {
				if (myArea != null && myArea.toString().equals(area[i])) {// 去集合中匹配
					selectPosition = i;
				}
				areaAdapter.add(area[i]);
			}
		}
		areaAdapter.notifyDataSetChanged();// 刷新
		this.area.setSelection(selectPosition);// 默认选中
	}

	// 初始化省市区数据
	private void initDatas() {
		try {
			JSONArray jsonArray = jsonObject.getJSONArray("citylist");// 获取整个json数据
			allProv = new String[jsonArray.length()];// 封装数据
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonP = jsonArray.getJSONObject(i);// jsonArray转jsonObject
				String provStr = jsonP.getString("name");// 获取所有的省
				allProv[i] = provStr;// 封装所有的省
				JSONArray jsonCity = null;

				try {
					jsonCity = jsonP.getJSONArray("city");// 在所有的省中取出所有的市，转jsonArray
				} catch (Exception e) {
					continue;
				}
				// 所有的市
				String[] allCity = new String[jsonCity.length()];// 所有市的长度
				for (int c = 0; c < jsonCity.length(); c++) {
					JSONObject jsonCy = jsonCity.getJSONObject(c);// 转jsonObject
					String cityStr = jsonCy.getString("name");// 取出所有的市
					allCity[c] = cityStr;// 封装市集合

					JSONArray jsonArea = null;
					try {
						jsonArea = jsonCy.getJSONArray("area");// 在从所有的市里面取出所有的区,转jsonArray
					} catch (Exception e) {
						continue;
					}
					String[] allArea = new String[jsonArea.length()];// 所有的区
					for (int a = 0; a < jsonArea.length(); a++) {
						String areaStr = jsonArea.getString(a);// 获取所有的区
						allArea[a] = areaStr;// 封装起来
					}

					areaMap.put(cityStr, allArea);// 某个市取出所有的区集合

				}
				cityMap.put(provStr, allCity);// 某个省取出所有的市,
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		jsonObject = null;// 清空所有的数据
	}

	/**
	 * 从assert文件夹中获取json数据
	 */
	private void initJsonData() {
		try {
			StringBuffer sb = new StringBuffer();
			InputStream is = getAssets().open("citylist.json");// 打开JSON数据
			byte[] by = new byte[is.available()];// 转字节
			int len = -1;
			while ((len = is.read(by)) != -1) {
				sb.append(new String(by, 0, len, "utf-8"));// 根据字节长度设置编码
			}
			is.close();// 关闭流
			jsonObject = new JSONObject(sb.toString());// 为JSON赋值
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 初始化点击
	private void initClick() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		commit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 把值返回给MainActivity
				Intent intent = new Intent();
				String detailString = detail.getText().toString();
				if (SystemUtils.isCotain(detailString)) {
					ToastUtils
							.textToast(AdressActivity.this, "详细地址不能包含非法字符或空格");
				} else {
					intent.putExtra("address", provinceName + " " + cityName
							+ " " + areaName + " " + detailString);
					setResult(MyValues.ADDRSS_RESULT_CODE, intent);
					finish();
				}
			}
		});
		gps.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 定位当前地址
				address = GPSUtil.getAddress(AdressActivity.this);
				choseByUser = false;
				setSpinnerData();
			}
		});
	}

	// 初始化控件
	private void initView() {
		ViewUtils.inject(this);
		address = getIntent().getStringExtra("address");
	}

}

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
 * ��ȡ��ַ ͨ���ý���ѡ��������ַ
 * 
 * @author lee
 *
 */
public class AdressActivity extends Activity {
	@ViewInject(R.id.address_back)
	private ImageView back;//�˳���ť
	@ViewInject(R.id.address_province)
	private Spinner province;//ʡ�����˵�
	@ViewInject(R.id.address_city)
	private Spinner city;//�������˵�
	@ViewInject(R.id.address_area)
	private Spinner area;//�������˵�
	@ViewInject(R.id.address_detail)
	private EditText detail;//��ϸ��ַ�����
	@ViewInject(R.id.address_commit)
	private Button commit;//�ύ��ť
	@ViewInject(R.id.address_gps)
	private Button gps;//ѡ��λ��ť
	private JSONObject jsonObject;// ��ȫ����ʡ��������Ϣ��JSON�ĸ�ʽ����
	private String[] allProv;// ���е�ʡ

	private ArrayAdapter<String> provinceAdapter;// ʡ������������
	private ArrayAdapter<String> cityAdapter;// ��������������
	private ArrayAdapter<String> areaAdapter;// ��������������

	private String[] allSpinList;// ��Spinner��ѡ�����ĵ�ַ��������Ҫ�ÿո����ʡ����

	private String address;// ��������Intent�Ĳ���

	private String provinceName;// ʡ������
	private String cityName;// �е�����
	private String areaName;// ��������
	
	// ʡ�����ļ���
	private Map<String, String[]> cityMap = new HashMap<String, String[]>();// key:ʡp---value:��n
																			// value��һ������
	private Map<String, String[]> areaMap = new HashMap<String, String[]>();// key:��n---value:��s
																			// ��Ҳ��һ������
	private boolean choseByUser;//�Ƿ����û��ֶ�ѡ��
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adress);
		initView();//��ʼ���ؼ�
		initJsonData();//��ʼ��JSON����
		initDatas();//��ʼ������
		setSpinner();//����Spinner
		setSpinnerData();//����SpinnerĬ����ʾ
		setListener();// ����spinner�ĵ������
		initClick();// ���ü���
	}

	/**
	 * spinner��ʼ��
	 */
	private void setSpinner() {
		// ʡ
		provinceAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		provinceAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		province.setAdapter(provinceAdapter);
		// ��
		cityAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		cityAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		city.setAdapter(cityAdapter);

		// ��
		areaAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		areaAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		area.setAdapter(areaAdapter);
	}

	/**
	 * ����SpinnerĬ��ֵ
	 */
	private void setSpinnerData() {
		if (address != null && !address.equals("")) {
			allSpinList = address.split(" ");// ���ִ������ݣ���Ϊʡ���С�������ϸ��ַ
			if (allSpinList.length == 4) {//��ͨʡ
				detail.setText(allSpinList[3].trim());
				updateProvince(allSpinList[0].trim(), allSpinList[1].trim(), allSpinList[2].trim());
			}else if (allSpinList.length==3) {//ֱϽ��
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
	 * ����Spinner�ĵ������,ʵ����������
	 */
	private void setListener() {
		// ʡ
		province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				provinceName = parent.getSelectedItem() + "";// ��ȡʡ����
				if (choseByUser) {
					updateCity(provinceName, null, null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		// ��
		city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				cityName = parent.getSelectedItem() + "";// ��ȡ�е�����
				if (choseByUser) {
					updateArea(parent.getSelectedItem() + "", null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// ��
		area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				areaName = parent.getSelectedItem() + "";// ��ȡ��������
				choseByUser = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	/**
	 * ����ʡ������������
	 * 
	 * @param province
	 * @param city
	 * @param area
	 */
	private void updateProvince(Object province, Object city, Object area) {
		int selectPosition = 0;// ����Ĭ����ʾ��һ��
		provinceAdapter.clear();
		for (int i = 0; i < allProv.length; i++) {
			// ��Spinnerʡ��ֵ,����Ĭ��ֵ
			if (province != null && province.toString().equals(allProv[i])) {
				selectPosition = i;// �ҵ���������ʡ��λ��
			}
			provinceAdapter.add(allProv[i]);// ���ÿһ��ʡ
		}
		provinceAdapter.notifyDataSetChanged();
		if (province == null) {
			updateCity(allProv[selectPosition], null, null);
		} else {
			this.province.setSelection(selectPosition);// ���õ�ǰ��ʾ�����ʡ
			updateCity(allProv[selectPosition], city, area);
		}

	}

	/**
	 * ���ݵ�ǰ��ʡ�������к�������Ϣ
	 * 
	 * @param object
	 * @param city
	 * @param area
	 */
	private void updateCity(Object object, Object city, Object area) {
		int selectPosition = 0;// ������ʱ������ƥ����У�Ĭ��ѡ��
		String[] cities = cityMap.get(object);
		cityAdapter.clear();// ���adapter������
		for (int i = 0; i < cities.length; i++) {
			if (city != null && city.toString().equals(cities[i])) {// �жϴ�������ڼ�����ƥ��
				selectPosition = i;
			}
			cityAdapter.add(cities[i]);// ������б��С���ӵ�adapter��
		}
		cityAdapter.notifyDataSetChanged();// ˢ��
		if (city == null) {
			updateArea(cities[0], null);// ������,û������Ĭ�ϵ�һ������
		} else {
			this.city.setSelection(selectPosition);
			updateArea(city, area);// �������ȥ������ƥ��
		}
	}

	/**
	 * ���ݵ�ǰ���У�����������Ϣ
	 * 
	 * @param object
	 * @param myArea
	 */
	private void updateArea(Object object, Object myArea) {
		int selectPosition = 0;// ��������ʱ������ƥ�������Ĭ��ѡ��
		String[] area = areaMap.get(object);
		areaAdapter.clear();// ���
		if (area != null) {
			for (int i = 0; i < area.length; i++) {
				if (myArea != null && myArea.toString().equals(area[i])) {// ȥ������ƥ��
					selectPosition = i;
				}
				areaAdapter.add(area[i]);
			}
		}
		areaAdapter.notifyDataSetChanged();// ˢ��
		this.area.setSelection(selectPosition);// Ĭ��ѡ��
	}

	// ��ʼ��ʡ��������
	private void initDatas() {
		try {
			JSONArray jsonArray = jsonObject.getJSONArray("citylist");// ��ȡ����json����
			allProv = new String[jsonArray.length()];// ��װ����
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonP = jsonArray.getJSONObject(i);// jsonArrayתjsonObject
				String provStr = jsonP.getString("name");// ��ȡ���е�ʡ
				allProv[i] = provStr;// ��װ���е�ʡ
				JSONArray jsonCity = null;

				try {
					jsonCity = jsonP.getJSONArray("city");// �����е�ʡ��ȡ�����е��У�תjsonArray
				} catch (Exception e) {
					continue;
				}
				// ���е���
				String[] allCity = new String[jsonCity.length()];// �����еĳ���
				for (int c = 0; c < jsonCity.length(); c++) {
					JSONObject jsonCy = jsonCity.getJSONObject(c);// תjsonObject
					String cityStr = jsonCy.getString("name");// ȡ�����е���
					allCity[c] = cityStr;// ��װ�м���

					JSONArray jsonArea = null;
					try {
						jsonArea = jsonCy.getJSONArray("area");// �ڴ����е�������ȡ�����е���,תjsonArray
					} catch (Exception e) {
						continue;
					}
					String[] allArea = new String[jsonArea.length()];// ���е���
					for (int a = 0; a < jsonArea.length(); a++) {
						String areaStr = jsonArea.getString(a);// ��ȡ���е���
						allArea[a] = areaStr;// ��װ����
					}

					areaMap.put(cityStr, allArea);// ĳ����ȡ�����е�������

				}
				cityMap.put(provStr, allCity);// ĳ��ʡȡ�����е���,
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		jsonObject = null;// ������е�����
	}

	/**
	 * ��assert�ļ����л�ȡjson����
	 */
	private void initJsonData() {
		try {
			StringBuffer sb = new StringBuffer();
			InputStream is = getAssets().open("citylist.json");// ��JSON����
			byte[] by = new byte[is.available()];// ת�ֽ�
			int len = -1;
			while ((len = is.read(by)) != -1) {
				sb.append(new String(by, 0, len, "utf-8"));// �����ֽڳ������ñ���
			}
			is.close();// �ر���
			jsonObject = new JSONObject(sb.toString());// ΪJSON��ֵ
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ��ʼ�����
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
				// ��ֵ���ظ�MainActivity
				Intent intent = new Intent();
				String detailString = detail.getText().toString();
				if (SystemUtils.isCotain(detailString)) {
					ToastUtils
							.textToast(AdressActivity.this, "��ϸ��ַ���ܰ����Ƿ��ַ���ո�");
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
				// ��λ��ǰ��ַ
				address = GPSUtil.getAddress(AdressActivity.this);
				choseByUser = false;
				setSpinnerData();
			}
		});
	}

	// ��ʼ���ؼ�
	private void initView() {
		ViewUtils.inject(this);
		address = getIntent().getStringExtra("address");
	}

}

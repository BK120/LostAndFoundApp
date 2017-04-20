package com.lostandfoundapp.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lostandfoundapp.service.IntnetService;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class GPSUtil {
	/**
	 * 获取位置经纬度
	 * 
	 * @param context
	 * @return
	 */
	public static double[] getGPS(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		//通过GPS卫星定位,定位级别到街
	    boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    //通过WLAN或者移动网络确定位置
	    boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!gps||!network) {
			Intent GPSIntent = new Intent();
		    GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvide");
		    GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		    GPSIntent.setData(Uri.parse("custom:3"));

		    try {
		        //使用PendingIntent发送广播告诉手机去开启GPS功能
		        PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		    } catch (PendingIntent.CanceledException e) {
		        e.printStackTrace();
		    }
		}
		    Location location;
		    if (network) {
		        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
		        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		    } else {
		        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
		        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		    }
		    if (location==null) {
				return null;
			}else{
			double longitude = location.getLongitude();// 经度
			double latitude = location.getLatitude();// 纬度
			removeListener(locationManager);
			return new double[] { latitude ,longitude};}
	}
	/**
	 * 通过Google API解析经纬度为城市信息
	 * @param context
	 * @return
	 */
	public static String getAddress(Context context) {
		IntnetService.listener.stateChange(1);
		Geocoder geocoder = new Geocoder(context);
		StringBuilder addressString = new StringBuilder();
		double[] address = getGPS(context);
		List<Address> fromLocation = new ArrayList<Address>();
		try {
			fromLocation = geocoder.getFromLocation(address[0], address[1], 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < fromLocation.size(); i++) {
			Address addr = fromLocation.get(i);
			String adString = addr.getAddressLine(0);//获取详细地址，但格式有所不同
			String city = addr.getLocality();//获取城市
			String detail = addr.getThoroughfare();//获取详细地址
			//将格式设定为相同的
			String[] split1 = adString.split(city);
			String[] split2 = split1[1].split(detail);
			String provide = "";
			if(!split1[0].equals(""))
				provide = split1[0].substring(0, split1[0].length()-1);
			city = city.substring(0,city.length()-1);
			addressString.append(provide+" "+city+" "+split2[0]+" "+detail+"\n");
		}
		String finalString = SystemUtils.setChar(addressString.toString(), "UTF-8");
		System.out.println(finalString);
		IntnetService.listener.stateChange(0);
		return finalString;
	}

	//GPS位置监听器
	private static LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// 状态变化
			switch (status) {
			case LocationProvider.AVAILABLE:
				Log.i("briup", "GPS当前可见");
				break;
			case LocationProvider.OUT_OF_SERVICE:
				Log.i("briup", "GPS当前不在服务区");
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i("briup", "GPS当前暂停服务");
				break;

			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			// GPS开启
		}

		@Override
		public void onProviderDisabled(String provider) {
			// GPS禁用
		}

		@Override
		public void onLocationChanged(Location location) {
			// 位置变化
			Log.i("LAF",
					"时间：" + location.getTime() + "经-" + location.getLongitude()
							+ "纬-" + location.getLatitude() + "海拔-"
							+ location.getAltitude());
		}
	};
	/**
	 * 取消监听绑定，释放资源
	 * @param locationManager
	 */
	private static void removeListener(LocationManager locationManager) {
		locationManager.removeUpdates(locationListener);
	}

}

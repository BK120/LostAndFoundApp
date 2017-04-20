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
	 * ��ȡλ�þ�γ��
	 * 
	 * @param context
	 * @return
	 */
	public static double[] getGPS(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		//ͨ��GPS���Ƕ�λ,��λ���𵽽�
	    boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    //ͨ��WLAN�����ƶ�����ȷ��λ��
	    boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!gps||!network) {
			Intent GPSIntent = new Intent();
		    GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvide");
		    GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		    GPSIntent.setData(Uri.parse("custom:3"));

		    try {
		        //ʹ��PendingIntent���͹㲥�����ֻ�ȥ����GPS����
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
			double longitude = location.getLongitude();// ����
			double latitude = location.getLatitude();// γ��
			removeListener(locationManager);
			return new double[] { latitude ,longitude};}
	}
	/**
	 * ͨ��Google API������γ��Ϊ������Ϣ
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
			String adString = addr.getAddressLine(0);//��ȡ��ϸ��ַ������ʽ������ͬ
			String city = addr.getLocality();//��ȡ����
			String detail = addr.getThoroughfare();//��ȡ��ϸ��ַ
			//����ʽ�趨Ϊ��ͬ��
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

	//GPSλ�ü�����
	private static LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// ״̬�仯
			switch (status) {
			case LocationProvider.AVAILABLE:
				Log.i("briup", "GPS��ǰ�ɼ�");
				break;
			case LocationProvider.OUT_OF_SERVICE:
				Log.i("briup", "GPS��ǰ���ڷ�����");
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i("briup", "GPS��ǰ��ͣ����");
				break;

			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			// GPS����
		}

		@Override
		public void onProviderDisabled(String provider) {
			// GPS����
		}

		@Override
		public void onLocationChanged(Location location) {
			// λ�ñ仯
			Log.i("LAF",
					"ʱ�䣺" + location.getTime() + "��-" + location.getLongitude()
							+ "γ-" + location.getLatitude() + "����-"
							+ location.getAltitude());
		}
	};
	/**
	 * ȡ�������󶨣��ͷ���Դ
	 * @param locationManager
	 */
	private static void removeListener(LocationManager locationManager) {
		locationManager.removeUpdates(locationListener);
	}

}

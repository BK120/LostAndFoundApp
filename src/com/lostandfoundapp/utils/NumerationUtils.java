package com.lostandfoundapp.utils;
/**
 * ������������Ĺ�����
 * @author lee
 *
 */
public class NumerationUtils {
	/**
	 * ��ȡ�����
	 */
	public static int random(){
		return (int) (Math.random()*9);
	}
	/**
	 * ��������Ϊ��ȷ����ʾ��ʽ
	 * @param m
	 * @return
	 */
	public static String setTime(int m){
		String s = "00";
		if (m<10) {
			s = "0"+m;
		}else {
			s = ""+m;
		}
		return s;
	}
}

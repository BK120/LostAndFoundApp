package com.lostandfoundapp.view.pull;

public interface Pullable
{
	/**
	 * �ж��Ƿ�����������������Ҫ�������ܿ���ֱ�ӷ��� false
	 * 
	 * @return ���������������true,���򷵻�false
	 */
	boolean canPullDown();

	/**
	 * �ж��Ƿ�����������������Ҫ�������ܿ���ֱ�ӷ��� false
	 * 
	 * @return ���������������true,���򷵻�false
	 */
	boolean canPullUp();
}

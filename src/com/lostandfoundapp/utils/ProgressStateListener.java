package com.lostandfoundapp.utils;
/**
 * 进程状态监听接口
 * 用于返回网络访问的状态
 * @author lee
 *
 */
public interface ProgressStateListener {
	void stateChange(int state);
}

package com.lostandfoundapp.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.data.Tips;
import com.lostandfoundapp.service.IntnetService;
import com.lostandfoundapp.utils.NumerationUtils;
import com.lostandfoundapp.utils.SharedPrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
/**
 * Splash界面，该界面为启动APP最先进入的界面
 * 该界面有本APP图片一张，随机弹出话语以动画形式显示
 * 该界面停顿2秒后即进入APP主界面HomeActivity
 * @author lee
 * 
 */
public class SplashActivity extends Activity {
	@ViewInject(R.id.splash_tip)
	private TextView splashTip;//显示在界面下方的话语
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ViewUtils.inject(this);//xUtils获取控件
		//随机获取话语
		String tip = Tips.tips[NumerationUtils.random()];
		splashTip.setText(tip);
		//补间动画运行
		splashTip.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_tip_anim));
		startIntnetService();
		//延时跳转界面
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// 延时2秒后判断是否播放过新手指引，若已播放过则进入主界面，若没有则进入新手指引
				Intent intent = new Intent();
				if (SharedPrefUtils.getBoolean(SplashActivity.this, MyValues.ISGUIDE)) {
					intent.setClass(SplashActivity.this, HomeActivity.class);
				}else {
					intent.setClass(SplashActivity.this, GuidActivity.class);
				}
				startActivity(intent);
				SplashActivity.this.finish();//界面跳转后销毁Splash界面，避免回退再次回到该界面
			}
		}, 2000);
	}

	private void startIntnetService() {
		// 启动网络相关服务
		Intent service = new Intent(this,IntnetService.class);
		startService(service);
	}

}

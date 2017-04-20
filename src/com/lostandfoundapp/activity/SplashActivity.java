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
 * Splash���棬�ý���Ϊ����APP���Ƚ���Ľ���
 * �ý����б�APPͼƬһ�ţ�������������Զ�����ʽ��ʾ
 * �ý���ͣ��2��󼴽���APP������HomeActivity
 * @author lee
 * 
 */
public class SplashActivity extends Activity {
	@ViewInject(R.id.splash_tip)
	private TextView splashTip;//��ʾ�ڽ����·��Ļ���
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ViewUtils.inject(this);//xUtils��ȡ�ؼ�
		//�����ȡ����
		String tip = Tips.tips[NumerationUtils.random()];
		splashTip.setText(tip);
		//���䶯������
		splashTip.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_tip_anim));
		startIntnetService();
		//��ʱ��ת����
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// ��ʱ2����ж��Ƿ񲥷Ź�����ָ�������Ѳ��Ź�����������棬��û�����������ָ��
				Intent intent = new Intent();
				if (SharedPrefUtils.getBoolean(SplashActivity.this, MyValues.ISGUIDE)) {
					intent.setClass(SplashActivity.this, HomeActivity.class);
				}else {
					intent.setClass(SplashActivity.this, GuidActivity.class);
				}
				startActivity(intent);
				SplashActivity.this.finish();//������ת������Splash���棬��������ٴλص��ý���
			}
		}, 2000);
	}

	private void startIntnetService() {
		// ����������ط���
		Intent service = new Intent(this,IntnetService.class);
		startService(service);
	}

}

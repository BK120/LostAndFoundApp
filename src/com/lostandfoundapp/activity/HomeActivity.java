package com.lostandfoundapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.adapter.LaFPagerAdapter;
import com.lostandfoundapp.animation.MenuAnimation;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.fragment.HomeFragment;
import com.lostandfoundapp.fragment.MessageFragment;
import com.lostandfoundapp.fragment.MineFragment;
import com.lostandfoundapp.listener.IntFeedBack;
import com.lostandfoundapp.service.IntnetService;
import com.lostandfoundapp.service.ShareService;
import com.lostandfoundapp.utils.DialogUtils;
import com.lostandfoundapp.utils.SystemUtils;
import com.lostandfoundapp.utils.ToastUtils;
import com.lostandfoundapp.utils.UpdataBack;
import com.lostandfoundapp.volley.OpenVolley;
import com.lostandfoundapp.volley.ResponseListener;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 主要与用户交互的界面 该界面将是整个APP最后退出的界面
 * 
 * @author lee
 *
 */
@SuppressLint("UseSparseArrays")
public class HomeActivity extends FragmentActivity implements OnClickListener {
	@ViewInject(R.id.home_pager)
	private ViewPager homeVP;// 用于存放界面并实现滑动
	@ViewInject(R.id.home_home)
	private ImageView menuHome;// 切换至大厅的图片按钮
	@ViewInject(R.id.home_message)
	private ImageView menuMessage;// 切换至我的招领的图片按钮
	@ViewInject(R.id.home_mine)
	private ImageView menuMine;// 切换至我的信息的图片按钮
	@ViewInject(R.id.home_edit)
	private ImageView menuEdit;// 发表丢失或招领的图片按钮
	@ViewInject(R.id.home_menu)
	private ImageView menuMenu;// 图片按钮显示隐藏的图片按钮

	private int sysWidth;// 屏幕宽度
	private int btnWidth;// 图片按钮宽度
	private int btnMarginRight;// 图片按钮右边距
	private int moveLength;// 图片按钮可移动长度
	private List<ImageView> list1;// 用于存储横向动画的图片
	private int flag;// 标记当前界面
	private boolean isOpen;// 用于标记按钮是否被打开
	private long exitTime;// 用于标记点击过物理返回键最后时间

	private static List<Fragment> fragments;// 界面集合

	private PopupWindow pop;// 弹出发布丢失或招领
	
	private SoundPool soundPool;//音效播放
	private Map<Integer, Integer> map;//存储音效

	private ServiceConnection conn;//服务连接
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what==MyValues.TestWHAT) {//修改IP地址后进行网络连接测试
				String result = (String) msg.obj;
				if (result.equals("Testing")) {
					IntnetService.INTNET = true;
					ToastUtils.textToast(HomeActivity.this, "网络连接成功");
				}else {
					IntnetService.INTNET = false;
					ToastUtils.textToast(HomeActivity.this, result+"\n请确保当前客户端与服务器处于相同局域网");
				}
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ViewUtils.inject(this);// xUtils获取控件
		initValues();//设置控件初始值
		initView();
		setPagerListener();
		startShareService();
	}

	private void startShareService() {
		// 启动分享相关服务
		conn = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// 在Activity与Service异常解除绑定时回调
				Log.i("LAF", "服务异常解绑");
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// 在Activity与Service进行绑定成功时回调
				Log.i("LAF", "服务绑定成功");
			}
		};
		Intent service = new Intent(this,ShareService.class);
		bindService(service, conn, Service.BIND_AUTO_CREATE);
	}

	private void initView() {
		// 初始化控件设置
		menuMenu.setOnClickListener(this);
		menuEdit.setOnClickListener(this);
		menuHome.setOnClickListener(this);
		menuMessage.setOnClickListener(this);
		menuMine.setOnClickListener(this);

		fragments = new ArrayList<Fragment>();
		fragments.add(new MessageFragment(this));
		fragments.add(new HomeFragment(this));
		fragments.add(new MineFragment(this));
		// 适配器配置
		homeVP.setAdapter(new LaFPagerAdapter(getSupportFragmentManager(),
				fragments));
	}

	private void initValues() {
		// 初始化数据
		sysWidth = SystemUtils.getSysWidth(this);
		SystemUtils.getViewWH(menuMenu, new IntFeedBack() {

			@Override
			public void feedBack(int... args) {
				// 从传回的两个参数中取到控件宽度和外右边距
				btnWidth = args[0];
				btnMarginRight = args[3];
				moveLength = sysWidth - btnWidth - btnMarginRight * 2;// 可移动长度为屏幕宽度减去图片宽度及两倍右外边距
			}
		});
		list1 = new ArrayList<ImageView>();
		list1.add(menuMine);
		list1.add(menuHome);
		list1.add(menuMessage);
		
		map = new HashMap<Integer, Integer>();
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		int sound = soundPool.load(this, R.raw.pao, 0);
		map.put(1, sound);
	}

	/**
	 * 设置监听
	 */
	@SuppressLint("UseSparseArrays")
	private void setPagerListener() {
		homeVP.setCurrentItem(0);// 默认显示第一个界面
		homeVP.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				flag = arg0;
				select(flag);// 滑动完成，选定界面
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		soundPool.play(map.get(1), 1, 1, 0, 0, 1);
		// 图片按钮事件监听
		switch (v.getId()) {
		case R.id.home_menu:// 菜单按钮，控制按钮开合动画
			if (isOpen) {
				MenuAnimation.closeButtons(list1, -moveLength / 3, 400);
				MenuAnimation.closeButton(menuEdit, -moveLength / 3, 400);
				setMenuOff(menuMenu);
				isOpen = false;
			} else {
				MenuAnimation.openButtons(list1, -moveLength / 3, 400);
				MenuAnimation.openButton(menuEdit, -moveLength / 3, 400);
				setMenuOn(menuMenu);
				isOpen = true;
				select(flag);
			}
			break;

		case R.id.home_home:// 切换到大厅界面
			homeVP.setCurrentItem(1);
			select(1);
			break;
		case R.id.home_message:// 切换到我的发布界面
			homeVP.setCurrentItem(0);
			select(0);
			break;
		case R.id.home_mine:// 切换到个人中心界面
			homeVP.setCurrentItem(2);
			select(2);
			break;

		case R.id.home_edit:// 打开我要发布
			ShowPopWindow(menuEdit);
			setMenuOn(menuEdit);
			break;
		}
	}

	/**
	 * 设置图片按钮背景为选中样式
	 * 
	 * @param view
	 */
	private void setMenuOn(ImageView view) {
		view.setBackgroundResource(R.drawable.menu_on_shape);
	}

	/**
	 * 设置图片按钮背景为未选中样式
	 * 
	 * @param view
	 */
	private void setMenuOff(ImageView view) {
		view.setBackgroundResource(R.drawable.menu_off_shape);
	}

	/**
	 * 选中界面改变样式
	 * 
	 * @param flag
	 */
	private void select(int flag) {
		clearSelect();
		switch (flag) {
		case 0:
			setMenuOn(menuMessage);
			break;

		case 1:
			setMenuOn(menuHome);
			break;

		case 2:
			setMenuOn(menuMine);
			break;
		}
	}

	/**
	 * 清除选中样式
	 */
	private void clearSelect() {
		setMenuOff(menuHome);
		setMenuOff(menuMessage);
		setMenuOff(menuMine);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 2秒内点击两次返回键退出程序
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {// 通过静态常量找到返回键
			if (System.currentTimeMillis() - exitTime > 2000) {
				ToastUtils.textToast(HomeActivity.this, "再按一次退出");
				exitTime = System.currentTimeMillis();
			} else {
				HomeActivity.this.finish();
			}
		}else if(keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN){
			DialogUtils.setIPDialog(HomeActivity.this, new UpdataBack() {
				
				@Override
				public void back(String back) {
					// TODO Auto-generated method stub
					isIntnet();
				}
			});
		}
		return true;
	}

	/**
	 * 显示PopWindow
	 * 
	 * @param v
	 */
	private void ShowPopWindow(View v) {
		getPopWindow();
		pop.showAsDropDown(v, -btnWidth/2, -btnWidth * 5);
	}

	/**
	 * 获取PopWindow
	 */
	private void getPopWindow() {
		if (pop != null) {
			pop.dismiss();
			setMenuOff(menuEdit);
			return;
		} else {
			initPopWindow();
		}
	}

	/**
	 * 初始化PopWindow
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void initPopWindow() {
		View contentView = View.inflate(this, R.layout.popwindow, null);// 获取PopWindow的界面布局
		Button lostBtn = (Button) contentView.findViewById(R.id.pop_lost);
		Button foundBtn = (Button) contentView.findViewById(R.id.pop_found);
		Button peopleBtn = (Button) contentView.findViewById(R.id.pop_people);
		lostBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转至发布丢失界面
				if (IntnetService.INTNET) {
					Intent intent = new Intent(HomeActivity.this,
							PublishActivity.class);
					intent.putExtra("type", 0);
					startActivity(intent);
				}else {
					ToastUtils.textToast(HomeActivity.this, "网络连接失败，请检查网络");
				}
				pop.dismiss();
				setMenuOff(menuEdit);
				soundPool.play(map.get(1), 1, 1, 0, 0, 1);
			}
		});
		foundBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转至发布招领界面
				if (IntnetService.INTNET) {
					Intent intent = new Intent(HomeActivity.this,
							PublishActivity.class);
					intent.putExtra("type", 1);
					startActivity(intent);
				}else {
					ToastUtils.textToast(HomeActivity.this, "网络连接失败，请检查网络");
				}
				pop.dismiss();
				setMenuOff(menuEdit);
				soundPool.play(map.get(1), 1, 1, 0, 0, 1);
			}
		});
		peopleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转至发布招领界面
				if (IntnetService.INTNET) {
					Intent intent = new Intent(HomeActivity.this,
							PublishActivity.class);
					intent.putExtra("type", 2);
					startActivity(intent);
				}else {
					ToastUtils.textToast(HomeActivity.this, "网络连接失败，请检查网络");
				}
				pop.dismiss();
				setMenuOff(menuEdit);
				soundPool.play(map.get(1), 1, 1, 0, 0, 1);
			}
		});
		pop = new PopupWindow(contentView,// 弹出界面的引用
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,// 界面的宽高属性
				true);// 是否可点击
		contentView.setOnTouchListener(new OnTouchListener() {
			// 设定点击收回效果
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (pop != null && pop.isShowing()) {// 如果pop已存在并且在显示
					pop.dismiss();
					setMenuOff(menuEdit);
					soundPool.play(map.get(1), 1, 1, 0, 0, 1);
				}
				return false;// 事件处理后返回false
			}
		});

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (conn!=null) {
			unbindService(conn);
		}
	}
	
	private void isIntnet() {
		// 判断本机网络是否可用
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			// 当前无可用网络
			IntnetService.INTNET = false;
			ToastUtils.textToast(this, "当前网络不可用");
		} else {
			// 当前有可用网络
			OpenVolley.strin(MyValues.TestURL, MyValues.TestTAG, null, new ResponseListener() {
				
				@Override
				public void onSuccess(String msg) {
					// TODO Auto-generated method stub
					onError(msg);
				}
				
				@Override
				public void onError(String msg) {
					// TODO Auto-generated method stub
					Message m = Message.obtain();
					m.obj = msg;
					m.what = MyValues.TestWHAT;
					handler.sendMessage(m);
				}
			});
		}
	}

}

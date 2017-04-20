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
 * ��Ҫ���û������Ľ��� �ý��潫������APP����˳��Ľ���
 * 
 * @author lee
 *
 */
@SuppressLint("UseSparseArrays")
public class HomeActivity extends FragmentActivity implements OnClickListener {
	@ViewInject(R.id.home_pager)
	private ViewPager homeVP;// ���ڴ�Ž��沢ʵ�ֻ���
	@ViewInject(R.id.home_home)
	private ImageView menuHome;// �л���������ͼƬ��ť
	@ViewInject(R.id.home_message)
	private ImageView menuMessage;// �л����ҵ������ͼƬ��ť
	@ViewInject(R.id.home_mine)
	private ImageView menuMine;// �л����ҵ���Ϣ��ͼƬ��ť
	@ViewInject(R.id.home_edit)
	private ImageView menuEdit;// ����ʧ�������ͼƬ��ť
	@ViewInject(R.id.home_menu)
	private ImageView menuMenu;// ͼƬ��ť��ʾ���ص�ͼƬ��ť

	private int sysWidth;// ��Ļ���
	private int btnWidth;// ͼƬ��ť���
	private int btnMarginRight;// ͼƬ��ť�ұ߾�
	private int moveLength;// ͼƬ��ť���ƶ�����
	private List<ImageView> list1;// ���ڴ洢���򶯻���ͼƬ
	private int flag;// ��ǵ�ǰ����
	private boolean isOpen;// ���ڱ�ǰ�ť�Ƿ񱻴�
	private long exitTime;// ���ڱ�ǵ���������ؼ����ʱ��

	private static List<Fragment> fragments;// ���漯��

	private PopupWindow pop;// ����������ʧ������
	
	private SoundPool soundPool;//��Ч����
	private Map<Integer, Integer> map;//�洢��Ч

	private ServiceConnection conn;//��������
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what==MyValues.TestWHAT) {//�޸�IP��ַ������������Ӳ���
				String result = (String) msg.obj;
				if (result.equals("Testing")) {
					IntnetService.INTNET = true;
					ToastUtils.textToast(HomeActivity.this, "�������ӳɹ�");
				}else {
					IntnetService.INTNET = false;
					ToastUtils.textToast(HomeActivity.this, result+"\n��ȷ����ǰ�ͻ����������������ͬ������");
				}
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ViewUtils.inject(this);// xUtils��ȡ�ؼ�
		initValues();//���ÿؼ���ʼֵ
		initView();
		setPagerListener();
		startShareService();
	}

	private void startShareService() {
		// ����������ط���
		conn = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// ��Activity��Service�쳣�����ʱ�ص�
				Log.i("LAF", "�����쳣���");
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// ��Activity��Service���а󶨳ɹ�ʱ�ص�
				Log.i("LAF", "����󶨳ɹ�");
			}
		};
		Intent service = new Intent(this,ShareService.class);
		bindService(service, conn, Service.BIND_AUTO_CREATE);
	}

	private void initView() {
		// ��ʼ���ؼ�����
		menuMenu.setOnClickListener(this);
		menuEdit.setOnClickListener(this);
		menuHome.setOnClickListener(this);
		menuMessage.setOnClickListener(this);
		menuMine.setOnClickListener(this);

		fragments = new ArrayList<Fragment>();
		fragments.add(new MessageFragment(this));
		fragments.add(new HomeFragment(this));
		fragments.add(new MineFragment(this));
		// ����������
		homeVP.setAdapter(new LaFPagerAdapter(getSupportFragmentManager(),
				fragments));
	}

	private void initValues() {
		// ��ʼ������
		sysWidth = SystemUtils.getSysWidth(this);
		SystemUtils.getViewWH(menuMenu, new IntFeedBack() {

			@Override
			public void feedBack(int... args) {
				// �Ӵ��ص�����������ȡ���ؼ���Ⱥ����ұ߾�
				btnWidth = args[0];
				btnMarginRight = args[3];
				moveLength = sysWidth - btnWidth - btnMarginRight * 2;// ���ƶ�����Ϊ��Ļ��ȼ�ȥͼƬ��ȼ���������߾�
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
	 * ���ü���
	 */
	@SuppressLint("UseSparseArrays")
	private void setPagerListener() {
		homeVP.setCurrentItem(0);// Ĭ����ʾ��һ������
		homeVP.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				flag = arg0;
				select(flag);// ������ɣ�ѡ������
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		soundPool.play(map.get(1), 1, 1, 0, 0, 1);
		// ͼƬ��ť�¼�����
		switch (v.getId()) {
		case R.id.home_menu:// �˵���ť�����ư�ť���϶���
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

		case R.id.home_home:// �л�����������
			homeVP.setCurrentItem(1);
			select(1);
			break;
		case R.id.home_message:// �л����ҵķ�������
			homeVP.setCurrentItem(0);
			select(0);
			break;
		case R.id.home_mine:// �л����������Ľ���
			homeVP.setCurrentItem(2);
			select(2);
			break;

		case R.id.home_edit:// ����Ҫ����
			ShowPopWindow(menuEdit);
			setMenuOn(menuEdit);
			break;
		}
	}

	/**
	 * ����ͼƬ��ť����Ϊѡ����ʽ
	 * 
	 * @param view
	 */
	private void setMenuOn(ImageView view) {
		view.setBackgroundResource(R.drawable.menu_on_shape);
	}

	/**
	 * ����ͼƬ��ť����Ϊδѡ����ʽ
	 * 
	 * @param view
	 */
	private void setMenuOff(ImageView view) {
		view.setBackgroundResource(R.drawable.menu_off_shape);
	}

	/**
	 * ѡ�н���ı���ʽ
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
	 * ���ѡ����ʽ
	 */
	private void clearSelect() {
		setMenuOff(menuHome);
		setMenuOff(menuMessage);
		setMenuOff(menuMine);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 2���ڵ�����η��ؼ��˳�����
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {// ͨ����̬�����ҵ����ؼ�
			if (System.currentTimeMillis() - exitTime > 2000) {
				ToastUtils.textToast(HomeActivity.this, "�ٰ�һ���˳�");
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
	 * ��ʾPopWindow
	 * 
	 * @param v
	 */
	private void ShowPopWindow(View v) {
		getPopWindow();
		pop.showAsDropDown(v, -btnWidth/2, -btnWidth * 5);
	}

	/**
	 * ��ȡPopWindow
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
	 * ��ʼ��PopWindow
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void initPopWindow() {
		View contentView = View.inflate(this, R.layout.popwindow, null);// ��ȡPopWindow�Ľ��沼��
		Button lostBtn = (Button) contentView.findViewById(R.id.pop_lost);
		Button foundBtn = (Button) contentView.findViewById(R.id.pop_found);
		Button peopleBtn = (Button) contentView.findViewById(R.id.pop_people);
		lostBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��ת��������ʧ����
				if (IntnetService.INTNET) {
					Intent intent = new Intent(HomeActivity.this,
							PublishActivity.class);
					intent.putExtra("type", 0);
					startActivity(intent);
				}else {
					ToastUtils.textToast(HomeActivity.this, "��������ʧ�ܣ���������");
				}
				pop.dismiss();
				setMenuOff(menuEdit);
				soundPool.play(map.get(1), 1, 1, 0, 0, 1);
			}
		});
		foundBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��ת�������������
				if (IntnetService.INTNET) {
					Intent intent = new Intent(HomeActivity.this,
							PublishActivity.class);
					intent.putExtra("type", 1);
					startActivity(intent);
				}else {
					ToastUtils.textToast(HomeActivity.this, "��������ʧ�ܣ���������");
				}
				pop.dismiss();
				setMenuOff(menuEdit);
				soundPool.play(map.get(1), 1, 1, 0, 0, 1);
			}
		});
		peopleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��ת�������������
				if (IntnetService.INTNET) {
					Intent intent = new Intent(HomeActivity.this,
							PublishActivity.class);
					intent.putExtra("type", 2);
					startActivity(intent);
				}else {
					ToastUtils.textToast(HomeActivity.this, "��������ʧ�ܣ���������");
				}
				pop.dismiss();
				setMenuOff(menuEdit);
				soundPool.play(map.get(1), 1, 1, 0, 0, 1);
			}
		});
		pop = new PopupWindow(contentView,// �������������
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,// ����Ŀ������
				true);// �Ƿ�ɵ��
		contentView.setOnTouchListener(new OnTouchListener() {
			// �趨����ջ�Ч��
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (pop != null && pop.isShowing()) {// ���pop�Ѵ��ڲ�������ʾ
					pop.dismiss();
					setMenuOff(menuEdit);
					soundPool.play(map.get(1), 1, 1, 0, 0, 1);
				}
				return false;// �¼�����󷵻�false
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
		// �жϱ��������Ƿ����
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			// ��ǰ�޿�������
			IntnetService.INTNET = false;
			ToastUtils.textToast(this, "��ǰ���粻����");
		} else {
			// ��ǰ�п�������
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

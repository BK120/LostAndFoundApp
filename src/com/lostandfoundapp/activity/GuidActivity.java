package com.lostandfoundapp.activity;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.data.MyValues;
import com.lostandfoundapp.utils.SharedPrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ����ָ������ �ý�����Ҫ�Ǽ���ͼƬ�Ի�����ʽ����
 * 
 * @author lee
 *
 */
public class GuidActivity extends Activity {
	@ViewInject(R.id.guide_viewpager)
	private ViewPager guideVp;// ViewPager�ؼ���ͼƬ�����Ļ���
	@ViewInject(R.id.guide_grey)
	private LinearLayout greyPoints;// ��Ż�ɫԲ��
	@ViewInject(R.id.guide_blue)
	private ImageView bluePoint;// ��ɫԲ��
	@ViewInject(R.id.guide_btn)
	private Button close;// ����򵼰�ť

	private int position;// ��ǰ����λ����
	private int pointWidth;// С�ҵ�ļ��
	private int[] images = { R.drawable.gui1, R.drawable.gui2, R.drawable.gui3,R.drawable.gui4 };// ���ֵ���һ�㶼��ͼƬ������
	private List<ImageView> list;// ���ͼƬ�ؼ���List����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guid);
		ViewUtils.inject(this);// xUtils��ȡ�ؼ�
		getPics();
		getAdapter();
	}

	/**
	 * ��ȡͼƬ��Բ��
	 */
	private void getPics() {
		list = new ArrayList<ImageView>();
		// ��ͼƬ������ת��ΪͼƬ�ؼ�����List�ļ�����
		for (int i = 0; i < images.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(images[i]);// ����Ӧ��ͼƬ���õ�IamageView
			imageView.setScaleType(ScaleType.FIT_XY);// ����ͼƬ�����췽ʽΪ����
			list.add(imageView);
			// ����С�ҵ�����м�������ͻ��Ƽ���
			ImageView points = new ImageView(this);
			points.setImageResource(R.drawable.point_grey);// ͨ��shape�ļ����ƺûҵ�
			// ����һ�������С�ҵ��������߾࣬��֤�����ҵ�ˮƽ����
			LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);// �õ��ҵ����������Բ���һ������״��һЩ�������ԣ�
			if (i > 0)
				lllp.leftMargin = 30;// ��������߾࣬����
			points.setLayoutParams(lllp);// �����ú�����߾����״���ø��ҵ�
			greyPoints.addView(points);// ���ҵ�������Բ���
		}
		// Ϊ����������ڽ��滬��ʱ�Ķ���Ч���������ȡ���ҵ�ı߾࣬ͨ����̬�ĸ��������ñ߾�����ɶ���Ч��
		// ������ִ��onCreate����ʱ�����滹û�л�����ɣ��޷���ȡpointWidth���趨С���������ɵ��¼���������С�����������ٻ�ȡ
		bluePoint.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						// ��ȡС�ҵ�Բ�ļ�ľ��룬��1���ҵ�͵ڶ����ҵ�ľ���
						pointWidth = greyPoints.getChildAt(1).getLeft()
								- greyPoints.getChildAt(0).getLeft();
					}
				});
	}

	/**
	 * ��ȡ���������¼�����
	 */
	private void getAdapter() {

		VPAdapter vpAdapter = new VPAdapter();// ����������
		guideVp.setAdapter(vpAdapter);// ViewPager����������
		// ΪViewPager�趨�������������ǻ���ʱ������Ҳ���Ŷ�
		guideVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			// ��ǰѡ�еڼ�������
			public void onPageSelected(int arg0) {
				position = arg0;
			}

			/**
			 * ���滬��ʱ�ص��˷��� arg0:��ǰ������ arg1:���滬�����İٷ�����0.0-1.0�� arg2:��ǰ����ƫ�Ƶ�����λ��
			 */
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				int width;// С���㵱ǰ��������
				width = (int) (arg1 * pointWidth + arg0 * pointWidth);// 1�������Ҫһ��С�ҵ�ľ��룬�ټ��ϻ������İٷֱȾ�����ǵ�ǰ�����λ��
				RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams) bluePoint
						.getLayoutParams();// �õ��������ڲ��ֵ���״
				rllp.leftMargin = width;// �������������߾�
				bluePoint.setLayoutParams(rllp);// �����úõ���״���ø�����
				// ��ʼ���鰴ťֻ�ܳ��������һҳ�������ڻ����Ĺ����б�����ʧ
				if (position == images.length - 1 && arg1 == 0)
					close.setVisibility(View.VISIBLE);
				else
					close.setVisibility(View.INVISIBLE);
			}

			// ״̬�ı�ʱ���ã�arg0=0��û����,arg0=1���ڻ���,arg0=2�������
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ���������ת�������棬ͬʱ���ļ��м�¼�Ѷ����´����������ٽ�������ָ������
				SharedPrefUtils.putBoolean(GuidActivity.this,
						MyValues.ISGUIDE, true);
				startActivity(new Intent(GuidActivity.this, HomeActivity.class));
				GuidActivity.this.finish();// ��ת�����ٽ��棬������˻ص��ý���
			}
		});

	}

	class VPAdapter extends PagerAdapter {
		/**
		 * ViewPager��������
		 */
		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView img = list.get(position);
			container.addView(img);
			return img;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

}

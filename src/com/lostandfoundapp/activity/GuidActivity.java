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
 * 新手指引界面 该界面主要是几张图片以滑动形式出现
 * 
 * @author lee
 *
 */
public class GuidActivity extends Activity {
	@ViewInject(R.id.guide_viewpager)
	private ViewPager guideVp;// ViewPager控件，图片滑动的基础
	@ViewInject(R.id.guide_grey)
	private LinearLayout greyPoints;// 存放灰色圆点
	@ViewInject(R.id.guide_blue)
	private ImageView bluePoint;// 蓝色圆点
	@ViewInject(R.id.guide_btn)
	private Button close;// 完成向导按钮

	private int position;// 当前界面位置数
	private int pointWidth;// 小灰点的间距
	private int[] images = { R.drawable.gui1, R.drawable.gui2, R.drawable.gui3,R.drawable.gui4 };// 新手导航一般都是图片做界面
	private List<ImageView> list;// 存放图片控件的List集合

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guid);
		ViewUtils.inject(this);// xUtils获取控件
		getPics();
		getAdapter();
	}

	/**
	 * 获取图片及圆点
	 */
	private void getPics() {
		list = new ArrayList<ImageView>();
		// 将图片的引用转化为图片控件存在List的集合中
		for (int i = 0; i < images.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(images[i]);// 将相应的图片设置到IamageView
			imageView.setScaleType(ScaleType.FIT_XY);// 设置图片的拉伸方式为充满
			list.add(imageView);
			// 绘制小灰点儿，有几个界面就绘制几个
			ImageView points = new ImageView(this);
			points.setImageResource(R.drawable.point_grey);// 通过shape文件绘制好灰点
			// 给第一个以外的小灰点儿设置左边距，保证三个灰点水平居中
			LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);// 拿到灰点所处的线性布局一样的形状（一些距离属性）
			if (i > 0)
				lllp.leftMargin = 30;// 设置左外边距，像素
			points.setLayoutParams(lllp);// 把设置好左外边距的形状设置给灰点
			greyPoints.addView(points);// 将灰点加入线性布局
		}
		// 为了完成蓝点在界面滑动时的动画效果，必须获取到灰点的边距，通过动态的给蓝点设置边距来完成动画效果
		// 由于在执行onCreate方法时，界面还没有绘制完成，无法获取pointWidth，设定小蓝点绘制完成的事件监听，当小蓝点绘制完成再获取
		bluePoint.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						// 获取小灰点圆心间的距离，第1个灰点和第二个灰点的距离
						pointWidth = greyPoints.getChildAt(1).getLeft()
								- greyPoints.getChildAt(0).getLeft();
					}
				});
	}

	/**
	 * 获取适配器及事件监听
	 */
	private void getAdapter() {

		VPAdapter vpAdapter = new VPAdapter();// 创建适配器
		guideVp.setAdapter(vpAdapter);// ViewPager加载适配器
		// 为ViewPager设定监听器，界面是滑动时让蓝点也跟着动
		guideVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			// 当前选中第几个界面
			public void onPageSelected(int arg0) {
				position = arg0;
			}

			/**
			 * 界面滑动时回调此方法 arg0:当前界面数 arg1:界面滑动过的百分数（0.0-1.0） arg2:当前界面偏移的像素位置
			 */
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				int width;// 小蓝点当前滑动距离
				width = (int) (arg1 * pointWidth + arg0 * pointWidth);// 1个界面就要一个小灰点的距离，再加上滑动过的百分比距离就是当前蓝点的位置
				RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams) bluePoint
						.getLayoutParams();// 拿到蓝点所在布局的形状
				rllp.leftMargin = width;// 设置蓝点的左外边距
				bluePoint.setLayoutParams(rllp);// 将设置好的形状设置给蓝点
				// 开始体验按钮只能出现在最后一页，并且在滑动的过程中保持消失
				if (position == images.length - 1 && arg1 == 0)
					close.setVisibility(View.VISIBLE);
				else
					close.setVisibility(View.INVISIBLE);
			}

			// 状态改变时调用：arg0=0还没滑动,arg0=1正在滑动,arg0=2滑动完毕
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击即会跳转至主界面，同时在文件中记录已读，下次启动将不再进入新手指引界面
				SharedPrefUtils.putBoolean(GuidActivity.this,
						MyValues.ISGUIDE, true);
				startActivity(new Intent(GuidActivity.this, HomeActivity.class));
				GuidActivity.this.finish();// 跳转后销毁界面，避免回退回到该界面
			}
		});

	}

	class VPAdapter extends PagerAdapter {
		/**
		 * ViewPager的适配器
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

package com.lostandfoundapp.animation;

import java.util.List;

import android.animation.ObjectAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

/**
 * 图片按钮动画效果工具类 该类包括图片按钮水平运动及垂直运动的静态方法
 * 
 * @author lee
 *
 */
public class MenuAnimation {
	/**
	 * 图片按钮水平打开动画
	 * 
	 * @param list
	 * @param l
	 * @param duration
	 */
	public static void openButtons(List<ImageView> list, float l, long duration) {
		for (int i = 1; i <= list.size(); i++) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(list.get(i - 1),
					"translationX", 0f, i * l);
			animator.setStartDelay(i * 150);
			animator.setInterpolator(new BounceInterpolator());// 重力回弹效果
			animator.setDuration(duration);
			animator.start();
		}
	}

	/**
	 * 图片按钮水平关闭动画
	 * 
	 * @param list
	 * @param l
	 * @param duration
	 */
	public static void closeButtons(List<ImageView> list, float l, long duration) {
		for (int i = 1; i <= list.size(); i++) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(list.get(i - 1),
					"translationX", i * l, 0f);
			animator.setStartDelay(i * 150);
			animator.setInterpolator(new BounceInterpolator());// 重力回弹效果
			animator.setDuration(duration);
			animator.start();
		}
	}
	/**
	 * 图片按钮垂直打开动画
	 * @param iv
	 * @param l
	 * @param duration
	 */
	public static void openButton(ImageView iv, float l, long duration) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "translationY",
				0f, l);
		animator.setInterpolator(new BounceInterpolator());// 重力回弹效果
		animator.setDuration(duration);
		animator.start();
	}
	/**
	 * 图片按钮垂直关闭动画
	 * @param iv
	 * @param l
	 * @param duration
	 */
	public static void closeButton(ImageView iv, float l, long duration) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "translationY", l,
				0f);
		animator.setInterpolator(new BounceInterpolator());// 重力回弹效果
		animator.setDuration(duration);
		animator.start();
	}
}

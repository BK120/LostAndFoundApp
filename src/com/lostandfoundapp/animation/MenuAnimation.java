package com.lostandfoundapp.animation;

import java.util.List;

import android.animation.ObjectAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

/**
 * ͼƬ��ť����Ч�������� �������ͼƬ��ťˮƽ�˶�����ֱ�˶��ľ�̬����
 * 
 * @author lee
 *
 */
public class MenuAnimation {
	/**
	 * ͼƬ��ťˮƽ�򿪶���
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
			animator.setInterpolator(new BounceInterpolator());// �����ص�Ч��
			animator.setDuration(duration);
			animator.start();
		}
	}

	/**
	 * ͼƬ��ťˮƽ�رն���
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
			animator.setInterpolator(new BounceInterpolator());// �����ص�Ч��
			animator.setDuration(duration);
			animator.start();
		}
	}
	/**
	 * ͼƬ��ť��ֱ�򿪶���
	 * @param iv
	 * @param l
	 * @param duration
	 */
	public static void openButton(ImageView iv, float l, long duration) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "translationY",
				0f, l);
		animator.setInterpolator(new BounceInterpolator());// �����ص�Ч��
		animator.setDuration(duration);
		animator.start();
	}
	/**
	 * ͼƬ��ť��ֱ�رն���
	 * @param iv
	 * @param l
	 * @param duration
	 */
	public static void closeButton(ImageView iv, float l, long duration) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "translationY", l,
				0f);
		animator.setInterpolator(new BounceInterpolator());// �����ص�Ч��
		animator.setDuration(duration);
		animator.start();
	}
}

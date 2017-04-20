package com.lostandfoundapp.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lostandfoundapp.utils.SystemUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * œ‘ æ‘§¿¿¥ÛÕº
 * 
 * @author lee
 *
 */
public class CheckPicActivity extends Activity {
	@ViewInject(R.id.check_pic)
	private ImageView pic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_pic);
		ViewUtils.inject(this);
		String fileName = getIntent().getStringExtra("pic");
		Bitmap bitmap = SystemUtils.getUserPic(fileName);
		int width = SystemUtils.getSysWidth(this);
		LayoutParams params = pic.getLayoutParams();
		params.width = width;
		params.height = width;
		pic.setLayoutParams(params);
		pic.setImageBitmap(bitmap);
		pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}

package com.lostandfoundapp.view;

import com.lostandfoundapp.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * �Զ�����Ͽؼ�
 * ����һ��ͼ�������
 * @author lee
 *
 */
public class MyView extends RelativeLayout {

	private ImageView image;
	private TextView text;

	public MyView(Context context) {
		this(context, null);
	}

	@SuppressLint("Recycle")
	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.myview);
		image.setImageDrawable(typedArray.getDrawable(R.styleable.myview_image));
		text.setText(typedArray.getText(R.styleable.myview_text));
	}

	private void initView(Context context) {
		// ��ʼ���ӿؼ�
		LayoutInflater.from(context).inflate(R.layout.myview, this,true);
		image = (ImageView) findViewById(R.id.myview_image);
		text = (TextView) findViewById(R.id.myview_text);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		// TODO Auto-generated method stub
		super.setOnClickListener(l);
	}
	
	public void setText(String textString){
		text.setText(textString);
	}
	
	public String getText(){
		return text.getText().toString();
	}
	
	public void setImage(int resid){
		image.setBackgroundResource(resid);
	}
	
	public void setImageBitmap(Bitmap bitmap){
		image.setImageBitmap(bitmap);
	}
	
	public ImageView getImageView(){
		return image;
	}
}

package com.lostandfoundapp.view;

import com.lostandfoundapp.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 自定义组合控件 包括一个文本框和一个用于清除文本框内容的图片
 * 
 * @author lee
 *
 */
public class MyEditText extends LinearLayout {
	private EditText text;
	private ImageView clear;
	private LinearLayout back;

	public MyEditText(Context context) {
		this(context, null);
	}

	@SuppressLint("Recycle")
	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView(context);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.myedittext);
		text.setHint(typedArray.getString(R.styleable.myedittext_text));// 设置文本框默认显示文字
		text.setEms(typedArray.getInteger(R.styleable.myedittext_ems, 1000));// 设置文本框最多显示字数
		text.setSingleLine(typedArray.getBoolean(
				R.styleable.myedittext_singleLine, false));// 设置文本框是否为单行显示
		text.setInputType(typedArray.getInt(R.styleable.myedittext_inputType, 0));
		clear.setVisibility(View.INVISIBLE);// 设置清除图片为不可见
		setListener();
	}

	private void setListener() {
		// 设置子控件监听
		text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 文本内容改变时

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// 文本内容改变前

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 文本内容改变后
				if (s.length() == 0) {
					clear.setVisibility(View.INVISIBLE);
				} else {
					clear.setVisibility(View.VISIBLE);
				}
			}
		});
		text.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// 文本框获取焦点后改变整个布局的背景
				if (hasFocus) {
					back.setBackgroundResource(R.drawable.edittext_on_shape);
				}else {
					back.setBackgroundResource(R.drawable.edittext_off_shape);
				}

			}
		});
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击后隐藏该图片并清空文本框内容
				text.setText("");
				clear.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void initView(Context context) {
		// 初始化子控件
		LayoutInflater.from(context).inflate(R.layout.myedittext, this, true);
		text = (EditText) findViewById(R.id.myedittext_text);
		clear = (ImageView) findViewById(R.id.myedittext_clear);
		back = (LinearLayout) findViewById(R.id.myedittext_back);
	}
	
	public void setText(String str){
		text.setText(str);
	}
	
	public void setHint(String hint){
		text.setHint(hint);
	}
	
	public String getText(){
		return text.getText().toString();
	}
	
	public void setVisible(int visibility){
		back.setVisibility(visibility);
	}

}

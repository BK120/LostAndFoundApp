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
 * �Զ�����Ͽؼ� ����һ���ı����һ����������ı������ݵ�ͼƬ
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
		text.setHint(typedArray.getString(R.styleable.myedittext_text));// �����ı���Ĭ����ʾ����
		text.setEms(typedArray.getInteger(R.styleable.myedittext_ems, 1000));// �����ı��������ʾ����
		text.setSingleLine(typedArray.getBoolean(
				R.styleable.myedittext_singleLine, false));// �����ı����Ƿ�Ϊ������ʾ
		text.setInputType(typedArray.getInt(R.styleable.myedittext_inputType, 0));
		clear.setVisibility(View.INVISIBLE);// �������ͼƬΪ���ɼ�
		setListener();
	}

	private void setListener() {
		// �����ӿؼ�����
		text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// �ı����ݸı�ʱ

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// �ı����ݸı�ǰ

			}

			@Override
			public void afterTextChanged(Editable s) {
				// �ı����ݸı��
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
				// �ı����ȡ�����ı��������ֵı���
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
				// ��������ظ�ͼƬ������ı�������
				text.setText("");
				clear.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void initView(Context context) {
		// ��ʼ���ӿؼ�
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

package com.lostandfoundapp.share;

import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
/**
 * ��Ϣ����QQ����ص��ӿ�
 * @author lee
 *
 */
public class ShareToQQListener implements IUiListener{

	@Override
	public void onCancel() {
		// ȡ������
		Log.i("LAF", "ȡ������");
	}

	@Override
	public void onComplete(Object arg0) {
		// ����ɹ�
		Log.i("LAF", "����ɹ�");
	}

	@Override
	public void onError(UiError arg0) {
		// �������
		Log.i("LAF", "�������");
	}

}

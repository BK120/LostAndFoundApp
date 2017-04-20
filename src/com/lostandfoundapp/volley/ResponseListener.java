package com.lostandfoundapp.volley;

public interface ResponseListener {
	void onSuccess(String msg);

	void onError(String msg);
}

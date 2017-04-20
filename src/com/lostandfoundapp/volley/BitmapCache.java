package com.lostandfoundapp.volley;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapCache implements ImageCache {

	private LruCache<String, Bitmap> cache;
	private int max = 10 * 1024 * 1024;

	public BitmapCache() {
		cache = new LruCache<String, Bitmap>(max) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};

	}

	@Override
	public Bitmap getBitmap(String url) {
		// TODO Auto-generated method stub
		return cache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// TODO Auto-generated method stub
		cache.put(url, bitmap);
	}

}

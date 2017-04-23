package com.tv.mytv.http;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tv.mytv.R;

public class HttpImageAsync {
	// private static String TAG = "ImageLoaderHelper";
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	public static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			super.onLoadingFailed(imageUri, view, failReason);

		}
	}

	public static void loadingImage(ImageView view, String url) {
		int defaultResourceId = R.drawable.shape_nopic;
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(defaultResourceId)
				.showImageForEmptyUri(defaultResourceId).showImageOnFail(defaultResourceId).cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader.displayImage(url, view, options, animateFirstListener);
	}

	public static void loadingImage( ImageView view, String url,
			SimpleImageLoadingListener aynsListenner) {
		int defaultResourceId = R.drawable.shape_nopic;
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(defaultResourceId)
				.showImageForEmptyUri(defaultResourceId).showImageOnFail(defaultResourceId).cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader.displayImage(url, view, options, aynsListenner);
	}

	public static void loadingRoundImage(ImageView view, String url) {
		int defaultResourceId = R.drawable.shape_nopic;
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(defaultResourceId)
				.showImageForEmptyUri(defaultResourceId).showImageOnFail(defaultResourceId).cacheInMemory(true)
				.displayer(new RoundedBitmapDisplayer(view.getLayoutParams().height / 2)).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader.displayImage(url, view, options, animateFirstListener);
	}

	public static void loadingRoundImage(ImageView view, String url,
			SimpleImageLoadingListener aynsListenner) {
		int defaultResourceId = R.drawable.shape_nopic;
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(defaultResourceId)
				.showImageForEmptyUri(defaultResourceId).showImageOnFail(defaultResourceId).cacheInMemory(true)
				.displayer(new RoundedBitmapDisplayer(view.getLayoutParams().height / 2)).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader.displayImage(url, view, options, aynsListenner);
	}

	public static void loadingImage(int defaultResourceId, String url, SimpleImageLoadingListener listener) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		// DisplayImageOptions options = new
		// DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader.loadImage(url, options, listener);
	}

	public static void clearDataCache() {
		imageLoader.clearDiskCache();
	}

	public static void releaseImage() {
		synchronized (imageLoader) {
			// MemoryCache cache =
			// ImageLoaderHelper.imageLoader.getMemoryCache();
			// Iterator<String> keys = cache.keys().iterator();
			// Bitmap bitmap = null;
			// while(keys.hasNext()){
			// bitmap = cache.get(keys.next());
			// if(bitmap != null && !bitmap.isRecycled()){
			// bitmap.recycle();
			// bitmap = null;
			// }
			// }
			imageLoader.clearMemoryCache();
		}
	}
}

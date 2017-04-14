package com.tv.mytv.http;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import com.tv.mytv.activity.MyApplication;
import com.tv.mytv.R;
import com.tv.mytv.util.LogUtil;
import com.tv.mytv.util.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;

/**
 * 图片异步请求公共类 2016.6.29 Xutils图片加载库
 */
public class HttpImageAsync {
	/**
	 * 图片加载配置
	 */
	public static ImageOptions options = new ImageOptions.Builder()
			// 设置加载过程中的图片
			.setLoadingDrawableId(R.mipmap.nopic)
			// 设置加载失败后的图片
			.setFailureDrawableId(R.mipmap.nopic)
			// 设置使用缓存
			 .setUseMemCache(true)
			// 设置显示圆形图片
			// .setCircular(true)
			// 设置支持gif
			.setIgnoreGif(false).build();

	/**
	 * 获取网络图片或者本地图片
	 * @param imageview
	 * @param
	 */
	public static void loadingImage(ImageView imageview, String urlOrpath) {
		LogUtil.i(urlOrpath);
		x.image().bind(imageview, urlOrpath, options);
	}
	
	/**
	 * 加载本地缓存图片
	 * @param url
	 * //url地址
	 */
	public static void loadingImagefile(String url) {
		x.image().loadFile(url, options, new Callback.CacheCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				ToastUtil.showShort(MyApplication.getContext(), "图片保存失败");
			}

			@Override
			public void onFinished() {

			}
			
			@Override
			public void onSuccess(File file) {

			}

			@Override
			public boolean onCache(File file) {
				LogUtil.i(file.getAbsolutePath() + file.getName());
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				Uri uri = Uri.fromFile(file);
				intent.setData(uri);
				MyApplication.getContext().sendBroadcast(intent);
				return false;
			}
		});
	}
}

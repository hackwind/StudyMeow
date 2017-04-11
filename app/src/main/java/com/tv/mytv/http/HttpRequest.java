package com.tv.mytv.http;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.tv.mytv.MyApplication;
import com.tv.mytv.util.LogUtil;
import com.tv.mytv.util.ToastUtil;
import com.tv.mytv.util.Util;

import org.xutils.common.Callback;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Http请求公共类 xUtils库
 * 
 * @author caogenqing 2016.6.29
 */
public class HttpRequest {

	/**
	 * 发送get请求
	 */
	public static void get(String url, Map<String, Object> map, final Object obj, final String mothod, final View view, final  Context context) {
		if (view != null) {
			view.setVisibility(View.VISIBLE);
		}
		LogUtil.i(url);
		String urls = null;// 调试
		if (map != null) {
			Set<String> set = map.keySet();
			Iterator<String> iter = set.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				urls = url + "&" + key + "=" + map.get(key);
			}
			LogUtil.i(urls);
		}
		XutilsHttp.Get(url, map, new CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException arg0) {
				if (view != null) {
					view.setVisibility(View.GONE);
				}
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				if (view != null) {
					view.setVisibility(View.GONE);
				}
				ToastUtil.showShort(MyApplication.getContext(), "网络连接异常,请检查网络设置");
				LogUtil.e(arg0.getMessage());
				Intent intent =new Intent(Util.ACTION_HTTP_ONERROR);
				context.sendBroadcast(intent);
			}

			@Override
			public void onFinished() {
				if (view != null) {
					view.setVisibility(View.GONE);
				}
			}

			@Override
			public void onSuccess(String arg0) {
				if (view != null) {
					view.setVisibility(View.GONE);
				}
				Class objClass = obj.getClass();
				Method method = null;
				try {
					method = objClass.getDeclaredMethod(mothod, String.class);
					method.invoke(obj, arg0.toString());
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param map
	 * @param obj
	 * @param mothod
	 */
	public static void post(String url, Map<String, Object> map, final Object obj, final String mothod, final View view) {
		if (view != null) {
			view.setVisibility(View.VISIBLE);
		}
		LogUtil.i(url);
		String urls = null;// 调试
		if (map != null) {
			Set<String> set = map.keySet();
			Iterator<String> iter = set.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				urls = url + "&" + key + "=" + map.get(key);
			}
			LogUtil.i(urls);
		}

		XutilsHttp.Post(url, map, new CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException arg0) {
				if (view != null) {
					view.setVisibility(View.GONE);
				}
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				if (view != null) {
					view.setVisibility(View.GONE);
				}
				ToastUtil.showShort(MyApplication.getContext(), "网络连接异常,请检查网络设置");
				LogUtil.e(arg0.getMessage());
				Intent intent =new Intent(Util.ACTION_HTTP_ONERROR);
				MyApplication.getContext().sendBroadcast(intent);
			}

			@Override
			public void onFinished() {
				if (view != null) {
					view.setVisibility(View.GONE);
				}
			}

			@Override
			public void onSuccess(String arg0) {
				if (view != null) {
					view.setVisibility(View.GONE);
				}
				Class<Object> objClass = (Class<Object>) obj.getClass();
				try {
					Method method = objClass.getMethod(mothod, String.class);
					method.invoke(obj, arg0.toString());
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 下载文件
	 * @param url
	 */
	public static void downloadprogressfile(String url) {
		RequestParams params = new RequestParams(url);
		// 自定义保存路径Environment.getExternalStorageDirectory()：SD卡的根目录
		String timeSring = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		params.setSaveFilePath(Environment.getExternalStorageDirectory() + "/huilongMobile/"+timeSring+".jpg");
		// 自动为文件命名
//		params.setAutoRename(true);
		params.setConnectTimeout(5000);
		// 设置断点续传
		params.setAutoResume(true);
		x.http().post(params, new Callback.ProgressCallback<File>() {
			
			@Override
			public void onSuccess(File result) {
				LogUtil.i(result.getAbsolutePath());
			}
			
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtil.showShort(MyApplication.getContext(), "文件下载异常");
			}

			@Override
			public void onCancelled(CancelledException cex) {
				
			}
			
			@Override
			public void onFinished() {
				
			}

			//网络请求之前回调
			@Override
			public void onWaiting() {
				
			}

			// 网络请求开始的时候回调
			@Override
			public void onStarted() {
				
			}

			// 下载的时候不断回调的方法
			@Override
			public void onLoading(long total, long current, boolean isDownloading) {
				// 当前进度和文件总大小
				Log.i("HttpRequest", "current：" + current + "，total：" + total);
			}
		});

	}
}

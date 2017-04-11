package com.tv.mytv.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;

import com.orhanobut.logger.Logger;
import com.tv.mytv.MyApplication;
import com.umeng.analytics.MobclickAgent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * CrashHandler是一个抓取崩溃log的工具类（可选）
 */
@SuppressLint({ "SdCardPath", "SimpleDateFormat" })
public class CrashHandler implements Thread.UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";
	private String mobile;
	private String mac;
	private String username;
	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("HH-mm-ss");

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler(){

	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
//		mobile = ConfigPreferences.getInstance(context).isLastMoblie();
//		mac = getLocalMacAddressFromWifiInfo(context);
//		username = ConfigPreferences.getInstance(context).isLastUsername();
	}

//	public static String getLocalMacAddressFromWifiInfo(Context context) {
//		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo info = wifi.getConnectionInfo();
//		return info.getMacAddress();
//	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Logger.e(TAG, "error : ", e);
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Logger.e("CrashHandler", "" + ex);
				Looper.loop();
			}
		}.start();
//		// 保存日志文件 日志上报
//		String filename = saveCrashInfo2File(ex);
//		File file = new File("/sdcard/crash/" + "//" + filename);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("file", file);
//		map.put("test", "");
//		HttpRequest.getInstance(mContext).post(HttpAddress.postCrashLog(), map, CrashHandler.this, "postCrashLog",
//				null);
		MobclickAgent.reportError(MyApplication.getContext(),ex);
		return true;
	}

//	public void postCrashLog(String str) {
//		LogUtil.i("postCrashLog", str);
//	}

//	/**
//	 * 保存错误信息到文件中
//	 *
//	 * @param ex
//	 * @return 返回文件名称,便于将文件传送到服务器
//	 */
//	private String saveCrashInfo2File(Throwable ex) {
//		StringBuffer sb = new StringBuffer();
//		File dir = null;
//		for (Map.Entry<String, String> entry : infos.entrySet()) {
//			String key = entry.getKey();
//			String value = entry.getValue();
//			sb.append(key + "=" + value + "\n");
//		}
//		Writer writer = new StringWriter();
//		PrintWriter printWriter = new PrintWriter(writer);
//		ex.printStackTrace(printWriter);
//		Throwable cause = ex.getCause();
//		while (cause != null) {
//			cause.printStackTrace(printWriter);
//			cause = cause.getCause();
//		}
//		printWriter.close();
//		String result = writer.toString();
//		sb.append(result);
//		try {
//			long timestamp = System.currentTimeMillis();
//			String time = formatter.format(new Date());
//			String fileName = "crash-" + mobile + "-V" +ConfigVersion.getVerName(mContext) +"-"+time + ".log";
//			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//				String path = "/sdcard/crash/";
//				dir = new File(path);
//				if (!dir.exists()) {
//					dir.mkdirs();
//				}
//				FileOutputStream fos = new FileOutputStream(path + fileName);
//				fos.write(sb.toString().getBytes());
//				fos.close();
//			}
//			return fileName;
//		} catch (Exception e) {
//			Log.e(TAG, "an error occured while writing file...", e);
//		}
//		return null;
//	}
}

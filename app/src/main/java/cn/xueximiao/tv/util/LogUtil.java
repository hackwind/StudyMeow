package cn.xueximiao.tv.util;

import com.orhanobut.logger.Logger;

/**
 * 日志管理类 2016.6.28
 */
public class LogUtil {
	private static final int VERBOSE = 1;
	private static final int DEBUG = 2;
	private static final int INFO = 3;
	private static final int WARN = 4;
	private static final int ERROR = 5;
	public static final int NOTHING = 6;
	/**
	 * 开发阶段将 LEVEL指定成 VERBOSE，当项目正式上线的时候将 LEVEL 指定成 NOTHING就可以了 日志将不会打印了
	 */
	public static final int LEVEL = VERBOSE;

	public static void v(String msg) {
		if (LEVEL <= VERBOSE) {
			Logger.v(msg);
		}
	}

	public static void d(String msg) {
		if (LEVEL <= DEBUG) {
			Logger.d(msg);
		}
	}

	public static void i(String msg) {
		if (LEVEL <= INFO) {
			Logger.i(msg);
		}
	}

	public static void w(String msg) {
		if (LEVEL <= WARN) {
			Logger.w(msg);
		}
	}

	public static void e(String msg) {
		if (LEVEL <= ERROR) {
			Logger.e(msg);
		}
	}

	public static void json(String json){
		if(LEVEL<=ERROR){
			Logger.json(json);
		}
	}

	public static void xml(String xml){
		if(LEVEL<=ERROR){
			Logger.xml(xml);
		}
	}
}

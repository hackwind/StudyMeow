package cn.xueximiao.tv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.xueximiao.tv.activity.MeowApplication;


/**
 * SharePreferences操作工具类
 */
public class SharePrefUtil {
	private static String tag = SharePrefUtil.class.getSimpleName();
	private final static String SP_NAME = "studytv_config";

	public static String KEY_USER_ID = "userid";
	public static String KEY_USER_NAME = "username";
	public static String KEY_NICK_NAME = "nickname";
	public static String KEY_AUTH = "auth";
	public static String KEY_THUMB = "thumb";
	public static String KEY_GROUP_ID = "group";
	public static String KEY_REG_DATE = "regdate";
	public static String KEY_UPDATE_TIME = "updatetime";

	private final static String SERIALPORT_NAME = "android_serialport_api.sample_preferences";
	private static SharedPreferences sp;
	private static SharedPreferences serialport_sp;


	/**
	 * 保存布尔值
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveBoolean(Context context, String key, boolean value) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		    sp.edit().putBoolean(key, value).commit();
	}

	/**
	 * 保存字符串
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveString(Context context, String key, String value) {
		Log.d("hjs","save string:" + value);
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		sp.edit().putString(key, value).commit();
		
	}

	public static void saveSerialportString(Context context, String key, String value) {
		if (serialport_sp == null)
			serialport_sp = MeowApplication.getContext().getSharedPreferences(SERIALPORT_NAME, 0);
		serialport_sp.edit().putString(key, value).commit();
	}

	public static String getSerialportString(Context context, String key, String defValue) {
		if (serialport_sp == null)
			serialport_sp = MeowApplication.getContext().getSharedPreferences(SERIALPORT_NAME, 0);
		return serialport_sp.getString(key, defValue);
	}

	public static void clear(Context context){
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		sp.edit().clear().commit();
	}

	/**
	 * 保存long型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveLong(Context context, String key, long value) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		sp.edit().putLong(key, value).commit();
	}

	/**
	 * 保存int型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveInt(Context context, String key, int value) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		sp.edit().putInt(key, value).commit();
	}

	/**
	 * 保存float型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveFloat(Context context, String key, float value) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		sp.edit().putFloat(key, value).commit();
	}

	/**
	 * 获取字符值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		return sp.getString(key, defValue);
	}

	/**
	 * 获取int值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context, String key, int defValue) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		return sp.getInt(key, defValue);
	}

	/**
	 * 获取long值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static long getLong(Context context, String key, long defValue) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		return sp.getLong(key, defValue);
	}

	/**
	 * 获取float值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static float getFloat(Context context, String key, float defValue) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		return sp.getFloat(key, defValue);
	}

	/**
	 * 获取布尔值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		return sp.getBoolean(key, defValue);
	}
	
	/**
	 * 将对象进行base64编码后保存到SharePref中
	 * 
	 * @param context
	 * @param key
	 * @param object
	 */
	public static void saveObj(Context context, String key, Object object) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			// 将对象的转为base64码
			
			String objBase64 = new String(Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT));

			sp.edit()
					.putString(key,objBase64).commit();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将SharePref中经过base64编码的对象读取出来
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static Object getObj(Context context, String key) {
		if (sp == null)
			sp = MeowApplication.getContext().getSharedPreferences(SP_NAME, 0);
		String objBase64 = sp.getString(key, null);
		if (TextUtils.isEmpty(objBase64))
			return null;

		// 对Base64格式的字符串进行解码
		
		byte[] base64Bytes = Base64.decode(objBase64.getBytes(),Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);

		ObjectInputStream ois;
		Object obj = null;
		try {
			ois = new ObjectInputStream(bais);
			obj = (Object) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}


}

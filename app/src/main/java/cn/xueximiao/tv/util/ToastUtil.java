package cn.xueximiao.tv.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 2016.7.14
 * @author caogenqing
 * Toast提示全局管理工具类
 */
public class ToastUtil {
	private ToastUtil()  
    {  
        throw new UnsupportedOperationException("cannot be instantiated");  
    }  
  
    public static boolean isShow = true; 
  
    /** 
     * 短时间显示Toast 
     * @param context 
     * @param message 
     */  
    public static void showShort(Context context, CharSequence message)  
    {  
        if (isShow)  
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();  
    }  
  
    /** 
     * 短时间显示Toast
     * @param context 
     * @param message 
     */  
    public static void showShort(Context context, int message)  
    {  
        if (isShow)  
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();  
    }  
  
    /** 
     * 长时间显示Toast 
     *  
     * @param context 
     * @param message 
     */  
    public static void showLong(Context context, CharSequence message)  
    {  
        if (isShow)  
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();  
    }  
  
    /** 
     * 长时间显示Toast 
     *  
     * @param context 
     * @param message 
     */  
    public static void showLong(Context context, int message)  
    {  
        if (isShow)  
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();  
    }  
  
    /** 
     * 自定义显示Toast时间 
     *  
     * @param context 
     * @param message 
     * @param duration 
     */  
    public static void show(Context context, CharSequence message, int duration)  
    {  
        if (isShow)  
            Toast.makeText(context, message, duration).show();  
    }  
  
    /** 
     * 自定义显示Toast时间 
     *  
     * @param context 
     * @param message 
     * @param duration 
     */  
    public static void show(Context context, int message, int duration)  
    {  
        if (isShow)  
            Toast.makeText(context, message, duration).show();  
    }  
}

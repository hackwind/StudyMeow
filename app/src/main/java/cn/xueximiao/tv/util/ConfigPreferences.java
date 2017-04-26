package cn.xueximiao.tv.util;

import android.content.Context;

/**
 * Created by Administrator on 2016/12/6.
 */

public class ConfigPreferences extends BaseSharedPreferences {

    private static final String NAME = "myappconfig";

    private static ConfigPreferences mInstance;

    private static final  String VIDEOPOSTION="videopostion";

    private ConfigPreferences(Context ctx) {
        super(ctx);
    }

    public static ConfigPreferences getInstance(Context context){
        if(mInstance==null){
            synchronized (ConfigPreferences.class){
                if(mInstance==null){
                    mInstance=new ConfigPreferences(context);
                }
            }
        }
        return  mInstance;
    }

    @Override
    public String getSpName() {
        return NAME;
    }

    public static void setVideoPostion(long postion){
        getSp().edit().putLong(VIDEOPOSTION,postion);
    }

    public static long getVideoPostion(){
        return  getSp().getLong(VIDEOPOSTION,0);
    }
}

package cn.xueximiao.tv.util;

import android.content.Context;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/6.
 */

public class ConfigPreferences extends BaseSharedPreferences {

    private static final String NAME = "history";

    private static ConfigPreferences mInstance;


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

    public static void setVideoPostion(String videoid ,long postion){
        Log.d("hjs","setVideoPos,videoId,pos:" + videoid + "," + postion);
        getSp().edit().putLong("id" + videoid,postion).commit();
    }

    public static long getVideoPostion(String videoId){
        Log.d("hjs","getVideoPos,videoId,pos:" + videoId );
        return  getSp().getLong("id" + videoId,0);
    }
}

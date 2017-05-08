package cn.xueximiao.tv.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import cn.xueximiao.tv.http.HttpImageAsync;
import com.umeng.analytics.MobclickAgent;

import org.xutils.x;

import io.vov.vitamio.Vitamio;

/**
 * Created by Administrator on 2016/11/14.
 */
public class MeowApplication extends Application {

    private  static Context context;

    private  static final String LOGTAG="THISMYTV";

    private static  final  String UMENGAPPKEY="58732e8e7666133921000e98";

    private static  final  String UMENGCHANNELID="Leshi";

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        Vitamio.isInitialized(context);
        x.Ext.init(this);
        initImageLoader(this);
        Logger.init(LOGTAG).logLevel(LogLevel.FULL);
//        CrashHandler.getInstance().init(MyApplication.this);
        //设置umeng统计盒子场景类型
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_ANALYTICS_OEM);
        //配置Umeng
        MobclickAgent. startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,UMENGAPPKEY,UMENGCHANNELID));
        //设置日志是否加密
        MobclickAgent.enableEncrypt(false);

    }

    public static Context getContext(){
        return  context;
    }

    public static void initImageLoader(Context context) {
        int memoryCacheSize;

        MemoryCache memoryCache;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 4);
            memoryCache = new LruMemoryCache(memoryCacheSize);
        } else {
            memoryCache = new WeakMemoryCache();
        }

        // memoryCache = new WeakMemoryCache();

        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2).memoryCache(memoryCache)
                .denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO) // LIFO
                // .enableLogging() // enable log
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public static String getChannel(Activity activity) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onLowMemory() {
        HttpImageAsync.imageLoader.clearMemoryCache();
        super.onLowMemory();
    }
}

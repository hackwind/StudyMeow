package com.tv.mytv;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.tv.mytv.util.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import org.xutils.x;

import io.vov.vitamio.Vitamio;

/**
 * Created by Administrator on 2016/11/14.
 */
public class MyApplication  extends Application {

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
        Logger.init(LOGTAG).logLevel(LogLevel.FULL);
//        CrashHandler.getInstance().init(MyApplication.this);
        //设置umeng统计盒子场景类型
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_ANALYTICS_OEM);
        //配置Umeng
        MobclickAgent. startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,UMENGAPPKEY,UMENGCHANNELID));
        //设置日志是否加密
        MobclickAgent.enableEncrypt(false);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);//DEBUG日志
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                LogUtil.i(deviceToken);
//                如需手动获取device token，可以调用mPushAgent.getRegistrationId()方法（需在注册成功后调用）。
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }

    public static Context getContext(){
        return  context;
    }

}

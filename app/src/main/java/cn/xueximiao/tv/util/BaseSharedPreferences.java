package cn.xueximiao.tv.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/6.
 */

public abstract class BaseSharedPreferences {
    public interface PreferenceChangedCallback {
        public void onSettingChanged(SharedPreferences sp, String key);
    }
    private static SharedPreferences sp;
    private Map<String, PreferenceChangedCallback> callbacks = new HashMap<String, PreferenceChangedCallback>();

    public BaseSharedPreferences(Context ctx) {
        sp = ctx.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                PreferenceChangedCallback cb = callbacks.get(key);
                if (cb != null) {
                    cb.onSettingChanged(sharedPreferences, key);
                }
            }
        };
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public void addCallback(String key, PreferenceChangedCallback callback) {
        callbacks.put(key, callback);
    }

    public abstract String getSpName();

    public static SharedPreferences getSp(){
        return sp;
    }
}

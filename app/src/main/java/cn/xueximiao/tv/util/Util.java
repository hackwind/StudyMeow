package cn.xueximiao.tv.util;

import android.content.Context;
import android.net.TrafficStats;

/**
 * Created by Administrator on 2017/1/5.
 */

public class Util {
    private static final String PACKAGE = "com.tv.mytv.";

    public static final String ACTION_HTTP_ONERROR = PACKAGE + "ACTION_HTTP_ONERROR";

    private static  long total_data = TrafficStats.getTotalRxBytes();
    //几秒刷新一次
    public static  final int count = 1;

    /**
     * 核心方法，得到当前网速
     * @return
     */
    public  static int getNetSpeed(int count) {
        long traffic_data = TrafficStats.getTotalRxBytes() - total_data;
        total_data = TrafficStats.getTotalRxBytes();
        return (int) traffic_data / count;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}

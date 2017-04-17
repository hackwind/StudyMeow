package com.tv.mytv.http;

import android.provider.Settings;

/**
 * Created by Administrator on 2016/11/14.
 */

public class HttpAddress {

    public static final  String  WEB_URL="http://120.77.182.205/52feed/index.php?";

    public static String token = "";

    /**
     * 获取token
     * @return
     */
    public static String getToken(String androidid) {
         return  WEB_URL+"m=member&c=app2&a=get&a=start&device_id=" + androidid;
    }

    /**
     * 获取栏目
     * @return
     */
    public static String getCategory() {
        return  WEB_URL+"m=member&c=app2&a=category&auth=" + token;
    }

    /**
     * 获取栏目视频列表
     * @return
     */
    public static String getList(String catid,int page,int pageSize) {
        return  WEB_URL+"m=member&c=app2&a=listinfo&auth=" + token + "&catid=" + catid + "&page=" + page + "&pageSize=" + pageSize;
    }

    /**
     * 详情接口
     * @return
     */
     public static String getVideoDetails(String catid, String id){
          return  WEB_URL+"m=member&c=app2&a=get&catid="+catid+"&id="+id;
//          return  WEB_URL+"index.php?m=member&c=app&a=get&catid=2&id=483";
     }

    /**
     * 提交视频播放
     * @param id
     * @return
     */
     public static String postCount(String id){
         return  WEB_URL+"m=member&c=app2&a=count&id="+id;
     }

    /**
     * 获取视频地址
     * @param id
     * @return
     */
     public static String getVideoPath(String id){
        return  WEB_URL+"m=member&c=app2&a=getViewSource&id="+id;
     }

    /**
     * 视频列表
     */
    public final static String MENU_URL = WEB_URL+"m=member&c=app2&a=category";

    public final static String VEDIO_LIST_URL = WEB_URL+"m=member&c=app2&a=listinfo&catid=4&page=1&pageSize=20";
}

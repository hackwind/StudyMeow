package com.tv.mytv.http;

/**
 * Created by Administrator on 2016/11/14.
 */

public class HttpAddress {

    public static final  String  WEB_URL="http://120.77.182.205/52feed/";

    /**
     * 详情接口
     * @return
     */
     public static String getVideoDetails(String catid, String id){
          return  WEB_URL+"index.php?m=member&c=app&a=get&catid="+catid+"&id="+id;
//          return  WEB_URL+"index.php?m=member&c=app&a=get&catid=2&id=483";
     }

    /**
     * 提交视频播放
     * @param id
     * @return
     */
     public static String postCount(String id){
         return  WEB_URL+"index.php?m=member&c=app&a=count&id="+id;
     }

    /**
     * 获取视频地址
     * @param id
     * @return
     */
     public static String getVideoPath(String id){
        return  WEB_URL+"index.php?m=member&c=app&a=getViewSource&id="+id;
     }

    /**
     * 视频列表
     */
    public final static String MENU_URL = WEB_URL+"index.php?m=member&c=app&a=category";

    public final static String VEDIO_LIST_URL = WEB_URL+"index.php?m=member&c=app&a=listinfo&catid=4&page=1&pageSize=20";
}

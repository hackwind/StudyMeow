package cn.xueximiao.tv.http;

import android.content.pm.PackageInfo;
import android.view.View;

import cn.xueximiao.tv.activity.MeowApplication;

/**
 * Created by Administrator on 2016/11/14.
 */

public class HttpAddress {

//    public static final  String  WEB_URL="http://120.77.182.205/52feed/index.php?";
    public static final String WEB_URL = "http://op.sintoon.com/index.php?";

    public static String auth = "";

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
        return  WEB_URL+"m=member&c=app2&a=category&auth=" + auth;
    }

    /**
     * 获取栏目视频列表
     * @return
     */
    public static String getList(String catid,int page,int pageSize) {
        return  WEB_URL+"m=member&c=app2&a=listinfo&auth=" + auth + "&catid=" + catid + "&page=" + page + "&pageSize=" + pageSize;
    }

    /**
     * 获取推荐
     * @return
     */
    public static String getRecommend() {
        return  WEB_URL+"m=member&c=app2&a=poster&auth=" + auth;
    }

    /**
     * 获取微信登陆二维码
     * @return
     */
    public static String getLoginQRCode() {
        return  WEB_URL+"m=member&c=app2&a=public_login&auth=" + auth;
    }

    /**
     * 获取意见反馈/联系我们微信二维码
     * @return
     */
    public static String getOtherQRCode(int type) {//1:问题反馈;2.联系我们
        String strType = type == 1 ? "feedback" : "contact";
        return  WEB_URL+"m=member&c=app2&a=public_erweima&type=" + strType + "&auth=" + auth;
    }

    /**
     * 获取是否扫描微信二维码登陆成功
     * @return
     */
    public static String getWhetherLogin(int sessionId) {
        return  WEB_URL+"m=member&c=app2&a=public_login&type=checkLogin&auth=" + auth +"&sessionid=" + sessionId;
    }

    /**
     * 详情接口
     * @return
     */
     public static String getVideoDetails(String catid, String id){
          return  WEB_URL+"m=member&c=app2&a=get&catid="+catid+"&id="+id + "&auth=" + auth;
//          return  WEB_URL+"index.php?m=member&c=app&a=get&catid=2&id=483";
     }

    /**
     * 加入收藏
     * @return
     */
    public static String addCollection(String courseid){
        return  WEB_URL+"m=member&c=app2&a=collection_add&courseid=" + courseid + "&auth=" + auth;
//          return  WEB_URL+"index.php?m=member&c=app&a=get&catid=2&id=483";
    }

    /**
     * 取消收藏
     * @return
     */
    public static String delCollection(String courseid){
        return  WEB_URL+"m=member&c=app2&a=collection_add&courseid=" + courseid + "&auth=" + auth + "&type=cancle";
    }

    /**
     * 获取用户协议地址
     * @return
     */
    public static String getAgreementUrl(){
        return  WEB_URL+"m=content&c=index&a=lists&catid=29";
    }

    /**
     * 获取历史纪录
     * @return
     */
    public static String getHistoryUrl(int page,int pageSize){
        return  WEB_URL+"m=member&c=app2&a=history_list&auth=" + auth + "&page=" + page + "&pageSize=" + pageSize;
    }

    /**
     * 获取收藏记录
     * @return
     */
    public static String getSubjectCollection(int page,int pageSize){
        return  WEB_URL+"m=member&c=app2&a=collection_list&auth=" + auth + "&page=" + page + "&pageSize=" + pageSize;
    }

    /**
     * 获得消费记录
     * @return
     */
    public static String getCosumenerList(){
        return  WEB_URL+"m=member&c=app2&a=subscribe_list&auth=" + auth;
    }

    /**
     * 支付订阅接口
     * @param courseId
     * @return
     */
    public static String getSubScribeQRCode(String courseId) {
        return  WEB_URL+"m=member&c=app2&a=subscribe&courseid=" + courseId + "&auth=" + auth;
    }

    /**
     * 检查是否已经支付
     * @param orderNo
     * @return
     */
    public static String checkSubscribe(String orderNo) {
        return  WEB_URL+"m=member&c=app2&a=subscribe&type=checkSubscribe&orderno=" + orderNo + "&auth=" + auth;
    }

    /**
     * 获取视频地址
     * @param id
     * @return
     */
     public static String getVideoPath(String catId,String id){
        return  WEB_URL+"m=member&c=app2&a=getViewSource&catid=" + catId + "&id=" + id + "&auth=" + auth;
     }

     public static String getUpdateUrl() {
         int versionCode = 1;
         try {
             PackageInfo pkg = MeowApplication.getContext().getPackageManager().getPackageInfo(MeowApplication.getContext().getApplicationContext().getPackageName(), 0);
             versionCode = pkg.versionCode;
         } catch(Exception e) {}
         return  WEB_URL+"m=member&c=app2&a=public_update&versionCode=" + versionCode + "&auth=" + auth;
     }

    /**
     * 更新播放时间给服务器
     * @param sid
     * @param time
     * @return
     */
     public static String getUpdatePlayTimeUrl(String sid,long time) {
         return  WEB_URL+"m=member&c=app2&a=updatePlayTime&partsid=" + sid + "&playTime=" + time + "&auth=" + auth;
     }

    /**
     * 获取订阅作者的二维码
     */
    public static String getSubscribe(String id) {
        return  WEB_URL+"m=member&c=app2&a=trailer&partsid=" + id + "&auth=" + auth;
    }

    /**
     * 获取上报播放错误
     */
    public static String getReportErrorUrl(String partsid) {
        return  WEB_URL+"m=member&c=app2&a=sourceError&partsid=" + partsid + "&auth=" + auth;
    }
}

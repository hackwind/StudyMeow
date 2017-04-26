package cn.xueximiao.tv.http;

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
    public static String getQRCode() {
        return  WEB_URL+"m=member&c=app2&a=public_login&auth=" + auth;
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
        return  WEB_URL+"m=content&c=index&a=list&catid=29";
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
     public static String getVideoPath(String catId,String id){
        return  WEB_URL+"m=member&c=app2&a=getViewSource&catid=" + catId + "&id=" + id + "&auth=" + auth;
     }

    /**
     * 视频列表
     */
    public final static String MENU_URL = WEB_URL+"m=member&c=app2&a=category";

    public final static String VEDIO_LIST_URL = WEB_URL+"m=member&c=app2&a=listinfo&catid=4&page=1&pageSize=20";
}
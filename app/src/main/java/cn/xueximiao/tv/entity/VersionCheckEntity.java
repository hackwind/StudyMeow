package cn.xueximiao.tv.entity;

/**
 * Created by Administrator on 2017/4/27.
 */

public class VersionCheckEntity extends BaseEntity {
    public VersionData data;

    public static class VersionData {
        public String url;//http://localhost/52feed/app.apk",//新版本下载地址，
        public String describe;//describedescribedescribedescribe",//更新内容
        public String versionName;//1.01.0004",//最新版本版本号
        public boolean isMustUpdate;// true,//是否必须更新，true表示必须，false表示可以暂不更新
        public String updatetime;// "2017-04-25"//更新时间
    }
}

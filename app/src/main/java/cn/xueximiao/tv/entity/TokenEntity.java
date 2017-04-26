package cn.xueximiao.tv.entity;

/**
 * Created by Administrator on 2017/4/17.
 */

public class TokenEntity extends BaseEntity {
    public Auth data;

    public static class Auth {
        public String auth;
    }
}

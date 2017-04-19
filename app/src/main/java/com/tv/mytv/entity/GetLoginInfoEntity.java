package com.tv.mytv.entity;

/**
 * Created by Administrator on 2017/4/19.
 */

public class GetLoginInfoEntity extends BaseEntity {
    public LoginData data;

    public static class LoginData {
        public String nickname;
        public String userid;
        public String username;
        public String auth;
        public String thumb;
        public String groupid;
    }
}

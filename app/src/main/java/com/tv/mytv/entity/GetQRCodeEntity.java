package com.tv.mytv.entity;

/**
 * Created by Administrator on 2017/4/19.
 */

public class GetQRCodeEntity extends BaseEntity {
    public QRData data;

    public static class QRData {
        public int sessionid;
        public String erweima;
    }
}

package com.tv.mytv.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 */

public class VideoDetailEntity extends BaseEntity {
    public DetailData data;
    public static class DetailData {
        public String id; //专辑ID
        public String title; //专辑名字
        public String thumb;//专辑图片
        public String hits;//访问次数
        public String playCount;//播放总集数
        public String updatedPlayCount;//目前更新集数
        public String school;
        public String source;
        public String author;
        public String type;//类型
        public String tags;//标签
        public boolean isCollection;//true or false
        public int money;//金额 免费为0
        public int validity;//购买后有限期(天)
        public int status;//1可试看，2、需购买观看、99为可以观看。
        public int validityDay;//当前用户剩余播放天数
        public String descript;//视频简介
        public String inputtime;//更新时间
        public List<Video> videoList;
    }

    public static class Video {
        public String id; //集id
        public String  title;//集名称
        public String  pNumber;//第几集
        public String  hits;//播放次数
        public String  source;//来源
        public String  thumb;//封面图
        public String  describe;//简介
        public String  status;//1可试看，2、需购买观看、99为可以观看。
        public String  minute;//可试看分钟数，status=1时该值有值
    }
}

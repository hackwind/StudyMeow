package com.tv.mytv.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/21.
 */

public class VideoSourceEntity extends BaseEntity {
    public SourceData data;

    public static class SourceData {
        public String id; //id
        public String title;//名称
        public String pNumber;//集数
        public String hits;//播放次数
        public String source;//来源
        public int totalVideo;//总时长  毫秒数
        public int status;//1可试看，2、需购买观看、99可以观看。
        public int minute; //可试看分钟数，status=1时该值有值
        public List<Source> videoSource; //播放地址集
    }

    public static class Source {
        public String url;//"http://27.221.83.181/youku/6573DA048523E8363ECFEF36E9/03000101005859068237D718FABCD830B67294-F082-14D7-EB74-FA547177706C.flv?sid=049241495727212cae0ef_00&ctype=12",//播放地址
        public String size;//"17057029",//文件大小
        public String audio;//"84614",////音频时长
        public String video;//"84200"//视频时长毫秒数,时长用于快进的。
    }
}

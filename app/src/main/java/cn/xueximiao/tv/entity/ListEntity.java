package cn.xueximiao.tv.entity;

import java.util.List;


/**
 * Created by Administrator on 2017/4/17.
 */

public class ListEntity extends BaseEntity {
    public VideoData data;

    public static class VideoData {
        public int pageCount;
        public int total;
        public List<VideoRow> rows;
    }

    public static class VideoRow {
        public String id;
        public String title;
        public String thumb;
        public String catid;
        public int money;
    }
}

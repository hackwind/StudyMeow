package com.tv.mytv.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/17.
 */

public class RecommendEntity extends BaseEntity {
    public PostData data;

    public static class PostData {
        public List<Poster> poster;
    }

    public static class Poster {
        public String id;
        public String name;
        public String image;
        public String linkType;
        public String linkData;
    }

}

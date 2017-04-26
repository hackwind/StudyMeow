package cn.xueximiao.tv.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/17.
 */

public class CategoryEntity extends BaseEntity {
    public CategoryData data;

    public static class CategoryData {
        public List<Category> category;
    }
    public static class Category {
        public String catid;
        public String catname;
        public String color;
        public String image;
        public String icon;
    }
}

package com.tv.mytv.bean;

import java.util.List;

/**
 * Created by Administrator on 2016-11-12.
 */

public class VedioList {

    /**
     * pageCount : 0
     * rows : [{"id":"365","thumb":"http://vimg3.ws.126.net/image/snapshot_movie/2012/6/0/N/M82IDMI0N.jpg","title":"可汗学院公开课：物理学 "},{"id":"366","thumb":"http://vimg2.ws.126.net/image/snapshot_movie/2012/4/C/Q/M7UCGP2CQ.jpg","title":"可汗学院公开课：生物学"}]
     * status : true
     * total : 2
     */

    private int pageCount;
    private boolean status;
    private String total;
    /**
     * id : 365
     * thumb : http://vimg3.ws.126.net/image/snapshot_movie/2012/6/0/N/M82IDMI0N.jpg
     * title : 可汗学院公开课：物理学
     */

    private List<RowsEntity> rows;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<RowsEntity> getRows() {
        return rows;
    }

    public void setRows(List<RowsEntity> rows) {
        this.rows = rows;
    }

    public static class RowsEntity {
        private String id;
        private String thumb;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}

package com.tv.mytv.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 * 视频地址接口
 * */
public class VideoPathBean {

    /**
     * status : true
     * msg : [{"id":"36086","title":"奥运走了，天大的事来了","pNumber":"0","hits":"10000","source":"优酷","totalVideo":"","videoSource":[[{"url":"http://121.29.55.231/youku/6569B949904379276A84687A/030001020057B6AD26C9CF082DF31591C594A6-7B23-9625-19F6-AFF2A9DD1248.flv","size":0,"audio":0,"video":0},{"url":"http://27.221.99.21/youku/656C2799003282B9412E25896/030001020157B6AD26C9CF082DF31591C594A6-7B23-9625-19F6-AFF2A9DD1248.flv","size":0,"audio":0,"video":0}]],"hdVideoSource":null}]
     */

    private boolean status;
    private List<MsgBean> msg;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<MsgBean> getMsg() {
        return msg;
    }

    public void setMsg(List<MsgBean> msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        /**
         * id : 36086
         * title : 奥运走了，天大的事来了
         * pNumber : 0
         * hits : 10000
         * source : 优酷
         * totalVideo :
         * videoSource : [[{"url":"http://121.29.55.231/youku/6569B949904379276A84687A/030001020057B6AD26C9CF082DF31591C594A6-7B23-9625-19F6-AFF2A9DD1248.flv","size":0,"audio":0,"video":0},{"url":"http://27.221.99.21/youku/656C2799003282B9412E25896/030001020157B6AD26C9CF082DF31591C594A6-7B23-9625-19F6-AFF2A9DD1248.flv","size":0,"audio":0,"video":0}]]
         * hdVideoSource : null
         */
        private String id;
        private String title;
        private String pNumber;
        private String hits;
        private String source;
        private String totalVideo;
        private Object hdVideoSource;
        private List<List<VideoSourceBean>> videoSource;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPNumber() {
            return pNumber;
        }

        public void setPNumber(String pNumber) {
            this.pNumber = pNumber;
        }

        public String getHits() {
            return hits;
        }

        public void setHits(String hits) {
            this.hits = hits;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTotalVideo() {
            return totalVideo;
        }

        public void setTotalVideo(String totalVideo) {
            this.totalVideo = totalVideo;
        }

        public Object getHdVideoSource() {
            return hdVideoSource;
        }

        public void setHdVideoSource(Object hdVideoSource) {
            this.hdVideoSource = hdVideoSource;
        }

        public List<List<VideoSourceBean>> getVideoSource() {
            return videoSource;
        }

        public void setVideoSource(List<List<VideoSourceBean>> videoSource) {
            this.videoSource = videoSource;
        }

        public static class VideoSourceBean {
            /**
             * url : http://121.29.55.231/youku/6569B949904379276A84687A/030001020057B6AD26C9CF082DF31591C594A6-7B23-9625-19F6-AFF2A9DD1248.flv
             * size : 0
             * audio : 0
             * video : 0
             */
            private String url;
            private int size;
            private int audio;
            private int video;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getAudio() {
                return audio;
            }

            public void setAudio(int audio) {
                this.audio = audio;
            }

            public int getVideo() {
                return video;
            }

            public void setVideo(int video) {
                this.video = video;
            }
        }
    }
}

package com.tv.mytv.bean;

import java.util.List;

/**
 * Created by Administrator on 2016-11-12.
 */

public class MenuList {


    /**
     * status : true
     * msg : [{"catid":"4","catname":"名家对话"},{"catid":"5","catname":"美食点心"},{"catid":"6","catname":"文学艺术"},{"catid":"7","catname":"管理课程"},{"catid":"8","catname":"科技观点"},{"catid":"9","catname":"舞蹈健身"},{"catid":"10","catname":"琴棋书画"},{"catid":"11","catname":"国学经典"},{"catid":"16","catname":"一席演讲"},{"catid":"17","catname":"TED精选"}]
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
         * catid : 4
         * catname : 名家对话
         */

        private String catid;
        private String catname;
        private int resId;

        public String getCatid() {
            return catid;
        }

        public void setCatid(String catid) {
            this.catid = catid;
        }

        public String getCatname() {
            return catname;
        }

        public void setCatname(String catname) {
            this.catname = catname;
        }

        public void setResId(int resId) {this.resId = resId;}

        public int getResId() {return this.resId;}
    }
}

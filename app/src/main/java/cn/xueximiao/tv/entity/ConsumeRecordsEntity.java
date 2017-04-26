package cn.xueximiao.tv.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/24.
 */

public class ConsumeRecordsEntity extends BaseEntity {
    public RecordsData data;

    public static class RecordsData {
        public int pageCount;
        public String total;
        public List<ConsumeRecord> rows;
    }

    public static class ConsumeRecord {
        public String orderno;//订单号
        public String title;//消费名称
        public String order_type;//订单类型
        public String starttime;//有效期开始时间
        public String endtime;//有效期结束时间
        public String inputtime;//订单时间
    }
}

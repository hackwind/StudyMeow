package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import cn.tv.tv.R;
import cn.xueximiao.tv.entity.ConsumeRecordsEntity;

import java.util.List;


public class ConsumeRecordsPresenter extends OpenPresenter {

    private List<ConsumeRecordsEntity.ConsumeRecord> records;
    private GeneralAdapter mAdapter;

    public ConsumeRecordsPresenter(List<ConsumeRecordsEntity.ConsumeRecord> records) {
        this.records = records;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }


    @Override
    public int getItemCount() {
        return records == null ? 0 : records.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cosume_record, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ConsumeRecordsEntity.ConsumeRecord record = records.get(position);
        GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
        gridViewHolder.title.setText(record.title);
//        gridViewHolder.orderStatus.setText(record.order_type);
        gridViewHolder.orderNo.setText(gridViewHolder.orderNo.getText() + record.orderno);
        gridViewHolder.validDate.setText(gridViewHolder.validDate.getText() + record.starttime + "至" + record.endtime);
        gridViewHolder.consumeDate.setText(gridViewHolder.consumeDate.getText() + record.inputtime);
    }


    public class GridViewHolder extends ViewHolder {

        public TextView title;
        public TextView orderStatus;
        public TextView orderNo;
        public TextView validDate;
        public TextView consumeDate;


        public GridViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            orderStatus = (TextView) itemView.findViewById(R.id.order_status);
            orderNo = (TextView) itemView.findViewById(R.id.order_no);
            validDate = (TextView) itemView.findViewById(R.id.valid_date);
            consumeDate = (TextView) itemView.findViewById(R.id.consume_date);
        }


    }
}
package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import cn.tv.tv.R;
import cn.xueximiao.tv.entity.ListEntity;
import cn.xueximiao.tv.http.HttpImageAsync;

import java.util.List;


public class VideoListRecyclerViewPresenter extends OpenPresenter {

    private final List<ListEntity.VideoRow> rows;
    private GeneralAdapter mAdapter;

    public VideoListRecyclerViewPresenter(List<ListEntity.VideoRow> rows) {
        this.rows = rows;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }


    @Override
    public int getItemCount() {
        return rows == null ? 0 : rows.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videolist_view, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
        TextView textView =  gridViewHolder.tv;
        ListEntity.VideoRow row = rows.get(position);
        textView.setText(row.title);
        HttpImageAsync.loadingImage(gridViewHolder.iv,row.thumb);
    }

    public class GridViewHolder extends OpenPresenter.ViewHolder {

        public ImageView iv;
        public TextView tv;

        public GridViewHolder(View itemView) {
            super(itemView);
		    iv = (ImageView)itemView.findViewById(R.id.image);
            tv = (TextView)itemView.findViewById(R.id.textView);
        }

    }
}

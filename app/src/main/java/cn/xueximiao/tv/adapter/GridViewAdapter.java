package cn.xueximiao.tv.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.ListEntity;
import cn.xueximiao.tv.fragment.VideoListFragment;
import cn.xueximiao.tv.http.HttpImageAsync;
import reco.frame.tv.view.component.TvBaseAdapter;

/**
 * Created by Administrator on 2016-11-15.
 */

public class GridViewAdapter extends TvBaseAdapter {

    private Context context;

    List<ListEntity.VideoRow> rows;

    public GridViewAdapter(Context context, List<ListEntity.VideoRow> rows) {
        this.context = context;
        this.rows = rows;

    }

    @Override
    public int getCount() {
        return rows == null ? 0 : rows.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_videolist_view,
                    null);
            holder = new MyViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.textView);
            holder.iv = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }
        ListEntity.VideoRow rowsEntity = rows.get(position);
        holder.tv.setText(rowsEntity.title);
        HttpImageAsync.loadingImage(holder.iv,rowsEntity.thumb);

        if ((onLoadMoreListener.getPage() - 1) * VideoListFragment.PageSize == position) {
            onLoadMoreListener.onLoadMore();
        }
        return convertView;
    }

    static class MyViewHolder {
        TextView tv;
        ImageView iv;
    }

    private String catId;

    public void setCatid(String catId) {
        this.catId = catId;
    }

    public interface OnLoadMoreListener {
        public void onLoadMore();

        public int getPage();
    }

    private CenterContentAdapter.OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(CenterContentAdapter.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
}

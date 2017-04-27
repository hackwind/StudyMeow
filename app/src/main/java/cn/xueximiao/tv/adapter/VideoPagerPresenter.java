package cn.xueximiao.tv.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;

import java.util.List;

import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.VideoDetailEntity;


public class VideoPagerPresenter extends OpenPresenter {
    private int pageSize;
    private int count;
    private GeneralAdapter mAdapter;
    private String[] pagers;

    public VideoPagerPresenter(int count ,int pageSize) {
        this.count = count;
        this.pageSize = pageSize;
        int pages = count % pageSize == 0 ? count / pageSize :count / pageSize + 1;
        pagers = new String[pages];
        for(int i = 0; i < pages; i ++) {
            pagers[i] = ((i) * pageSize + 1) + "-" + ((i + 1) * pageSize > count ? count : (i + 1) * pageSize);
        }
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }


    @Override
    public int getItemCount() {
        return pagers == null ? 0 : pagers.length;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_pager, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
        gridViewHolder.title.setText(pagers[position]);
    }

    public class GridViewHolder extends ViewHolder {

        public TextView title;

        public GridViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
        }

    }
}

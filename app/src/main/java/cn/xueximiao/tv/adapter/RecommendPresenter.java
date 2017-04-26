package cn.xueximiao.tv.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.RecommendEntity;
import cn.xueximiao.tv.http.HttpImageAsync;

import java.util.List;


public class RecommendPresenter extends OpenPresenter {

    private List<RecommendEntity.Poster> posters;
    private GeneralAdapter mAdapter;

    public RecommendPresenter(List<RecommendEntity.Poster> posters) {
        this.posters = posters;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }


    @Override
    public int getItemCount() {
        return posters == null ? 0 : posters.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_view, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
        ImageView icon = gridViewHolder.iv;
        HttpImageAsync.loadingImage(icon,posters.get(position).image);
    }

}

package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import com.tv.mytv.R;
import com.tv.mytv.entity.RecommendEntity;
import com.tv.mytv.http.HttpImageAsync;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewPresenter extends OpenPresenter {

    private List<RecommendEntity.Poster> posters;
    private GeneralAdapter mAdapter;

    public RecyclerViewPresenter(List<RecommendEntity.Poster> posters) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_view, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
        ImageView icon = gridViewHolder.iv;
        HttpImageAsync.loadingImage(icon,posters.get(position).image);
    }

}

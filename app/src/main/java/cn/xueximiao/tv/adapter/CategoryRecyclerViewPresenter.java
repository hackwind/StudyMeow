package cn.xueximiao.tv.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.CategoryEntity;
import cn.xueximiao.tv.http.HttpImageAsync;

import java.util.List;


public class CategoryRecyclerViewPresenter extends OpenPresenter {

    private final List<CategoryEntity.Category> categories;
    private GeneralAdapter mAdapter;

    public CategoryRecyclerViewPresenter(List<CategoryEntity.Category> categories) {
        this.categories = categories;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }


    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
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
        CategoryEntity.Category category = categories.get(position);
        gridViewHolder.tv.setText(category.catname);
        gridViewHolder.view.setBackgroundColor(Color.parseColor(category.color));
        HttpImageAsync.loadingImage(gridViewHolder.iv,category.image);
    }

    class GridViewHolder extends OpenPresenter.ViewHolder {

        public ImageView iv;
        public TextView tv;

        public GridViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.icon);
            tv = (TextView) itemView.findViewById(R.id.textView);
        }
    }

}

package com.tv.mytv.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.mytv.R;
import com.tv.mytv.bean.VedioList;
import com.tv.mytv.fragment.VedioListAllHotFragment;
import com.tv.mytv.http.HttpImageAsync;

import org.xutils.image.ImageOptions;

import java.util.List;

import reco.frame.tv.view.component.TvBaseAdapter;

/**
 * Created by Administrator on 2016-11-15.
 */

public class GridViewAdapter extends TvBaseAdapter {

    private Context context;

    private List<VedioList.RowsEntity> rows;

    private ImageOptions ios;

    public GridViewAdapter(Context context, List<VedioList.RowsEntity> rows) {
        this.context = context;
        this.rows = rows;
//        .setImageScaleType(ImageView.ScaleType.FIT_XY)
        ios = new ImageOptions.Builder().setImageScaleType(ImageView.ScaleType.FIT_XY).setAutoRotate(true).setLoadingDrawableId(R.mipmap.nopic).setFailureDrawableId(R.mipmap.nopic).build();
    }

    @Override
    public int getCount() {
        return rows.size();
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
            convertView = View.inflate(context, R.layout.vedio_item,
                    null);
            holder = new MyViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv_vedio);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_vedio);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }
        VedioList.RowsEntity rowsEntity = rows.get(position);
        holder.tv.setText(rowsEntity.getTitle());
//      x.image().bind(holder.iv, rowsEntity.getThumb(), ios);
        HttpImageAsync.loadingImage(holder.iv,rowsEntity.getThumb());

        if ((onLoadMoreListener.getPage() - 1) * VedioListAllHotFragment.PAGESIZE == position) {
            onLoadMoreListener.onLoadMore();
        }
//        HttpImageAsync.loadingImage(holder.iv,rowsEntity.getThumb());
        return convertView;
//        GlobalContents.systemOut(position+"");
//        View inflate = View.inflate(context, R.layout.vedio_item, null);
//        ImageView iv = (ImageView) inflate.findViewById(R.id.iv_vedio);
//        TextView tv = (TextView) inflate.findViewById(R.id.tv_vedio);
//        tv.setText(rowsEntity.getTitle());
//        HttpImageAsync.loadingImage(iv,rowsEntity.getThumb());
//        return inflate;
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

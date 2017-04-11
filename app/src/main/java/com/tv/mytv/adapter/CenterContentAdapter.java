package com.tv.mytv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.mytv.CourseDetailsActivity;
import com.tv.mytv.R;
import com.tv.mytv.bean.VedioList;
import com.tv.mytv.fragment.VedioListAllHotFragment;
import com.tv.mytv.http.HttpImageAsync;

import org.xutils.image.ImageOptions;

import java.util.List;



/**
 * Created by Administrator on 2016/4/26.
 */
public class CenterContentAdapter extends RecyclerView.Adapter<CenterContentAdapter.MyViewHolder> {
    private Context context;
    private List<VedioList.RowsEntity> rows;
    private ImageOptions ios;
    private int count;

    public CenterContentAdapter(Context context, List<VedioList.RowsEntity> rows) {
        this.context = context;
        this.rows = rows;
        count = rows.size();
        ios = new ImageOptions.Builder().setImageScaleType(ImageView.ScaleType.FIT_XY).setAutoRotate(true).setLoadingDrawableId(R.mipmap.pic_default).setFailureDrawableId(R.mipmap.pic_default).build();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.vedio_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final VedioList.RowsEntity rowsEntity = rows.get(position);
        holder.tv.setText(rowsEntity.getTitle());
//        x.image().bind(holder.iv, rowsEntity.getThumb());
        HttpImageAsync.loadingImage(holder.iv,rowsEntity.getThumb());
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,position+"", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context, CourseDetailsActivity.class);
                intent.putExtra("id",rowsEntity.getId());
                intent.putExtra("catId",catId);
                context.startActivity(intent);
            }
        });

//        GlobalContents.systemOut((onLoadMoreListener.getPage() - 1)* VedioListAllHotFragment.PAGESIZE+"---"+position);
        if ((onLoadMoreListener.getPage() - 1)* VedioListAllHotFragment.PAGESIZE == position ){
            onLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView iv;
        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_vedio);
            iv = (ImageView) view.findViewById(R.id.iv_vedio);
        }
    }

    public interface OnLoadMoreListener {
        public void onLoadMore();
        public int getPage();
    }

    private OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;

    }
    private String catId;
    public void setCatid(String catId){
        this.catId = catId;

    }
}

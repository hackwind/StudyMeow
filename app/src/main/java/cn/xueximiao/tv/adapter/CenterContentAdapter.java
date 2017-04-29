package cn.xueximiao.tv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.xutils.image.ImageOptions;


import java.util.List;

import cn.xueximiao.tv.R;
import cn.xueximiao.tv.activity.VideoDetailActivity;
import cn.xueximiao.tv.entity.ListEntity;
import cn.xueximiao.tv.fragment.VideoListFragment;
import cn.xueximiao.tv.http.HttpImageAsync;


/**
 * Created by Administrator on 2016/4/26.
 */
public class CenterContentAdapter extends RecyclerView.Adapter<CenterContentAdapter.MyViewHolder> {
    private Context context;
    private List<ListEntity.VideoRow> rows;
    private ImageOptions ios;
    private int count;

    public CenterContentAdapter(Context context, List<ListEntity.VideoRow> rows) {
        this.context = context;
        this.rows = rows;
        count = rows.size();
        ios = new ImageOptions.Builder().setImageScaleType(ImageView.ScaleType.FIT_XY).setAutoRotate(true).setLoadingDrawableId(R.drawable.shape_nopic).setFailureDrawableId(R.drawable.shape_nopic).build();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_videolist_view, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final ListEntity.VideoRow rowsEntity = rows.get(position);
        holder.tv.setText(rowsEntity.title);
        HttpImageAsync.loadingImage(holder.iv,rowsEntity.thumb);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,position+"", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context, VideoDetailActivity.class);
                intent.putExtra("id",rowsEntity.id);
                intent.putExtra("catId",catId);
                context.startActivity(intent);
            }
        });

//        GlobalContents.systemOut((onLoadMoreListener.getPage() - 1)* VedioListAllHotFragment.PAGESIZE+"---"+position);
        if ((onLoadMoreListener.getPage() - 1)* VideoListFragment.PageSize == position ){
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
            tv = (TextView) view.findViewById(R.id.textView);
            iv = (ImageView) view.findViewById(R.id.image);
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

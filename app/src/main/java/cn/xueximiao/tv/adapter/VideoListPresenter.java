package cn.xueximiao.tv.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.VideoDetailEntity;

import java.util.List;


public class VideoListPresenter extends OpenPresenter {

    private List<VideoDetailEntity.Video> list;
    private GeneralAdapter mAdapter;
    private int bgResId;
    private int freeStatus = 1;//1 免费,2,收费,3,已购买

    public VideoListPresenter(List<VideoDetailEntity.Video> list,int viewBackgroundResource,int freeStatus) {
        this.list = list;
        this.bgResId = viewBackgroundResource;
        this.freeStatus = freeStatus;
    }

    public void setList(List<VideoDetailEntity.Video> list) {
        this.list = list;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);
        view.setBackgroundResource(this.bgResId);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
        gridViewHolder.title.setText(list.get(position).title);
        if(freeStatus == 1) {//可以观看
            gridViewHolder.free.setText(R.string.free);
        } else if(freeStatus == 2){
            gridViewHolder.free.setText(R.string.charge);
        } else if(freeStatus == 3) {
            gridViewHolder.free.setText(R.string.buy_yet);
        }
    }

    public class GridViewHolder extends OpenPresenter.ViewHolder {

        public TextView title;
        public TextView free;

        public GridViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            free = (TextView)itemView.findViewById(R.id.free);
        }

    }
}

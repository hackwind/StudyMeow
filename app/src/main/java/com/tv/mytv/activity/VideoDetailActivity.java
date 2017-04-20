package com.tv.mytv.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.tv.mytv.R;
import com.tv.mytv.entity.VideoDetailEntity;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpImageAsync;
import com.tv.mytv.http.HttpRequest;

import java.util.List;

import adapter.VideoListPresenter;

/**
 * Created by Administrator on 2017/4/19.
 */

public class VideoDetailActivity extends BaseActivity {
    private ImageView thumbImage;
    private TextView thumbName;
    private TextView sourceFrom;
    private TextView updateTime;
    private TextView author;
    private TextView thumbDesc;
    private TextView bugYet;
    private LinearLayout buttonPlay;
    private LinearLayout buttonBuy;
    private LinearLayout buttonCollect;
    private RecyclerViewTV videoList;
    private LinearLayout progressBar;
    private VideoListPresenter presenter;
    private GeneralAdapter generalAdapter;

    private TextView subTitle;
    private TextView subDesc;
    private ImageView subIcon;


    private String id;
    private String catid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        id = getIntent().getStringExtra("id");
        catid = getIntent().getStringExtra("catid");

        initView();

        getVideoDetail();
    }

    private void initView() {
        thumbImage = (ImageView)findViewById(R.id.thumb_image);
        thumbName = (TextView)findViewById(R.id.thumb_name);
        sourceFrom = (TextView)findViewById(R.id.source_from);
        updateTime = (TextView)findViewById(R.id.thumb_update_time);
        author  = (TextView)findViewById(R.id.thumb_author);
        thumbDesc = (TextView)findViewById(R.id.thumb_desc);
        bugYet = (TextView)findViewById(R.id.thumb_buy_yet);
        buttonPlay = (LinearLayout)findViewById(R.id.button_play);
        buttonBuy = (LinearLayout)findViewById(R.id.button_buy);
        buttonCollect = (LinearLayout)findViewById(R.id.button_collect);
        videoList = (RecyclerViewTV)findViewById(R.id.video_list);
        progressBar = (LinearLayout)findViewById(R.id.progressBar);

        subTitle = (TextView)findViewById(R.id.sub_title);
        subDesc = (TextView)findViewById(R.id.sub_title);
        subIcon = (ImageView)findViewById(R.id.sub_icon);
    }

    private void initVideoListRecyclerView(final List<VideoDetailEntity.Video> list) {
        LinearLayoutManager gridlayoutManager = new LinearLayoutManager(this); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        videoList.setLayoutManager(gridlayoutManager);
        videoList.setFocusable(false);
        videoList.setSelectedItemAtCentered(true); // 设置item在中间移动.
        videoList.addItemDecoration(new SpaceItemDecoration((int)getResources().getDimension(R.dimen.w_20)));
        presenter = new VideoListPresenter(list);
        generalAdapter = new GeneralAdapter(presenter);
        videoList.setAdapter(generalAdapter);
        videoList.setDefaultSelect(0);

        videoList.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                VideoDetailEntity.Video video = list.get(position);
                subTitle.setText(video.title);
                subDesc.setText(video.describe);
                HttpImageAsync.loadingImage(subIcon,video.thumb);
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

            }
        });
    }

    private void getVideoDetail() {
        HttpRequest.get(HttpAddress.getVideoDetails(catid,id),null,VideoDetailActivity.this,"getDetailBack",progressBar,this, VideoDetailEntity.class);
    }

    public void getDetailBack(VideoDetailEntity entity,String totalResult) {
        if(entity.status == false || entity.data == null) {
            return;
        }
        HttpImageAsync.loadingImage(thumbImage,entity.data.thumb);
        thumbName.setText(entity.data.title);
        if(entity.data.money == 0 || entity.data.validity > 0) {
            buttonBuy.setVisibility(View.GONE);
            bugYet.setVisibility(View.VISIBLE);
        } else {
            buttonPlay.setVisibility(View.GONE);
        }
        thumbDesc.setText(entity.data.descript);
        author.setText(entity.data.author);
        updateTime.setText(entity.data.inputtime);
        sourceFrom.setText(entity.data.source);

        initVideoListRecyclerView(entity.data.videoList);
    }

    class SpaceItemDecoration extends RecyclerViewTV.ItemDecoration{
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = space;
        }
    }
}

package com.tv.mytv.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.tv.mytv.R;
import com.tv.mytv.entity.BaseEntity;
import com.tv.mytv.entity.VideoDetailEntity;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpImageAsync;
import com.tv.mytv.http.HttpRequest;
import com.tv.mytv.util.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import adapter.VideoListPresenter;

/**
 * Created by Administrator on 2017/4/19.
 */

public class VideoDetailActivity extends BaseActivity implements View.OnFocusChangeListener,View.OnClickListener{
    private ImageView thumbImage;
    private TextView thumbName;
    private TextView sourceFrom;
    private TextView updateTime;
    private TextView author;
    private TextView thumbDesc;
    private TextView bugYet;
    private ImageView iconCollect;
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

    private int selectedVideoIndex = 0;
    private List<VideoDetailEntity.Video> list;

    private MyHandler handler = new MyHandler(this);
    private String strVideoDetail;

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
        iconCollect = (ImageView) findViewById(R.id.collect_icon);

        subTitle = (TextView)findViewById(R.id.sub_title);
        subDesc = (TextView)findViewById(R.id.sub_title);
        subIcon = (ImageView)findViewById(R.id.sub_icon);

        buttonPlay.setOnFocusChangeListener(this);
        buttonBuy.setOnFocusChangeListener(this);
        buttonCollect.setOnFocusChangeListener(this);
        buttonPlay.setOnClickListener(this);
        buttonCollect.setOnClickListener(this);
        buttonBuy.setOnClickListener(this);
    }

    private void initVideoListRecyclerView() {
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

        videoList.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                selectedVideoIndex = position;
                VideoDetailEntity.Video video = list.get(position);

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("title",video.title);
                data.putString("desc",video.describe);
                data.putString("thumb",video.thumb);
                msg.setData(data);
                handler.sendMessage(msg);

            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

            }
        });
        videoList.setDefaultSelect(0);
    }

    private void getVideoDetail() {
        HttpRequest.get(HttpAddress.getVideoDetails(catid,id),null,VideoDetailActivity.this,"getDetailBack",progressBar,this, VideoDetailEntity.class);
    }

    public void getDetailBack(VideoDetailEntity entity,String totalResult) {
        if(entity.status == false || entity.data == null) {
            return;
        }
        strVideoDetail = totalResult;
        HttpImageAsync.loadingImage(thumbImage,entity.data.thumb);
        thumbName.setText(entity.data.title);
        if(entity.data.money == 0 || entity.data.validity > 0) {
            buttonBuy.setVisibility(View.GONE);
            buttonPlay.setVisibility(View.VISIBLE);
            if(entity.data.money > 0) {
                bugYet.setVisibility(View.VISIBLE);
            }
        } else {
            buttonPlay.setVisibility(View.GONE);
            buttonBuy.setVisibility(View.VISIBLE);
            bugYet.setVisibility(View.GONE);
        }
        findViewById(R.id.thumb_list_text).setVisibility(View.VISIBLE);
        buttonCollect.setVisibility(View.VISIBLE);
        thumbDesc.setText(entity.data.descript);
        author.setText(entity.data.author);
        updateTime.setText(entity.data.inputtime);
        sourceFrom.setText(entity.data.source);
        if(entity.data.isCollection) {
            iconCollect.setImageResource(R.drawable.collect_yet);
        } else {
            iconCollect.setImageResource(R.drawable.collect_not);
        }

        list = entity.data.videoList;
        initVideoListRecyclerView();

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.button_buy:
                buttonBuy.setSelected(true);
                buttonCollect.setSelected(false);
                break;
            case R.id.button_collect:
                buttonCollect.setSelected(true);
                buttonBuy.setSelected(false);
                buttonPlay.setSelected(false);
                break;
            case R.id.button_play:
                buttonPlay.setSelected(true);
                buttonCollect.setSelected(false);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_buy:
                Intent intent = new Intent(this,VideoPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.button_collect:
                if(list == null || list.size() == 0 || list.size() < selectedVideoIndex + 1) {
                    return;
                }
                addCollection();
                break;
            case R.id.button_play:
                if(list == null || list.size() == 0 || list.size() < selectedVideoIndex + 1) {
                    return;
                }
                if(TextUtils.isEmpty(strVideoDetail)){
                    return;
                }
                intent = new Intent(this,VideoPlayerActivity.class);
                intent.putExtra("videodetail",strVideoDetail);
                startActivity(intent);
                break;
        }
    }

    private void addCollection() {
        HttpRequest.get(HttpAddress.addCollection(id),null,VideoDetailActivity.this,"addCollectionBack",null,this, BaseEntity.class);
    }

    public void addCollectionBack(BaseEntity entity,String totalEesult) {
        if(entity != null && entity.status) {
            ToastUtil.showLong(this,"添加关注成功");
            iconCollect.setImageResource(R.drawable.collect_yet);
        }
    }

    class MyHandler extends Handler {
        private WeakReference<VideoDetailActivity> ref;
        public MyHandler(VideoDetailActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if(data == null) {
                return;
            }
            Log.d("hjs","title:" + data.getString("title"));
            HttpImageAsync.loadingImage(subIcon,data.getString("thumb"));
            subTitle.setText(data.getString("title"));
            subTitle.invalidate();
            subDesc.setText(data.getString("desc"));
            subDesc.invalidate();
        }
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

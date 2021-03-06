package cn.xueximiao.tv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import cn.xueximiao.tv.R;
import cn.xueximiao.tv.adapter.VideoPagerPresenter;
import cn.xueximiao.tv.entity.BaseEntity;
import cn.xueximiao.tv.entity.GetSubScribeQRCodeEntity;
import cn.xueximiao.tv.entity.VideoDetailEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpImageAsync;
import cn.xueximiao.tv.http.HttpRequest;
import cn.xueximiao.tv.util.ToastUtil;
import cn.xueximiao.tv.util.Util;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.xueximiao.tv.adapter.VideoListPresenter;

/**
 * Created by Administrator on 2017/4/19.
 */

public class VideoDetailActivity extends BaseActivity implements View.OnFocusChangeListener,View.OnClickListener{
    private final static int PAGE_SIZE = 10;
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
    private RecyclerViewTV selectionPages;//选集页码段
    private ProgressBar progressBar;
    private VideoListPresenter presenter;
    private GeneralAdapter generalAdapter;
    private VideoPagerPresenter pagerPresenter;
    private GeneralAdapter pagerAdapter;

    private TextView subTitle;
    private TextView subDesc;
    private ImageView subIcon;
    private TextView buyCourceName;
    private TextView buyPrice;
    private LinearLayout buyContainer;
    private ImageView buyQRCode;

    private String id;
    private String catid;

    private int selectedVideoIndex = 0;
    private List<VideoDetailEntity.Video> list;
    private List<VideoDetailEntity.Video> subVideoList;//某个分页段内的专辑列表

    private String strVideoDetail;
    private boolean isCollect = false;

    private final static int REPEAT_INTERVAL = 2000;
    private String orderNo;
    private Timer timer;
    private boolean checkingSubscribe = false;
    private boolean needBuy = false;
    private int pageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        id = getIntent().getStringExtra("id");
        catid = getIntent().getStringExtra("catid");

        initView();

        //网络连接失败
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Util.ACTION_HTTP_ONERROR);
        registerReceiver(MyNetErrorReceiver,intentFilter);

        getVideoDetail();
    }

    @Override
    public void onBackPressed() {
        if(buyContainer != null && buyContainer.getVisibility() == View.VISIBLE) {
            buyContainer.setVisibility(View.GONE);
            checkingSubscribe = false;
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(MyNetErrorReceiver);
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    private BroadcastReceiver MyNetErrorReceiver =new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Util.ACTION_HTTP_ONERROR)){
                if(checkingSubscribe) {
                    startTimer();
                }
            }
        }
    };

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
        selectionPages = (RecyclerViewTV)findViewById(R.id.video_pages);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        iconCollect = (ImageView) findViewById(R.id.collect_icon);

        subTitle = (TextView)findViewById(R.id.sub_title);
        subDesc = (TextView)findViewById(R.id.sub_desc);
        subIcon = (ImageView)findViewById(R.id.sub_icon);
        buyCourceName = (TextView)findViewById(R.id.buy_course_name);
        buyPrice = (TextView)findViewById(R.id.buy_price);
        buyContainer = (LinearLayout)findViewById(R.id.buy_container);
        buyQRCode = (ImageView)findViewById(R.id.buy_qrcode);

        buttonPlay.setOnFocusChangeListener(this);
        buttonBuy.setOnFocusChangeListener(this);
        buttonCollect.setOnFocusChangeListener(this);
        buttonPlay.setOnClickListener(this);
        buttonCollect.setOnClickListener(this);
        buttonBuy.setOnClickListener(this);
    }

    private void initVideoListRecyclerView(int freeStatus) {
        LinearLayoutManager gridlayoutManager = new LinearLayoutManager(this); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        videoList.setLayoutManager(gridlayoutManager);
        videoList.setFocusable(false);
        videoList.setSelectedItemAtCentered(true); // 设置item在中间移动.
        videoList.addItemDecoration(new SpaceItemDecoration((int)getResources().getDimension(R.dimen.w_20)));
        subVideoList = list.subList(0,PAGE_SIZE  > list.size() ? list.size() : PAGE_SIZE );
        presenter = new VideoListPresenter(subVideoList,R.drawable.selector_video_detail_list,freeStatus);
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
                if(TextUtils.isEmpty(video.thumb)) {
                    subIcon.setVisibility(View.GONE);
                } else {
                    subIcon.setVisibility(View.VISIBLE);
                    HttpImageAsync.loadingImage(subIcon,video.thumb);
                }

                subTitle.setText(video.title);
                subDesc.setText(video.describe);

                //页码部分变色
                if(selectionPages.getVisibility() == View.VISIBLE) {
                    resetTextColor();
                }
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

            }
        });
        videoList.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                if(needBuy) {
                    checkingSubscribe = true;
                    buyContainer.setVisibility(View.VISIBLE);
                    getBuyQRCode();
                } else {
                    Intent intent = new Intent(VideoDetailActivity.this, VideoPlayerActivity.class);
                    intent.putExtra("videodetail", strVideoDetail);
                    intent.putExtra("index", position + pageIndex * PAGE_SIZE);
                    startActivity(intent);
                }
            }
        });
        if(list != null && list.size() > 0) {
            VideoDetailEntity.Video video = list.get(0);
            if(TextUtils.isEmpty(video.thumb)) {
                subIcon.setVisibility(View.GONE);
            } else {
                subIcon.setVisibility(View.VISIBLE);
                HttpImageAsync.loadingImage(subIcon,video.thumb);
            }
            subTitle.setText(video.title);
            subDesc.setText(video.describe);
        }
    }
    //选集列表
    private void initPageList() {
        if(list == null || list.size() < PAGE_SIZE) {
            selectionPages.setVisibility(View.GONE);
            return;
        }
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(this); // 解决快速长按焦点丢失问题.
        linearlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        linearlayoutManager.setSmoothScrollbarEnabled(false);
        selectionPages.setLayoutManager(linearlayoutManager);
        selectionPages.setFocusable(true);
        selectionPages.addItemDecoration(new SpaceItemDecoration((int)getResources().getDimension(R.dimen.w_20)));
        pagerPresenter = new VideoPagerPresenter(list == null ? 0 : list.size(),PAGE_SIZE);
        pagerAdapter = new GeneralAdapter(pagerPresenter);
        selectionPages.setAdapter(pagerAdapter);
        selectionPages.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
            }


            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                pageIndex = position;
                int start = position * PAGE_SIZE;
                int end = (position + 1) * PAGE_SIZE ;
                end = end > list.size()  ? list.size() : end;

                subVideoList = list.subList(start,end);
                presenter.setList(subVideoList);
                videoList.getAdapter().notifyDataSetChanged();
                videoList.invalidate();

                resetTextColor();
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
            }
        });

    }
    private void resetTextColor() {
        View child = null;
        for(int i = 0;i < selectionPages.getChildCount();i ++) {
            child = selectionPages.getChildAt(i).findViewById(R.id.title);
            if(child == null) {
                continue;
            }
            if(i != pageIndex){
                ((TextView)child).setTextColor(getResources().getColor(R.color.trans_white));
            } else {
                ((TextView)child).setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    private void getVideoDetail() {
        HttpRequest.get(HttpAddress.getVideoDetails(catid,id),null,VideoDetailActivity.this,"getDetailBack",progressBar,this, VideoDetailEntity.class);
    }

    public void getDetailBack(VideoDetailEntity entity,String totalResult) {
        progressBar.setVisibility(View.GONE);
        if(entity.status == false || entity.data == null) {
            return;
        }
        strVideoDetail = totalResult;
        HttpImageAsync.loadingImage(thumbImage,entity.data.thumb);
        thumbName.setText(entity.data.title);
        if(entity.data.money == 0 || entity.data.validityDay > 0) {
            needBuy = false;
            buttonBuy.setVisibility(View.GONE);
            buttonPlay.setVisibility(View.VISIBLE);
            if(entity.data.money > 0) {
                bugYet.setVisibility(View.VISIBLE);
            }
        } else {
            needBuy = true;
            buttonPlay.setVisibility(View.GONE);
            buttonBuy.setVisibility(View.VISIBLE);
            bugYet.setVisibility(View.GONE);
            buyPrice.setText(entity.data.money + "元/" + entity.data.validity + "天");
            buyCourceName.setText(String.format(buyCourceName.getText().toString(),entity.data.title));
        }
        findViewById(R.id.thumb_list_text).setVisibility(View.VISIBLE);
        buttonCollect.setVisibility(View.VISIBLE);
        thumbDesc.setText(entity.data.descript);
        author.setText(entity.data.author);
        updateTime.setText(entity.data.inputtime);
        if(!TextUtils.isEmpty(entity.data.source)) {
            sourceFrom.setText("来源:" + entity.data.source);
        }
        if(isCollect = entity.data.isCollection) {
            iconCollect.setImageResource(R.drawable.collect_yet);
        } else {
            iconCollect.setImageResource(R.drawable.collect_not);
        }

        list = entity.data.videoList;
        int freeStatus = 1;
        if(entity.data.money == 0) {
            freeStatus = 1;
        } else if(entity.data.validityDay > 0) {
            freeStatus = 3;
        } else {
            freeStatus = 2;
        }
        initVideoListRecyclerView(freeStatus);
        initPageList();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.button_buy:
                if(b) {
                    buttonBuy.setSelected(true);
                    buttonCollect.setSelected(false);
                } else {
                    buttonPlay.setSelected(false);
                }
                break;
            case R.id.button_collect:
                if(b) {
                    buttonCollect.setSelected(true);
                    buttonBuy.setSelected(false);
                    buttonPlay.setSelected(false);
                } else {
                        buttonCollect.setSelected(false);
                 }
                break;
            case R.id.button_play:
                if(b) {
                    buttonPlay.setSelected(true);
                    buttonCollect.setSelected(false);
                } else {
                    buttonPlay.setSelected(false);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_buy:
                checkingSubscribe = true;
                buyContainer.setVisibility(View.VISIBLE);
                getBuyQRCode();
                break;
            case R.id.button_collect:
                if(isCollect) {
                    delCollection();
                } else {
                    addCollection();
                }
                break;
            case R.id.button_play:
                if(list == null || list.size() == 0 || list.size() < selectedVideoIndex + 1) {
                    return;
                }
                if(TextUtils.isEmpty(strVideoDetail)){
                    return;
                }
                Intent intent = new Intent(this,VideoPlayerActivity.class);
                intent.putExtra("videodetail",strVideoDetail);
                intent.putExtra("index",0);
                startActivity(intent);
                break;
        }
    }

    private void getBuyQRCode() {
        HttpRequest.get(HttpAddress.getSubScribeQRCode(id),null,VideoDetailActivity.this,"getBuyQRCodeBack",progressBar,this, GetSubScribeQRCodeEntity.class);
    }

    public void getBuyQRCodeBack(GetSubScribeQRCodeEntity entity,String result) {
        if(entity == null) {
            return;
        }
        if(entity.status == false) {
            ToastUtil.showShort(this,entity.msg);
            return;
        }
        HttpImageAsync.loadingImage(buyQRCode,entity.data.erweima);
        orderNo = entity.data.orderno;
        startTimer();
    }

    private void addCollection() {
        HttpRequest.get(HttpAddress.addCollection(id),null,VideoDetailActivity.this,"addCollectionBack",progressBar,this, BaseEntity.class);
    }

    public void addCollectionBack(BaseEntity entity,String totalEesult) {
        if(entity != null && entity.status) {
            ToastUtil.showLong(this,"添加关注成功");
            iconCollect.setImageResource(R.drawable.collect_yet);
            isCollect = true;
        }
    }

    private void delCollection() {
        HttpRequest.get(HttpAddress.delCollection(id),null,VideoDetailActivity.this,"delCollectionBack",progressBar,this, BaseEntity.class);
    }

    public void delCollectionBack(BaseEntity entity,String totalEesult) {
        if(entity != null && entity.status) {
            ToastUtil.showLong(this,"取消关注成功");
            iconCollect.setImageResource(R.drawable.collect_not);
            isCollect = false;
        }
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {//一次只定时请求一次，等失败再次请求下次，不做定期无限循环请求，当网络不好情况，容易堵塞
            @Override
            public void run() {
                checkSubScribe();
            }
        }, REPEAT_INTERVAL);
    }

    private void checkSubScribe() {
        HttpRequest.get(HttpAddress.checkSubscribe(orderNo),null,VideoDetailActivity.this,"checkSubScribeBack",null,this, BaseEntity.class);
    }

    public void checkSubScribeBack(BaseEntity entity,String result) {
        if(entity != null && entity.status) {//支付成功
            checkingSubscribe = false;
            buttonBuy.setVisibility(View.GONE);
            buttonPlay.setVisibility(View.VISIBLE);
            bugYet.setVisibility(View.VISIBLE);
            buyContainer.setVisibility(View.GONE);
            getVideoDetail();//重新获取一次
        } else {
            if(buyContainer.getVisibility() == View.VISIBLE) {
                startTimer();
            }
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

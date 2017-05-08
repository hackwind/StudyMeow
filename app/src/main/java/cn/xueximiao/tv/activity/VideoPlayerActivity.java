package cn.xueximiao.tv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import cn.xueximiao.tv.R;
import cn.xueximiao.tv.adapter.VideoListPresenter;
import cn.xueximiao.tv.adapter.VideoPagerPresenter;
import cn.xueximiao.tv.entity.BaseEntity;
import cn.xueximiao.tv.entity.TrailerEntity;
import cn.xueximiao.tv.entity.VideoDetailEntity;
import cn.xueximiao.tv.entity.VideoSourceEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpImageAsync;
import cn.xueximiao.tv.http.HttpRequest;
import cn.xueximiao.tv.util.ConfigPreferences;
import cn.xueximiao.tv.util.SharePrefUtil;
import cn.xueximiao.tv.util.ToastUtil;
import cn.xueximiao.tv.util.Util;
import cn.xueximiao.tv.view.MyMediaController;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.umeng.analytics.MobclickAgent;


import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import static cn.xueximiao.tv.util.Util.count;

/**
 * 播放器
 */
public class VideoPlayerActivity extends BaseActivity {
    private final static int PAGE_SIZE = 10;
    //视频路径
    private String videoPath;

    private VideoView mVideoView;

    private LinearLayout mVideo_error;

    private LinearLayout loading;

    private LinearLayout bottomSelection;

    private String title;

    private String source;

    private TextView title_pro;

    private TextView source_pro;

    private MyMediaController mediaController;

    private boolean isPlay = true;

    private String str;
    private String catId;//专辑id
    //集数ID
    private String videoId;//当前正在播放的视频id

    private TextView  netWork_pro;

    private  TextView error_text;

    //正在加载布局
    private  LinearLayout linear_buffer;

    //是否需要自动恢复播放，用于自动暂停，恢复播放
    private boolean needResume;

    private TextView network_buffer;

    private Button butttonRetry;
    private String strVideoDetail;
    private List<VideoDetailEntity.Video> videoList;//整个专辑列表
    private List<VideoDetailEntity.Video> subVideoList;//某个分页段内的专辑列表
    private int playIndex = 0;//当前正在播放的专辑列表索引
    private int segIndex = 0;//某一集下正在播放的的片段索引
    private List<VideoSourceEntity.VideoSource> currentVideoSource;

    private RecyclerViewTV selectionList;//选集
    private RecyclerViewTV selectionPages;//选集页码段
    private VideoListPresenter listPresenter;
    private GeneralAdapter listAdapter;
    private VideoPagerPresenter pagerPresenter;
    private GeneralAdapter pagerAdapter;

    private RelativeLayout subscribeLayout;//一集播放完毕订阅作者的二维码层
    private ImageView subscribeQRCode;

    private RelativeLayout pauseLayout;//暂停时订阅作者的二维码层
    private ImageView pauseQRCode;
    private int freeStatus = 1;
    private int pageIndex = 0;//集数分页，当前所在页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);

        strVideoDetail = getIntent().getStringExtra("videodetail");
        playIndex = getIntent().getIntExtra("index",0);
        VideoDetailEntity entity = new Gson().fromJson(strVideoDetail,VideoDetailEntity.class);
        if(entity == null) {
            finish();
        }
        title = entity.data.title;
        source = entity.data.source;
        catId = entity.data.id;
        videoList = entity.data.videoList;//专辑列表

        if(entity.data.money == 0) {
            freeStatus = 1;
        } else if(entity.data.validityDay > 0) {
            freeStatus = 3;
        } else {
            freeStatus = 2;
        }

        //网络连接失败
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Util.ACTION_HTTP_ONERROR);
        registerReceiver(MyNetErrorReceiver,intentFilter);

        //播放页是在一个独立的进程，需要再次赋值给auth
        HttpAddress.auth = SharePrefUtil.getString(this,SharePrefUtil.KEY_AUTH,"");

        initview();
        getVideoSourcePath();
    }

    @Override
    public void onBackPressed() {
        if(bottomSelection.getVisibility() == View.VISIBLE) {
            bottomSelection.setVisibility(View.GONE);
            return;
        }
        updatePlayTime();
        super.onBackPressed();
    }

    private void play(long seekPos) {
        Log.d("hjs","begin play, seek position:" + seekPos);
        mediaController = new MyMediaController(VideoPlayerActivity.this, mVideoView, VideoPlayerActivity.this);
        mediaController.setVideoName(title);
        mediaController.show(5000);
        mediaController.setOnShownListener(new MediaController.OnShownListener() {
            @Override
            public void onShown() {
                bottomSelection.setVisibility(View.GONE);
            }
        });
        if ("".equals(videoPath) || videoPath == null || videoPath == "") {
            ToastUtil.showShort(VideoPlayerActivity.this, "没有获取到播放源");
            return;
        } else {

            //设置硬件解码
            mVideoView.setHardwareDecoder(false);
            //设置缓冲大小
            mVideoView.setBufferSize(512 * 1024);
            //mVideoView.setVideoPath("http://pl-ali.youku.com/playlist/m3u8?ts=1491379421&keyframe=1&vid=51774769&type=hd2&sid=049137942172720424e20&token=5496&oip=1696944366&did=898d39a045c9ba106aadb7948a82db41&ctype=20&ev=1&ep=8dSpcv4XiMQqYrmnnzZUQtLwiiT4LnBKV%2ByLYxVL26AF5USSf8P4s7i4KEcpvH8L&website=[cloud.ckjiexi.com]--2");
            mVideoView.setVideoPath(videoPath);
            //设置媒体控制器
            mVideoView.setMediaController(mediaController);
            mVideoView.requestFocus();
            mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//高品质
            mVideoView.seekTo(seekPos);//根据播放记录跳
            //视频预处理完成之后调用
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //最大范围2.0f
                    mp.setPlaybackSpeed(1.0f);
                    loading.setVisibility(View.GONE);
                    mVideoView.start();
                }
            });
            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    reportError();
                    mVideo_error.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    if (what == 200) {
                        error_text.setText("视频源地址可能失效");
                    } else if (what == 0) {
                        error_text.setText("未知错误");
                    } else if (what == 700) {
                        error_text.setText("无法解码此视频");
                    } else {
                        error_text.setText("视频加载失败");
                    }
                    return false;
                }
            });

            //视频播放完成
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(segIndex < currentVideoSource.size() - 1) { //同一个视频有多个片段
                        segIndex ++;
                        videoPath = currentVideoSource.get(segIndex).url;
                        play(0);
                    } else  if(playIndex < videoList.size() - 1) {//多集
                        getSubscribeQRCode();//先查看订阅作者的二维码

                    }
                }
            });

//            //视频缓冲处理
            mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener(){
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what){
                        //开始缓冲
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            linear_buffer.setVisibility(View.VISIBLE);
                            if(mVideoView.isPlaying()){
                                mVideoView.pause();
                                needResume=true;
                            }
                            break;
                        //缓冲完成继续播放
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            linear_buffer.setVisibility(View.GONE);
                            if(needResume && isPlay){
                               mVideoView.start();
                            }
                            break;
                        //缓冲变化
                        case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                            network_buffer.setText(extra+"K/s");
                            break;
                    }
                    return true;
                }
            });

        }
    }

    private void initview() {
        mVideoView = (VideoView) findViewById(R.id.mVideoView);
        mVideo_error = (LinearLayout) findViewById(R.id.video_error);
        loading = (LinearLayout) findViewById(R.id.loading);
        title_pro = (TextView) findViewById(R.id.title_pro);
        source_pro = (TextView) findViewById(R.id.source_pro);
        mVideoView = (VideoView) findViewById(R.id.mVideoView);

        netWork_pro= (TextView) findViewById(R.id.netWork_pro);
        error_text= (TextView) findViewById(R.id.error_text);
        linear_buffer= (LinearLayout) findViewById(R.id.linear_buffer);
        network_buffer= (TextView) findViewById(R.id.netWork_buffer);
        butttonRetry = (Button) findViewById(R.id.button_retry);
        title_pro.setText(title);
        source_pro.setText("来源:" + source);

        bottomSelection = (LinearLayout) findViewById(R.id.bottom_selection);
        selectionList = (RecyclerViewTV)findViewById(R.id.video_list) ;
        selectionPages = (RecyclerViewTV)findViewById(R.id.video_pages) ;

        subscribeLayout = (RelativeLayout)findViewById(R.id.subscribe_layout);
        subscribeQRCode = (ImageView)  subscribeLayout.findViewById(R.id.qrcode_image);

        pauseLayout = (RelativeLayout)findViewById(R.id.pause_layout);
        pauseQRCode = (ImageView) pauseLayout.findViewById(R.id.pause_qrcode_image);

        butttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVideoSourcePath();
            }
        });
        initVideoList();
        initPageList();
    }
    //选集列表
    private void initVideoList() {
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(this); // 解决快速长按焦点丢失问题.
        linearlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        linearlayoutManager.setSmoothScrollbarEnabled(false);
        selectionList.setLayoutManager(linearlayoutManager);
        selectionList.setFocusable(true);
        selectionList.addItemDecoration(new SpaceItemDecoration((int)getResources().getDimension(R.dimen.w_16)));
        subVideoList = videoList.subList(0,PAGE_SIZE  > videoList.size() ? videoList.size() : PAGE_SIZE );

        listPresenter = new VideoListPresenter(subVideoList,R.drawable.selector_video_play_list,freeStatus);
        listAdapter = new GeneralAdapter(listPresenter);
        selectionList.setAdapter(listAdapter);
        selectionList.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                if(selectionPages.getVisibility() == View.VISIBLE) {
                    resetTextColor();
                }
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
            }
        });
        selectionList.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                if(isPlay) {
                    mVideoView.pause();
                }
                bottomSelection.setVisibility(View.GONE);
                playIndex = position + pageIndex * PAGE_SIZE;
                getVideoSourcePath();
            }
        });

    }

    //选集列表
    private void initPageList() {
        if(videoList == null || videoList.size() < PAGE_SIZE) {
            selectionPages.setVisibility(View.GONE);
            return;
        }
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(this); // 解决快速长按焦点丢失问题.
        linearlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        linearlayoutManager.setSmoothScrollbarEnabled(false);
        selectionPages.setLayoutManager(linearlayoutManager);
        selectionPages.setFocusable(true);
        selectionPages.addItemDecoration(new SpaceItemDecoration((int)getResources().getDimension(R.dimen.w_20)));
        pagerPresenter = new VideoPagerPresenter(videoList == null ? 0 : videoList.size(),PAGE_SIZE);
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
                end = end > videoList.size()  ? videoList.size() : end;

                subVideoList = videoList.subList(start,end);
                listPresenter.setList(subVideoList);
                selectionList.getAdapter().notifyDataSetChanged();
                selectionList.invalidate();

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

    private void getVideoSourcePath() {
        videoId = videoList.get(playIndex).id;
        String url = HttpAddress.getVideoPath(catId,videoId);
        //获取视频地址
        HttpRequest.get(url, null, VideoPlayerActivity.this, "getPathResult", loading,VideoPlayerActivity.this,VideoSourceEntity.class);
        //定时刷新网速
        new Thread(mRunnable).start();
    }

    /** 获取视频源接口返回 */
    public void getPathResult(VideoSourceEntity entity ,String str) {
        if(entity == null) {
            return;
        }
        if(entity.status == false || entity.data == null || entity.data.videoSource == null) {
            if(!TextUtils.isEmpty(entity.msg)) {
                ToastUtil.showShort(this,entity.msg);
            }
            return;
        }
        //播放视频
        currentVideoSource = entity.data.videoSource;
        segIndex = 0;
        long  historyPos = ConfigPreferences.getInstance(this).getVideoPostion(videoId);
        long seekPos = 0;
        if(historyPos > 0) {
            long totalLen = 0;
            for(int i = 0; i < currentVideoSource.size(); i ++) {
                totalLen += Long.parseLong(currentVideoSource.get(i).video);
                if(totalLen >= historyPos) {
                    segIndex = i;
                    seekPos = Long.parseLong(currentVideoSource.get(i).video) - (totalLen - historyPos);
                    break;
                }
            }
        }

        videoPath = currentVideoSource.get(segIndex).url;
        title = entity.data.title;
        play(seekPos);
    }

    Runnable  mRunnable = new Runnable(){
        @Override
        public void run() {
            mHandler.postDelayed(mRunnable, count * 1000);
            Message msg = mHandler.obtainMessage();
            msg.what = 1;
            msg.arg1 = Util.getNetSpeed(count);
            mHandler.sendMessage(msg);
        }
    };

    private void getSubscribeQRCode() {
        HttpRequest.get(HttpAddress.getSubscribe(videoId), null, VideoPlayerActivity.this, "getSubscribeBack", loading,VideoPlayerActivity.this,TrailerEntity.class);
    }

    public void getSubscribeBack(TrailerEntity entity ,String totalResult) {
        if(entity == null || entity.status == false) {
            playIndex++;
            getVideoSourcePath();
        } else {
            subscribeLayout.setVisibility(View.VISIBLE);
            HttpImageAsync.loadingImage(subscribeQRCode,entity.data.erweima);

        }
    }

    private void getPauseQRCode() {
        HttpRequest.get(HttpAddress.getSubscribe(videoId), null, VideoPlayerActivity.this, "getPauseQRCodeBack", loading,VideoPlayerActivity.this,TrailerEntity.class);
    }

    public void getPauseQRCodeBack(TrailerEntity entity ,String totalResult) {
        if(entity == null || entity.status == false) {

        } else {
            pauseLayout.setVisibility(View.VISIBLE);
            HttpImageAsync.loadingImage(pauseQRCode,entity.data.erweima);
        }
    }


    long curPosition = 0;
    int count = 0;
    long startTime;
    long duration;
    Runnable runnable  = new Runnable() {
        @Override
        public void run() {
            mVideoView.seekTo(curPosition);
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(subscribeLayout.getVisibility() == View.VISIBLE) {//关注页面显示，按任意键播放下一集
            subscribeLayout.setVisibility(View.GONE);
            playIndex++;
            getVideoSourcePath();
            return true;
        }
        curPosition = mVideoView.getCurrentPosition();
        duration =  mVideoView.getDuration();
        switch (keyCode) {
            //回车
            case KeyEvent.KEYCODE_ENTER:
                if(bottomSelection.getVisibility() == View.GONE) {
                    if (isPlay) {
                        mVideoView.pause();
                        isPlay = false;
                        getPauseQRCode();
                    } else {
                        mVideoView.start();
                        isPlay = true;
                        pauseLayout.setVisibility(View.GONE);
                    }
                }
                break;

            //左
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(bottomSelection.getVisibility() == View.GONE && pauseLayout.getVisibility() == View.GONE) {
                    if(count == 0 || System.currentTimeMillis() - startTime <= 1000) {
                        startTime = System.currentTimeMillis();
                        count ++;
                    } else if(System.currentTimeMillis() - startTime > 1000) {
                        count = 0;
                    }
                    mediaController.show();
                    curPosition = curPosition - (duration / 100) * (count * count + 1) ;//指数级加速
                    mediaController.setProgress(curPosition);

                    mVideoView.removeCallbacks(runnable);
                    mVideoView.postDelayed(runnable,1000);
                }
                break;

            //右
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(bottomSelection.getVisibility() == View.GONE && pauseLayout.getVisibility() == View.GONE) {
                    if(count == 0 || System.currentTimeMillis() - startTime <= 1000) {
                        startTime = System.currentTimeMillis();
                        count ++;
                    } else if(System.currentTimeMillis() - startTime > 1000) {
                        count = 0;
                    }
                    mediaController.show();
                    curPosition = curPosition + (duration  / 100) * (count * count + 1) ;
                    mediaController.setProgress(curPosition);

                    mVideoView.removeCallbacks(runnable);
                    mVideoView.postDelayed(runnable,1000);
                }
                break;
            //向上键
            case KeyEvent.KEYCODE_DPAD_UP:
                if(!mediaController.isShowing()) {
                    showSelectionLayer();
                }
                break;
            //向下键
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;

            //音量调小键
            case KeyEvent.KEYCODE_VOLUME_DOWN:

                break;
            //音量调大键
            case KeyEvent.KEYCODE_VOLUME_UP:

                break;
            //确认键
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (isPlay) {
                    mVideoView.pause();
                    isPlay = false;
                    getPauseQRCode();
                } else {
                    mVideoView.start();
                    isPlay = true;
                    pauseLayout.setVisibility(View.GONE);
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                showSelectionLayer();
                break;

        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSelectionLayer() {
        mediaController.hide();
        bottomSelection.setVisibility(View.VISIBLE);
        selectionList.setVisibility(View.VISIBLE);
        selectionList.requestFocus();
        Log.d("hjs","getSelectedPosition:" + selectionList.getSelectPostion());
        if(selectionList.getSelectPostion() == -1) {
            selectionList.setDefaultSelect(playIndex);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mVideoView!=null){
            mVideoView.stopPlayback();
        }
        if(MyNetErrorReceiver!=null){
            unregisterReceiver(MyNetErrorReceiver);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        //保存进度
        if(!TextUtils.isEmpty(videoId) && mVideoView != null && mVideoView.getCurrentPosition() != 0) {
            ConfigPreferences.getInstance(VideoPlayerActivity.this).setVideoPostion(videoId, mVideoView.getCurrentPosition());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //float real_data = (float)msg.arg1;
                if (msg.arg1 > 1024) {
                    netWork_pro.setText(msg.arg1 / 1024 + "K/s");
                } else {
                    netWork_pro.setText(msg.arg1 + "B/s");
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void updatePlayTime() {
        if(videoList == null || videoList.size() == 0 || currentVideoSource == null) {
            return;
        }
        long totalTime = mVideoView.getCurrentPosition();//当前片段已播放时间,单位毫秒
        for(int i = 0; i < segIndex; i ++) {
            totalTime += Long.parseLong(currentVideoSource.get(i).video);//之前片段已播放时间
        }
        if(totalTime != 0) {
            ConfigPreferences.getInstance(VideoPlayerActivity.this).setVideoPostion(videoId, totalTime);//保存单位也是毫秒
            HttpRequest.get(HttpAddress.getUpdatePlayTimeUrl(videoId, totalTime / 1000), null, VideoPlayerActivity.this, "getPathResult", loading, VideoPlayerActivity.this, BaseEntity.class);
        }
    }

    public void reportError() {
        HttpRequest.get(HttpAddress.getReportErrorUrl(videoId), null, VideoPlayerActivity.this, "", null, VideoPlayerActivity.this, BaseEntity.class);
    }

    private BroadcastReceiver MyNetErrorReceiver =new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Util.ACTION_HTTP_ONERROR)){
                loading.setVisibility(View.GONE);
                linear_buffer.setVisibility(View.GONE);
                mVideo_error.setVisibility(View.VISIBLE);
                error_text.setText("网络连接异常");
            }
        }
    };

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

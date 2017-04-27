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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import cn.xueximiao.tv.R;
import cn.xueximiao.tv.adapter.VideoListPresenter;
import cn.xueximiao.tv.adapter.VideoPagerPresenter;
import cn.xueximiao.tv.entity.VideoDetailEntity;
import cn.xueximiao.tv.entity.VideoSourceEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpRequest;
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

    private Button error_back;
    private String strVideoDetail;
    private List<VideoDetailEntity.Video> videoList;//专辑详情
    private List<VideoDetailEntity.Video> subVideoList;//专辑详情
    private int playIndex = 0;//当前正在播放的专辑列表索引
    private int segIndex = 0;//同一个视频的片段
    private List<VideoSourceEntity.VideoSource> currentVideoSource;

    private RecyclerViewTV selectionList;
    private RecyclerViewTV selectionPages;
    private VideoListPresenter listPresenter;
    private GeneralAdapter listAdapter;
    private VideoPagerPresenter pagerPresenter;
    private GeneralAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);

        strVideoDetail = getIntent().getStringExtra("videodetail");
        VideoDetailEntity entity = new Gson().fromJson(strVideoDetail,VideoDetailEntity.class);
        if(entity == null) {
            finish();
        }
        title = entity.data.title;
        source = entity.data.source;
        catId = entity.data.id;
        videoList = entity.data.videoList;//专辑列表

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
        super.onBackPressed();
    }

    private void play() {
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
            mVideoView.setHardwareDecoder(true);
            //设置缓冲大小
            mVideoView.setBufferSize(512 * 1024);
            //mVideoView.setVideoPath("http://pl-ali.youku.com/playlist/m3u8?ts=1491379421&keyframe=1&vid=51774769&type=hd2&sid=049137942172720424e20&token=5496&oip=1696944366&did=898d39a045c9ba106aadb7948a82db41&ctype=20&ev=1&ep=8dSpcv4XiMQqYrmnnzZUQtLwiiT4LnBKV%2ByLYxVL26AF5USSf8P4s7i4KEcpvH8L&website=[cloud.ckjiexi.com]--2");
            mVideoView.setVideoPath(videoPath);
            //设置媒体控制器
            mVideoView.setMediaController(mediaController);
            mVideoView.requestFocus();
            mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//高品质
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
                        play();
                    } else  if(playIndex < videoList.size() - 1) {//多集
                        playIndex++;
                        getVideoSourcePath();
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
                            if(needResume){
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
        mVideo_error = (LinearLayout) findViewById(R.id.video_error);
        loading = (LinearLayout) findViewById(R.id.loading);
        title_pro = (TextView) findViewById(R.id.title_pro);
        source_pro = (TextView) findViewById(R.id.source_pro);
        netWork_pro= (TextView) findViewById(R.id.netWork_pro);
        error_text= (TextView) findViewById(R.id.error_text);
        linear_buffer= (LinearLayout) findViewById(R.id.linear_buffer);
        network_buffer= (TextView) findViewById(R.id.netWork_buffer);
        error_back= (Button) findViewById(R.id.back);
        title_pro.setText(title);
        source_pro.setText("来源:" + source);

        bottomSelection = (LinearLayout) findViewById(R.id.bottom_selection);
        selectionList = (RecyclerViewTV)findViewById(R.id.video_list) ;
        selectionPages = (RecyclerViewTV)findViewById(R.id.video_pages) ;

        error_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoPlayerActivity.this.finish();
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
        listPresenter = new VideoListPresenter(subVideoList,R.drawable.selector_video_play_list);
        listAdapter = new GeneralAdapter(listPresenter);
        selectionList.setAdapter(listAdapter);
        selectionList.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
            }
        });
        selectionList.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                playIndex = position;
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
                int start = position * PAGE_SIZE;
                int end = (position + 1) * PAGE_SIZE ;
                end = end > videoList.size()  ? videoList.size() : end;

                subVideoList = videoList.subList(start,end);
                listPresenter.setList(subVideoList);
                selectionList.getAdapter().notifyDataSetChanged();
                selectionList.invalidate();
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
            }
        });

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
        videoPath = currentVideoSource.get(segIndex).url;
        play();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long postion = mVideoView.getCurrentPosition();
        switch (keyCode) {
            //回车
            case KeyEvent.KEYCODE_ENTER:
                if(selectionList.getVisibility() == View.GONE) {
                    if (isPlay) {
                        mVideoView.pause();
                        isPlay = false;
                    } else {
                        mVideoView.start();
                        isPlay = true;
                    }
                }
                break;

            //左
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(selectionList.getVisibility() == View.GONE) {
                    postion = postion - 15000;
                    mVideoView.seekTo(postion);
                    mediaController.setProgress();
                }
                break;

            //右
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(selectionList.getVisibility() == View.GONE) {
                    postion = postion + 15000;
                    mVideoView.seekTo(postion);
                    mediaController.setProgress();
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
                } else {
                    mVideoView.start();
                    isPlay = true;
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
        selectionList.requestFocus();
        Log.d("hjs","getSelectedPosition:" + selectionList.getSelectPostion());
        if(selectionList.getSelectPostion() == -1) {
            selectionList.setDefaultSelect(playIndex);
        }
    }

    private void hideSelectionLayer() {
        bottomSelection.setVisibility(View.GONE);
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
//      ConfigPreferences.getInstance(VideoPlayerActivity.this).setVideoPostion(mVideoView.getCurrentPosition());
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

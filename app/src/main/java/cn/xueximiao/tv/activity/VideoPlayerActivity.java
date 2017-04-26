package cn.xueximiao.tv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import cn.xueximiao.tv.entity.VideoDetailEntity;
import cn.xueximiao.tv.entity.VideoSourceEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpRequest;
import cn.xueximiao.tv.util.SharePrefUtil;
import cn.xueximiao.tv.util.ToastUtil;
import cn.xueximiao.tv.util.Util;
import cn.xueximiao.tv.view.MyMediaController;
import com.umeng.analytics.MobclickAgent;


import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

import static cn.xueximiao.tv.util.Util.count;

/**
 * 播放器
 */
public class VideoPlayerActivity extends BaseActivity {
    //视频路径
    private String videoPath;

    private VideoView mVideoView;

    private LinearLayout mVideo_error;

    private LinearLayout loading;

    private String title;

    private String source;

    private TextView title_pro;

    private TextView source_pro;

    private MyMediaController mediaController;
    //集数
    private int postion;
    //段数
    private int number_segments = 0;

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
    private int playIndex = 0;//当前正在播放的专辑列表索引
    private List<VideoSourceEntity.VideoSource> currentVideoSource;

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
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Util.ACTION_HTTP_ONERROR);
        registerReceiver(MyNetErrorReceiver,intentFilter);

        //播放页是在一个独立的进程，需要再次赋值给auth
        HttpAddress.auth = SharePrefUtil.getString(this,SharePrefUtil.KEY_AUTH,"");

        initview();
        getVideoSourcePath();
    }

    private void play() {
        mediaController = new MyMediaController(VideoPlayerActivity.this, mVideoView, VideoPlayerActivity.this);
        mediaController.setVideoName(title);
        mediaController.show(5000);
        if ("".equals(videoPath) || videoPath == null || videoPath == "") {
            ToastUtil.showShort(VideoPlayerActivity.this, "没有获取到播放源");
            return;
        } else {

            //设置硬件解码
//            mVideoView.setHardwareDecoder(true);
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
                    number_segments++;
//                    LogUtil.e("" + number_segments + "-----" + listVideo.size() + "--------" + mList.size());
//                    if (number_segments > listVideo.size() - 1) { //当前段播放完成
//                        postion++;
//                        number_segments = 0;
//                        LogUtil.e("" + postion);
//                        if (postion > mList.size() - 1) {
//                            ToastUtil.showShort(VideoPlayerActivity.this, "已经是最后一集了");
//                        } else {
//                            nowPlay = mList.get(postion);//获取下一个播放对象;
////                          listVideo = nowPlay.getVideoSource().get(0);
//                            String videoPathId=nowPlay.getId();
//                            HttpRequest.get(HttpAddress.getVideoPath(videoPathId), null, VideoPlayerActivity.this, "getPathResult", null,VideoPlayerActivity.this);
//                        }
//                    }
//                    //未播放完成，使用当前源播放下一个段
//                    VideoPathBean.MsgBean.VideoSourceBean videoBean = listVideo.get(number_segments);//获取播放段
//                    String videoUrl = videoBean.getUrl();
//                    //String videoUrl=mList.get(0);
//                    loading.setVisibility(View.VISIBLE);
//                    title_pro.setText(title);
//                    source_pro.setText("来源:" + source);
//                    mediaController.setVideoName(title);
//                    mVideoView.setVideoPath(videoUrl);
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

        error_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoPlayerActivity.this.finish();
            }
        });
    }

    private void getVideoSourcePath() {

        videoId = videoList.get(playIndex).id;
        String url = HttpAddress.getVideoPath(catId,videoId);
        Log.d("hjs","begin getVideoSource Path：" + url);
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
        videoPath = currentVideoSource.get(0).url;
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
                if (isPlay) {
                    mVideoView.pause();
                    isPlay = false;
                } else {
                    mVideoView.start();
                    isPlay = true;
                }
                break;

            //左
            case KeyEvent.KEYCODE_DPAD_LEFT:
                postion = postion - 15000;
                mVideoView.seekTo(postion);
                mediaController.setProgress();
                break;

            //右
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                postion = postion + 15000;
                mVideoView.seekTo(postion);
                mediaController.setProgress();
                break;

            //音量增加键
            case KeyEvent.KEYCODE_VOLUME_DOWN:

                break;
            //音量减小键
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
                super.openOptionsMenu(); //调用这个就可以弹出菜单
                break;
        }
        return super.onKeyDown(keyCode, event);
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

}

package com.tv.mytv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.tv.mytv.bean.CourseDetailsBean;
import com.tv.mytv.bean.VideoPathBean;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpRequest;
import com.tv.mytv.util.LogUtil;
import com.tv.mytv.util.ToastUtil;
import com.tv.mytv.util.Util;
import com.tv.mytv.view.MyMediaController;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

import static com.tv.mytv.util.Util.count;

/**
 * Created by Administrator on 2016/11/14.
 * 播放器
 */
public class VideoPlayerActivity extends Activity {
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
    //视频总集数List
    private List<CourseDetailsBean.MsgBean.VideoListBean> mList = new ArrayList<CourseDetailsBean.MsgBean.VideoListBean>();

    private boolean isPlay = true;

    private String str;

//    private TextView mediacontroller_time_total;

//    private  int Currnttime;
    //当前集数播放对象
    private  CourseDetailsBean.MsgBean.VideoListBean nowPlay;

    private List<VideoPathBean.MsgBean.VideoSourceBean> listVideo;

  //  private MediaPlayer mediaPlayer;
    //集数ID
    private String id;

    private TextView  netWork_pro;

    private  TextView error_text;

    //正在加载布局
    private  LinearLayout linear_buffer;

    //是否需要自动恢复播放，用于自动暂停，恢复播放
    private boolean needResume;

    private TextView network_buffer;

    private Button error_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
//        videoPath = getIntent().getStringExtra("videoPath");
        title = getIntent().getStringExtra("title");
        source = getIntent().getStringExtra("source");
        postion = getIntent().getIntExtra("postion", 0);

        id = getIntent().getStringExtra("id");

//        mList= (List<CourseDetailsBean.MsgBean.VideoListBean>) getIntent().getSerializableExtra("list");
//        LogUtil.e(videoPath);
        str = getIntent().getStringExtra("list");
//        mediaPlayer = new MediaPlayer(VideoPlayerActivity.this, true);

        //
        Gson gson = new Gson();
        mList = gson.fromJson(str, new TypeToken<List<CourseDetailsBean.MsgBean.VideoListBean>>() {
        }.getType());
        nowPlay = mList.get(postion); //当前播放对象
//      listVideo = nowPlay.getVideoSource().get(0);//当前段集合

        //网络连接失败
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Util.ACTION_HTTP_ONERROR);
        registerReceiver(MyNetErrorReceiver,intentFilter);

        initview();

    }

    private void playfunction() {
        mediaController = new MyMediaController(VideoPlayerActivity.this, mVideoView, VideoPlayerActivity.this);
        mediaController.setVideoName(title);
        mediaController.show(5000);
        if ("".equals(videoPath) || videoPath == null || videoPath == "") {
            ToastUtil.showShort(VideoPlayerActivity.this, "播放失败");
            return;
        } else {

            Logger.e("AAAAAA",videoPath);

            //设置硬件解码
//            mVideoView.setHardwareDecoder(true);
            //设置缓冲大小
            mVideoView.setBufferSize(512 * 1024);
            mVideoView.setVideoPath("http://pl-ali.youku.com/playlist/m3u8?ts=1491379421&keyframe=1&vid=51774769&type=hd2&sid=049137942172720424e20&token=5496&oip=1696944366&did=898d39a045c9ba106aadb7948a82db41&ctype=20&ev=1&ep=8dSpcv4XiMQqYrmnnzZUQtLwiiT4LnBKV%2ByLYxVL26AF5USSf8P4s7i4KEcpvH8L&website=[cloud.ckjiexi.com]--2");
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
                    LogUtil.e("" + number_segments + "-----" + listVideo.size() + "--------" + mList.size());
                    if (number_segments > listVideo.size() - 1) { //当前段播放完成
                        postion++;
                        number_segments = 0;
                        LogUtil.e("" + postion);
                        if (postion > mList.size() - 1) {
                            ToastUtil.showShort(VideoPlayerActivity.this, "已经是最后一集了");
                        } else {
                            nowPlay = mList.get(postion);//获取下一个播放对象;
//                          listVideo = nowPlay.getVideoSource().get(0);
                            String videoPathId=nowPlay.getId();
                            HttpRequest.get(HttpAddress.getVideoPath(videoPathId), null, VideoPlayerActivity.this, "getPathResult", null,VideoPlayerActivity.this);
                        }
                    }
                    //未播放完成，使用当前源播放下一个段
                    VideoPathBean.MsgBean.VideoSourceBean videoBean = listVideo.get(number_segments);//获取播放段
                    String videoUrl = videoBean.getUrl();
                    //String videoUrl=mList.get(0);
                    loading.setVisibility(View.VISIBLE);
                    title_pro.setText(title);
                    source_pro.setText("来源:" + source);
                    mediaController.setVideoName(title);
                    mVideoView.setVideoPath(videoUrl);
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
//        此处的   mymediacontroller  为我们自定义控制器的布局文件名称
//        View view_voice = View.inflate(VideoPlayerActivity.this, R.layout.mymediacontroller, null);
        mVideoView = (VideoView) findViewById(R.id.mVideoView);
        mVideo_error = (LinearLayout) findViewById(R.id.video_error);
        loading = (LinearLayout) findViewById(R.id.loading);
        title_pro = (TextView) findViewById(R.id.title_pro);
        source_pro = (TextView) findViewById(R.id.source_pro);
//      mediacontroller_time_total = (TextView) view_voice.findViewById(mediacontroller_time_total);
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
        //获取视频地址
        HttpRequest.get(HttpAddress.getVideoPath(id), null, VideoPlayerActivity.this, "getPathResult", null,VideoPlayerActivity.this);
        //定时刷新网速
        new Thread(mRunnable).start();

        error_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoPlayerActivity.this.finish();
            }
        });
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

    public void getPathResult(String str) {
        LogUtil.i(str);
        if (!TextUtils.isEmpty(str)) {
            Gson gson = new Gson();
            VideoPathBean bean = gson.fromJson(str, VideoPathBean.class);
            List<VideoPathBean.MsgBean> list = bean.getMsg();
            VideoPathBean.MsgBean msgBean = list.get(0);
            title = msgBean.getTitle();
            source = msgBean.getSource();
            //拿到Video集合
            List<List<VideoPathBean.MsgBean.VideoSourceBean>> mDataList = msgBean.getVideoSource();
            //拿到Video视频集合
            listVideo = mDataList.get(0);
//            ToastUtil.showShort(VideoPlayerActivity.this,listVideo.size()+"");
            if(listVideo.size()<1){
                mVideo_error.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }
            //拿到当前格式视频集合
            VideoPathBean.MsgBean.VideoSourceBean dataBean = listVideo.get(0);
            videoPath = dataBean.getUrl();
            //播放视频
            playfunction();
        } else {
            ToastUtil.showShort(VideoPlayerActivity.this, "数据返回错误");
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

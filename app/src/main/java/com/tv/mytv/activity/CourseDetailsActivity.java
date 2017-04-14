package com.tv.mytv.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tv.mytv.R;
import com.tv.mytv.adapter.CourseGridApapter;
import com.tv.mytv.bean.CourseDetailsBean;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpImageAsync;
import com.tv.mytv.http.HttpRequest;
import com.tv.mytv.util.LogUtil;
import com.tv.mytv.util.ToastUtil;
import com.tv.mytv.util.Util;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 * 详情页
 */
public class CourseDetailsActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "CourseDetailsActivity";

    private GridView mGridView;

    private CourseGridApapter mAdapter;

    private List<CourseDetailsBean.MsgBean.VideoListBean> myList = new ArrayList<CourseDetailsBean.MsgBean.VideoListBean>();

    private TextView course_play, course_collection, briefintroduction;

//    private static final String VideoPath = "http://v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4";

    private ImageView largeImg;

    private String videoId;

    private TextView course_title;

    private TextView Hits, type, playCount, school, source;

    private String catId;

    private ProgressBar mProgressBar;

    private RelativeLayout course_main;

    //网速显示
    private TextView netWork;
    //几秒刷新一次
    private final int count = 1;

    private  LinearLayout onError_text;

    private Button back;

    private LinearLayout loading;

    private  String mList_strjson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetails);
        videoId = getIntent().getStringExtra("id");
        catId = getIntent().getStringExtra("catId");

        //网络连接失败
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Util.ACTION_HTTP_ONERROR);
        registerReceiver(MyNetErrorReceiver,intentFilter);

        initview();
    }

    /**
     * 初始化控件
     */
    private void initview() {
        mGridView = (GridView) findViewById(R.id.course_gridview);
        course_play = (TextView) findViewById(R.id.play);
        course_collection = (TextView) findViewById(R.id.collection);
        largeImg = (ImageView) findViewById(R.id.largeImg);
        course_title = (TextView) findViewById(R.id.course_title);
        briefintroduction = (TextView) findViewById(R.id.briefintroduction);
        playCount = (TextView) findViewById(R.id.playCount);
        source = (TextView) findViewById(R.id.source);
        school = (TextView) findViewById(R.id.school);
        Hits = (TextView) findViewById(R.id.Hits);
        type = (TextView) findViewById(R.id.type);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        course_main = (RelativeLayout) findViewById(R.id.course_main);
        netWork = (TextView) findViewById(R.id.netWork);
        onError_text= (LinearLayout) findViewById(R.id.LinearLayout_error);
        back = (Button) findViewById(R.id.back);
        loading= (LinearLayout) findViewById(R.id.LinearLayout_loading);
        course_play.setOnClickListener(CourseDetailsActivity.this);
        course_collection.setOnClickListener(CourseDetailsActivity.this);
        mGridView.setOnItemClickListener(CourseDetailsActivity.this);
        back.setOnClickListener(CourseDetailsActivity.this);
        getData();
        //定时刷新网速
        new Thread(mRunnable).start();
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

    private void getData() {
        String url = HttpAddress.getVideoDetails(catId, videoId);
        HttpRequest.get(url, null, CourseDetailsActivity.this, "thisData", null,CourseDetailsActivity.this);
    }

    public void thisData(String str) {
        mProgressBar.setVisibility(View.GONE);
        netWork.setVisibility(View.GONE);
        LogUtil.i(str);
        if (!TextUtils.isEmpty(str)) {
            Gson gson = new Gson();
            CourseDetailsBean bean = gson.fromJson(str, CourseDetailsBean.class);
            if (bean.isStatus()) {
                CourseDetailsBean.MsgBean msgBean = bean.getMsg();
                String imageurl = msgBean.getThumb();
                String title = msgBean.getTitle();
                String descript = msgBean.getDescript();
                course_title.setText(title);
                Hits.setText("播放:" + msgBean.getHits() + "次");
                type.setText("类型:" + msgBean.getType());
                briefintroduction.setText("简介:" + descript);
                playCount.setText("集数:" + msgBean.getPlayCount() + "集");
                school.setText("学校:" + msgBean.getSchool());
                myList = msgBean.getVideoList();
                mAdapter = new CourseGridApapter(myList, CourseDetailsActivity.this);
                mGridView.setAdapter(mAdapter);
                HttpImageAsync.loadingImage(largeImg, imageurl);
                CourseDetailsBean.MsgBean.VideoListBean videoBean = myList.get(0);
                String source_str = videoBean.getSource();
                source.setText("来源:" + source_str);
                course_main.setVisibility(View.VISIBLE);
                mList_strjson = gson.toJson(myList);
//              ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
////       Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
////which implements ImageAware interface)
//                imageLoader.displayImage(imageurl, largeImg);
////Load image, decode it to Bitmap and return Bitmap to callback
//                imageLoader.loadImage(imageurl, new SimpleImageLoadingListener() {
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        // Do whatever you want with Bitmap
//                        ToastUtil.showShort(CourseDetailsActivity.this,"图片加载完成");
//                    }
//                });
            } else {
                course_main.setVisibility(View.GONE);
                ToastUtil.showShort(CourseDetailsActivity.this, "数据获取失败");
            }
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.play:
                final CourseDetailsBean.MsgBean.VideoListBean bean = myList.get(0);
//                List<String> mList = bean.getVideoSource();
                List<List<CourseDetailsBean.MsgBean.VideoListBean.VideoSourceBean>> mList = bean.getVideoSource();
                String id = bean.getId();
                if (mList != null && !mList.isEmpty()) {
//                    String videoUrl = mList.get(0);
                    List<CourseDetailsBean.MsgBean.VideoListBean.VideoSourceBean> listVideo = mList.get(0);
                    CourseDetailsBean.MsgBean.VideoListBean.VideoSourceBean videoBean = listVideo.get(0);
                    String videoUrl = videoBean.getUrl();
                    intent = new Intent(CourseDetailsActivity.this, VideoPlayerActivity.class);
//                    intent.putExtra("videoPath",videoUrl);
                    intent.putExtra("title",bean.getTitle());
                    intent.putExtra("source",bean.getSource());
                    intent.putExtra("postion", 0);
                    intent.putExtra("list", mList_strjson);
                    intent.putExtra("id", bean.getId());

                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("videoNumber","1");
                    //统计发生次数
                    MobclickAgent.onEvent(CourseDetailsActivity.this,"PlayVideo"+bean.getTitle(),map);

                    HttpRequest.get(HttpAddress.postCount(id), null, CourseDetailsActivity.this, "getData", null,CourseDetailsActivity.this);
                    startActivity(intent);
                } else {
                    ToastUtil.showShort(CourseDetailsActivity.this, "视频地址错误");
                }
                break;
            case R.id.collection:

                break;
            case R.id.back:
                CourseDetailsActivity.this.finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//        Bundle bundle=new Bundle();
        final CourseDetailsBean.MsgBean.VideoListBean bean = myList.get(i);
        List<List<CourseDetailsBean.MsgBean.VideoListBean.VideoSourceBean>> mList = bean.getVideoSource();
//        List<String> mList = bean.getVideoSource();
//        String id = bean.getId();
        if (mList != null && !mList.isEmpty()) {
//            String videoUrl = mList.get(0);
//            List<CourseDetailsBean.MsgBean.VideoListBean.VideoSourceBean> listVideo = mList.get(0);
//            CourseDetailsBean.MsgBean.VideoListBean.VideoSourceBean videoBean = listVideo.get(0);
//            String videoUrl = videoBean.getUrl();
            Intent intent = new Intent(CourseDetailsActivity.this, VideoPlayerActivity.class);
//            intent.putExtra("videoPath",videoUrl);
            intent.putExtra("title",bean.getTitle());
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("videoNumber",""+i);
            map.put("title",bean.getTitle());
            //统计发生次数
            MobclickAgent.onEvent(CourseDetailsActivity.this,"PlayVideo",map);
            intent.putExtra("source",bean.getSource());
            intent.putExtra("postion", i);
            intent.putExtra("list", mList_strjson);
            intent.putExtra("id", bean.getId());
//            HttpRequest.get(HttpAddress.postCount(id), null, CourseDetailsActivity.this, "getData",null);
            startActivity(intent);
        }
    }

    public void getData(String str) {
        try {
            JSONObject obj = new JSONObject(str);
            Boolean stutas = obj.optBoolean("status");
            if (stutas) {
                LogUtil.i("数据统计成功");
            } else {
                LogUtil.i("数据统计失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //float real_data = (float)msg.arg1;
                if (msg.arg1 > 1024) {
                    netWork.setText(msg.arg1 / 1024 + "K/s");
                } else {
                    netWork.setText(msg.arg1 + "B/s");
                }
            }
        }
    };

    private BroadcastReceiver MyNetErrorReceiver =new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Util.ACTION_HTTP_ONERROR)){
                loading.setVisibility(View.GONE);
                onError_text.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
        if(MyNetErrorReceiver!=null){
            unregisterReceiver(MyNetErrorReceiver);
        }
        LogUtil.i("onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        LogUtil.i("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        LogUtil.i("onPause");
        onError_text.setVisibility(View.GONE);
    }
}

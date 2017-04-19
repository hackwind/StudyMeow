package com.tv.mytv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tv.mytv.R;
import com.tv.mytv.entity.GetLoginInfoEntity;
import com.tv.mytv.entity.GetQRCodeEntity;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpImageAsync;
import com.tv.mytv.http.HttpRequest;
import com.tv.mytv.util.Util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/17.
 */

public class LoginActivity extends AppCompatActivity {
    private final static int REPEAT_INTERVAL = 2000;
    private ImageView wxImage;
    private int sessionId;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        wxImage = (ImageView)findViewById(R.id.wechat_image);

        getQRCode();
        //网络连接失败
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Util.ACTION_HTTP_ONERROR);
        registerReceiver(MyNetErrorReceiver,intentFilter);

    }
    private BroadcastReceiver MyNetErrorReceiver =new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Util.ACTION_HTTP_ONERROR)){
                Log.d("hjs","http error");
                startTimer();
            }
        }
    };


    private void getQRCode() {
        HttpRequest.get(HttpAddress.getQRCode(),null,LoginActivity.this,"getQRCodeBack",null,this, GetQRCodeEntity.class);
    }

    private void getLoginInfoRepeat() {
        HttpRequest.get(HttpAddress.getWhetherLogin(sessionId),null,LoginActivity.this,"getLoginInfoBack",null,this, GetLoginInfoEntity.class);
    }

    public void getQRCodeBack(GetQRCodeEntity entity,String totalResult) {
        if(entity == null || entity.data == null || entity.data.erweima == null) {
            return;
        }
        sessionId = entity.data.sessionid;
        HttpImageAsync.loadingImage(wxImage,entity.data.erweima);

        startTimer();

    }

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            getLoginInfoRepeat();
        }
    };

    public void getLoginInfoBack(GetLoginInfoEntity entity,String totalResult) {
        if(entity != null && entity.status == true) {
            timer.cancel();
            Intent intent = new Intent(this,MyActivity.class);
            startActivity(intent);
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(task, REPEAT_INTERVAL);
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
}

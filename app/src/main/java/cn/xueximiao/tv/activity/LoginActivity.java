package cn.xueximiao.tv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.GetLoginInfoEntity;
import cn.xueximiao.tv.entity.GetQRCodeEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpImageAsync;
import cn.xueximiao.tv.http.HttpRequest;
import cn.xueximiao.tv.util.SharePrefUtil;
import cn.xueximiao.tv.util.Util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/17.
 */

public class LoginActivity extends BaseActivity {
    private final static int REPEAT_INTERVAL = 2000;
    private ImageView wxImage;
    private int sessionId;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        HttpRequest.get(HttpAddress.getLoginQRCode(),null,LoginActivity.this,"getQRCodeBack",null,this, GetQRCodeEntity.class);
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

    public void getLoginInfoBack(GetLoginInfoEntity entity,String totalResult) {
        if(entity != null && entity.status == true) {
            timer.cancel();
            HttpAddress.auth = entity.data.auth;//替换token
            //保存其他用户信息
            SharePrefUtil.saveString(this,SharePrefUtil.KEY_USER_ID,entity.data.userid);
            SharePrefUtil.saveString(this,SharePrefUtil.KEY_NICK_NAME,entity.data.nickname);
            SharePrefUtil.saveString(this,SharePrefUtil.KEY_AUTH,entity.data.auth);
            SharePrefUtil.saveString(this,SharePrefUtil.KEY_THUMB,entity.data.thumb);
            SharePrefUtil.saveString(this,SharePrefUtil.KEY_USER_NAME,entity.data.username);
            SharePrefUtil.saveString(this,SharePrefUtil.KEY_GROUP_ID,entity.data.groupid);
            SharePrefUtil.saveString(this,SharePrefUtil.KEY_REG_DATE,entity.data.regdate);
            Intent intent = new Intent(this,MyActivity.class);
            startActivity(intent);
            finish();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getLoginInfoRepeat();
            }
        }, REPEAT_INTERVAL);
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

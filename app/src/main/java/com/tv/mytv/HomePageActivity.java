package com.tv.mytv;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tv.mytv.bean.MenuList;
import com.tv.mytv.fragment.VedioListFragment;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpRequest;
import com.tv.mytv.util.LogUtil;
import com.tv.mytv.util.SharePrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import reco.frame.tv.view.TvTabHost;

/**
 * Created by Administrator on 2016/12/28
 * 首页
 */
public class HomePageActivity extends FragmentActivity {

    private List<MenuList.MsgBean> msg;

    private String menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        initData();
    }

    private void loadFrag(){
        /**
         * 添加页面
         */
        TvTabHost tth_container = (TvTabHost) findViewById(R.id.tth_container);
        for (int i = 0; i < msg.size(); i++){
            VedioListFragment vedioListFragment = new VedioListFragment();
            vedioListFragment.setcatid(msg.get(i).getCatid());
            tth_container.addPage(getSupportFragmentManager(), vedioListFragment,
                    msg.get(i).getCatname());
            LogUtil.e(msg.get(i).getCatname());
        }
        tth_container.buildLayout();
        /**
         *设监听
         */
        tth_container.setOnPageChangeListener(new TvTabHost.ScrollPageChangerListener() {
            @Override
            public void onPageSelected(int pageCurrent) {
                LogUtil.i("第 " + (pageCurrent + 1) + " 页");
            }
        });
        /**
         * 页面跳转
         */
        tth_container.setCurrentPage(0);
    }

    private void initData(){
        menu = SharePrefUtil.getString(HomePageActivity.this,"menu","");
        if (!TextUtils.isEmpty(menu)){
            onSuccess(menu);
        }
        HttpRequest.get(HttpAddress.MENU_URL,null,HomePageActivity.this,"MyResult",null,HomePageActivity.this);
    }

    public void MyResult(String result) {
        LogUtil.i(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("status")) {
                SharePrefUtil.saveString(HomePageActivity.this, "menu", result);
                Gson gson = new Gson();
                MenuList menuList = gson.fromJson(result, MenuList.class);
                msg = menuList.getMsg();
                if (TextUtils.isEmpty(menu)) {
                    loadFrag();
                }
            } else {
                Toast.makeText(HomePageActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e){
            e.printStackTrace();
       }
   }

    private void onSuccess(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("status")) {
                Gson gson = new Gson();
                MenuList menuList = gson.fromJson(result, MenuList.class);
                msg = menuList.getMsg();
                loadFrag();
            } else {
                Toast.makeText(HomePageActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

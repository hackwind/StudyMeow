package com.tv.mytv.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tv.mytv.R;
import com.tv.mytv.adapter.MenuAdapter;
import com.tv.mytv.bean.MenuList;
import com.tv.mytv.fragment.MainFragment;
import com.tv.mytv.fragment.VedioListFragment;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpRequest;
import com.tv.mytv.util.LogUtil;
import com.tv.mytv.util.SharePrefUtil;
import com.tv.mytv.view.MyListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Fragment[] fragments;
    private int currentTabIndex;
    private MyListView menuListView;
    private List<MenuList.MsgBean> msg;
    private MenuAdapter menuAdapter;
    private String menu;
    private int keyBackClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState){
            // 隐藏标题栏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            // 隐藏状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_main);
            initViews();
            initData();
        }
    }

    private void initViews(){
        menuListView = (MyListView) findViewById(R.id.lv_menu);
        menuListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeFragment(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        menuListView.setOnItemSelectListener(new TvListView.OnItemSelectListener() {
//
//            @Override
//            public void onItemSelect(View item, int position) {
//                changeFragment(position);
//                tv_menu.setText(msg.get(position).getCatname());
//            }
//
//            @Override
//            public void onItemDisSelect(View item, int position) {
//
//            }
//        });
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeFragment(position);
            }
        });

    }

    private void initData() {
//        menu = SharePrefUtil.getString(MainActivity.this, "menu", "");
        menu = "{\"status\":true,\"msg\":[{\"catid\":\"1\",\"catname\":\"我的\",\"resId\":" + R.drawable.selector_menu_my + "},{\"catid\":\"2\",\"catname\":\"分类\",\"resId\":" + R.drawable.selector_menu_category + "},{\"catid\":\"3\",\"catname\":\"设置\",\"resId\":" + R.drawable.selector_menu_setup +"}]}";
        if (!TextUtils.isEmpty(menu)) {
            onSuccess(menu);
        }
//        HttpRequest.get(HttpAddress.MENU_URL,null,MainActivity.this,"MyResult",null,MainActivity.this);

    }

    public void MyResult(String result){
        LogUtil.i(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("status")){
                SharePrefUtil.saveString(MainActivity.this, "menu", result);
                Gson gson = new Gson();
                MenuList menuList = gson.fromJson(result, MenuList.class);
                msg = menuList.getMsg();
                if (TextUtils.isEmpty(menu)){
                    menuAdapter = new MenuAdapter(MainActivity.this, msg);
                    menuListView.setAdapter(menuAdapter);
                    menuListView.setItemChecked(0,true);
                    initFragments();
                }else {
                    menuAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(MainActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
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
                menuAdapter = new MenuAdapter(MainActivity.this, msg);
                menuListView.setAdapter(menuAdapter);
                menuListView.setItemChecked(0,true);
                if(menuListView.getChildCount() > 0) {
                    menuListView.getChildAt(0).requestFocus();
                }
                initFragments();
            } else {
                Toast.makeText(MainActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragments = new Fragment[msg.size()];
        for (int i = 0; i < msg.size(); i++) {
            MainFragment vedioListFragment = new MainFragment();
//            vedioListFragment.setcatid(msg.get(i).getCatid());
            if (i == 0) {
                ft.add(R.id.fragment, vedioListFragment).show(vedioListFragment).commit();
            }
            fragments[i] = vedioListFragment;
        }
    }

    public void changeFragment(int index) {
        if (currentTabIndex != index) {
            FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
            fragmentManager.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                fragmentManager.add(R.id.fragment, fragments[index]);
            }
            fragmentManager.show(fragments[index]).commit();
            currentTabIndex = index;
        }
    }

    /**
     *退出程序
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyBackClickCount++){
                case 0:
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:
                    MobclickAgent.onKillProcess(MainActivity.this);//保存用户统计数据
                    // 退出程序
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                    this.finish();
                    break;
                default:
                    break;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

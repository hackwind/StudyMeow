package cn.xueximiao.tv.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.menu.OpenMenuImpl;
import com.open.androidtvwidget.view.MainUpView;

import java.util.List;

import cn.xueximiao.tv.R;
import cn.xueximiao.tv.adapter.FreeMenuPresenter;
import cn.xueximiao.tv.adapter.MenuAdapter;
import cn.xueximiao.tv.entity.CategoryEntity;
import cn.xueximiao.tv.fragment.VideoListFragment;
import cn.xueximiao.tv.util.SharePrefUtil;
import cn.xueximiao.tv.util.ToastUtil;
import cn.xueximiao.tv.widget.MyListView;

/**
 * Created by Administrator on 2017/4/29/029.
 */

public class VideoListActivity extends FragmentActivity {
    private static String catId;
    private RecyclerViewTV freeMenu;
    private MyListView leftMenu;
    private TextView categoryName;
    private TextView pageCountView;
    private MenuAdapter menuAdapter;

    private static boolean forFree = false;
    private VideoListFragment[] fragments;

    private int currentTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        catId = getIntent().getStringExtra("catid");
        initFreeMenu();
        initViews();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {

        return super.dispatchKeyEvent(event);
    }

    private void initViews(){
        categoryName = (TextView)findViewById(R.id.category_name) ;
        pageCountView = (TextView)findViewById(R.id.category_page) ;

        leftMenu = (MyListView) findViewById(R.id.lv_menu);

        final CategoryEntity categoryEntity = getCacheCategory();
        if(categoryEntity == null || categoryEntity.data == null) {
            return;
        }
        leftMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("hjs","left menu onItemSelected");
                for(int i = 0 ; i < fragments.length;i++) {
                    fragments[i].setCurrentMenuView(view);
                }
                if(currentTabIndex != position) {
                    changeFragment(position);
                    categoryName.setText(categoryEntity.data.category.get(position).catname);
                }
                for(int i = 0 ;i < leftMenu.getChildCount(); i ++) {
                    leftMenu.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                view.setBackgroundResource(R.drawable.left_menu_checkde);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("hjs","left menu onNothingSelected");
            }
        });
        menuAdapter = new MenuAdapter(VideoListActivity.this, categoryEntity.data.category);
        leftMenu.setAdapter(menuAdapter);
        leftMenu.setSelection(currentTabIndex);
        initFragments(categoryEntity.data.category);
    }

    private CategoryEntity getCacheCategory() {
        final String category = SharePrefUtil.getString(this, "category", "");
        if (TextUtils.isEmpty(category)) {
            return null;
        }
        Gson gson = new Gson();
        final CategoryEntity categoryEntity = gson.fromJson(category, CategoryEntity.class);
        if (categoryEntity == null) {
            ToastUtil.showShort(this, "解析栏目分类出错");
            return null;
        }
        return categoryEntity;
    }

    public void changeFragment(int index) {
        if (currentTabIndex != index) {
            FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
            fragmentManager.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                fragmentManager.add(R.id.fl_video_list, fragments[index]);
            }
            fragmentManager.show(fragments[index]).commit();
            currentTabIndex = index;
        }
    }

    private void initFreeMenu() {
        OpenMenuImpl openMenu = new OpenMenuImpl();
        openMenu.add("全部");
        openMenu.add("免费");
        final MainUpView mainUpView = new MainUpView(this);
        mainUpView.setEffectBridge(new OpenEffectBridge());
        mainUpView.setUpRectResource(R.drawable.left_menu_bg_selector);
        freeMenu = (RecyclerViewTV) findViewById(R.id.free_menu);

        LinearLayoutManagerTV managerMenu = new LinearLayoutManagerTV(this);
        managerMenu.setOrientation(LinearLayoutManager.HORIZONTAL);
        managerMenu.setSmoothScrollbarEnabled(false);
        freeMenu.setLayoutManager(managerMenu);
        final GeneralAdapter menuAdapter = new GeneralAdapter(new FreeMenuPresenter(freeMenu, openMenu));
        freeMenu.setAdapter(menuAdapter);
        freeMenu.setItemAnimator(new DefaultItemAnimator());
        freeMenu.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {

            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                boolean oldFree = forFree;
                if(position == 0) {
                    forFree = false;
                } else {
                    forFree = true;
                }
                if(oldFree == forFree) {//没有发生改变
                    return;
                }
                TextView allButton = (TextView)(freeMenu.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.title_tv));
                TextView freeButton = (TextView)(freeMenu.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.title_tv));
                if(forFree) {
                    allButton.setTextColor(getResources().getColor(R.color.trans_white));
                    freeButton.setTextColor(getResources().getColor(R.color.selector));
                } else {
                    allButton.setTextColor(getResources().getColor(R.color.selector));
                    freeButton.setTextColor(getResources().getColor(R.color.trans_white));
                }
                for(int i = 0; i < fragments.length;i ++) {
                    fragments[i].setFree(forFree);
                }
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

            }
        });
        freeMenu.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                Intent intent = new Intent(VideoListActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        freeMenu.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView allButton = (TextView)(freeMenu.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.title_tv));
                TextView freeButton = (TextView)(freeMenu.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.title_tv));
                allButton.setTextColor(getResources().getColor(R.color.selector));
                freeButton.setTextColor(getResources().getColor(R.color.trans_white));
            }
        },500);
    }

    private void initFragments(List<CategoryEntity.Category> msg) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragments = new VideoListFragment[msg.size()];
        for (int i = 0; i < msg.size(); i++) {
            VideoListFragment videoListFragment = VideoListFragment.newInstance(msg.get(i).catid,msg.get(i).catname,msg.get(i).icon);

            if (msg.get(i).catid.equals(catId)) {
                Log.d("hjs","match the catid position:" + i);
                currentTabIndex = i;
                categoryName.setText(msg.get(i).catname);
                ft.add(R.id.fl_video_list, videoListFragment).show(videoListFragment).commit();
            }
            fragments[i] = videoListFragment;
        }
    }
}

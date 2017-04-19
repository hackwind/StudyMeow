package com.tv.mytv.activity;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.menu.OpenMenuImpl;
import com.open.androidtvwidget.view.MainUpView;
import com.tv.mytv.R;
import com.tv.mytv.entity.CategoryEntity;
import com.tv.mytv.entity.ListEntity;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpRequest;
import com.tv.mytv.util.SharePrefUtil;
import com.tv.mytv.util.ToastUtil;
import com.tv.mytv.util.Util;
import com.tv.mytv.widget.MyOpenMenuImpl;
import com.tv.mytv.widget.SpaceItemDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.FreeMenuPresenter;
import adapter.ListMenuPresenter;
import adapter.VideoListRecyclerViewPresenter;

/**
 * Created by Administrator on 2017/4/17.
 */

public class ListActivity extends AppCompatActivity {

    private RecyclerViewTV leftMenu;
    private RecyclerViewTV freeMenu;
    private RecyclerViewTV contentView;
    private GeneralAdapter mMyGeneralAdapter;
    private MainUpView mainUpView1;
    private RecyclerViewBridge mRecyclerViewBridge;
    private VideoListRecyclerViewPresenter mMyRecyclerViewPresenter;

    private TextView categoryName;
    private TextView pageCountView;
    private View oldView;

    private static String catId;
    private String catName;
    private int pageCount = 0;
    private int rowCount = 0;
    private int page = 1;
    private int total = 0;
    private static int pageSize = 50;
    private int leftMenuSelectedIndex = 0;
    private int defaultPos = 0;
    private static boolean forFree = false;
    private static int ROW_SIZE = 5;

    private List<ListEntity.VideoRow> videoList;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_list);
        catId = getIntent().getStringExtra("catid");
        catName = getIntent().getStringExtra("catname");

        //网络连接失败
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Util.ACTION_HTTP_ONERROR);
        registerReceiver(MyNetErrorReceiver,intentFilter);

        initViews();
        getPageData();
    }
    private BroadcastReceiver MyNetErrorReceiver =new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Util.ACTION_HTTP_ONERROR)){
                Log.d("hjs","http error");
                loading = false;
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(MyNetErrorReceiver);
    }

    private void initViews(){
        categoryName = (TextView)findViewById(R.id.category_name) ;
        pageCountView = (TextView)findViewById(R.id.category_page) ;
        categoryName.setText(catName);

        initLeftMenu();
        initFreeMenu();
    }

    private void initLeftMenu() {
        final String category = SharePrefUtil.getString(this,"category","");
        if(TextUtils.isEmpty(category)) {
            return;
        }
        Gson gson = new Gson();
        final CategoryEntity categoryEntity = gson.fromJson(category, CategoryEntity.class);
        if(categoryEntity == null) {
            ToastUtil.showShort(this,"解析栏目分类出错");
            return;
        }

        MyOpenMenuImpl openMenu = new MyOpenMenuImpl();
        int i = 0;
        for(CategoryEntity.Category cat : categoryEntity.data.category){
            openMenu.add(cat.catname,cat.icon);
            if(catId.equals(cat.catid)) {
                Log.d("hjs","id equal:" + i);
                defaultPos = i;
            }
            i ++;
        }

        final MainUpView mainUpView = new MainUpView(this);
        mainUpView.setEffectBridge(new OpenEffectBridge());
        mainUpView.setUpRectResource(R.drawable.left_menu_bg_selector);
        leftMenu = (RecyclerViewTV) findViewById(R.id.lv_menu);

        LinearLayoutManagerTV managerMenu = new LinearLayoutManagerTV(this);
        managerMenu.setOrientation(LinearLayoutManager.VERTICAL);
        managerMenu.setSmoothScrollbarEnabled(false);
        leftMenu.setLayoutManager(managerMenu);
        GeneralAdapter menuAdapter = new GeneralAdapter(new ListMenuPresenter(leftMenu, openMenu));
        leftMenu.setAdapter(menuAdapter);
        leftMenu.setItemAnimator(new DefaultItemAnimator());

        leftMenu.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
                // 传入 itemView也可以, 自己保存的 oldView也可以.
//                if(mainUpView != null) {
//                    mainUpView.setUnFocusView(itemView);
//                }
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                leftMenuSelectedIndex = position;
                oldView = itemView;

                for(int i = 0 ;i < leftMenu.getChildCount(); i ++) {
                    leftMenu.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                leftMenu.getChildAt(leftMenuSelectedIndex).setBackgroundResource(R.drawable.left_menu_checkde);

                CategoryEntity.Category category = categoryEntity.data.category.get(position);
                if(!catId.equals(category.catid) ) {
                    catId = category.catid;
                    catName = category.catname;
                    page = 1;
                    total = 0;
                    pageCount = 0;
                    videoList = null;
                    categoryName.setText(catName);
                    getPageData();
                }
            }

            /**
             * 这里是调整开头和结尾的移动边框.
             */
            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
//                if(mainUpView != null) {
//                    mainUpView.setFocusView(itemView, 1.0f);
//                }
                oldView = itemView;
            }
        });
        leftMenu.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                // 测试.点击效果，实际电视没有点击
//                if(mainUpView != null) {
//                    mainUpView.setFocusView(itemView, oldView, 1.0f);
//                }
                oldView = itemView;
                //
//                onViewItemClick(itemView, position,true);
            }
        });
        leftMenu.clearFocus();
        leftMenu.postDelayed(new Runnable() {
            @Override
            public void run() {
                leftMenu.setDefaultSelect(defaultPos);
            }
        },200);
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
                if(position == 0) {
                    forFree = false;
                } else {
                    forFree = true;
                }
                getPageData();
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

            }
        });
        freeMenu.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                Intent intent = new Intent(ListActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initContentView() {
        if(videoList == null) {
            return;
        }
        contentView = (RecyclerViewTV)findViewById(R.id.recyclerview_content);
        mainUpView1 = (MainUpView)findViewById(R.id.mainUpView1);
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, ROW_SIZE);
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        contentView.setLayoutManager(gridlayoutManager);
        contentView.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.h_64),ROW_SIZE,total));
        contentView.setFocusable(false);
        contentView.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mMyRecyclerViewPresenter = new VideoListRecyclerViewPresenter(videoList);
        mMyGeneralAdapter = new GeneralAdapter(mMyRecyclerViewPresenter);
        contentView.setAdapter(mMyGeneralAdapter);

        mainUpView1.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView1.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.select_cover);
        //87 是左边间距 23是绿色框的厚度
        RectF receF = new RectF(getResources().getDimension(R.dimen.w_87) + getResources().getDimension(R.dimen.w_23), getResources().getDimension(R.dimen.h_29)+ getResources().getDimension(R.dimen.h_23) ,
                getResources().getDimension(R.dimen.w_87)+ getResources().getDimension(R.dimen.w_23) , getResources().getDimension(R.dimen.h_89)+ getResources().getDimension(R.dimen.h_23) );
        mRecyclerViewBridge.setDrawUpRectPadding(receF);
        //防止切换焦点时，亮框移动幅度太大
        mRecyclerViewBridge.setOnAnimatorListener(new OpenEffectBridge.NewAnimatorListener() {
            @Override
            public void onAnimationStart(OpenEffectBridge bridge, View view,
                                         Animator animation) {
                mRecyclerViewBridge.setVisibleWidget(false);
            }

            @Override
            public void onAnimationEnd(OpenEffectBridge bridge, View view,
                                       Animator animation) {
                if (view.hasFocus())
                    mRecyclerViewBridge.setVisibleWidget(false);
            }
        });

        //
        contentView.setOnItemListener(new RecyclerViewTV.OnItemListener() {

            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
                mRecyclerViewBridge.setUnFocusView(itemView);
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                mRecyclerViewBridge.setFocusView(itemView, 1.2f);
                oldView = itemView;

                int row = position / ROW_SIZE + 1;
                pageCountView.setText(row + "/" + rowCount);
                if(videoList.size() - position <= 3 * ROW_SIZE && !loading && videoList.size() < total) {//进入倒首第三行就加载下一页
                    Log.d("hjs","loading next page");
                    page ++;
                    getPageData();
                }
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
                mRecyclerViewBridge.setFocusView(itemView, 1.2f);
                oldView = itemView;
            }
        });
        // item 单击事件处理.
        contentView.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
            }
        });
    }


    public float getDimension(int id) {
        return getResources().getDimension(id);
    }
    private void getPageData() {
        loading = true;
        Map<String,Object> map = new HashMap<>();
        if(forFree) {
            map.put("isFree", forFree ? 1 : 0);
        }
        HttpRequest.get(HttpAddress.getList(catId,page,pageSize),map,ListActivity.this,"getListBack",null,this,ListEntity.class);
    }

    /**
     * 获取列表数据接口返回调用接口
     * @param entity
     * @param result
     */
    public void getListBack(ListEntity entity,String result) {
        Log.d("hjs","getListBack");
        loading = false;
        if(entity == null || entity.data == null ||  entity.data.rows == null) {
            return;
        }
        pageCount = entity.data.pageCount;
        total = entity.data.total;
        if(videoList == null) {
            rowCount = entity.data.total / ROW_SIZE + 1;
            pageCountView.setText("1/" + rowCount);
            videoList = entity.data.rows;
            initContentView();
        }else {
            videoList.addAll(entity.data.rows);
            contentView.getAdapter().notifyDataSetChanged();
        }
    }


}

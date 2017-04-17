package com.tv.mytv.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
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
import com.tv.mytv.widget.MyOpenMenuImpl;
import com.tv.mytv.widget.SpaceItemDecoration;

import java.util.List;

import adapter.FreeMenuPresenter;
import adapter.ListMenuPresenter;
import adapter.RecyclerViewPresenter;
import adapter.VideoListRecyclerViewPresenter;

/**
 * Created by Administrator on 2017/4/17.
 */

public class ListActivity extends AppCompatActivity implements RecyclerViewTV.OnItemListener{

    private RecyclerViewTV leftMenu;
    private RecyclerViewTV contentView;
    private RecyclerViewTV freeMenu;
    private VideoListRecyclerViewPresenter mMyRecyclerViewPresenter;
    private GeneralAdapter mMyGeneralAdapter;
    private RecyclerViewBridge mRecyclerViewBridge;

    private MainUpView mainUpView1;

    private TextView categoryName;
    private TextView pageCount;
    private View oldView;

    private String catId;
    private String catName;
    private int page = 1;
    private int pageSize = 10;
    private int leftMenuSelectedIndex = 0;

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
        initViews();
        getData();
    }

    private void initViews(){
        categoryName = (TextView)findViewById(R.id.category_name) ;
        pageCount = (TextView)findViewById(R.id.category_page) ;
        categoryName.setText(catName);

        initLeftMenu();
        initFreeMenu();
    }
    private void initLeftMenu() {
        String category = SharePrefUtil.getString(this,"category","");
        if(TextUtils.isEmpty(category)) {
            return;
        }
        Gson gson = new Gson();
        CategoryEntity categoryEntity = gson.fromJson(category, CategoryEntity.class);
        if(categoryEntity == null) {
            ToastUtil.showShort(this,"解析栏目分类出错");
            return;
        }
        int defaultPos = 0;
        MyOpenMenuImpl openMenu = new MyOpenMenuImpl();
        int i = 0;
        for(CategoryEntity.Category cat : categoryEntity.data.catergory){
            openMenu.add(cat.catname,cat.icon);
            if(catId.equals(cat.catid)) {
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
                mainUpView.setUnFocusView(itemView);
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                leftMenuSelectedIndex = position;
                mainUpView.setFocusView(itemView, 1.0f);
                oldView = itemView;
//                onViewItemClick(itemView, position,false);

                for(int i = 0 ;i < leftMenu.getChildCount(); i ++) {
                    leftMenu.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                leftMenu.getChildAt(leftMenuSelectedIndex).setBackgroundResource(R.drawable.left_menu_checkde);
                if(mainUpView1 != null) {
                    mainUpView1.setVisibility(View.GONE);
                }
            }

            /**
             * 这里是调整开头和结尾的移动边框.
             */
            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
                mainUpView.setFocusView(itemView, 1.0f);
                oldView = itemView;
            }
        });
        leftMenu.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                // 测试.点击效果，实际电视没有点击
                mainUpView.setFocusView(itemView, oldView, 1.0f);
                oldView = itemView;
                //
//                onViewItemClick(itemView, position,true);
            }
        });
        leftMenu.setDefaultSelect(defaultPos);
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
                //TODO
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
    private void initContentView(List<ListEntity.VideoRow> rows) {
        contentView = (RecyclerViewTV) findViewById(R.id.recyclerview_content);
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 5); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        contentView.setLayoutManager(gridlayoutManager);
        contentView.addItemDecoration(new SpaceItemDecoration((int)getDimension(R.dimen.h_94),(int)getDimension(R.dimen.h_12),5));
        contentView.setFocusable(false);
        contentView.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mMyRecyclerViewPresenter = new VideoListRecyclerViewPresenter(rows);
        mMyGeneralAdapter = new GeneralAdapter(mMyRecyclerViewPresenter);
        contentView.setAdapter(mMyGeneralAdapter);

        mainUpView1 = (MainUpView) findViewById(R.id.mainUpView1);
        mainUpView1.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView1.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.select_cover);
        RectF receF = new RectF(getDimension(R.dimen.w_92), getDimension(R.dimen.w_12) ,
                getDimension(R.dimen.w_92) , getDimension(R.dimen.h_82) );
        mRecyclerViewBridge.setDrawUpRectPadding(receF);
        //防止切换焦点时，亮框移动幅度太大
        mRecyclerViewBridge.setOnAnimatorListener(new OpenEffectBridge.NewAnimatorListener() {
            @Override
            public void onAnimationStart(OpenEffectBridge bridge, View view,
                                         Animator animation) {
                mRecyclerViewBridge.setVisibleWidget(true);
            }

            @Override
            public void onAnimationEnd(OpenEffectBridge bridge, View view,
                                       Animator animation) {
                if (view.hasFocus())
                    mRecyclerViewBridge.setVisibleWidget(false);
            }
        });

        //
        contentView.setOnItemListener(this);
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
    private void getData() {
        HttpRequest.get(HttpAddress.getList(catId,page,pageSize),null,ListActivity.this,"getListBack",null,this,ListEntity.class);
    }

    public void getListBack(ListEntity entity,String result) {
        if(entity == null || entity.data == null || entity.data.videoList == null || entity.data.videoList.rows == null) {
            return;
        }
        initContentView(entity.data.videoList.rows);
    }

    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
        if(parent == leftMenu) {
//            mMenuRecyclerViewBridge.setUnFocusView(oldView);
        } else if(parent == contentView){
            mRecyclerViewBridge.setUnFocusView(oldView);
        }
    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
        if(parent == leftMenu) {
//            mMenuRecyclerViewBridge.setFocusView(itemView, 1.0f);
        } else if(parent == contentView){
            mRecyclerViewBridge.setFocusView(itemView, 1.2f);
        }
        oldView = itemView;

        if(parent == contentView) {
            for(int i = 0 ;i < leftMenu.getChildCount(); i ++) {
                leftMenu.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            leftMenu.getChildAt(leftMenuSelectedIndex).setBackgroundResource(R.drawable.left_menu_selected_unfocus);
        }
    }

    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
        if(parent == leftMenu) {
//            mMenuRecyclerViewBridge.setFocusView(itemView, 1.0f);
        } else if(parent == contentView){
            mRecyclerViewBridge.setFocusView(itemView, 1.2f);
        }
        oldView = itemView;
    }
}

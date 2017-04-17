package com.tv.mytv.activity;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.menu.OpenMenuImpl;
import com.open.androidtvwidget.view.MainUpView;
import com.tv.mytv.R;
import com.tv.mytv.adapter.MenuAdapter;
import com.tv.mytv.bean.MenuList;
import com.tv.mytv.fragment.MainFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import adapter.MenuPresenter;
import adapter.RecyclerViewPresenter;
import adapter.TreeMenuPresenter;

public class MainActivity extends AppCompatActivity implements RecyclerViewTV.OnItemListener{

    private RecyclerViewTV menuListView;
    private int keyBackClickCount = 0;
    private TextView tvLogin;
    private MainUpView mainUpView1;
    private View oldView;

    private RecyclerViewTV rvMy;
    private RecyclerViewTV rvCategory;
    private RecyclerViewPresenter mMyRecyclerViewPresenter;
    private GeneralAdapter mMyGeneralAdapter;
    private RecyclerViewPresenter mCategoryRecyclerViewPresenter;
    private GeneralAdapter mCategoryGeneralAdapter;
    private RecyclerViewBridge mRecyclerViewBridge;
    private ScrollView scrollView;
    private TextView txtMy;
    private TextView txtCategory;

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
        }
    }

    private void initViews(){
        tvLogin = (TextView)findViewById(R.id.login);
        tvLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    tvLogin.setSelected(true);
                } else {
                    tvLogin.setSelected(false);
                }
            }
        });
        scrollView = (ScrollView)findViewById(R.id.content_scroll_view);
        txtMy = (TextView)findViewById(R.id.text_my);
        txtCategory = (TextView)findViewById(R.id.text_category);
        initLeftMenu();
        initRightViews();
    }
    public float getDimension(int id) {
        return getResources().getDimension(id);
    }

    public void initLeftMenu(){
        OpenMenuImpl openMenu = new OpenMenuImpl();
        openMenu.add("我的").setIconRes(R.drawable.selector_menu_my);
        openMenu.add("分类").setIconRes(R.drawable.selector_menu_category);
        openMenu.add("设置").setIconRes(R.drawable.selector_menu_setup);
        final MainUpView mainUpView = new MainUpView(this);
        mainUpView.setEffectBridge(new OpenEffectBridge());
        mainUpView.setUpRectResource(R.drawable.left_menu_bg_selector);
        menuListView = (RecyclerViewTV) findViewById(R.id.lv_menu);

        LinearLayoutManagerTV managerMenu = new LinearLayoutManagerTV(this);
        managerMenu.setOrientation(LinearLayoutManager.VERTICAL);
        managerMenu.setSmoothScrollbarEnabled(false);
        menuListView.setLayoutManager(managerMenu);
        GeneralAdapter menuAdapter = new GeneralAdapter(new TreeMenuPresenter(menuListView, openMenu));
        menuListView.setAdapter(menuAdapter);
        menuListView.setItemAnimator(new DefaultItemAnimator());

        //
//        menuListView.setOnItemListener(this);
//        // item 单击事件处理.
//        menuListView.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
//            @Override
//            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
//            }
//        });

        menuListView.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
                // 传入 itemView也可以, 自己保存的 oldView也可以.
                mainUpView.setUnFocusView(itemView);
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                mainUpView.setFocusView(itemView, 1.0f);
                oldView = itemView;
                onViewItemClick(itemView, position);

                switch (position) {
                    case 0:
                        itemView.setBackgroundResource(R.drawable.left_menu_checkde);
                        menuListView.getChildAt(1).setBackgroundColor(Color.TRANSPARENT);
                        menuListView.getChildAt(2).setBackgroundColor(Color.TRANSPARENT);
                        break;
                    case 1:
                        itemView.setBackgroundResource(R.drawable.left_menu_checkde);
                        menuListView.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
                        menuListView.getChildAt(2).setBackgroundColor(Color.TRANSPARENT);
                        break;
                    case 2:
                        itemView.setBackgroundResource(R.drawable.left_menu_checkde);
                        menuListView.getChildAt(1).setBackgroundColor(Color.TRANSPARENT);
                        menuListView.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
                        break;
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
        menuListView.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                // 测试.点击效果，实际电视没有点击
                mainUpView.setFocusView(itemView, oldView, 1.0f);
//                oldView = itemView;
                //
                onViewItemClick(itemView, position);
            }
        });
        menuListView.setDefaultSelect(0);

    }

    private void onViewItemClick(View itemView, int position) {
        switch (position) {
            case 0:
                scrollView.scrollTo(0,(int)txtMy.getY());
                break;
            case 1:
                scrollView.scrollTo(0,(int)txtCategory.getY());
                break;
        }

    }

    private void initRightViews(){
        rvMy = (RecyclerViewTV)findViewById(R.id.recyclerview_my);
        rvCategory = (RecyclerViewTV)findViewById(R.id.recyclerview_category);
//        scrollView = (ScrollView) rootView;
        txtMy = (TextView)findViewById(R.id.text_my);
        txtCategory = (TextView)findViewById(R.id.text_category);
        initMyRecyclerViewGridLayout();
        initCategoryRecyclerViewGridLayout();
    }

    private void initMyRecyclerViewGridLayout() {
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 6); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvMy.setLayoutManager(gridlayoutManager);
        rvMy.addItemDecoration(new SpaceItemDecoration((int)getDimension(R.dimen.h_94),(int)getDimension(R.dimen.h_12)));
        rvMy.setFocusable(false);
        rvMy.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mMyRecyclerViewPresenter = new RecyclerViewPresenter(12);
        mMyGeneralAdapter = new GeneralAdapter(mMyRecyclerViewPresenter);
        rvMy.setAdapter(mMyGeneralAdapter);

        mainUpView1 = (MainUpView) findViewById(R.id.mainUpView1);
        mainUpView1.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView1.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.select_cover);
        RectF receF = new RectF(getDimension(R.dimen.w_92), getDimension(R.dimen.w_22) ,
                getDimension(R.dimen.w_92) , getDimension(R.dimen.h_92) );
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
        rvMy.setOnItemListener(this);
        // item 单击事件处理.
        rvMy.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
            }
        });
    }

    private void initCategoryRecyclerViewGridLayout() {
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 6); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvCategory.setLayoutManager(gridlayoutManager);
        rvCategory.setFocusable(false);
        rvCategory.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mCategoryRecyclerViewPresenter = new RecyclerViewPresenter(6);
        mCategoryGeneralAdapter = new GeneralAdapter(mCategoryRecyclerViewPresenter);
        rvCategory.setAdapter(mCategoryGeneralAdapter);

        //
        rvCategory.setOnItemListener(this);
        // item 单击事件处理.
        rvCategory.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
            }
        });
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

    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
        if(parent == menuListView) {
//            mMenuRecyclerViewBridge.setUnFocusView(oldView);
        } else {
            mRecyclerViewBridge.setUnFocusView(oldView);
        }
    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
        if(parent == menuListView) {
//            mMenuRecyclerViewBridge.setFocusView(itemView, 1.0f);
        } else {
            mRecyclerViewBridge.setFocusView(itemView, 1.1f);
        }
        oldView = itemView;

        if(parent == rvCategory) {
            scrollView.scrollTo(0,(int)txtCategory.getY() );
            menuListView.getChildAt(1).setBackgroundResource(R.drawable.left_menu_selected_unfocus);
            menuListView.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
            menuListView.getChildAt(2).setBackgroundColor(Color.TRANSPARENT);
        } else {
            scrollView.scrollTo(0,(int)txtMy.getY());
            menuListView.getChildAt(0).setBackgroundResource(R.drawable.left_menu_selected_unfocus);
            menuListView.getChildAt(1).setBackgroundColor(Color.TRANSPARENT);
            menuListView.getChildAt(2).setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
        if(parent == menuListView) {
//            mMenuRecyclerViewBridge.setFocusView(itemView, 1.0f);
        } else {
            mRecyclerViewBridge.setFocusView(itemView, 1.1f);
        }
        oldView = itemView;
    }

    public class SpaceItemDecoration extends RecyclerViewTV.ItemDecoration{

        private int bottomSpace;
        private int rightSpace;

        public SpaceItemDecoration(int bottomSpace,int rightSpace) {
            this.bottomSpace = bottomSpace;
            this.rightSpace = rightSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
            if (parent.getChildLayoutPosition(view) < 6) {
                outRect.bottom = bottomSpace;
            }
//            outRect.left = rightSpace;
            //由于每行都只有6个，所以第一个都是6的倍数，把左边距设为0
//            if (parent.getChildLayoutPosition(view) %6 == 0) {
//                outRect.left = rightSpace;
//            }
        }
    }
}

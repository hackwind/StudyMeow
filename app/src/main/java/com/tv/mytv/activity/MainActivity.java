package com.tv.mytv.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.tv.mytv.entity.BaseEntity;
import com.tv.mytv.entity.CategoryEntity;
import com.tv.mytv.entity.TokenEntity;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpRequest;
import com.tv.mytv.util.SharePrefUtil;
import com.tv.mytv.widget.SpaceItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

import adapter.CategoryRecyclerViewPresenter;
import adapter.LoginMenuPresenter;
import adapter.RecyclerViewPresenter;
import adapter.TreeMenuPresenter;

public class MainActivity extends AppCompatActivity implements RecyclerViewTV.OnItemListener{

    private RecyclerViewTV menuListView;
    private RecyclerViewTV loginView;
    private int keyBackClickCount = 0;
    private MainUpView mainUpView1;
    private View oldView;

    private RecyclerViewTV rvMy;
    private RecyclerViewTV rvCategory;
    private RecyclerViewPresenter mMyRecyclerViewPresenter;
    private GeneralAdapter mMyGeneralAdapter;
    private CategoryRecyclerViewPresenter mCategoryRecyclerViewPresenter;
    private GeneralAdapter mCategoryGeneralAdapter;
    private RecyclerViewBridge mRecyclerViewBridge;
    private ScrollView scrollView;
    private TextView txtMy;
    private TextView txtCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        getToken();
        initViews();
    }
    private void getToken() {
        HttpRequest.get(HttpAddress.getToken(Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID)),null,MainActivity.this,"getTokenBack",null,this, TokenEntity.class);
    }

    private void getCategory() {
        HttpRequest.get(HttpAddress.getCategory(),null,MainActivity.this,"getCategoryBack",null,this, CategoryEntity.class);
    }

    public void getTokenBack(TokenEntity entity,String totalResult) {
        String token = entity.data.auth;
        HttpAddress.token = token;
        SharePrefUtil.saveString(this, "token", token);
        getCategory();
    }

    public void getCategoryBack(CategoryEntity entity,String totalResult) {
        initCategoryRecyclerViewGridLayout(entity);
        SharePrefUtil.saveString(this,"category",totalResult);
    }

    private void initViews(){
        scrollView = (ScrollView)findViewById(R.id.content_scroll_view);
        txtMy = (TextView)findViewById(R.id.text_my);
        txtCategory = (TextView)findViewById(R.id.text_category);
        initLoginView();
        initLeftMenu();
        initRightViews();
    }
    public float getDimension(int id) {
        return getResources().getDimension(id);
    }
    private void initLoginView() {
        OpenMenuImpl openMenu = new OpenMenuImpl();
        openMenu.add("登陆").setIconRes(R.mipmap.login);
        final MainUpView mainUpView = new MainUpView(this);
        mainUpView.setEffectBridge(new OpenEffectBridge());
        mainUpView.setUpRectResource(R.drawable.left_menu_bg_selector);
        loginView = (RecyclerViewTV) findViewById(R.id.login);

        LinearLayoutManagerTV managerMenu = new LinearLayoutManagerTV(this);
        managerMenu.setOrientation(LinearLayoutManager.HORIZONTAL);
        managerMenu.setSmoothScrollbarEnabled(false);
        loginView.setLayoutManager(managerMenu);
        final GeneralAdapter menuAdapter = new GeneralAdapter(new LoginMenuPresenter(loginView, openMenu));
        loginView.setAdapter(menuAdapter);
        loginView.setItemAnimator(new DefaultItemAnimator());
        loginView.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {

            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                int count = menuListView.getChildCount();
                for(int i = 0; i < count;i ++) {
                    menuListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                mainUpView1.setVisibility(View.GONE);
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

            }
        });
        loginView.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initLeftMenu(){
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

        menuListView.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
                // 传入 itemView也可以, 自己保存的 oldView也可以.
//                if(mainUpView1 != null) {
//                    mainUpView.setUnFocusView(itemView);
//                }
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {

                onViewItemClick(itemView, position,false);

                for(int i = 0 ;i < menuListView.getChildCount(); i ++) {
                    menuListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                itemView.setBackgroundResource(R.drawable.left_menu_checkde);

//                if(mainUpView1 != null) {
//                    mainUpView.setFocusView(itemView, 1.0f);
//                    oldView = itemView;
//                    mainUpView1.setVisibility(View.GONE);
//                }
            }

            /**
             * 这里是调整开头和结尾的移动边框.
             */
            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
//                mainUpView.setFocusView(itemView, 1.0f);
//                oldView = itemView;
            }
        });
        menuListView.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                // 测试.点击效果，实际电视没有点击
//                mainUpView.setFocusView(itemView, oldView, 1.0f);
//                oldView = itemView;
                //
                onViewItemClick(itemView, position,true);
            }
        });
        menuListView.setDefaultSelect(0);

    }

    private void onViewItemClick(View itemView, int position,boolean isRealClick) {
        switch (position) {
            case 0:
                scrollView.scrollTo(0,(int)txtMy.getY());
                break;
            case 1:
                scrollView.scrollTo(0,(int)txtCategory.getY());
                break;
            case 2:
                if(isRealClick) {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
                break;
        }

    }

    private void initRightViews(){
        rvMy = (RecyclerViewTV)findViewById(R.id.recyclerview_my);
        rvCategory = (RecyclerViewTV)findViewById(R.id.recyclerview_category);
        txtMy = (TextView)findViewById(R.id.text_my);
        txtCategory = (TextView)findViewById(R.id.text_category);
        initMyRecyclerViewGridLayout();
//        initCategoryRecyclerViewGridLayout();
    }

    private void initMyRecyclerViewGridLayout() {
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 6); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvMy.setLayoutManager(gridlayoutManager);
        rvMy.addItemDecoration(new SpaceItemDecoration((int)getDimension(R.dimen.h_94),(int)getDimension(R.dimen.h_12),6));
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
        RectF receF = new RectF(getResources().getDimension(R.dimen.w_87), getResources().getDimension(R.dimen.w_29) ,
                getResources().getDimension(R.dimen.w_87) , getResources().getDimension(R.dimen.h_89));
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

    private void initCategoryRecyclerViewGridLayout(final CategoryEntity categoryEntity) {
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 6); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvCategory.setLayoutManager(gridlayoutManager);
        rvCategory.setFocusable(false);
        rvCategory.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mCategoryRecyclerViewPresenter = new CategoryRecyclerViewPresenter(categoryEntity.data.catergory);
        mCategoryGeneralAdapter = new GeneralAdapter(mCategoryRecyclerViewPresenter);
        rvCategory.setAdapter(mCategoryGeneralAdapter);

        //
        rvCategory.setOnItemListener(this);
        // item 单击事件处理.
        rvCategory.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                intent.putExtra("catid",categoryEntity.data.catergory.get(position).catid);
                intent.putExtra("catname",categoryEntity.data.catergory.get(position).catname);
                startActivity(intent);
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


}

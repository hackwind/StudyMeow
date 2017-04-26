package cn.xueximiao.tv.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.CategoryEntity;
import cn.xueximiao.tv.entity.RecommendEntity;
import cn.xueximiao.tv.entity.TokenEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpImageAsync;
import cn.xueximiao.tv.http.HttpRequest;
import cn.xueximiao.tv.util.SharePrefUtil;
import cn.xueximiao.tv.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.xueximiao.tv.adapter.CategoryRecyclerViewPresenter;
import cn.xueximiao.tv.adapter.RecommendPresenter;
import cn.xueximiao.tv.adapter.TreeMenuPresenter;

public class MainActivity extends BaseActivity implements RecyclerViewTV.OnItemListener{
    private final static int ROW_SIZE = 6;
    private RecyclerViewTV menuListView;
    private LinearLayout loginView;
    private int keyBackClickCount = 0;
    private MainUpView mainUpView1;
    private View oldView;

    private RecyclerViewTV rvMy;
    private RecyclerViewTV rvCategory;
    private RecommendPresenter mMyRecyclerViewPresenter;
    private GeneralAdapter mMyGeneralAdapter;
    private CategoryRecyclerViewPresenter mCategoryRecyclerViewPresenter;
    private GeneralAdapter mCategoryGeneralAdapter;
    private RecyclerViewBridge mRecyclerViewBridge;
    private ScrollView scrollView;
    private TextView txtMy;
    private TextView txtCategory;
    private ImageView userIcon;
    private TextView userNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getToken();
    }
    private void getToken() {
        if(TextUtils.isEmpty(SharePrefUtil.getString(this,SharePrefUtil.KEY_USER_ID,""))) { // 还未登陆
            HttpRequest.get(HttpAddress.getToken(Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID)), null, MainActivity.this, "getTokenBack", null, this, TokenEntity.class);
        } else {
            HttpAddress.auth = SharePrefUtil.getString(this,SharePrefUtil.KEY_AUTH,"");
            getRecommend();
            getCategory();
        }
    }

    private void getCategory() {
        HttpRequest.get(HttpAddress.getCategory(),null,MainActivity.this,"getCategoryBack",null,this, CategoryEntity.class);
    }

    private void getRecommend() {
        HttpRequest.get(HttpAddress.getRecommend(),null,MainActivity.this,"getRecommendBack",null,this, RecommendEntity.class);
    }

    /** 获取token回调 */
    public void getTokenBack(TokenEntity entity,String totalResult) {
        String auth = entity.data.auth;
        HttpAddress.auth = auth;
        SharePrefUtil.saveString(this, SharePrefUtil.KEY_AUTH, auth);
        getRecommend();
        getCategory();
    }
    /** 获取分类回调 */
    public void getCategoryBack(CategoryEntity entity,String totalResult) {
        if(entity.status == false) {
            ToastUtil.showShort(this,entity.msg);
            return;
        }
        initCategoryRecyclerViewGridLayout(entity);
        SharePrefUtil.saveString(this,"category",totalResult);
    }
    /** 获取推荐数据回调 */
    public void getRecommendBack(RecommendEntity entity,String totalResult) {
        if(entity.status == false) {
            ToastUtil.showShort(this,entity.msg);
            return;
        }
        initMyRecyclerViewGridLayout(entity);
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

        loginView = (LinearLayout) findViewById(R.id.login_button);
        userIcon = (ImageView)loginView.findViewById(R.id.user_icon);
        userNick = (TextView)loginView.findViewById(R.id.user_nick);
        if(TextUtils.isEmpty(SharePrefUtil.getString(this,SharePrefUtil.KEY_USER_ID,""))) {//未登录
            userIcon.setImageResource(R.mipmap.login);
            userNick.setText(R.string.login);
        } else {
            String nickName = SharePrefUtil.getString(this,SharePrefUtil.KEY_NICK_NAME,"");
            String url = SharePrefUtil.getString(this,SharePrefUtil.KEY_THUMB,"");
            userNick.setText(nickName);
            HttpImageAsync.loadingRoundImage(userIcon,url);
        }
        loginView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    loginView.setBackgroundResource(R.drawable.left_menu_checkde);
                    if(mainUpView1 != null) {
                        mainUpView1.setVisibility(View.GONE);
                    }
                } else {
                    loginView.setBackgroundResource(R.drawable.left_menu_nocheckde);
                }
            }
        });
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View itemView) {
                if(TextUtils.isEmpty(SharePrefUtil.getString(MainActivity.this,SharePrefUtil.KEY_USER_ID,""))) {//未登录
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, MyActivity.class);
                    startActivity(intent);
                }
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
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {

                onViewItemClick(itemView, position,false);

                for(int i = 0 ;i < menuListView.getChildCount(); i ++) {
                    menuListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                itemView.setBackgroundResource(R.drawable.left_menu_checkde);

            }

            /**
             * 这里是调整开头和结尾的移动边框.
             */
            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
            }
        });
        menuListView.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                onViewItemClick(itemView, position,true);
            }
        });
        menuListView.setDefaultSelect(0);

    }

    private void onViewItemClick(View itemView, int position,boolean isRealClick) {
        switch (position) {
            case 0:
                scrollView.smoothScrollTo(0,0);
                break;
            case 1:
                scrollView.smoothScrollTo(0,100000);
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
    }

    private void initMyRecyclerViewGridLayout(final RecommendEntity entity) {
        if(entity == null || entity.data == null || entity.data.poster == null) {
            return;
        }
        List<RecommendEntity.Poster> posters = entity.data.poster;
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 6); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvMy.setLayoutManager(gridlayoutManager);
        rvMy.addItemDecoration(new BottomSpaceItemDecoration((int)getDimension(R.dimen.h_94),ROW_SIZE,posters.size()));
        rvMy.setFocusable(false);
//        rvMy.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mMyRecyclerViewPresenter = new RecommendPresenter(posters);
        mMyGeneralAdapter = new GeneralAdapter(mMyRecyclerViewPresenter);
        rvMy.setAdapter(mMyGeneralAdapter);

        mainUpView1 = (MainUpView) findViewById(R.id.mainUpView1);
        mainUpView1.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView1.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.select_cover);
        RectF receF = new RectF(getResources().getDimension(R.dimen.w_44) ,
                getResources().getDimension(R.dimen.w_17) ,
                getResources().getDimension(R.dimen.w_44)  ,
                getResources().getDimension(R.dimen.h_42) );
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
                RecommendEntity.Poster poster = entity.data.poster.get(position);
                if("my".equals(poster.linkType)) {//我的
                    String userId = SharePrefUtil.getString(MainActivity.this,"userid","");
                    if(TextUtils.isEmpty(userId)) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, MyActivity.class);
                        startActivity(intent);
                    }
                } else if("history".equals(poster.linkType)) {//历史记录
                    Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
                    startActivity(intent);
                } else if("category".equals(poster.linkType)) {//打开分类，进入列表
                    Intent intent = new Intent(MainActivity.this,ListActivity.class);
                    intent.putExtra("catid",poster.linkData);
                    startActivity(intent);
                } else if("detail".equals(poster.linkType)) {//进入专辑详情页，{“linkData”: “20,3930”}格式:catid,id
                    Intent intent = new Intent(MainActivity.this,VideoDetailActivity.class);
                    try {
                        String[] ids = poster.linkData.split(",");
                        intent.putExtra("catid", ids[0]);
                        intent.putExtra("id", ids[1]);
                    } catch (Exception e){}
                    startActivity(intent);
                } else if("url".equals(poster.linkType)) {//打开webview
                    Intent intent = new Intent(MainActivity.this,WebActivity.class);
                    intent.putExtra("url",poster.linkData);
                    startActivity(intent);
                }
            }
        });
    }


    private void initCategoryRecyclerViewGridLayout(final CategoryEntity categoryEntity) {
        if(categoryEntity == null || categoryEntity.status == false || categoryEntity.data == null || categoryEntity.data.category == null) {
            return;
        }
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 6); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvCategory.setLayoutManager(gridlayoutManager);
        rvCategory.setFocusable(false);
        rvCategory.addItemDecoration(new BottomSpaceItemDecoration((int)getDimension(R.dimen.h_94),ROW_SIZE,categoryEntity.data.category.size()));
        mCategoryRecyclerViewPresenter = new CategoryRecyclerViewPresenter(categoryEntity.data.category);
        mCategoryGeneralAdapter = new GeneralAdapter(mCategoryRecyclerViewPresenter);
        rvCategory.setAdapter(mCategoryGeneralAdapter);

        //
        rvCategory.setOnItemListener(this);
        // item 单击事件处理.
        rvCategory.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                intent.putExtra("catid",categoryEntity.data.category.get(position).catid);
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
        if(TextUtils.isEmpty(SharePrefUtil.getString(this,SharePrefUtil.KEY_USER_ID,""))) {//未登录
            userIcon.setImageResource(R.mipmap.login);
            userNick.setText(R.string.login);
        } else {
            String nickName = SharePrefUtil.getString(this,SharePrefUtil.KEY_NICK_NAME,"");
            String url = SharePrefUtil.getString(this,SharePrefUtil.KEY_THUMB,"");
            userNick.setText(nickName);
            HttpImageAsync.loadingRoundImage(userIcon,url);
        }
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
            scrollView.smoothScrollTo(0,100000);
            menuListView.getChildAt(1).setBackgroundResource(R.drawable.left_menu_selected_unfocus);
            menuListView.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
            menuListView.getChildAt(2).setBackgroundColor(Color.TRANSPARENT);
        } else {
            scrollView.smoothScrollTo(0,0);
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

    public class BottomSpaceItemDecoration extends RecyclerViewTV.ItemDecoration{

        private int bottomSpace;
        private int count;
        private int rowSize;
        private int rowCount;

        public BottomSpaceItemDecoration(int bottomSpace,int rowSize,int count) {
            this.bottomSpace = bottomSpace;
            this.count = count;
            this.rowSize = rowSize;
            rowCount = count % rowSize == 0 ? count/rowSize : count/rowSize + 1;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //只有一行的情况，补齐高度，达到2行的高度
            if(count <= rowSize) {
                Log.i("hjs","only one row ,add space:" + view.getHeight());
                outRect.bottom = bottomSpace + (int)getDimension(R.dimen.h_322);
            } else {
                outRect.bottom = bottomSpace;
            }
        }
    }

}

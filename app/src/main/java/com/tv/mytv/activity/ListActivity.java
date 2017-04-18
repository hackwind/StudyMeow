package com.tv.mytv.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.FreeMenuPresenter;
import adapter.ListMenuPresenter;
import adapter.VideoListRecyclerViewPresenter;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by Administrator on 2017/4/17.
 */

public class ListActivity extends AppCompatActivity {

    private RecyclerViewTV leftMenu;
    private RecyclerViewTV freeMenu;


    private TextView categoryName;
    private TextView pageCountView;
    private View oldView;

    private static String catId;
    private String catName;
    private int pageCount = 0;
    private int page = 1;
    private static int pageSize = 10;
    private int leftMenuSelectedIndex = 0;
    private int defaultPos = 0;
    private static boolean forFree = false;

    private List<ListEntity.VideoRow> videoList;

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
        getFirstPageData();

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
        for(CategoryEntity.Category cat : categoryEntity.data.catergory){
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
                if(mainUpView != null) {
                    mainUpView.setUnFocusView(itemView);
                }
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                leftMenuSelectedIndex = position;
                if(mainUpView != null) {
                    mainUpView.setFocusView(itemView, 1.0f);
                }
                oldView = itemView;
//                onViewItemClick(itemView, position,false);

                for(int i = 0 ;i < leftMenu.getChildCount(); i ++) {
                    leftMenu.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                leftMenu.getChildAt(leftMenuSelectedIndex).setBackgroundResource(R.drawable.left_menu_checkde);

                CategoryEntity.Category category = categoryEntity.data.catergory.get(position);
                if(!catId.equals(category.catid) ) {
                    catId = category.catid;
                    catName = category.catname;
                    page = 1;
                    categoryName.setText(catName);
                    getFirstPageData();
                }
            }

            /**
             * 这里是调整开头和结尾的移动边框.
             */
            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
                if(mainUpView != null) {
                    mainUpView.setFocusView(itemView, 1.0f);
                }
                oldView = itemView;
            }
        });
        leftMenu.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                // 测试.点击效果，实际电视没有点击
                if(mainUpView != null) {
                    mainUpView.setFocusView(itemView, oldView, 1.0f);
                }
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
                getFirstPageData();
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

    private static final float MIN_SCALE = 0.75f;
    private static final float MIN_ALPHA = 0.75f;
    private void initVerticalViewPager(List<ListEntity.VideoRow> firstPageVideos,final int pageCount) {
        pageCountView.setText("1/" + pageCount);
        VerticalViewPager verticalViewPager = (VerticalViewPager) findViewById(R.id.vertical_viewpager);

        verticalViewPager.setAdapter(new DummyAdapter(getSupportFragmentManager(),firstPageVideos,pageCount));
//        verticalViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.h_50));
//        verticalViewPager.setPageMarginDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pageCountView.setText((position + 1) + "/" + pageCount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        verticalViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                int pageWidth = view.getWidth();
                int pageHeight = view.getHeight();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);

                } else if (position <= 1) { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        view.setTranslationY(vertMargin - horzMargin / 2);
                    } else {
                        view.setTranslationY(-vertMargin + horzMargin / 2);
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    // Fade the page relative to its size.
                    view.setAlpha(MIN_ALPHA +
                            (scaleFactor - MIN_SCALE) /
                                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            }
        });
    }


    public float getDimension(int id) {
        return getResources().getDimension(id);
    }
    private void getFirstPageData() {

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
        if(entity == null || entity.data == null ||  entity.data.rows == null) {
            return;
        }
        pageCount = entity.data.pageCount;
        videoList = entity.data.rows;
//        initContentView(entity.data.rows);
        initVerticalViewPager(entity.data.rows,pageCount);
    }


    public class DummyAdapter extends FragmentPagerAdapter {
        private List<ListEntity.VideoRow> firstPageVideos;
        private int pageCount;

        public DummyAdapter(FragmentManager fm,List<ListEntity.VideoRow> firstPageVideos,int pageCount) {
            super(fm);
            this.firstPageVideos = firstPageVideos;
            this.pageCount = pageCount;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment fragment = PlaceholderFragment.newInstance(position + 1);
            if(position == 0) {
                fragment.setVideoData(firstPageVideos);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "第" + (position + 1) + "页";
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_PAGE_NUMBER = "page";

        private RecyclerViewTV contentView;
        private GeneralAdapter mMyGeneralAdapter;
        private MainUpView mainUpView1;
        private RecyclerViewBridge mRecyclerViewBridge;
        private VideoListRecyclerViewPresenter mMyRecyclerViewPresenter;
        private int page;
        private List<ListEntity.VideoRow> videoData;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int page) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE_NUMBER, page);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        public void setVideoData(List<ListEntity.VideoRow> videoData) {
            this.videoData = videoData;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_vertical_pageview, container, false);
            contentView = (RecyclerViewTV) rootView.findViewById(R.id.recyclerview_content);
            mainUpView1 = (MainUpView) rootView.findViewById(R.id.mainUpView1);
            page = getArguments().getInt(ARG_PAGE_NUMBER);
//            if(page == 1) {//第一页数据默认外部已经传入，不需要再获取
//                initContentView(videoData);
//            } else {
                getCurrentPageData();
//            }
            return rootView;
        }

        private void getCurrentPageData() {
            Map<String,Object> map = new HashMap<>();
            if(forFree) {
                map.put("isFree", forFree ? 1 : 0);
            }
            HttpRequest.get(HttpAddress.getList(catId,page,pageSize),map,PlaceholderFragment.this,"getListBack",null,getContext(),ListEntity.class);
        }

        /**
         * 获取列表数据接口返回调用接口
         * @param entity
         * @param result
         */
        public void getListBack(ListEntity entity,String result) {
            if(entity == null || entity.data == null ||  entity.data.rows == null) {
                return;
            }
            initContentView(entity.data.rows);
        }
        private View oldView;
        private void initContentView(List<ListEntity.VideoRow> rows) {
            if(rows == null) {
                return;
            }
            GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(getContext(), 5);
            gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            gridlayoutManager.setSmoothScrollbarEnabled(false);
            contentView.setLayoutManager(gridlayoutManager);
            contentView.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.h_94),(int)getResources().getDimension(R.dimen.h_12),5));
            contentView.setFocusable(false);
            contentView.setSelectedItemAtCentered(true); // 设置item在中间移动.
            mMyRecyclerViewPresenter = new VideoListRecyclerViewPresenter(rows);
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
    }
}

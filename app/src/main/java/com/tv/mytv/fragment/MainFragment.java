package com.tv.mytv.fragment;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;
import com.tv.mytv.R;

import adapter.RecyclerViewPresenter;
import io.vov.vitamio.utils.Log;


public class MainFragment extends Fragment implements RecyclerViewTV.OnItemListener{
    private View rootView;
    private RecyclerViewTV rvMy;
    private RecyclerViewTV rvCategory;
    private RecyclerViewPresenter mMyRecyclerViewPresenter;
    private GeneralAdapter mMyGeneralAdapter;
    private RecyclerViewPresenter mCategoryRecyclerViewPresenter;
    private GeneralAdapter mCategoryGeneralAdapter;
    private MainUpView mainUpView1;
    private RecyclerViewBridge mRecyclerViewBridge;
    private View oldView;
    private ScrollView scrollView;
    private TextView txtMy;
    private TextView txtCategory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews(){
        rvMy = (RecyclerViewTV)rootView.findViewById(R.id.recyclerview_my);
        rvCategory = (RecyclerViewTV)rootView.findViewById(R.id.recyclerview_category);
        scrollView = (ScrollView) rootView;
        txtMy = (TextView)rootView.findViewById(R.id.text_my);
        txtCategory = (TextView)rootView.findViewById(R.id.text_category);
        initMyRecyclerViewGridLayout();
        initCategoryRecyclerViewGridLayout();
    }

    private void initMyRecyclerViewGridLayout() {
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(getContext(), 6); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvMy.setLayoutManager(gridlayoutManager);
        rvMy.addItemDecoration(new SpaceItemDecoration((int)getDimension(R.dimen.h_94),(int)getDimension(R.dimen.h_12)));
        rvMy.setFocusable(false);
        rvMy.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mMyRecyclerViewPresenter = new RecyclerViewPresenter(12);
        mMyGeneralAdapter = new GeneralAdapter(mMyRecyclerViewPresenter);
        rvMy.setAdapter(mMyGeneralAdapter);

        mainUpView1 = (MainUpView) rootView.findViewById(R.id.mainUpView1);
        mainUpView1.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView1.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.select_cover);
        RectF receF = new RectF(getDimension(R.dimen.w_92), getDimension(R.dimen.w_22) ,
                getDimension(R.dimen.w_92) , getDimension(R.dimen.h_92) );
        mRecyclerViewBridge.setDrawUpRectPadding(receF);

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
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(getContext(), 6); // 解决快速长按焦点丢失问题.
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

    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setUnFocusView(oldView);
    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
          mRecyclerViewBridge.setFocusView(itemView, 1.1f);
          oldView = itemView;

        if(parent == rvCategory) {
            Log.d("hjs:parent is rvCategory,y:" + (int)txtCategory.getY(),"");
            scrollView.scrollTo(0,(int)txtCategory.getY() );
        } else {
            scrollView.scrollTo(0,(int)txtMy.getY());
        }
    }

    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
            mRecyclerViewBridge.setFocusView(itemView, 1.1f);
            oldView = itemView;
    }

    public float getDimension(int id) {
        return getResources().getDimension(id);
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

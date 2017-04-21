package com.tv.mytv.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;
import com.tv.mytv.R;
import com.tv.mytv.http.HttpImageAsync;
import com.tv.mytv.util.SharePrefUtil;
import com.tv.mytv.widget.SpaceItemDecoration;

import adapter.MyButtonPresenter;
import adapter.RecommendPresenter;

/**
 * Created by Administrator on 2017/4/19.
 */

public class MyActivity extends BaseActivity {
    private ImageView ivUserIcon;
    private TextView tvUserNick;
    private TextView tvRegDate;
    private RecyclerViewTV rvButtonList;
    private MainUpView mainUpView;
    private MyButtonPresenter presenter;
    private GeneralAdapter generalAdapter;
    private RecyclerViewBridge bridge;
    private View oldView;

    private String[] buttonNames = {"会员协议","消费记录","退出登陆"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        initView();
    }

    private void initView() {
        ivUserIcon = (ImageView)findViewById(R.id.user_icon);
        tvUserNick = (TextView)findViewById(R.id.nick_name);
        tvRegDate = (TextView)findViewById(R.id.regdate);

        String iconUrl = SharePrefUtil.getString(this,SharePrefUtil.KEY_THUMB,"");
        String nickName = SharePrefUtil.getString(this,SharePrefUtil.KEY_NICK_NAME,"");
        String regDate = SharePrefUtil.getString(this,SharePrefUtil.KEY_REG_DATE,"");

        HttpImageAsync.loadingImage(ivUserIcon,iconUrl);
        tvUserNick.setText(nickName);
        tvRegDate.setText(regDate);

        initRecycleView();
    }

    private void initRecycleView() {
        rvButtonList = (RecyclerViewTV)findViewById(R.id.my_button_list) ;
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 3); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvButtonList.setLayoutManager(gridlayoutManager);
        rvButtonList.setFocusable(false);
        rvButtonList.setSelectedItemAtCentered(true); // 设置item在中间移动.
        presenter = new MyButtonPresenter(buttonNames);
        generalAdapter = new GeneralAdapter(presenter);
        rvButtonList.setAdapter(generalAdapter);

        mainUpView = (MainUpView) findViewById(R.id.mainUpView);
        mainUpView.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        bridge = (RecyclerViewBridge) mainUpView.getEffectBridge();
        bridge.setUpRectResource(R.drawable.select_cover);
        RectF receF = new RectF(getResources().getDimension(R.dimen.w_87) ,
                getResources().getDimension(R.dimen.w_29) ,
                getResources().getDimension(R.dimen.w_87)  ,
                getResources().getDimension(R.dimen.h_89) );
        bridge.setDrawUpRectPadding(receF);
        //防止切换焦点时，亮框移动幅度太大
        bridge.setOnAnimatorListener(new OpenEffectBridge.NewAnimatorListener() {
            @Override
            public void onAnimationStart(OpenEffectBridge bridge, View view,
                                         Animator animation) {
                bridge.setVisibleWidget(true);
            }

            @Override
            public void onAnimationEnd(OpenEffectBridge bridge, View view,
                                       Animator animation) {
                if (view.hasFocus())
                    bridge.setVisibleWidget(false);
            }
        });
        rvButtonList.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
                bridge.setUnFocusView(oldView);
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                bridge.setFocusView(itemView, 1.1f);
                oldView = itemView;
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
                bridge.setFocusView(itemView, 1.1f);
                oldView = itemView;
            }
        });
        rvButtonList.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                onViewItemClick(itemView, position);
            }
        });
    }

    private void onViewItemClick(View itemView, int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(MyActivity.this, WebActivity.class);
                startActivity(intent);
                break;
            case 1:

                break;
            case 2:


                break;

        }

    }
}

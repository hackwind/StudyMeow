package com.tv.mytv.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.menu.OpenMenuImpl;
import com.open.androidtvwidget.view.MainUpView;
import com.tv.mytv.R;

import adapter.SettingPresenter;
import adapter.TreeMenuPresenter;

/**
 * Created by Administrator on 2017/4/17.
 */

public class SettingActivity extends BaseActivity {
    private RecyclerViewTV settingListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }

    private void initView() {
        OpenMenuImpl openMenu = new OpenMenuImpl();
        openMenu.add("问题反馈").setIconRes(R.drawable.selector_setting_feedback);
        openMenu.add("检查更新").setIconRes(R.drawable.selector_setting_checkupdate);
        openMenu.add("联系我们").setIconRes(R.drawable.selector_setting_contactus);
        openMenu.add("版本信息").setIconRes(R.drawable.selector_setting_versioninfo);
        final MainUpView mainUpView = new MainUpView(this);
        mainUpView.setEffectBridge(new OpenEffectBridge());
        mainUpView.setUpRectResource(R.drawable.left_menu_bg_selector);
        settingListView = (RecyclerViewTV) findViewById(R.id.setting_list);

        LinearLayoutManagerTV managerMenu = new LinearLayoutManagerTV(this);
        managerMenu.setOrientation(LinearLayoutManager.VERTICAL);
        managerMenu.setSmoothScrollbarEnabled(false);
        settingListView.setLayoutManager(managerMenu);
        settingListView.addItemDecoration(new SpaceItemDecoration((int)getResources().getDimension(R.dimen.h_20)));
        GeneralAdapter menuAdapter = new GeneralAdapter(new SettingPresenter(settingListView, openMenu));
        settingListView.setAdapter(menuAdapter);
        settingListView.setItemAnimator(new DefaultItemAnimator());

        settingListView.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {

                for(int i = 0 ;i < settingListView.getChildCount(); i ++) {
                    settingListView.getChildAt(i).setBackgroundResource(R.drawable.setting_item_bg_normal);
                }
                itemView.setBackgroundResource(R.drawable.setting_item_bg_selected);
            }

            /**
             * 这里是调整开头和结尾的移动边框.
             */
            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
            }
        });
        settingListView.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                onViewItemClick(itemView, position);
            }
        });
        settingListView.setDefaultSelect(0);
    }

    private void onViewItemClick(View itemView, int position) {
        switch (position) {
            case 0:

                break;
            case 1:

                break;
            case 2:
                Intent intent = new Intent(SettingActivity.this, ContactUsActivity.class);
                startActivity(intent);

                break;

        }

    }

    public class SpaceItemDecoration extends RecyclerViewTV.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerViewTV.State state) {

            if(parent.getChildLayoutPosition(view) != 0)
                outRect.top = space;
        }
    }
}

package com.tv.mytv.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;

/**
 * Created by Administrator on 2017/4/17.
 */

public class SpaceItemDecoration extends RecyclerViewTV.ItemDecoration{

    private int bottomSpace;

    public SpaceItemDecoration(int bottomSpace) {
        this.bottomSpace = bottomSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = bottomSpace;
    }
}
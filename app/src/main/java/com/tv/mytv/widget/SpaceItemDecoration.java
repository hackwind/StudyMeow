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
    private int rightSpace;
    private int count;

    public SpaceItemDecoration(int bottomSpace,int rightSpace,int rowcount) {
        this.bottomSpace = bottomSpace;
        this.rightSpace = rightSpace;
        this.count = rowcount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        if (parent.getChildLayoutPosition(view) < count) {
            outRect.bottom = bottomSpace;
        }
//            outRect.left = rightSpace;
        //由于每行都只有6个，所以第一个都是6的倍数，把左边距设为0
//            if (parent.getChildLayoutPosition(view) %6 == 0) {
//                outRect.left = rightSpace;
//            }
    }
}
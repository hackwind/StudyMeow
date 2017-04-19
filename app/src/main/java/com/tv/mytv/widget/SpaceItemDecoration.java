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
    private int count;
    private int rowSize;
    private int rowCount;

    public SpaceItemDecoration(int bottomSpace,int rowSize,int count) {
        this.bottomSpace = bottomSpace;
        this.count = count;
        this.rowSize = rowSize;
        rowCount = count % rowSize == 0 ? count/rowSize : count/rowSize + 1;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是最后一行格子都设一个底部的间距
        int pos = parent.getChildLayoutPosition(view) + 1;
        int curRow = pos % rowSize == 0 ? pos / rowSize :
                pos / rowSize + 1;
        if ( curRow != rowCount) {
            outRect.bottom = bottomSpace;
        }
    }
}
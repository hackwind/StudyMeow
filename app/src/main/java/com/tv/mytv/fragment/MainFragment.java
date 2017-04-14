package com.tv.mytv.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.tv.mytv.R;


public class MainFragment extends Fragment {
    private View rootView;
    private RecyclerViewTV rvMy;
    private RecyclerViewTV rvCategory;

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
    }


}

package com.tv.mytv.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tv.mytv.R;


/**
 * Created by Administrator on 2016-11-11.
 */

public class VedioListFragment extends Fragment {

    private Fragment[] fragments;
    private int currentTabIndex;
    private String catid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vedio_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initViews();
        initFragments();
    }
    public void setcatid(String catid){

        this.catid = catid;
    }
//    private void initViews(){
//        View view = getView();
//        final RadioButton hotRadioButton = (RadioButton)view.findViewById(R.id.rb_hot);
//        final RadioButton allRadioButton = (RadioButton)view.findViewById(R.id.rb_all);
//        hotRadioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                allRadioButton.setChecked(false);
//            }
//        });
//        allRadioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hotRadioButton.setChecked(false);
//            }
//        });
//        hotRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    changeFragment(0);
//                }
//            }
//        });
//        allRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    changeFragment(1);
//                }
//            }
//        });
//    }


    private void initFragments() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//        VedioListAllHotFragment AllFragment = new VedioListAllHotFragment();
        VedioListAllHotFragment HotFragment = new VedioListAllHotFragment();
        HotFragment.setcatid(catid);
//        AllFragment.setcatid("8");
        ft.add(R.id.vedio_list, HotFragment).show(HotFragment).commit();
        fragments = new Fragment[]{HotFragment};
    }
    public void changeFragment(int index) {
        if (currentTabIndex != index) {
            FragmentTransaction fragmentManager = getChildFragmentManager().beginTransaction();
            fragmentManager.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                fragmentManager.add(R.id.vedio_list, fragments[index]);
            }
            fragmentManager.show(fragments[index]).commit();
            currentTabIndex = index;
        }
    }
}

package com.tv.mytv.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tv.mytv.CourseDetailsActivity;
import com.tv.mytv.MyApplication;
import com.tv.mytv.R;
import com.tv.mytv.adapter.CenterContentAdapter;
import com.tv.mytv.adapter.GridViewAdapter;
import com.tv.mytv.bean.VedioList;
import com.tv.mytv.http.HttpAddress;
import com.tv.mytv.http.HttpRequest;
import com.tv.mytv.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reco.frame.tv.view.TvGridView;


/**
 * Created by Administrator on 2016-11-12.
 */
public class VedioListAllHotFragment extends Fragment {

        private TvGridView mGridView;

        private String catid;

        private List<VedioList.RowsEntity> rows;

        private Activity activity;

    //    public CenterContentAdapter centerAdapter;

        public static int PAGESIZE = 9;

        public int page = 0;

        private ProgressBar progressBar;

        private boolean TAG;

        private GridViewAdapter gridViewAdapter;

//        private TextView text_none_data;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_all_hot, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            activity = getActivity();
            initViews();
            TAG=false;
            initData();
        }

        public void setcatid(String catid) {
            this.catid = catid;
        }

        private void initViews() {
            View view = getView();
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            mGridView = (TvGridView) view.findViewById(R.id.rv_more);
//            text_none_data= (TextView) view.findViewById(R.id.none);
            mGridView.setOnItemClickListener(new TvGridView.OnItemClickListener() {
    //            @Override
    //            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
    //                Intent intent=new Intent(activity, CourseDetailsActivity.class);
    //                intent.putExtra("id",rows.get(position).getId());
    //                intent.putExtra("catId",catid);
    //                startActivity(intent);
    //            }

                @Override
                public void onItemClick(View item, int position) {
                    Intent intent = new Intent(activity, CourseDetailsActivity.class);
                    intent.putExtra("id", rows.get(position).getId());
                    intent.putExtra("catId", catid);
                    startActivity(intent);
                }
            });

            mGridView.setOnItemSelectListener(new TvGridView.OnItemSelectListener() {
                @Override
                public void onItemSelect(View item, int position) {
    //                ToastUtil.showShort(MyApplication.getContext(),"选中了"+position);
                }
            });
        }

        private void initData() {
            Map<String, Object> map = new HashMap<>();
            map.put("catid", catid);
            map.put("page", ++page);
            map.put("pageSize", PAGESIZE);
            HttpRequest.get(HttpAddress.VEDIO_LIST_URL, map, VedioListAllHotFragment.this, "dataResult", null, MyApplication.getContext());
        }

        public void dataResult(String result) {
            LogUtil.i(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                progressBar.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                if (jsonObject.getBoolean("status")){
//                    text_none_data.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    VedioList vedioList = gson.fromJson(result, VedioList.class);
                    if (!TAG) {
                        rows = vedioList.getRows();
                        setData();
                    } else {
                        List<VedioList.RowsEntity> rowsMore = vedioList.getRows();
                        rows.addAll(rowsMore);
                        gridViewAdapter.notifyDataSetChanged();
                    }
                } else {
                  Toast.makeText(activity, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
//                    text_none_data.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void setData() {
            mGridView.setAdapter(gridViewAdapter = new GridViewAdapter(activity, rows));
            gridViewAdapter.setCatid(catid);
            gridViewAdapter.setOnLoadMoreListener(new CenterContentAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    TAG=true;
                    initData();
                }
                @Override
                public int getPage() {
                    return page;
                }
            });
        }
}

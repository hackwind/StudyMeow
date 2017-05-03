package cn.xueximiao.tv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xueximiao.tv.R;
import cn.xueximiao.tv.activity.VideoDetailActivity;
import cn.xueximiao.tv.adapter.CenterContentAdapter;
import cn.xueximiao.tv.adapter.GridViewAdapter;
import cn.xueximiao.tv.entity.ListEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpRequest;
import reco.frame.tv.view.TvGridView;

/**
 * Created by Administrator on 2017/4/29/029.
 */

public class VideoListFragment extends Fragment {
    private final static int ROW_SIZE = 5;
    private int page = 1;
    private int total = 0;
    public static int PageSize = 50;
    public static int RowSize = 5;

    private String catId;
    private String catName;
    private String catResUrl;
    private boolean forFree = false;
    private ProgressBar progressBar;
    private TvGridView gridView;

    private List<ListEntity.VideoRow> videoList;
    private GridViewAdapter gridViewAdapter;
    private View rootView;
    private View currentMenuView;
    private OnGridItemSelectListener itemSelectListener;

    public VideoListFragment() {

    }

    public static VideoListFragment newInstance(String catid, String catName, String resUrl) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", catid);
        bundle.putString("name", catName);
        bundle.putString("icon", resUrl);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void setFree(boolean free) {
        forFree = free;
    }

    public void setCurrentMenuView(View view) {
        currentMenuView = view;
        if(gridView != null) {
            gridView.setCurrentMenuView(view);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            catId = bundle.getString("id");
            catName = bundle.getString("name");
            catResUrl = bundle.getString("icon");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_video_list, container, false);
            initViews(rootView);
            getPageData();
        }
        if(gridView != null && currentMenuView != null) {
            gridView.setCurrentMenuView(currentMenuView);
        }
        return rootView;
    }

    private void initViews(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        gridView = (TvGridView) view.findViewById(R.id.rv_more);
        gridView.setOnItemClickListener(new TvGridView.OnItemClickListener() {

            @Override
            public void onItemClick(View item, int position) {
                Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
                intent.putExtra("id", videoList.get(position).id);
                intent.putExtra("catid", catId);
                startActivity(intent);
            }
        });

        gridView.setOnItemSelectListener(new TvGridView.OnItemSelectListener() {
            @Override
            public void onItemSelect(View item, int position) {
                if(itemSelectListener != null) {
                    int rowCount = total % ROW_SIZE == 0 ? total / ROW_SIZE : total / ROW_SIZE + 1;
                    int row =  position / ROW_SIZE + 1;
                    itemSelectListener.onItemSelect(row,rowCount);
                }
            }
        });


    }

    public void setOnGridItemSelectListener(OnGridItemSelectListener listener) {
        this.itemSelectListener = listener;
    }

    private void getPageData() {
        Map<String,Object> map = new HashMap<>();
        if(forFree) {
            map.put("isFree", forFree ? 1 : 0);
        }
        HttpRequest.get(HttpAddress.getList(catId,page,PageSize),map,VideoListFragment.this,"getListBack",page > 1 ? null : progressBar,getContext(),ListEntity.class);
    }

    /**
     * 获取列表数据接口返回调用接口
     * @param entity
     * @param result
     */
    public void getListBack(ListEntity entity,String result) {
        Log.d("hjs", "getListBack");
        if (entity == null || entity.data == null || entity.data.rows == null) {
            return;
        }

        total = entity.data.total;

        gridView.setVisibility(View.VISIBLE);
        if (videoList == null) { //第一页加载
            videoList = entity.data.rows;
            setData();

            if(itemSelectListener != null) {
                int rowCount = total % ROW_SIZE == 0 ? total / ROW_SIZE : total / ROW_SIZE + 1;
                int row = 1;
                itemSelectListener.onItemSelect(row, rowCount);
            }
        } else {//其他页加载
            videoList.addAll(entity.data.rows);
            gridViewAdapter.notifyDataSetChanged();

        }

    }
        private void setData() {
            gridView.setAdapter(gridViewAdapter = new GridViewAdapter(getActivity(), videoList));
            gridViewAdapter.setCatid(catId);
            gridViewAdapter.setOnLoadMoreListener(new CenterContentAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    page ++;
                    getPageData();
                }
                @Override
                public int getPage() {
                    return page;
                }
            });
        }

        public interface OnGridItemSelectListener {
            public void onItemSelect(int row,int rowCount);
        }
}

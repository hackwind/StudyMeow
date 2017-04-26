package cn.xueximiao.tv.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;
import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.ListEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpRequest;
import cn.xueximiao.tv.util.ToastUtil;
import cn.xueximiao.tv.widget.SpaceItemDecoration;

import java.util.List;

import cn.xueximiao.tv.adapter.VideoListRecyclerViewPresenter;

/**
 * Created by Administrator on 2017/4/19.
 */

public class HistoryActivity extends BaseActivity implements View.OnFocusChangeListener{
    private final static int ROW_SIZE = 6;
    private final static int PAGE_SIZE = 50;
    private TextView buttonPlayHistory;
    private TextView buttonSubjectCollection;
    private TextView layerNoCollection;
    private RecyclerViewTV rvHistory;
    private RecyclerViewTV rvCollection;
    private ProgressBar progressBar;
    private TextView pageView;
    private View oldView;

    private MainUpView mainUpView;
    private VideoListRecyclerViewPresenter mHistoryPresenter;
    private VideoListRecyclerViewPresenter mCollectionPresenter;
    private GeneralAdapter mHistoryAdapter;
    private GeneralAdapter mCollectionAdapter;
    private RecyclerViewBridge mHistoryBridge;
    private RecyclerViewBridge mCollectionBridge;
    private int totalHistory = 0;
    private int totalCollection = 0;
    private int pageHistory = 1;
    private int pageCollection = 1;
    private int rowCount = 1;
    private boolean loading;
    private List<ListEntity.VideoRow> historyList;
    private List<ListEntity.VideoRow> collectionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initView();
    }

    private void initView() {
        buttonPlayHistory = (TextView)findViewById(R.id.play_history);
        buttonSubjectCollection = (TextView)findViewById(R.id.collection);
        layerNoCollection = (TextView)findViewById(R.id.no_collection);
        pageView = (TextView)findViewById(R.id.pageView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        rvHistory = (RecyclerViewTV)findViewById(R.id.recyclerview_history);
        rvCollection = (RecyclerViewTV)findViewById(R.id.recyclerview_collection);

        mainUpView = (MainUpView)findViewById(R.id.mainUpView);

        buttonPlayHistory.setOnFocusChangeListener(this);
        buttonSubjectCollection.setOnFocusChangeListener(this);
    }
    private void initHistoryView() {

        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, ROW_SIZE);
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvHistory.setLayoutManager(gridlayoutManager);
        rvHistory.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.h_64)));
        rvHistory.setFocusable(false);
        rvHistory.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mHistoryPresenter = new VideoListRecyclerViewPresenter(historyList);
        mHistoryAdapter = new GeneralAdapter(mHistoryPresenter);
        rvHistory.setAdapter(mHistoryAdapter);

        mainUpView.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mHistoryBridge = (RecyclerViewBridge) mainUpView.getEffectBridge();
        mHistoryBridge.setUpRectResource(R.drawable.select_cover);
        //
        RectF receF = new RectF(getResources().getDimension(R.dimen.w_44) ,
                getResources().getDimension(R.dimen.w_17) ,
                getResources().getDimension(R.dimen.w_42)  ,
                getResources().getDimension(R.dimen.h_42) );
        mHistoryBridge.setDrawUpRectPadding(receF);
        //防止切换焦点时，亮框移动幅度太大
        mHistoryBridge.setOnAnimatorListener(new OpenEffectBridge.NewAnimatorListener() {
            @Override
            public void onAnimationStart(OpenEffectBridge bridge, View view,
                                         Animator animation) {
                mHistoryBridge.setVisibleWidget(false);
            }

            @Override
            public void onAnimationEnd(OpenEffectBridge bridge, View view,
                                       Animator animation) {
                if (view.hasFocus())
                    mHistoryBridge.setVisibleWidget(false);
            }
        });

        //
        rvHistory.setOnItemListener(new RecyclerViewTV.OnItemListener() {

            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
                mHistoryBridge.setUnFocusView(itemView);
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                mHistoryBridge.setFocusView(itemView, 1.2f);
                oldView = itemView;

                int row = position / ROW_SIZE + 1;
                pageView.setText(row + "/" + rowCount);
                if(historyList.size() - position <= 3 * ROW_SIZE && !loading && historyList.size() < totalCollection) {//进入倒首第三行就加载下一页
                    Log.d("hjs","loading next page");
                    pageHistory ++;
                    getHistoryData();
                }

                buttonSubjectCollection.setTextColor(getResources().getColor(R.color.trans_white));
                buttonSubjectCollection.setBackgroundColor(Color.TRANSPARENT);
                buttonPlayHistory.setTextColor(getResources().getColor(R.color.trans_white));
                buttonPlayHistory.setBackgroundResource(R.drawable.shape_rectange_round_unselected_bg2);
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
                mHistoryBridge.setFocusView(itemView, 1.2f);
                oldView = itemView;
            }
        });
        // item 单击事件处理.
        rvHistory.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                ListEntity.VideoRow row = historyList.get(position);
                Intent intent = new Intent(HistoryActivity.this,VideoDetailActivity.class);
                intent.putExtra("id",row.id);
                intent.putExtra("catid",row.catid);
                startActivity(intent);
            }
        });
    }
    private void initCollection() {

        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, ROW_SIZE);
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvCollection.setLayoutManager(gridlayoutManager);
        rvCollection.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.h_64)));
        rvCollection.setFocusable(false);
        rvCollection.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mCollectionPresenter = new VideoListRecyclerViewPresenter(collectionList);
        mCollectionAdapter = new GeneralAdapter(mCollectionPresenter);
        rvCollection.setAdapter(mCollectionAdapter);

        mainUpView.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mCollectionBridge = (RecyclerViewBridge) mainUpView.getEffectBridge();
        mCollectionBridge.setUpRectResource(R.drawable.select_cover);
        //
        RectF receF = new RectF(getResources().getDimension(R.dimen.w_44) ,
                getResources().getDimension(R.dimen.w_17) ,
                getResources().getDimension(R.dimen.w_42)  ,
                getResources().getDimension(R.dimen.h_42) );
        mCollectionBridge.setDrawUpRectPadding(receF);
        //防止切换焦点时，亮框移动幅度太大
        mCollectionBridge.setOnAnimatorListener(new OpenEffectBridge.NewAnimatorListener() {
            @Override
            public void onAnimationStart(OpenEffectBridge bridge, View view,
                                         Animator animation) {
                mCollectionBridge.setVisibleWidget(false);
            }

            @Override
            public void onAnimationEnd(OpenEffectBridge bridge, View view,
                                       Animator animation) {
                if (view.hasFocus())
                    mCollectionBridge.setVisibleWidget(false);
            }
        });

        //
        rvCollection.setOnItemListener(new RecyclerViewTV.OnItemListener() {

            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
                mCollectionBridge.setUnFocusView(itemView);
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                mCollectionBridge.setFocusView(itemView, 1.2f);
                oldView = itemView;

                int row = position / ROW_SIZE + 1;
                pageView.setText(row + "/" + rowCount);
                if(collectionList.size() - position <= 3 * ROW_SIZE && !loading && collectionList.size() < totalCollection) {//进入倒首第三行就加载下一页
                    Log.d("hjs","loading next page");
                    pageCollection ++;
                    getCollectionData();
                }

                buttonPlayHistory.setTextColor(getResources().getColor(R.color.trans_white));
                buttonPlayHistory.setBackgroundColor(Color.TRANSPARENT);
                buttonSubjectCollection.setTextColor(getResources().getColor(R.color.trans_white));
                buttonSubjectCollection.setBackgroundResource(R.drawable.shape_rectange_round_unselected_bg2);

            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
                mCollectionBridge.setFocusView(itemView, 1.2f);
                oldView = itemView;
            }
        });
        // item 单击事件处理.
        rvCollection.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                ListEntity.VideoRow row = historyList.get(position);
                Intent intent = new Intent(HistoryActivity.this,VideoDetailActivity.class);
                intent.putExtra("id",row.id);
                intent.putExtra("catid",row.catid);
                startActivity(intent);
            }
        });
    }

    private void getHistoryData() {
        HttpRequest.get(HttpAddress.getHistoryUrl(pageHistory,PAGE_SIZE),null,HistoryActivity.this,"getHistoryBack",pageHistory > 1 ? null : progressBar,this,ListEntity.class);
    }

    public void getHistoryBack(ListEntity entity,String totalResult) {
        if(entity.status == false) {
            ToastUtil.showShort(this,entity.msg);
            return;
        }
        if(entity == null || entity.data == null ||  entity.data.rows == null) {
            return;
        }
        totalHistory = entity.data.total;
        if(historyList == null || historyList.size() == 0) {
            rowCount = entity.data.total / ROW_SIZE + 1;
            pageView.setText("1/" + rowCount);
        }
        if(historyList == null) {
            historyList = entity.data.rows;
            initHistoryView();
        }else {
            historyList.addAll(entity.data.rows);
            rvHistory.getAdapter().notifyDataSetChanged();
        }
        if(historyList.size() == 0) {
            layerNoCollection.setVisibility(View.VISIBLE);
            layerNoCollection.setText(R.string.no_history);
            rvCollection.setVisibility(View.GONE);
            rvHistory.setVisibility(View.GONE);
        } else {
            layerNoCollection.setVisibility(View.GONE);
        }

    }

    private void getCollectionData() {
        HttpRequest.get(HttpAddress.getSubjectCollection(pageCollection,PAGE_SIZE),null,HistoryActivity.this,"getCollectionBack",pageCollection > 1 ? null : progressBar,this,ListEntity.class);
    }

    public void getCollectionBack(ListEntity entity,String totalResult) {
        if(entity.status == false) {
            ToastUtil.showShort(this,entity.msg);
            return;
        }
        if(entity == null || entity.data == null ||  entity.data.rows == null) {
            return;
        }
        totalCollection = entity.data.total;
        if(collectionList == null || collectionList.size() == 0) {
            rowCount = entity.data.total / ROW_SIZE + 1;
            pageView.setText("1/" + rowCount);
        }
        if(collectionList == null) {
            collectionList = entity.data.rows;
            initCollection();
        }else {
            collectionList.addAll(entity.data.rows);
            rvCollection.getAdapter().notifyDataSetChanged();

        }
        if(collectionList.size() == 0) {
            layerNoCollection.setVisibility(View.VISIBLE);
            layerNoCollection.setText(R.string.no_collection);
            rvCollection.setVisibility(View.GONE);
            rvHistory.setVisibility(View.GONE);
        } else {
            layerNoCollection.setVisibility(View.GONE);
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(mainUpView != null) {
            mainUpView.setVisibility(View.GONE);
        }
        switch (v.getId()) {
            case R.id.play_history:
                if(hasFocus) {
                    buttonPlayHistory.setBackgroundResource(R.drawable.shape_rectange_round_selected_bg);
                    buttonSubjectCollection.setBackgroundResource(R.drawable.shape_rectange_round_unselected_bg);
                    if (historyList == null) {
                        getHistoryData();
                    }
                    rvCollection.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.collection:
                if(hasFocus) {
                    buttonPlayHistory.setBackgroundResource(R.drawable.shape_rectange_round_unselected_bg);
                    buttonSubjectCollection.setBackgroundResource(R.drawable.shape_rectange_round_selected_bg);
                    if (collectionList == null) {
                        getCollectionData();
                    }
                    rvCollection.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                }
                break;
        }
    }
}

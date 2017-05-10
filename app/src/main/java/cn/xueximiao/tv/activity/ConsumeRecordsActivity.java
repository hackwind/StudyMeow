package cn.xueximiao.tv.activity;

import android.animation.Animator;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
import cn.xueximiao.tv.entity.ConsumeRecordsEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpRequest;
import cn.xueximiao.tv.util.ToastUtil;

import java.util.List;

import cn.xueximiao.tv.adapter.ConsumeRecordsPresenter;

/**
 * Created by Administrator on 2017/4/19.
 */

public class ConsumeRecordsActivity extends BaseActivity {
    private final static int ROW_SIZE = 3;
    private TextView pageView;
    private ProgressBar progressBar;
    private RecyclerViewTV rvRecordsList;
    private MainUpView mainUpView;
    private ConsumeRecordsPresenter presenter;
    private GeneralAdapter generalAdapter;
    private RecyclerViewBridge bridge;
    private View oldView;

    private List<ConsumeRecordsEntity.ConsumeRecord> records;
    private int total = 0;
    private int page = 1;
    private int rowCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosumerecords);
        initView();
        getRecordsList();
    }

    private void initView() {
        pageView = (TextView)findViewById(R.id.pageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        initRecycleView();
    }

    private void initRecycleView() {
        rvRecordsList = (RecyclerViewTV)findViewById(R.id.my_records_list) ;
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, ROW_SIZE);
        gridlayoutManager.setOrientation(GridLayoutManagerTV.VERTICAL);
        gridlayoutManager.setSmoothScrollbarEnabled(false);
        rvRecordsList.setLayoutManager(gridlayoutManager);
        rvRecordsList.addItemDecoration(new SpaceItemDecoration((int)getResources().getDimension(R.dimen.w_10)));
        rvRecordsList.setFocusable(false);
        rvRecordsList.setSelectedItemAtCentered(false); // 设置item在中间移动.
        presenter = new ConsumeRecordsPresenter(records);
        generalAdapter = new GeneralAdapter(presenter);
        rvRecordsList.setAdapter(generalAdapter);

        mainUpView = (MainUpView) findViewById(R.id.mainUpView);
        mainUpView.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        bridge = (RecyclerViewBridge) mainUpView.getEffectBridge();
        bridge.setUpRectResource(R.drawable.my_button);
        RectF receF = new RectF(getResources().getDimension(R.dimen.w_13) ,
                getResources().getDimension(R.dimen.w_14) ,
                getResources().getDimension(R.dimen.w_13)  ,
                getResources().getDimension(R.dimen.h_14) );
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
        rvRecordsList.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
                bridge.setUnFocusView(oldView);
            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                bridge.setFocusView(itemView, 1.1f);
                oldView = itemView;

                int row = position / ROW_SIZE + 1;
                pageView.setText(row + "/" + rowCount);
            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
                bridge.setFocusView(itemView, 1.1f);
                oldView = itemView;
            }
        });
        rvRecordsList.setPagingableListener(new RecyclerViewTV.PagingableListener() {
            @Override
            public void onLoadMoreItems() {
                if(records != null && records.size() < total){
                    page++;
                    getRecordsList();
                }
            }
        });

        rvRecordsList.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvRecordsList.setDefaultSelect(0);
            }
        },500);

    }

    private void getRecordsList() {
        HttpRequest.get(HttpAddress.getCosumenerList(),null,ConsumeRecordsActivity.this,"getRecordsBack",progressBar,this,ConsumeRecordsEntity.class);
    }

    public void getRecordsBack(ConsumeRecordsEntity entity,String totalResult) {
        if(entity == null) {
            return;
        }
        if(entity.status == false) {
            ToastUtil.showShort(this,entity.msg);
        }
        try {
            total = Integer.parseInt(entity.data.total);
        }catch (Exception e){}

        if(records == null) {
            rowCount = total / ROW_SIZE + 1;
            pageView.setText("1/" + rowCount);
            records = entity.data.rows;
            initRecycleView();
        } else {
            records.addAll(entity.data.rows);
            rvRecordsList.getAdapter().notifyDataSetChanged();
        }
        if(records.size() == total) {
            rvRecordsList.setOnLoadMoreComplete();
        }
    }

    public class SpaceItemDecoration extends RecyclerViewTV.ItemDecoration{

        private int rightSpace;

        public SpaceItemDecoration(int rightSpace) {
            this.rightSpace = rightSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildLayoutPosition(view) % ROW_SIZE != 0) {
                outRect.left = rightSpace;
            }
        }
    }

}

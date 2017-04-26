package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import cn.tv.tv.R;


public class MyButtonPresenter extends OpenPresenter {

    private String[] buttonNames;
    private GeneralAdapter mAdapter;

    public MyButtonPresenter(String[] buttonNames) {
        this.buttonNames = buttonNames;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }


    @Override
    public int getItemCount() {
        return buttonNames == null ? 0 : buttonNames.length;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_button, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
        gridViewHolder.tv.setText(buttonNames[position]);
    }

    public class GridViewHolder extends OpenPresenter.ViewHolder {

        public TextView tv;

        public GridViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.textView);
        }


    }
}
package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import com.tv.mytv.R;
import com.tv.mytv.bean.MenuList;

import java.util.ArrayList;
import java.util.List;


public class MenuPresenter extends OpenPresenter {

    private List<MenuList.MsgBean> menus;
    private GeneralAdapter mAdapter;

    public MenuPresenter(List<MenuList.MsgBean> menus) {
        this.menus = menus;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getItemCount() {
        return menus == null ? 0 : menus.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_item, parent, false);
        return new MenuHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MenuHolder gridViewHolder = (MenuHolder) viewHolder;
        TextView textView = (TextView) gridViewHolder.tv;
        ImageView raidoIcon = (ImageView)gridViewHolder.iv;
        textView.setText(menus.get(position).getCatname());
        raidoIcon.setImageResource(menus.get(position).getResId());
        if (position == 0){
            gridViewHolder.view.setSelected(true);
            gridViewHolder.view.requestFocus();
        } else {
            gridViewHolder.view.setSelected(false);
        }
    }

}

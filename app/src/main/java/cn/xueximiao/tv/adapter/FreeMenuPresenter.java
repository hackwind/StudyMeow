package cn.xueximiao.tv.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.menu.OpenMenu;
import com.open.androidtvwidget.menu.OpenMenuItem;
import com.open.androidtvwidget.menu.OpenMenuItemView;
import cn.xueximiao.tv.R;

import java.util.ArrayList;
import java.util.List;


public class FreeMenuPresenter extends OpenPresenter {

    private RecyclerViewTV mRecyclerViewTV;
    private OpenMenu mOpenMenu;

    public FreeMenuPresenter(RecyclerViewTV recyclerViewTV, OpenMenu openMenu) {
        this.mRecyclerViewTV = recyclerViewTV;
        this.mOpenMenu = openMenu;
    }

    private OpenMenuItem getItemPosition(int position) {
        return mOpenMenu.getMenuDatas().get(position);
    }

    private void addAll(List<OpenMenuItem> list, int pos) {
        mOpenMenu.getMenuDatas().addAll(pos, list);
        getAdapter().notifyItemRangeInserted(pos, list.size());
    }

    private void removeAll(List<OpenMenuItem> list, int pos) {
        int sizeNum = removeAllSubMenu(list);
        getAdapter().notifyItemRangeRemoved(pos, sizeNum);
    }

    private int removeAllSubMenu(List<OpenMenuItem> list) {
        int sizeNum = list.size();
        for (OpenMenuItem menuItem : list) {
            if (menuItem.hasSubMenu() && menuItem.isShowSubMenu()) {
                menuItem.setShowSubMenu(false);
                List<OpenMenuItem> delSubItems = menuItem.getSubMenu().getMenuDatas();
                sizeNum += removeAllSubMenu(delSubItems);
            }
        }
        mOpenMenu.getMenuDatas().removeAll(list);
        return sizeNum;
    }

    @Override
    public int getItemCount() {
        return mOpenMenu != null ? mOpenMenu.getMenuDatas().size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_free_item, parent, false);
        //
        ViewHolder result = new ContainerViewHolder(rootView);
        return result;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ArrayList<OpenMenuItem> items = mOpenMenu.getMenuDatas();
        OpenMenuItem menuItem = items.get(position);
        ContainerViewHolder holder = (ContainerViewHolder) viewHolder;
        OpenMenuItemView openMenuItemView = (OpenMenuItemView) holder.view;
        openMenuItemView.initialize(menuItem);
        if(position == 0) {
            openMenuItemView.setTextColor(mRecyclerViewTV.getContext().getResources().getColor(R.color.selector));
        }
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder viewHolder) {
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder viewHolder) {
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }

    public GeneralAdapter getAdapter() {
        return (GeneralAdapter) mRecyclerViewTV.getAdapter();
    }

    static class ContainerViewHolder extends ViewHolder {
        public ContainerViewHolder(View view) {
            super(view);
        }
    }

}
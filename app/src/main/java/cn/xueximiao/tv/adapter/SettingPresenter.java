package cn.xueximiao.tv.adapter;

import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.menu.OpenMenu;
import com.open.androidtvwidget.menu.OpenMenuItem;
import cn.xueximiao.tv.R;

import java.util.ArrayList;
import java.util.List;


public class SettingPresenter extends OpenPresenter {

    private RecyclerViewTV mRecyclerViewTV;
    private OpenMenu mOpenMenu;

    public SettingPresenter(RecyclerViewTV recyclerViewTV, OpenMenu openMenu) {
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
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item, parent, false);
        //
        ViewHolder result = new ContainerViewHolder(rootView);
        return result;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ArrayList<OpenMenuItem> items = mOpenMenu.getMenuDatas();
        OpenMenuItem menuItem = items.get(position);
        ContainerViewHolder holder = (ContainerViewHolder) viewHolder;
        RelativeLayout openMenuItemView = (RelativeLayout)holder.view;
        ImageView icon = (ImageView)openMenuItemView.findViewById(R.id.icon);
        TextView title = (TextView)openMenuItemView.findViewById(R.id.title_tv);
        ImageView arrow = (ImageView)openMenuItemView.findViewById(R.id.arrow);
        TextView version = (TextView)openMenuItemView.findViewById(R.id.version);
        icon.setImageResource(menuItem.getIconRes());
        title.setText(menuItem.getTitle());
        if(position == getItemCount() - 1) {
            arrow.setVisibility(View.GONE);
            try {
                PackageInfo pkg = mRecyclerViewTV.getContext().getPackageManager().getPackageInfo(mRecyclerViewTV.getContext().getApplicationContext().getPackageName(), 0);
                String versionName = pkg.versionName;
                version.setText("V" + versionName);
                version.setVisibility(View.VISIBLE);
            } catch(Exception e) {}
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
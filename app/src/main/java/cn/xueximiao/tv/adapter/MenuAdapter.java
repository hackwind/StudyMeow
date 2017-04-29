package cn.xueximiao.tv.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import java.util.List;

import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.CategoryEntity;
import cn.xueximiao.tv.http.HttpImageAsync;


/**
 * Created by Administrator on 2016-11-12.
 */

public class MenuAdapter extends BaseAdapter {
    private List<CategoryEntity.Category> msg;
    private Context context;
    private RadioButton checked;
    public MenuAdapter(Context context , List<CategoryEntity.Category> msg){
        this.context = context;
        this.msg = msg;
    }
    @Override
    public int getCount() {
        return msg.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.menu_list_item_linear, null);
        TextView radioButton = (TextView)view.findViewById(R.id.title_tv);
        ImageView icon = (ImageView)view.findViewById(R.id.icon);
        radioButton.setText(msg.get(position).catname);
        HttpImageAsync.loadingImage(icon,msg.get(position).icon);
        return view;
    }
}

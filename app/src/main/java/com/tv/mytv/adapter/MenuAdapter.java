package com.tv.mytv.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tv.mytv.R;
import com.tv.mytv.bean.MenuList;

import java.util.List;


/**
 * Created by Administrator on 2016-11-12.
 */

public class MenuAdapter extends BaseAdapter {
    private List<MenuList.MsgBean> msg;
    private Context context;
    private RadioButton checked;
    public MenuAdapter(Context context , List<MenuList.MsgBean> msg){
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
        View view = View.inflate(context, R.layout.menu_list_item, null);
        TextView radioButton = (TextView)view.findViewById(R.id.rb_item);
        ImageView raidoIcon = (ImageView)view.findViewById(R.id.icon);
        raidoIcon.setImageResource(msg.get(position).getResId());
//        Drawable leftDraw = context.getResources().getDrawable(msg.get(position).getResId());
//
//        radioButton.setCompoundDrawablesWithIntrinsicBounds(msg.get(position).getResId(),0,0,0);
//        radioButton.setCompoundDrawablePadding((int)context.getResources().getDimension(R.dimen.w_40));
        radioButton.setText(msg.get(position).getCatname());
        if (position == 0){
            view.setSelected(true);
            view.requestFocus();
        } else {
            view.setSelected(false);
        }
        return view;
    }
}

package com.tv.mytv.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
//        radioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (radioButton != checked){
//                    checked.setChecked(fToastUtilalse);
//                    checked = radioButton;
//
//                    ((MainActivity)context).changeFragment(position);
//
//                }
//            }
//        });
        radioButton.setText(msg.get(position).getCatname());
//        if (position == 0){
//            radioButton.setChecked(true);
//            checked = radioButton;
//        }
        return view;
    }
}

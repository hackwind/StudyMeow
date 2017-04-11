package com.tv.mytv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tv.mytv.R;
import com.tv.mytv.bean.CourseDetailsBean;
import com.tv.mytv.util.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/11.
 */
public class CourseGridApapter extends BaseAdapter {

    private  static  final  String TAG ="CourseGridApapter";

    private List<CourseDetailsBean.MsgBean.VideoListBean> mList;

    private Context context;

    private  ViewHolder viewHolder;

    private LayoutInflater inflater;

    public CourseGridApapter(List<CourseDetailsBean.MsgBean.VideoListBean> list, Context context){
        this.mList=list;
        this.context=context;
        inflater = LayoutInflater.from(context);
        LogUtil.e(mList.size()+"");
    }

    @Override

    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_coure_gridview, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CourseDetailsBean.MsgBean.VideoListBean bean=mList.get(i);
        viewHolder.tv.setText(bean.getPNumber());
        return convertView;
    }

    public class ViewHolder{
        TextView tv;
        ViewHolder(View view){
            tv= (TextView) view.findViewById(R.id.item_num);
        }
    }
}

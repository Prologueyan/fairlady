package com.example.acer.myfairlady.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.acer.myfairlady.R;

import java.util.List;

/**
 * Created by acer on 2016/3/10.
 */
public abstract class CommonAdapter<T> extends BaseAdapter{

    private Context mContext;
    private List<T> mDatas;
    private LayoutInflater mInflater;

    public CommonAdapter(Context context,List<T> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public  View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewholder = ViewHolder.get(mContext, convertView, parent, R.layout.item_listview, position);

        convert(viewholder,getItem(position));
        return viewholder.getConvertView();
    }

    public abstract void convert(ViewHolder holder,T t);


}
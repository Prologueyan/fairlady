package com.example.acer.myfairlady.Menu;

import android.content.Context;
import android.view.LayoutInflater;

import com.example.acer.myfairlady.Adapter.CommonAdapter;
import com.example.acer.myfairlady.Adapter.ViewHolder;
import com.example.acer.myfairlady.R;

import java.util.List;

/**
 * Created by acer on 2016/3/10.
 */
public class MenuAdapter extends CommonAdapter<Item> {
    protected LayoutInflater mInflater;
    protected List<Item> mDatas;
    protected Context mContext;

    public MenuAdapter(Context context, List<Item> datas) {
        super(context, datas);
    }

    public void convert(ViewHolder holder, Item t) {

        holder.setText(R.id.item_tv, t.getmTitle());
    }
}

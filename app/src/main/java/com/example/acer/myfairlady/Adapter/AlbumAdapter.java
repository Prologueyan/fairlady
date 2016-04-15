package com.example.acer.myfairlady.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.acer.myfairlady.R;
import com.example.acer.myfairlady.entity.AlbumInfo;

import java.util.List;

/**
 * Created by acer on 2016/3/16.
 */
public class AlbumAdapter extends BaseAdapter {
    private List<AlbumInfo> mData = null;
    private Context mContext = null;
    private boolean mMenuVisible = true;

    public AlbumAdapter(Context c, List<AlbumInfo> songList) {
        mContext = c;
        mData = songList;
    }

    public void setData(List<AlbumInfo> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<AlbumInfo> getData() {
        return mData;
    }


    @Override
    public boolean isEmpty() {
        return mData.size() == 0;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public AlbumInfo getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_album, null);
            holder.album = (TextView) convertView
                    .findViewById(R.id.album_name);
            holder.songnum = (TextView) convertView
                    .findViewById(R.id.album_songnum);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.album.setText(mData.get(position).getAlbumName());
        holder.songnum.setText("一共有"
                        + mData.get(position).getNumberOfSongs()+ "首歌"
        );

        return convertView;
    }

    static class ViewHolder {
        TextView album;
        TextView songnum;
    }
}

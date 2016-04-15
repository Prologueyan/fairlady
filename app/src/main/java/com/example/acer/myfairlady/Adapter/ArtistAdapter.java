package com.example.acer.myfairlady.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.acer.myfairlady.R;
import com.example.acer.myfairlady.entity.Artist;

import java.util.List;

/**
 * Created by acer on 2016/3/16.
 */
public class ArtistAdapter extends BaseAdapter {
    private List<Artist> mData = null;
    private Context mContext = null;
    private boolean mMenuVisible = true;

    public ArtistAdapter(Context c, List<Artist> songList) {
        mContext = c;
        mData = songList;
    }

    public void setData(List<Artist> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<Artist> getData() {
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
    public Artist getItem(int position) {
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
                    R.layout.item_artist, null);

            holder.artist = (TextView) convertView
                    .findViewById(R.id.tv_artist_artist);
            holder.songnum = (TextView) convertView.findViewById(R.id.tv_artist_songnum);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.artist.setText(""
                + mData.get(position).getArtist());
        holder.songnum.setText("一共有" + mData.get(position).getNumber_of_tracks() + "首歌");

        return convertView;
    }

    static class ViewHolder {
        TextView artist;
        TextView songnum;
    }
}

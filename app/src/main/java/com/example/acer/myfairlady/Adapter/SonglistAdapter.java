package com.example.acer.myfairlady.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.acer.myfairlady.R;
import com.example.acer.myfairlady.entity.Song;

import java.util.List;

/**
 * Created by acer on 2016/3/12.
 */
public class SonglistAdapter extends BaseAdapter {
    private List<Song> mData = null;
    private Context mContext = null;
    private boolean mMenuVisible = true;
    /**
     * 播放时为相应播放条目显示一个播放标记
     */
    private int mActivateItemPos = -1;

    public SonglistAdapter(Context c, List<Song> songList) {
        mContext = c;
        mData = songList;
    }

    public void setData(List<Song> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<Song> getData() {
        return mData;
    }

    /**
     * 让指定位置的条目显示一个正在播放标记（活动状态标记）
     */
    public void setSpecifiedIndicator(int position) {
        mActivateItemPos = position;
        notifyDataSetChanged();
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
    public Song getItem(int position) {
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
                    R.layout.item_song, null);
            holder.titlesong = (TextView) convertView
                    .findViewById(R.id.tv_songname);
            holder.artist = (TextView) convertView
                    .findViewById(R.id.tv_artist);
            holder.indicator = convertView.findViewById(R.id.play_indicator);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mActivateItemPos == position) {
            holder.indicator.setVisibility(View.VISIBLE);
        } else {
            holder.indicator.setVisibility(View.INVISIBLE);
        }

        holder.titlesong.setText(mData.get(position).getTitle());
        holder.artist.setText(""
                + mData.get(position).getArtist());

        return convertView;
    }

    static class ViewHolder {
        TextView titlesong;
        TextView artist;
        View indicator;
    }
}

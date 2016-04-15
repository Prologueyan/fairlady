package com.example.acer.myfairlady.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.acer.myfairlady.Adapter.AlbumAdapter;
import com.example.acer.myfairlady.Loader.AlbumInfoRetrieveLoader;
import com.example.acer.myfairlady.R;
import com.example.acer.myfairlady.entity.AlbumInfo;

import java.util.List;

/**
 * Created by acer on 2016/3/15.
 */
public class AlbumFragment extends android.support.v4.app.Fragment {

    private List<AlbumInfo> mAlbums;
    private ListView mListView;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.album, container, false);
        initView();
        return view;
    }

    private void initView() {
        mListView = (ListView) view.findViewById(R.id.lv_album);

        AlbumInfoRetrieveLoader loader = new AlbumInfoRetrieveLoader(getContext(), null, null, null);
        mAlbums = loader.loadInBackground();
        mListView.setAdapter(new AlbumAdapter(getContext(), mAlbums));

    }

}

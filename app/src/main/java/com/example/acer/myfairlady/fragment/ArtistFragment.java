package com.example.acer.myfairlady.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.acer.myfairlady.Adapter.ArtistAdapter;
import com.example.acer.myfairlady.Loader.ArtistInfoRetrieveLoader;
import com.example.acer.myfairlady.R;
import com.example.acer.myfairlady.entity.Artist;

import java.util.List;

/**
 * Created by acer on 2016/3/12.
 */
public class ArtistFragment extends android.support.v4.app.Fragment {

    private List<Artist> mArtists;
    private ListView mListView;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.artist, container, false);
        initView();
        return view;
    }

    private void initView() {
        mListView = (ListView) view.findViewById(R.id.lv_artist);

        ArtistInfoRetrieveLoader loader = new ArtistInfoRetrieveLoader(getContext(), null, null, null);
        mArtists = loader.loadInBackground();
        mListView.setAdapter(new ArtistAdapter(getContext(), mArtists));

    }
}

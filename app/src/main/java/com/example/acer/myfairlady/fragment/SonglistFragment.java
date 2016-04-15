package com.example.acer.myfairlady.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.acer.myfairlady.Activity.MainActivity;
import com.example.acer.myfairlady.Adapter.SonglistAdapter;
import com.example.acer.myfairlady.Listener.OnPlaybackStateChangeListener;
import com.example.acer.myfairlady.Loader.MusicRetrieveLoader;
import com.example.acer.myfairlady.R;
import com.example.acer.myfairlady.Service.MusicService;
import com.example.acer.myfairlady.Util.Constant;
import com.example.acer.myfairlady.entity.AlbumInfo;
import com.example.acer.myfairlady.entity.Artist;
import com.example.acer.myfairlady.entity.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2016/3/11.
 */
public class SonglistFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener {

    // 调试用的标记
    private final String TAG = this.getClass().getSimpleName();
    /**
     * 手势检测
     */
    private GestureDetector mDetector = null;

    private MainActivity mActivity = null;

    private ListView mListview = null;
    private View view = null;
    private View mView_PlayAll = null;
    private ImageView songimage = null;
    private ImageView playorstop = null;
    private ImageView next = null;
    private TextView name_song = null;
    private TextView artist_song = null;

    private Bundle mCurrentPlayInfo = null;

    private String mSortOrder = MediaStore.Audio.Media.TITLE_KEY;

    private boolean mHasNewData = false;
    private boolean mIsPlay = false;

    private SonglistAdapter mAdapter = null;
    private List<Song> mOriginalData = new ArrayList<Song>();
    private List<Song> mShowData = new ArrayList<Song>();

    private Artist mArtistInfo = null;
    private AlbumInfo mAlbumInfo = null;
    private Song mToDeleteTrack = null;
    private Song mPlayingTrack = null;

    private List<Song> songList;
    private MusicService.MusicPlaybackLocalBinder mMusicServiceBinder = null;

    private SharedPreferences mSystemPreferences = null;

    private InputMethodManager mInputMethodManager = null;

    /**
     * 与Service连接时交互的类
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mMusicServiceBinder = (MusicService.MusicPlaybackLocalBinder) service;
            mMusicServiceBinder
                    .registerOnPlaybackStateChangeListener(mOnPlaybackStateChangeListener);
            mCurrentPlayInfo = mMusicServiceBinder.getCurrentPlayInfo();
        }

        // 与服务端连接异常丢失时才调用，调用unBindService不调用此方法哎
        public void onServiceDisconnected(ComponentName className) {
        }
    };

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "onAttach");
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            mActivity = (MainActivity) activity;
        }
        mSystemPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
//        mSystemPreferences
//                .registerOnSharedPreferenceChangeListener(mFilterPreferenceChangedListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mInputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "oncreateview");

        view = inflater.inflate(R.layout.songlist, container, false);

        mView_PlayAll = view.findViewById(R.id.play_all);

        mListview = (ListView) view.findViewById(R.id.lv_songlist);

        songimage = (ImageView) view.findViewById(R.id.song_image);
        playorstop = (ImageView) view.findViewById(R.id.play_stop_bottom);
        next = (ImageView) view.findViewById(R.id.next_bottom);
        name_song = (TextView) view.findViewById(R.id.song_name_bottom);
        artist_song = (TextView) view.findViewById(R.id.artist_name_bottom);

//        Bundle args = getArguments();
//        if (args != null) {
//            switch (args.getInt(Constant.PARENT)) {
//                case Constant.START_FROM_LOCAL_MUSIC:
//                    mOverflowPopupMenu.getMenuInflater().inflate(
//                            R.menu.popup_local_music_list,
//                            mOverflowPopupMenu.getMenu());
//                    break;
//                default:
//                    mOverflowPopupMenu.getMenuInflater().inflate(
//                            R.menu.popup_track_list, mOverflowPopupMenu.getMenu());
//                    break;
//            }
//        }

        return view;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        handleArguments();
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        // 在Fragment可见时绑定服务 ，以使服务可以发送消息过来
        getActivity().bindService(
                new Intent(getActivity(), MusicService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
//        if (mActivity instanceof MainActivity) {
//            mActivity.registerBackKeyPressedListener(this);
//        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
//        mActivity.unregisterBackKeyPressedListener(this);
        // Fragment不可见时，无需更新UI，取消服务绑定
        mActivity.unbindService(mServiceConnection);
        mMusicServiceBinder
                .unregisterOnPlaybackStateChangeListener(mOnPlaybackStateChangeListener);
        mMusicServiceBinder = null;
//        getActivity().unregisterReceiver(mExternalStorageReceiver);
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
//        mSystemPreferences
//                .unregisterOnSharedPreferenceChangeListener(mFilterPreferenceChangedListener);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        mPlayingTrack = null;
        mAdapter = null;
        mActivity = null;
        mArtistInfo = null;
        mAlbumInfo = null;
        mCurrentPlayInfo = null;
        mShowData.clear();
        mShowData = null;
        mOriginalData.clear();
        mOriginalData = null;
    }

    private void initView() {

        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
        String where = "(" + MediaStore.Audio.Media.DATA
                + " like'%.mp3' or " + MediaStore.Audio.Media.DATA + " like'%.wma') and "
                + MediaStore.Audio.Media.DURATION + " > " + 1000 * 60 + " and "
                + MediaStore.Audio.Media.SIZE + " > " + 1024;

        MusicRetrieveLoader loader = new MusicRetrieveLoader(getContext(), where, null, MediaStore.Audio.Media.TITLE_KEY);
        songList = loader.loadInBackground();
        mListview.setOnItemClickListener(this);
        // 创建一个空的适配器，用来显示加载的数据，适配器内容稍后由Loader填充
        mAdapter = new SonglistAdapter(getActivity(), songList);
        // 为ListView绑定数据适配器
        mListview.setAdapter(mAdapter);
        // 全部播放按钮----------------------------------------------------------
        mView_PlayAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mHasNewData && mMusicServiceBinder != null) {
                    mMusicServiceBinder.setCurrentPlayList(mAdapter.getData());
                }
                mHasNewData = false;
                Intent intent = new Intent(MusicService.ACTION_PLAY);
                mActivity.startService(intent);
                mActivity.switchToPlayer();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startService(new Intent(
                        MusicService.ACTION_NEXT));
            }
        });
        playorstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlay) {
                    mIsPlay = false;
                    playorstop.setImageResource(R.drawable.play);
                    mActivity.startService(new Intent(
                            MusicService.ACTION_PAUSE));
                } else {
                    mIsPlay = true;
                    playorstop.setImageResource(R.drawable.icon_pause);
                    mActivity.startService(new Intent(
                            MusicService.ACTION_PLAY));
                }
            }
        });


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mHasNewData = true && mMusicServiceBinder != null) {
            Log.i(TAG, "setplaylist");
            mMusicServiceBinder.setCurrentPlayList(mAdapter.getData());
        }


        mIsPlay = true;
        playorstop.setImageResource(R.drawable.icon_pause);

        mHasNewData = false;
        Intent intent = new Intent(getContext(), MusicService.class);
        intent.setAction(MusicService.ACTION_PLAY);
        intent.putExtra(Constant.REQUEST_PLAY_ID, mAdapter.getItem(position)
                .getId());
        intent.putExtra(Constant.CLICK_ITEM_IN_LIST, true);
        mActivity.startService(intent);
        mActivity.switchToPlayer();


    }

    private OnPlaybackStateChangeListener mOnPlaybackStateChangeListener = new OnPlaybackStateChangeListener() {

        @Override
        public void onMusicPlayed() {

        }

        @Override
        public void onMusicPaused() {

        }

        @Override
        public void onMusicStopped() {

        }

        @Override
        public void onPlayNewSong(Song playingSong) {
            mPlayingTrack = playingSong;
            mAdapter.setSpecifiedIndicator(MusicService.seekPosInListById(
                    mAdapter.getData(), playingSong.getId()));
        }

        @Override
        public void onPlayModeChanged(int playMode) {

        }

        @Override
        public void onPlayProgressUpdate(int currentMillis) {

        }

    };

    /**
     * 处理从启动处传递过来的参数
     */
    private void handleArguments() {
        // 如果有谁传递数据过来了，就设置一下
        Bundle args = getArguments();
        if (args != null) {
            switch (args.getInt(Constant.PARENT)) {
                case Constant.START_FROM_ARTIST:
                    // 如果是从歌手列表里启动的
                    mArtistInfo = args.getParcelable(Artist.class
                            .getSimpleName());
                    if (mArtistInfo != null) {
                        // 更新标题
                        if (!mArtistInfo.getArtist().equals("<unknown>")) {
                            name_song.setText(mArtistInfo.getArtist() + "("
                                    + mArtistInfo.getNumber_of_tracks() + ")");
                        } else {
                            name_song.setText(getResources().getString(
                                    R.string.unknown_artist)
                                    + "(" + mArtistInfo.getNumber_of_tracks() + ")");
                        }
                        setTitleLeftDrawable();
                    }
                    break;
//                case Constant.START_FROM_PLAYLIST:
//                    // 如果是从播放列表里启动的
//                    mPlaylistInfo = args.getParcelable(PlaylistInfo.class
//                            .getSimpleName());
//                    if (mPlaylistInfo != null) {
//                        // 更新标题
//                        name_song.setText(mPlaylistInfo.getPlaylistName() + "("
//                                + mPlaylistInfo.getNumOfMembers() + ")");
//                        setTitleLeftDrawable();
//                    }
//                    break;
                case Constant.START_FROM_ALBUM:
                    // 如果是从专辑列表里启动的
                    mAlbumInfo = args
                            .getParcelable(AlbumInfo.class.getSimpleName());
                    if (mAlbumInfo != null) {
                        // 更新标题
                        name_song.setText(mAlbumInfo.getAlbumName() + "("
                                + mAlbumInfo.getNumberOfSongs() + ")");
                        setTitleLeftDrawable();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void setTitleLeftDrawable() {
        name_song.setClickable(true);
        Drawable title_drawable = getResources().getDrawable(
                R.drawable.btn_titile_back);
        title_drawable.setBounds(0, 0, title_drawable.getIntrinsicWidth(),
                title_drawable.getIntrinsicHeight());
        name_song.setCompoundDrawables(title_drawable, null, null, null);
        name_song.setBackgroundResource(R.drawable.button_backround_light);
    }

//    private BroadcastReceiver mExternalStorageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)
//                    || intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)
//                    || intent.getAction().equals(
//                    Intent.ACTION_MEDIA_BAD_REMOVAL)) {
//                // SD卡移除，设置列表为空
//                mView_TrackOperations.setVisibility(View.GONE);
//                mView_MoreFunctions.setClickable(false);
//                name_song.setText("");
//                mView_ListView.setEmptyView(mView_EmptyNoStorage);
//                mAdapter.setData(null);
//
//                // 提示SD卡不可用
//                Toast.makeText(getActivity(), R.string.sdcard_cannot_use,
//                        Toast.LENGTH_SHORT).show();
//            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
//                // SD卡正常挂载,重新加载数据
//                mView_ListView.setEmptyView(mView_EmptyLoading);
//                TrackBrowserFragment.this.getLoaderManager().restartLoader(
//                        MUSIC_RETRIEVE_LOADER, null, TrackBrowserFragment.this);
//            }
//
//        }
////    };
//    /** 在装载器需要被创建时执行此方法，这里只有一个装载器，所以我们不必关心装载器的ID */
//    @Override
//    public Loader<List<Song>> onCreateLoader(int id, Bundle args) {
//        Log.i(TAG, "onCreateLoader");
//
//        // 数据库查询条件子句
//        // StringBuffer select = new StringBuffer("(" + Media.DATA
//        // + " like'%.mp3' or " + Media.DATA + " like'%.wma')");
//        StringBuffer select = new StringBuffer(" 1=1 ");
//
//        // 检查系统设置，是否需要按文件大小过滤
//        if (mSystemPreferences.getBoolean(SettingFragment.KEY_FILTER_BY_SIZE,
//                true)) {
//            // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
//            select.append(" and " + MediaStore.Audio.Media.SIZE + " > " + Constant.FILTER_SIZE);
//        }
//
//        // 检查系统设置，是否需要按歌曲时长过滤
//        if (mSystemPreferences.getBoolean(
//                SettingFragment.KEY_FILTER_BY_DURATION, true)) {
//            select.append(" and " + MediaStore.Audio.Media.DURATION + " > "
//                    + Constant.FILTER_DURATION);
//        }
//
//        if (mArtistInfo != null) {
//            select.append(" and " + MediaStore.Audio.Media.ARTIST + " = '"
//                    + mArtistInfo.getArtist() + "'");
//        } else if (mFolderInfo != null) {
//            select.append(" and " + MediaStore.Audio.Media.DATA + " like '"
//                    + mFolderInfo.getFolderPath() + File.separator + "%'");
//        } else if (mPlaylistInfo != null) {
//            select.append(" and " + MediaStore.Audio.Media._ID + " in (select "
//                    + MediaStore.Audio.Playlists.Members.AUDIO_ID
//                    + " from audio_playlists_map where "
//                    + MediaStore.Audio.Playlists.Members.PLAYLIST_ID + "="
//                    + mPlaylistInfo.getId() + ")");
//        } else if (mAlbumInfo != null) {
//            select.append(" and " + MediaStore.Audio.Media.ALBUM_ID + " = "
//                    + mAlbumInfo.getAlbumId());
//        }
//
//        MusicRetrieveLoader loader = new MusicRetrieveLoader(getActivity(),
//                select.toString(), null, mSortOrder);
//
//
//        // 创建并返回一个Loader
//        return loader;
//    }

//    @Override
//    public void onLoadFinished(Loader<List<Song>> loader,
//                               List<Song> data) {
//        Log.i(TAG, "onLoadFinished");
//        mHasNewData = true;
//
//
//        mOriginalData.clear();
//        mOriginalData.addAll(data);
//        mShowData.clear();
//        mShowData.addAll(data);
//
//
//        mAdapter.setData(data);
//
//        // 每次加载新的数据设置一下标题中的歌曲数目
//        if (getArguments() != null) {
//            switch (getArguments().getInt(Constant.PARENT)) {
//                case Constant.START_FROM_LOCAL_MUSIC:
//                    name_song.setText(getResources()
//                            + "(" + data.size() + ")");
//                    break;
//                case Constant.START_FROM_ARTIST:
//                    name_song.setText(mArtistInfo.getArtist() + "("
//                            + data.size() + ")");
//                    break;
//                case Constant.START_FROM_ALBUM:
//                    name_song.setText(mAlbumInfo.getAlbumName() + "("
//                            + data.size() + ")");
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        if (mCurrentPlayInfo != null) {
//            initCurrentPlayInfo(mCurrentPlayInfo);
//        }
//    }
//
////    /** 此方法在提供给onLoadFinished()最后的一个游标准备关闭时调用，我们要确保不再使用它 */
////    @Override
////    public void onLoaderReset(Loader<List<Song>> loader) {
////        Log.i(TAG, "onLoaderReset");
////        mAdapter.setData(null);
//////        mView_TrackOperations.setVisibility(View.GONE);
//////        mView_MoreFunctions.setClickable(false);
////    }

    /**
     * 初始化当前播放信息
     */
    private void initCurrentPlayInfo(Bundle bundle) {
        mPlayingTrack = bundle.getParcelable(Constant.PLAYING_MUSIC_ITEM);

        if (mPlayingTrack != null) {
            mAdapter.setSpecifiedIndicator(MusicService.seekPosInListById(
                    mAdapter.getData(), mPlayingTrack.getId()));
        } else {
            mAdapter.setSpecifiedIndicator(-1);
        }

    }
}

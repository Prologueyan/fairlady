package com.example.acer.myfairlady.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.myfairlady.Listener.OnPlaybackStateChangeListener;
import com.example.acer.myfairlady.R;
import com.example.acer.myfairlady.Service.MusicService;
import com.example.acer.myfairlady.Util.Constant;
import com.example.acer.myfairlady.Util.TimeHelper;
import com.example.acer.myfairlady.entity.Song;

import co.mobiwise.library.MusicPlayerView;

/**
 * Created by acer on 2016/3/16.
 */
public class PlayerActivity extends android.support.v4.app.FragmentActivity {

    public static final String TAG = PlayerActivity.class.getSimpleName();

    private MusicPlayerView mpv;
    private boolean mIsPlay = false;
    private Song mPlaySong = null;
    private View relativelayout;
    private TextView tv_song;
    private TextView tv_singer;
    private ImageView im_like;
    private ImageView im_next;
    private ImageView im_previous;
    private ImageView im_playmode;
    private MusicService.MusicPlaybackLocalBinder mMusicServiceBinder = null;
    /**
     * 手势检测
     */
    private GestureDetector mDetector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "oncreate");
        super.onCreate(savedInstanceState);

        findViews();

        initViewsSetting();

    }


    private void findViews() {

        setContentView(R.layout.player);
        relativelayout = findViewById(R.id.relativelayout);
        tv_song = (TextView) findViewById(R.id.textViewSong);
        tv_singer = (TextView) findViewById(R.id.textViewSinger);
        im_next = (ImageView) findViewById(R.id.next);
        im_like = (ImageView) findViewById(R.id.like);
        im_previous = (ImageView) findViewById(R.id.previous);
        im_playmode = (ImageView) findViewById(R.id.play_mode);
        mpv = (MusicPlayerView) findViewById(R.id.mpv);


    }

    private void initViewsSetting() {


        mpv.setButtonColor(Color.DKGRAY);
        mpv.setProgressEmptyColor(Color.GRAY);
        mpv.setProgressLoadedColor(Color.BLUE);
        mpv.setTimeColor(Color.WHITE);

        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                } else {
                    mpv.start();
                }
            }
        });

        // 手势设置----------------------------------------------
        // 左滑切换至主页
        mDetector = new GestureDetector(getApplicationContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        // 从左向右滑动
                        if (e1.getX() - e2.getX() < -120) {
                            switchToMain();
                            return true;
                        }
                        return false;
                    }
                });
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (mDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        relativelayout.setOnTouchListener(gestureListener);


        // 播放控制-----------------------------------------------------------------
        // 播放模式--
        im_playmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicServiceBinder != null) {
                    mMusicServiceBinder.changePlayMode();
                }
            }
        });

        // 上一首--
        im_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerActivity.this.startService(new Intent(
                        MusicService.ACTION_PREVIOUS));
            }
        });

        // 播放、暂停
        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlay) {
                    PlayerActivity.this.startService(new Intent(
                            MusicService.ACTION_PAUSE));
                } else {
                    PlayerActivity.this.startService(new Intent(
                            MusicService.ACTION_PLAY));
                }
            }
        });

        // 下一首--
        im_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerActivity.this.startService(new Intent(
                        MusicService.ACTION_NEXT));
            }
        });


    }

    private void switchToMain() {
        Intent intent = new Intent(PlayerActivity.this,
                MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        this.finish();
    }


    /**
     * 与Service连接时交互的类
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "onServiceConnected");

            // 保持对Service的Binder引用，以便调用Service提供给客户端的方法
            mMusicServiceBinder = (MusicService.MusicPlaybackLocalBinder) service;

            // 传递LyricListener对象给Service，以便歌词发生变化时通知本Activity
//            mMusicServiceBinder.registerLyricListener(mLyricListener);

            // 传递OnPlaybackStateChangeListener对象给Service，以便音乐回放状态发生变化时通知本Activity
            mMusicServiceBinder
                    .registerOnPlaybackStateChangeListener(mOnPlaybackStateChangeListener);

            // 请求加载歌词
//            mMusicServiceBinder.requestLoadLyric();

            initCurrentPlayInfo(mMusicServiceBinder.getCurrentPlayInfo());
        }

        // 与服务端连接异常丢失时才调用，调用unBindService不调用此方法哎
        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "onServiceDisconnected");

            if (mMusicServiceBinder != null) {
                mMusicServiceBinder
                        .unregisterOnPlaybackStateChangeListener(mOnPlaybackStateChangeListener);
            }
        }
    };

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();

        bindService(new Intent(getApplicationContext(), MusicService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();

        // 本界面不可见时取消绑定服务，服务端无需发送消息过来，不可见时无需更新界面
        unbindService(mServiceConnection);
        if (mMusicServiceBinder != null) {
            mMusicServiceBinder
                    .unregisterOnPlaybackStateChangeListener(mOnPlaybackStateChangeListener);
            mMusicServiceBinder = null;
        }

    }

    /**
     * 初始化当前播放信息
     */
    private void initCurrentPlayInfo(Bundle bundle) {

        int playMode = bundle.getInt(Constant.PLAY_MODE);
        int currentPlayerState = bundle.getInt(Constant.PLAYING_STATE);
        Song playingSong = bundle
                .getParcelable(Constant.PLAYING_MUSIC_ITEM);

        // 根据播放状态，设置播放按钮的图片
        if (currentPlayerState == MusicService.State.Playing
                || currentPlayerState == MusicService.State.Preparing) {
            mIsPlay = true;
//            mView_ib_play_or_pause.setImageResource(R.drawable.button_pause);
            mpv.stop();

        } else {
            mIsPlay = false;
//            mView_ib_play_or_pause.setImageResource(R.drawable.button_play);
            mpv.start();
        }

        // 设置歌曲标题、时长、当前播放时间、当前播放进度
        mPlaySong = playingSong;
        if (playingSong != null) {
            mpv.setMax(Integer.parseInt(TimeHelper
                    .milliSecondsToFormatTimeString(playingSong.getDuration())));
            tv_song.setText(playingSong.getTitle());
            tv_singer.setText(playingSong.getArtist());


        }

        // 设置播放模式按钮图片
        setPlayModeImage(playMode);
    }

    /**
     * 根据播放模式设置播放模式按钮的图标
     *
     * @param mode 音乐播放模式
     */
    private void setPlayModeImage(int mode) {
        switch (mode) {
            case MusicService.PlayMode.REPEAT_SINGLE:
                im_playmode
                        .setImageResource(R.drawable.button_playmode_repeat_single);
                // Toast.makeText(getApplicationContext(),
                // getResources().getString(R.string.playmode_repeat_single),
                // Toast.LENGTH_SHORT).show();
                break;
            case MusicService.PlayMode.REPEAT:
                im_playmode
                        .setImageResource(R.drawable.button_playmode_repeat);
                // Toast.makeText(getApplicationContext(),
                // getResources().getString(R.string.playmode_repeat),
                // Toast.LENGTH_SHORT).show();
                break;
            case MusicService.PlayMode.SEQUENTIAL:
                im_playmode
                        .setImageResource(R.drawable.button_playmode_sequential);
                // Toast.makeText(getApplicationContext(),
                // getResources().getString(R.string.playmode_sequential),
                // Toast.LENGTH_SHORT).show();
                break;
            case MusicService.PlayMode.SHUFFLE:
                im_playmode
                        .setImageResource(R.drawable.button_playmode_shuffle);
                // Toast.makeText(getApplicationContext(),
                // getResources().getString(R.string.playmode_shuffle),
                // Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (!getSupportFragmentManager().popBackStackImmediate()) {
            switchToMain();
        }
    }

    private OnPlaybackStateChangeListener mOnPlaybackStateChangeListener = new OnPlaybackStateChangeListener() {

        @Override
        public void onMusicPlayed() {
            // 音乐播放时，播放按钮设置为暂停的图标（意为点击暂停）
            mIsPlay = true;
//            mView_ib_play_or_pause.setImageResource(R.drawable.button_pause);
        }

        @Override
        public void onMusicPaused() {
            // 音乐暂停时，播放按钮设置为播放的图标（意为点击播放）
            mIsPlay = false;
//            mView_ib_play_or_pause.setImageResource(R.drawable.button_play);
        }

        @Override
        public void onMusicStopped() {
            // 音乐播放停止时，清空歌曲信息的显示
            mIsPlay = false;
//            mView_ib_play_or_pause.setImageResource(R.drawable.button_play);
            mpv.setMax(Integer.parseInt(TimeHelper
                    .milliSecondsToFormatTimeString(0)));
            tv_song.setText("");

            mpv.setProgress(0);
//            mLyricAdapter.setLyric(null);
//            mLyricAdapter.notifyDataSetChanged();
            mPlaySong = null;
        }

        @Override
        public void onPlayNewSong(Song playingSong) {
            // 播放新的歌曲时，更新显示的歌曲信息
            mPlaySong = playingSong;
            if (playingSong != null) {
                mpv.setMax(Integer.parseInt(TimeHelper
                        .milliSecondsToFormatTimeString(playingSong
                                .getDuration())));
                tv_song.setText(playingSong.getTitle());
                mpv.setProgress(0);
            }
            // 歌词秀清空
//            mView_tv_lyric_empty.setText(R.string.lyric_Loading);
        }

        @Override
        public void onPlayModeChanged(int playMode) {
            setPlayModeImage(playMode);
        }

        @Override
        public void onPlayProgressUpdate(int currentMillis) {
            // 更新当前播放时间
//            mView_tv_current_time.setText(TimeHelper
//                    .milliSecondsToFormatTimeString(currentMillis));
            // 更新当前播放进度
//            mpv.setProgress(currentMillis
//                    * mView_sb_song_progress.getMax()
//                    / (int) mPlaySong.getDuration());
        }
    };
}

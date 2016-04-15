package com.example.acer.myfairlady.Loader;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.acer.myfairlady.entity.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2016/3/11.
 */
public class MusicRetrieveLoader extends AsyncTaskLoader<List<Song>> {

    private final String Tag = MusicRetrieveLoader.class.getSimpleName();
    /**
     * 要从MediaStore检索的列
     */
    private final String[] mProjection = new String[]{MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DISPLAY_NAME};

    // 数据库查询相关参数
    private String mSelection = null;
    private String[] mSelectionArgs = null;
    private String mSortOrder = null;

    private ContentResolver mContentResolver = null;

    private List<Song> mMusicItemList = null;


    int index_id;
    int index_title;
    int index_data;
    int index_artist;
    int index_album;
    int index_duration;
    int index_size;
    int index_displayname;


    public MusicRetrieveLoader(Context context, String selection,
                               String[] selectionArgs, String sortOrder) {
        super(context);
        this.mSelection = selection;
        this.mSelectionArgs = selectionArgs;
        this.mSortOrder = sortOrder;
        mContentResolver = context.getContentResolver();
    }

    @Override
    public List<Song> loadInBackground() {

        Log.i("log", "loadInBackground");
        List<Song> itemsList = new ArrayList<Song>();
        Song item = null;

        Cursor cursor = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mProjection, mSelection, mSelectionArgs, mSortOrder);

        // 将数据库查询结果保存到一个List集合中(存放在RAM)
        if (cursor != null) {
            index_id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            index_title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            index_data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            index_artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            index_album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            index_duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            index_size = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            index_displayname = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            while (cursor.moveToNext()) {

//                // 如果设置了文件夹过滤
//                if (mFolerPattern != null) {
//                    // 过滤出指定的文件夹下的文件，忽略子目录
//                    Matcher matcher = mFolerPattern.matcher(cursor
//                            .getString(index_data));
//                    // 如果是以xxx.xxx结尾的路径，则就是当前目录下的文件了
//                    if (matcher.find() && matcher.group().matches(".*\\..*")) {
//                        item = createNewItem(cursor);
//                    } else {// 是文件夹就忽略了
//                        continue;
//                    }
//                } else {// 正常的创建新的条目
//                    item = createNewItem(cursor);
//                }
                item = createNewItem(cursor);
                itemsList.add(item);
            }
            cursor.close();
        }

        Log.i("log", "itemListlength" + itemsList.size());
        return itemsList;
    }

    private Song createNewItem(Cursor cursor) {
        Song item = new Song();
        item.setTitle(cursor.getString(index_title));
        item.setArtist(cursor.getString(index_artist));
        item.setId(cursor.getLong(index_id));
        item.setAlbum(cursor.getString(index_album));
        item.setDuration(cursor.getLong(index_duration));
        item.setSize(cursor.getLong(index_size));
        item.setData(cursor.getString(index_data));
        return item;
    }

    @Override
    public void deliverResult(List<Song> data) {
        Log.i("log", "deliverResult");
        if (isReset()) {
            // An async query came in while the loader is stopped. We
            // don't need the result.
            if (data != null) {
                onReleaseResources(data);
            }
        }
        List<Song> oldList = data;
        mMusicItemList = data;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldList != null) {
            onReleaseResources(oldList);
        }
    }

    protected void onReleaseResources(List<Song> data) {
        Log.i("log", "onReleaseResources");
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }

    @Override
    protected void onStartLoading() {
        Log.i("log", "onStartLoading");
        if (mMusicItemList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mMusicItemList);
        }
        // If the data has changed since the last time it was loaded
        // or is not currently available, start a load.
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        Log.i("log", "onStartLoading");
        super.onStopLoading();
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(List<Song> data) {
        super.onCanceled(data);
        Log.i("log", "onCanceled");
        // At this point we can release the resources associated with 'data'
        // if needed.
        onReleaseResources(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        Log.i("log", "onReset");
        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'data'
        // if needed.
        if (mMusicItemList != null) {
            onReleaseResources(mMusicItemList);
            mMusicItemList = null;
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }
}

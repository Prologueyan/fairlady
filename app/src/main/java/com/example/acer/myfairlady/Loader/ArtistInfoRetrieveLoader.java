package com.example.acer.myfairlady.Loader;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.acer.myfairlady.entity.Artist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2016/3/11.
 */
public class ArtistInfoRetrieveLoader extends AsyncTaskLoader<List<Artist>> {

    private final String TAG = ArtistInfoRetrieveLoader.class.getSimpleName();

    /**
     * 要从MediaStore检索的列
     */
    private final String[] mProjection = new String[]{
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS};

    // 数据库查询相关参数
    private String mSelection = null;
    private String[] mSelectionArgs = null;
    private String mSortOrder = null;

    private ContentResolver mContentResolver = null;

    private List<Artist> mArtistList = null;

    public ArtistInfoRetrieveLoader(Context context, String selection,
                                    String[] selectionArgs, String sortOrder) {
        super(context);
        this.mSelection = selection;
        this.mSelectionArgs = selectionArgs;
        if (sortOrder == null) {
            // 默认按艺术家名称排序
            this.mSortOrder = MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                    + " desc";
        }
        this.mSortOrder = sortOrder;
        mContentResolver = context.getContentResolver();
    }

    @Override
    public List<Artist> loadInBackground() {
        Log.i(TAG, "loadInBackground");
        Cursor cursor = mContentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, mProjection,
                mSelection, mSelectionArgs, mSortOrder);

        List<Artist> itemsList = new ArrayList<Artist>();

        // 将数据库查询结果保存到一个List集合中(存放在RAM)
        if (cursor != null) {
            int index_artist = cursor
                    .getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            int index_number_of_tracks = cursor
                    .getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
            int index_number_of_albums = cursor
                    .getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
            while (cursor.moveToNext()) {
                Artist item = new Artist();
                item.setArtist(cursor.getString(index_artist));
                item.setNumber_of_tracks(cursor.getInt(index_number_of_tracks));
                item.setNumber_of_albums(cursor.getInt(index_number_of_albums));
                itemsList.add(item);
            }
            cursor.close();
        }
        // 如果没有扫描到媒体文件，itemsList的size为0，因为上面new过了
        return itemsList;
    }

    @Override
    public void deliverResult(List<Artist> data) {
        Log.i(TAG, "deliverResult");
        if (isReset()) {
            // An async query came in while the loader is stopped. We
            // don't need the result.
            if (data != null) {
                onReleaseResources(data);
            }
        }
        List<Artist> oldList = data;
        mArtistList = data;

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

    protected void onReleaseResources(List<Artist> data) {
        Log.i(TAG, "onReleaseResources");
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }

    @Override
    protected void onStartLoading() {
        Log.i(TAG, "onStartLoading");
        if (mArtistList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mArtistList);
        }
        // If the data has changed since the last time it was loaded
        // or is not currently available, start a load.
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        Log.i(TAG, "onStartLoading");
        super.onStopLoading();
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(List<Artist> data) {
        super.onCanceled(data);
        Log.i(TAG, "onCanceled");
        // At this point we can release the resources associated with 'data'
        // if needed.
        onReleaseResources(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        Log.i(TAG, "onReset");
        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'data'
        // if needed.
        if (mArtistList != null) {
            onReleaseResources(mArtistList);
            mArtistList = null;
        }
    }

    @Override
    protected void onForceLoad() {
        // TODO Auto-generated method stub
        super.onForceLoad();
    }
}

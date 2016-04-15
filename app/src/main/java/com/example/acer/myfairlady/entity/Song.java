package com.example.acer.myfairlady.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by acer on 2016/3/11.
 */
public class Song implements Parcelable {

    String title;
    String artist;
    String displayname;
    Long id;
    String album;
    String data;
    Long duration;
    Long size;

    public Song() {
    }


    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDisplayname() {
        return displayname;
    }

    public Long getId() {
        return id;
    }

    public String getAlbum() {
        return album;
    }

    public String getData() {
        return data;
    }

    public Long getDuration() {
        return duration;
    }

    public Long getSize() {
        return size;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //写数据进行保存
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("title", title);
        bundle.putString("album", album);
        bundle.putString("artist", artist);
        bundle.putString("data", data);
        bundle.putLong("size", size);
        bundle.putLong("duration", duration);
        dest.writeBundle(bundle);

    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    private Song(Parcel in) {

        Bundle bundle = in.readBundle();
        id = bundle.getLong("id");
        title = bundle.getString("title");
        album = bundle.getString("album");
        artist = bundle.getString("artist");
        data = bundle.getString("data");
        size = bundle.getLong("size");
        duration = bundle.getLong("duration");
    }

}

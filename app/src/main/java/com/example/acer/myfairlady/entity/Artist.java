package com.example.acer.myfairlady.entity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by acer on 2016/3/11.
 */
public class Artist implements Parcelable {

    private String artist;
    private int number_of_tracks;
    private int number_of_albums;

    public Artist() {
    }


    public String getArtist() {
        return artist;
    }

    public int getNumber_of_tracks() {
        return number_of_tracks;
    }

    public int getNumber_of_albums() {
        return number_of_albums;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setNumber_of_tracks(int number_of_tracks) {
        this.number_of_tracks = number_of_tracks;
    }

    public void setNumber_of_albums(int number_of_albums) {
        this.number_of_albums = number_of_albums;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("artist", artist);
        bundle.putInt("number_of_tracks", number_of_tracks);
        bundle.putInt("number_of_albums", number_of_albums);
        dest.writeBundle(bundle);
    }

    private Artist(Parcel in) {
        Bundle bundle = in.readBundle();
        artist = bundle.getString(artist);
        number_of_albums = bundle.getInt("number_of_albums");
        number_of_tracks = bundle.getInt("number_of_tracks");

    }

}

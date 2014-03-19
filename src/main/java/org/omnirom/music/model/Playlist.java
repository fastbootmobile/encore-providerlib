package org.omnirom.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Playlist implements Parcelable {
    private List<Song> mSongs;

    public static final Creator<Playlist> CREATOR = new
            Creator<Playlist>() {
                public Playlist createFromParcel(Parcel in) {
                    return new Playlist(in);
                }

                public Playlist[] newArray(int size) {
                    return new Playlist[size];
                }
            };

    public Playlist(final Parcel in) {
        readFromParcel(in);
    }

    public Playlist() {
        mSongs = new ArrayList<Song>();
    }

    public void addSong(Song s) {
        mSongs.add(s);
    }

    public void removeSong(Song s) {
        mSongs.remove(s);
    }

    public void removeSong(int i) {
        mSongs.remove(i);
    }

    public Iterator<Song> iterator() {
        return mSongs.iterator();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeList(mSongs);
    }

    public void readFromParcel(Parcel in) {
        mSongs = in.readArrayList(Song.class.getClassLoader());
    }
}

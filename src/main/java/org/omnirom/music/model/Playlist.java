package org.omnirom.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Playlist implements Parcelable {
    private ArrayList<String> mSongs;
    private String mRef;
    private String mName;

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

    public Playlist(final String ref) {
        mRef = ref;
        mSongs = new ArrayList<String>();
    }

    public String getRef() {
        return mRef;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void addSong(String s) {
        mSongs.add(s);
    }

    public void removeSong(String s) {
        mSongs.remove(s);
    }

    public void removeSong(int i) {
        mSongs.remove(i);
    }

    public Iterator<String> songs() {
        return mSongs.iterator();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mRef);
        out.writeString(mName);
        out.writeList(mSongs);
    }

    public void readFromParcel(Parcel in) {
        mRef = in.readString();
        mName = in.readString();
        mSongs = in.readArrayList(String.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "'" + mName + "', " + mSongs.size() + " songs, ref: " + mRef;
    }
}

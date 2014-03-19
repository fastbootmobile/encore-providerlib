package org.omnirom.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Iterator;
import java.util.List;

public class Album implements Parcelable {
    private List<Song> mSongs;
    private String mName;

    public static final Creator<Album> CREATOR = new
            Creator<Album>() {
                public Album createFromParcel(Parcel in) {
                    return new Album(in);
                }

                public Album[] newArray(int size) {
                    return new Album[size];
                }
            };

    public Album(final String name) {
        mName = name;
    }

    public Album(final Parcel in) {
        readFromParcel(in);
    }

    public String getName() {
        return mName;
    }

    public void addSong(Song s) {
        mSongs.add(s);
    }

    public Iterator<Song> songs() {
        return mSongs.iterator();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeList(mSongs);
    }

    public void readFromParcel(Parcel in) {
        mName = in.readString();
        mSongs = in.readArrayList(Song.class.getClassLoader());
    }
}

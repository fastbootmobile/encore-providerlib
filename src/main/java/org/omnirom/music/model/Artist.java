package org.omnirom.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Iterator;
import java.util.List;

public class Artist implements Parcelable {
    private String mName;
    private List<Album> mAlbums;

    public static final Creator<Artist> CREATOR = new
            Creator<Artist>() {
                public Artist createFromParcel(Parcel in) {
                    return new Artist(in);
                }

                public Artist[] newArray(int size) {
                    return new Artist[size];
                }
            };

    private Artist(final Parcel in) {
        readFromParcel(in);
    }

    private Artist(final String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void addAlbum(Album a) {
        mAlbums.add(a);
    }

    public Iterator<Album> getAlbums() {
        return mAlbums.iterator();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeList(mAlbums);
    }

    public void readFromParcel(Parcel in) {
        mName = in.readString();
        mAlbums = in.readArrayList(Album.class.getClassLoader());
    }
}

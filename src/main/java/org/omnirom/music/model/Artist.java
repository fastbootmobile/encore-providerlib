package org.omnirom.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Iterator;
import java.util.List;

public class Artist implements Parcelable {
    private String mName;
    private String mRef;
    private List<String> mAlbums;

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

    private Artist(final String ref) {
        mRef = ref;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void addAlbum(String a) {
        mAlbums.add(a);
    }

    public Iterator<String> getAlbums() {
        return mAlbums.iterator();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mRef);
        out.writeString(mName);
        out.writeList(mAlbums);
    }

    public void readFromParcel(Parcel in) {
        mRef = in.readString();
        mName = in.readString();
        mAlbums = in.readArrayList(String.class.getClassLoader());
    }
}

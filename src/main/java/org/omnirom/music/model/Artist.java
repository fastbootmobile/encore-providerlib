package org.omnirom.music.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Artist extends BoundEntity {
    private String mName;
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

    public Artist(final Parcel in) {
        super(in);
    }

    public Artist(final String ref) {
        super(ref);
        mAlbums = new ArrayList<String>();
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

    public Iterator<String> albums() {
        return mAlbums.iterator();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(mName);
        out.writeList(mAlbums);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mName = in.readString();
        mAlbums = in.readArrayList(String.class.getClassLoader());
    }
}

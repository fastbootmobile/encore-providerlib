package org.omnirom.music.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Album extends BoundEntity {
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

    public Album(final Parcel in) {
        super(in);
    }

    public Album(final String ref) {
        super(ref);
        mSongs = new ArrayList<Song>();
    }

    public void setName(String name) {
        mName = name;
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
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(mName);
        out.writeList(mSongs);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mName = in.readString();
        mSongs = in.readArrayList(Song.class.getClassLoader());
    }
}

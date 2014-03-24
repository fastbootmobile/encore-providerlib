package org.omnirom.music.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Album extends BoundEntity {
    private List<String> mSongs;
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
        mSongs = new ArrayList<String>();
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

    public Iterator<String> songs() {
        return mSongs.iterator();
    }

    public int getSongsCount() { return mSongs.size(); }

    @Override
    public boolean isIdentical(Object other) {
        if (other instanceof Album) {
            Album remote = (Album) other;
            if (remote.getName().equals(getName()) && remote.getRef().equals(getRef())) {
                Iterator<String> remoteSongs = remote.songs();
                Iterator<String> ourSongs = songs();

                while (remoteSongs.hasNext()) {
                    String next = remoteSongs.next();

                    if (!ourSongs.hasNext()) {
                        return false;
                    } else if (!ourSongs.next().equals(next)) {
                        return false;
                    }
                }
            } else {
                return false;
            }

            return true;
        } else {
            return false;
        }
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
        mSongs = in.readArrayList(String.class.getClassLoader());
    }
}

package org.omnirom.music.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;

public class Playlist extends BoundEntity {
    private ArrayList<String> mSongs;
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
        super(in);
    }

    public Playlist(final String ref) {
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

    public void removeSong(String s) {
        mSongs.remove(s);
    }

    public void removeSong(int i) {
        mSongs.remove(i);
    }

    public Iterator<String> songs() {
        return mSongs.iterator();
    }

    public int getSongsCount() { return mSongs.size(); }

    @Override
    public boolean isIdentical(Object other) {
        if (other instanceof Playlist) {
            Playlist remote = (Playlist) other;
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

    @Override
    public String toString() {
        return "'" + mName + "', " + mSongs.size() + " songs, ref: " + getRef();
    }
}

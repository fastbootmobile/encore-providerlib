package org.omnirom.music.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by h4o on 06/07/2014.
 */
public class Genre  extends BoundEntity {
    private ArrayList<String> mSongs;
    private String mName;

    public static final Creator<Genre> CREATOR = new
            Creator<Genre>() {
                public Genre createFromParcel(Parcel in) {
                    return new Genre(in);
                }

                public Genre[] newArray(int size) {
                    return new Genre[size];
                }
            };

    public Genre(final Parcel in) {
        super(in);
    }

    public Genre(final String ref) {
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

    public List<String> songsList() {return mSongs;}

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

    public void setSong(int pos,String s){
        mSongs.set(pos,s);
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

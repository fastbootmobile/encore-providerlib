package org.omnirom.music.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an Album (release)
 */
public class Album extends BoundEntity {
    /**
     * A list of songs, in the order of the album
     */
    private List<String> mSongs;

    /**
     * The name of the album
     */
    private String mName;

    /**
     * The release year of the album
     */
    private int mYear;

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

    /**
     * Sets the name of the album
     * @param name The name of the album
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Returns the name of the album
     * @return The name of the album, or null if it hasn't been set by the provider
     */
    public String getName() {
        return mName;
    }

    /**
     * Adds a song to this album. Please note that songs must be added in order.
     * @param s The reference String of the song to add
     */
    public void addSong(String s) {
        mSongs.add(s);
    }

    /**
     * Returns an iterator of the songs references of this album, in order.
     * @return A String Iterator
     */
    public Iterator<String> songs() {
        return mSongs.iterator();
    }

    /**
     * Returns the number of songs in this album
     * @return The number of songs in this album
     */
    public int getSongsCount() { return mSongs.size(); }

    public void setYear(int year) {
        mYear = year;
    }

    /**
     * Returns the year of the album, or zero if not set
     * @return A release year (e.g. 2007), or 0 if not set
     */
    public int getYear() {
        return mYear;
    }

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
        out.writeInt(mYear);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mName = in.readString();
        mSongs = in.readArrayList(String.class.getClassLoader());
        mYear = in.readInt();
    }
}

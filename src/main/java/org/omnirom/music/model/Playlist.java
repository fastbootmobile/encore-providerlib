package org.omnirom.music.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    /**
     * Sets the name of the playlist
     * @param name Name of the playlist
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * @return The name of the playlist
     */
    public String getName() {
        return mName;
    }

    /**
     * Adds a song at the end of this playlist
     * @param s The reference of the song to add
     */
    public void addSong(String s) {
        mSongs.add(s);
    }

    /**
     * Remove the song with the specified reference from the playlist
     * @param s The reference of the song to remove
     */
    public void removeSong(String s) {
        mSongs.remove(s);
    }

    /**
     * Remove the song at the specified index from the playlist
     * @param i The index of the song
     */
    public void removeSong(int i) {
        mSongs.remove(i);
    }

    /**
     * @return An iterator to the list of songs of this playlist
     */
    public Iterator<String> songs() {
        return mSongs.iterator();
    }

    /**
     * @return The list of songs of this playlist
     */
    public List<String> songsList() {
        return mSongs;
    }

    /**
     * @return The number of songs in this playlist
     */
    public int getSongsCount() {
        return mSongs.size();
    }

    /**
     * Set the song at the specified index
     * @param pos The index of the song
     * @param s The reference of the song to set
     */
    public void setSong(int pos, final String s){
        mSongs.set(pos, s);
    }

    @Override
    public boolean isIdentical(Object other) {
        if (other instanceof Playlist) {
            Playlist remote = (Playlist) other;
            if (remote.getName().equals(getName()) && remote.getRef().equals(getRef())) {
                Iterator<String> remoteSongs = remote.songs();
                Iterator<String> ourSongs = songs();

                if (remote.getSongsCount() != getSongsCount()) {
                    return false;
                } else {
                    while (remoteSongs.hasNext()) {
                        String next = remoteSongs.next();

                        if (!ourSongs.hasNext()) {
                            return false;
                        } else if (!ourSongs.next().equals(next)) {
                            return false;
                        }
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

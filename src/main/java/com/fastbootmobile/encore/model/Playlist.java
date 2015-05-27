/*
 * Copyright (C) 2014 Fastboot Mobile, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fastbootmobile.encore.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Playlist extends BoundEntity {
    private ArrayList<String> mSongs;
    private String mName;
    private boolean mIsOfflineCapable;
    private int mOfflineStatus;

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

    /**
     * Sets the offline status of the song
     * @param status One of BoundEntity.OFFLINE_STATUS_*
     */
    public void setOfflineStatus(int status) {
        mOfflineStatus = status;
    }

    /**
     * Returns the offline status of the song
     * @return One of BoundEntity.OFFLINE_STATUS_*
     */
    public int getOfflineStatus() {
        return mOfflineStatus;
    }

    /**
     * Sets whether or not the provider supports downloading this playlist for offline usage
     * @param capable true if it is valid to call setPlaylistOfflineMode with this playlist
     */
    public void setOfflineCapable(boolean capable) {
        mIsOfflineCapable = capable;
    }

    /**
     * Returns whether or not this playlist can be download for offline usage
     * @return true if it is valid to call setPlaylistOfflineMode with this playlist
     */
    public boolean isOfflineCapable() {
        return mIsOfflineCapable;
    }

    @Override
    public boolean isIdentical(Object other) {
        if (other instanceof Playlist) {
            Playlist remote = (Playlist) other;
            if (remote.getRef().equals(getRef()) &&
                    (remote.getName() != null && getName() != null &&
                    remote.getName().equals(getName()))) {
                Iterator<String> remoteSongs = remote.songs();
                Iterator<String> ourSongs = songs();

                if (remote.getSongsCount() != getSongsCount() ||
                        remote.getOfflineStatus() != getOfflineStatus() ||
                        remote.isOfflineCapable() != isOfflineCapable()) {
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
        out.writeInt(mIsOfflineCapable ? 1 : 0);
        out.writeInt(mOfflineStatus);
    }


    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mName = in.readString();
        mSongs = in.readArrayList(String.class.getClassLoader());
        mIsOfflineCapable = (in.readInt() == 1);
        mOfflineStatus = in.readInt();
    }

    @Override
    public String toString() {
        return "'" + mName + "', " + mSongs.size() + " songs, ref: " + getRef();
    }
}

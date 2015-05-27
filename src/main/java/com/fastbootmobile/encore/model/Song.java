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

public class Song extends BoundEntity {
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private int mYear;
    private int mDurationMillis;
    private int mOfflineStatus;
    private boolean mIsAvailable;

    public static final Creator<Song> CREATOR = new
            Creator<Song>() {
                public Song createFromParcel(Parcel in) {
                    return new Song(in);
                }

                public Song[] newArray(int size) {
                    return new Song[size];
                }
            };

    public Song(final Parcel in) {
        super(in);
    }

    public Song(String ref) {
        super(ref);
    }

    /**
     * @return The title of the song
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets the title of this song
     * @param title The title
     */
    public void setTitle(final String title) {
        mTitle = title;
    }

    /**
     * @return The reference of the artist of this song
     */
    public String getArtist() {
        return mArtist;
    }

    /**
     * Sets the artist of this song
     * @param artist The unique reference name of the artist of this song
     */
    public void setArtist(final String artist) {
        mArtist = artist;
    }

    /**
     * @return The reference of the album of this song
     */
    public String getAlbum() {
        return mAlbum;
    }

    /**
     * Sets the album of this song
     * @param album The reference name of the album of this song
     */
    public void setAlbum(final String album) {
        mAlbum = album;
    }

    /**
     * @return The release year of this song or 0 if undefined
     */
    public int getYear() {
        return mYear;
    }

    /**
     * Sets the release year of this song
     * @param year The year of the song
     */
    public void setYear(final int year) {
        mYear = year;
    }

    /**
     * Returns the duration of the track
     * @return The duration of the track, in milliseconds
     */
    public int getDuration() {
        return mDurationMillis;
    }

    /**
     * Sets the duration of the track
     * @param millis The duration, in milliseconds
     */
    public void setDuration(int millis) {
        mDurationMillis = millis;
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
     * Sets whether or not a track is playable (available).
     * @param available True if the track can be played (default), false otherwise
     */
    public void setAvailable(boolean available) {
        mIsAvailable = available;
    }

    /**
     * Returns whether or not the track is playable (available).
     * @return true if the track can be played, false otherwise
     */
    public boolean isAvailable() {
        return mIsAvailable;
    }

    @SuppressWarnings("StringEquality")
    @Override
    public boolean isIdentical(Object other) {
        if (other instanceof Song) {
            Song remote = (Song) other;

            // Check for potential null values differences first
            if (remote.getAlbum() == null && getAlbum() != null
                    || getAlbum() == null && remote.getAlbum() != null) {
                return false;
            } else if (getAlbum() != null && !getAlbum().equals(remote.getAlbum())) {
                return false;
            }

            if (remote.getArtist() == null && getArtist() != null
                    || getArtist() == null && remote.getArtist() != null) {
                return false;
            } else if (getArtist() != null && !getArtist().equals(remote.getArtist())) {
                return false;
            }

            if (remote.getTitle() == null && getTitle() != null
                    || getTitle() == null && remote.getTitle() != null) {
                return false;
            } else if (getTitle() != null && !getTitle().equals(remote.getTitle())) {
                return false;
            }

            return (
                    remote.getRef().equals(getRef()) &&
                            remote.isLoaded() == isLoaded() &&
                            remote.getYear() == getYear() &&
                            remote.getDuration() == getDuration() &&
                            remote.getOfflineStatus() == getOfflineStatus() &&
                            remote.isAvailable() == isAvailable()
            );
        } else {
            return false;
        }
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(mTitle);
        out.writeString(mArtist);
        out.writeString(mAlbum);
        out.writeInt(mYear);
        out.writeInt(mDurationMillis);
        out.writeInt(mOfflineStatus);
        out.writeInt(mIsAvailable ? 1 : 0);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mTitle = in.readString();
        mArtist = in.readString();
        mAlbum = in.readString();
        mYear = in.readInt();
        mDurationMillis = in.readInt();
        mOfflineStatus = in.readInt();
        mIsAvailable = (in.readInt() == 1);
    }
}

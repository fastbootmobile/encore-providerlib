package org.omnirom.music.model;

import android.os.Parcel;

import org.omnirom.music.providers.ProviderIdentifier;

public class Song extends BoundEntity {
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private int mYear;
    private int mDurationMillis;

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

    @Override
    public boolean isIdentical(Object other) {
        if (other instanceof Song) {
            Song remote = (Song) other;
            return (
                    remote.getRef().equals(getRef()) &&
                            remote.isLoaded() == isLoaded() &&
                            remote.getAlbum().equals(getAlbum()) &&
                            remote.getArtist().equals(getArtist()) &&
                            remote.getTitle().equals(getTitle()) &&
                            remote.getYear() == getYear() &&
                            remote.getDuration() == getDuration()
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
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mTitle = in.readString();
        mArtist = in.readString();
        mAlbum = in.readString();
        mYear = in.readInt();
        mDurationMillis = in.readInt();
    }
}

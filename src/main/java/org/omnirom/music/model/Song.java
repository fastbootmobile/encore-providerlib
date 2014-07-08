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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String title) {
        mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(final String artist) {
        mArtist = artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(final String album) {
        mAlbum = album;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(final int year) {
        mYear = year;
    }

    public int getDuration() {
        return mDurationMillis;
    }

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

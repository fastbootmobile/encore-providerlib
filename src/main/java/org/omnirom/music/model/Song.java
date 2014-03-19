package org.omnirom.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.omnirom.music.providers.IMusicProvider;

public class Song implements Parcelable {
    private IMusicProvider mProvider;
    private String mTitle;
    private String mArtist;
    private Artist mMatchedArtist;
    private String mAlbum;
    private Album mMatchedAlbum;
    private int mYear;

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
        readFromParcel(in);
    }

    public Song(IMusicProvider provider) {
        mProvider = provider;
    }

    public IMusicProvider getProvider() {
        return mProvider;
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

    public Artist getMatchedArtist() {
        return mMatchedArtist;
    }

    public void setMatchedArtist(final Artist artist) {
        mMatchedArtist = artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(final String album) {
        mAlbum = album;
    }

    public Album getMatchedAlbum() {
        return mMatchedAlbum;
    }

    public void setMatchedAlbum(final Album album) {
        mMatchedAlbum = album;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(final int year) {
        mYear = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(mProvider);
        out.writeString(mTitle);
        out.writeString(mArtist);
        out.writeString(mAlbum);
        out.writeInt(mYear);
        out.writeValue(mMatchedArtist);
        out.writeValue(mMatchedAlbum);
    }

    public void readFromParcel(Parcel in) {
        mProvider = (IMusicProvider) in.readValue(IMusicProvider.class.getClassLoader());
        mTitle = in.readString();
        mArtist = in.readString();
        mAlbum = in.readString();
        mYear = in.readInt();
        mMatchedArtist = (Artist) in.readValue(Artist.class.getClassLoader());
        mMatchedAlbum = (Album) in.readValue(Album.class.getClassLoader());
    }
}

package org.omnirom.music.model;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.List;

/**
 * Created by h4o on 10/07/2014.
 */
public class SearchResult implements Parcelable {

    private String mQuery;
    private List<String> mSongsList;
    private List<String> mAlbumsList;
    private List<String> mArtistList;
    private List<String> mPlaylistList;
    public static final Creator<SearchResult> CREATOR = new
            Creator<SearchResult>() {
                public SearchResult createFromParcel(Parcel in) {
                    return new SearchResult(in);
                }

                public SearchResult[] newArray(int size) {
                    return new SearchResult[size];
                }
            };
    public SearchResult(final String query) {
        mQuery = query;
    }

    public SearchResult(final Parcel in) {
        readFromParcel(in);
    }

    public void setAlbumsList(List<String> albumsList) {
        this.mAlbumsList = albumsList;
    }

    public void setArtistList(List<String> artistList) {
        this.mArtistList = artistList;
    }

    public void setPlaylistList(List<String> playlistList) {
        this.mPlaylistList = playlistList;
    }

    public void setSongsList(List<String> songsList) {
        this.mSongsList = songsList;
    }

    public List<String> getAlbumsList() {
        return mAlbumsList;
    }

    public List<String> getArtistList() {
        return mArtistList;
    }

    public List<String> getPlaylistList() {
        return mPlaylistList;
    }

    public List<String> getSongsList() {
        return mSongsList;
    }

    @Override
    public int describeContents() {
        return mQuery.hashCode();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mQuery);
        out.writeList(mSongsList);
        out.writeList(mAlbumsList);
        out.writeList(mArtistList);
        out.writeList(mPlaylistList);
    }

    public void readFromParcel(Parcel in) {
        mQuery = in.readString();
        mSongsList = in.readArrayList(String.class.getClassLoader());
        mAlbumsList = in.readArrayList(String.class.getClassLoader());
        mArtistList = in.readArrayList(String.class.getClassLoader());
        mPlaylistList = in.readArrayList(String.class.getClassLoader());
    }
    public String getQuery(){
        return mQuery;
    }



    @Override
    public boolean equals(Object o) {
        if (o instanceof SearchResult) {
            SearchResult ent = (SearchResult) o;
            return mQuery.equals(ent.getQuery());
        } else {
            return false;
        }

    }
}

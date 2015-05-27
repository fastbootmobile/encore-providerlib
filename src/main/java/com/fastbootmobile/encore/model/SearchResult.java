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
import android.os.Parcelable;


import com.fastbootmobile.encore.providers.ProviderIdentifier;

import java.util.ArrayList;
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
    public ProviderIdentifier mIdentifier;
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
        mSongsList = new ArrayList<String>();
        mArtistList = new ArrayList<String>();
        mAlbumsList = new ArrayList<String>();
        mPlaylistList = new ArrayList<String>();
    }

    public SearchResult(final Parcel in) {
        readFromParcel(in);
    }

    public void setIdentifier(ProviderIdentifier id) {
        mIdentifier = id;
    }

    public ProviderIdentifier getIdentifier() {
        return mIdentifier;
    }

    public void addAlbum(String ref) {
        mAlbumsList.add(ref);
    }

    public void addArtist(String ref) {
        mArtistList.add(ref);
    }

    public void addPlaylist(String ref) {
        mPlaylistList.add(ref);
    }

    public void addSong(String ref) {
        mSongsList.add(ref);
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
        out.writeParcelable(mIdentifier, 0);
        out.writeString(mQuery);
        out.writeList(mSongsList);
        out.writeList(mAlbumsList);
        out.writeList(mArtistList);
        out.writeList(mPlaylistList);
    }

    public void readFromParcel(Parcel in) {
        mIdentifier = in.readParcelable(ProviderIdentifier.class.getClassLoader());
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

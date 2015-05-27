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

package com.fastbootmobile.encore.providers;

import com.fastbootmobile.encore.model.Song;
import com.fastbootmobile.encore.model.Album;
import com.fastbootmobile.encore.model.Playlist;
import com.fastbootmobile.encore.model.Artist;
import com.fastbootmobile.encore.model.Genre;
import com.fastbootmobile.encore.model.SearchResult;


import com.fastbootmobile.encore.providers.ProviderIdentifier;

/**
 * This class represents callbacks made from the provider to the main app. It is used
 * to notify the app of various events that happened (song playback started, track finished,
 * artist browse result, etc).
 * Providers must call the methods here when relevant events happen. For example,
 * when a track starts playing, the provider must call the "onSongPlaying" method of all
 * the callbacks registered to it.
 * Calls are thread-safe on the app's side and thus don't necessarily need to be called from
 * the provider's main event loop.
 */
interface IProviderCallback {

    /**
     * Returns the unique identifier code of this callback. When calling unregisterCallback
     * on the provider, you must match this callback identifier as the object proxy will
     * be different.
     */
    int getIdentifier();

    /**
     * Called by the provider when a feedback is available about a login request. Offline
     * providers don't need to call this method.
     *
     * @param success Whether or not the login succeeded
     */
    void onLoggedIn(in ProviderIdentifier provider, boolean success);

    /**
     * Called by the provider when the user login has expired, or has been kicked from
     * the service. Offline providers don't need to call this method.
     */
    void onLoggedOut(in ProviderIdentifier provider);

    /**
     * Called by the provider when a Playlist has been added or updated. The app's provider
     * syndicator will automatically update the local cache of playlists based on the playlist
     * reference.
     * @param p The playlist that has been updated
     */
    void onPlaylistAddedOrUpdated(in ProviderIdentifier provider, in Playlist p);

    /**
     * Called by the provider when a Playlist has been removed from the user playlists container.
     * @param ref The reference of the playlist that has been removed
     */
    void onPlaylistRemoved(in ProviderIdentifier provider, String ref);

    /**
     * Called by the provider when the information of a song has been updated. The app
     * will automatically merge the information with its local cache based on the song's
     * reference.
     * @param s The song that has been updated
     */
    void onSongUpdate(in ProviderIdentifier provider, in Song s);

    /**
     * Called by the provider when the details of an album have been updated.
     * Please note that while the app supports uploading multiple times the same album ref with
     * different objects (the one with the most information/tracks will be kept), it is strongly
     * encouraged for optimization purposes to pass the same Album objects for every call to update
     * (unless of course it is a new album).
     * @param a The album that has been updated
     */
    void onAlbumUpdate(in ProviderIdentifier provider, in Album a);

    /**
     * Called by the provider when the details of an artist have been updated.
     * @param a The artist that has been updated
     */
    void onArtistUpdate(in ProviderIdentifier provider, in Artist a);
    /**
     * Called by the provider when the details of a genre have been updated.
     * @param g The genre that has been updated
     */
     void onGenreUpdate(in ProviderIdentifier provider, in Genre g);

    /**
     * Called by the provider when a song starts playing, either when a requested song
     * started playing, or when the track was paused and resumed.
     */
    void onSongPlaying(in ProviderIdentifier provider);

    /**
     * Called by the provider when the playback has been paused
     */
    void onSongPaused(in ProviderIdentifier provider);

    /**
     * Called by the provider when the currently playing track ended
     */

    void onTrackEnded(in ProviderIdentifier provider);

    /**
     * Called by the provider when a requested search completed, to provide the main app
     * results found.
     */
    void onSearchResult(in SearchResult searchResult);

}

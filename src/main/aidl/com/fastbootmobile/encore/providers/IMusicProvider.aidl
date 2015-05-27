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
import com.fastbootmobile.encore.providers.IProviderCallback;
import com.fastbootmobile.encore.providers.ProviderIdentifier;
import com.fastbootmobile.encore.providers.IArtCallback;

/**
 * This interface represents a music provider and must be implemented as the binder of a service
 * implementing the PICK_PROVIDER action.
 * Please note that all the classes of the Model package (Album, Artist, Playlist, Song, ...) take
 * a "ref" String in their constructor. This reference MUST be unique for ALL providers. It is
 * strongly recommended to prefix it with the name of your provider service. A good example of
 * reference format is: "spotify:user:xplodwild:playlist:5oT5U5svjYPQZewzscRIQW"
 * If you want to support Rosetta Stone IDs playback through this provider (e.g. you want to be
 * able to play Spotify URIs), you MUST use URIs as reference. For more information about
 * Rosetta Stone, see http://developer.echonest.com/docs/v4/index.html#rosetta and
 * getSupportedRosettaPrefix();
 */
interface IMusicProvider {
    /**
     * Returns the API Version used by this provider.
     * Providers must return Constants.API_VERSION to make sure to return the actual value from
     * providerlib and avoid mistakes when updating.
     */
    int getVersion();

    /**
     * Sets the Provider Identifier for this provider. The service must retain this identifier
     * and pass it on every callback to tell the app the source.
     */
    void setIdentifier(in ProviderIdentifier identifier);

    /**
     * Register a callback for the app to be notified of events. Remember that the providers calls
     * should be asynchronous whenever possible. For example, if your data isn't immediately
     * available (if you need to fetch it from the Web), return an entity with the final reference
     * and set the loaded flag to 'false'. Then, when the final data is available, update it
     * via the IProviderCallback methods (onSongUpdate, onAlbumUpdate, etc).
     * On the other hand, if your data is quickly available (cached, etc), you can return the
     * object with filled data without the need to call onXxxUpdated.
     */
    void registerCallback(IProviderCallback cb);

    /**
     * Removes a registered callback. Make sure to unregister callbacks using callback's identifiers
     * and not using the callback object's equals method.
     */
    void unregisterCallback(IProviderCallback cb);

    /**
     * Returns whether or not the provider is fully setup and ready to use (for example, if the
     * user entered his login and password to authenticate to the service in the configuration
     * activity).
     * As long as this returns false, the app won't try to login or do any action on the provider.
     * The app will check on resume, so it is advised to use "isSetup" only if you need to force
     * the user to go through your settings/setup activity.
     *
     * @returns true if the provider is configured and ready to use
     */
    boolean isSetup();

    /**
     * Request authenticatication of the user against the provider. It is up to the provider to
     * store the credentials and grab them through a configuration activity. See provider.Constants
     * for more details about the configuration activity.
     *
     * @return true if the authentication request succeeded, false otherwise
     */
    boolean login();

    /**
     * Indicates whether or not this provider has successfully authenticated against the remote
     * provider servers.
     * In case an authentication is not needed, this method should simply return true at all
     * times. No login attempt will be then made by the app.
     *
     * @return true if this provider is authenticated and ready to be used, false otherwise
     */
    boolean isAuthenticated();

    /**
     * Informs whether or not this provider is infinite (ie. it's a cloud provider that allows
     * you to access a virtually unlimited number of tracks, such as Spotify or Deezer ; the local
     * storage or a simple storage provider would return false).
     *
     * @return true if there's no defined number of tracks, false otherwise
     */
    boolean isInfinite();

    /**
     * Returns the list of all albums
     * This method call is only valid when isInfinite returns false. Infinite providers should
     * notify of album information in the onAlbumUpdate callback whenever the information is
     * available, or when requested through fetchArtistAlbums()
     *
     * @return A list of all the albums available on the provider
     */
    List<Album> getAlbums();

    /**
     * Returns the list of all artists
     * This method call is only valid when isInfinite returns false
     *
     * @return A list of all the artists available on the provider
     */
    List<Artist> getArtists();

    /**
     * Returns the list of all songs
     * This method call is only valid when isInfinite returns false. This method could return
     * a lot of entries, however the Android IPC binder system has a limited size of 512KB to 1MB
     * buffer depending on the device and the state of the buffer. In order to avoid transaction
     * failures due to a too large transaction, the app will query songs in a fixed size batch.
     *
     * @param offset The offset at which the list of songs needs to be loaded
     * @param limit The maximum number of songs in the list
     * @return A list of all songs available on the provider in the requested [offset, offset+limit]
     *         range. It may be null if there are no songs or no songs in that range.
     */
    List<Song> getSongs(int offset, int limit);

    /**
     * Returns the list of all playlists on this provider
     * This method is valid for both infinite and defined providers.
     *
     * @return A list of all playlists on this provider
     */
    List<Playlist> getPlaylists();


    /**
    * Returns the list of all genre on this provider
    * this method is valid for both infinite and finite providers, but the genre may
    * have no songs if the provider is infinite
    *
    * @return A list of all genre on this provider
    */
    List<Genre> getGenres();

    /**
     * Returns a particular song
     * The provider may not return all the information immediately, and must set the IsLoaded
     * flag accordingly.
     * Song information should be then updated with onSongUpdate callback.
     * Note that a Song doesn't necessarily have an album or artist reference attached, if
     * that information isn't available.
     * It must not return null however, unless the reference is invalid.
     *
     * @param ref The reference of the song
     */
    Song getSong(String ref);

    /**
     * Returns a particular artist
     * The provider may not return all the information immediately, and must set the IsLoaded
     * flag accordingly.
     * Artist information should be then updated with onArtistUpdate callback.
     * It must not return null however, unless the reference is invalid.
     *
     * @param ref The reference of the artist
     */
    Artist getArtist(String ref);

    /**
     * Returns a particular album
     * The provider may not return all the information immediately, and must set the IsLoaded
     * flag accordingly.
     * Album information should be then updated with onAlbumUpdate callback.
     * It must not return null however, unless the reference is invalid.
     *
     * @param ref The reference of the album
     */
    Album getAlbum(String ref);

    /**
     * Returns a particular playlist
     * The provider may not return all the information immediately, and must set the IsLoaded
     * flag accordingly.
     * Playlist information should be then updated with onPlaylistUpdate callback.
     * It must not return null however, unless the reference is invalid.
     *
     * @param ref The reference of the playlist
     */
    Playlist getPlaylist(String ref);

    /**
     * Returns a bitmap for the given entity. This method isn't mandatory and may return false at
     * all times if the provider isn't capable of returning a song art. When this method returns
     * false, the app will manually fetch art from the Web.
     *
     * @param entity The entity for which we want the bitmap
     * @param callback The callback that will be called when the bitmap is ready
     * @return true if the call succeded and there are chances that the provider has an art for the
     *              entity, false otherwise
     */
    boolean getArtistArt(in Artist entity, in IArtCallback callback);
    boolean getAlbumArt(in Album entity, in IArtCallback callback);
    boolean getPlaylistArt(in Playlist entity, in IArtCallback callback);
    boolean getSongArt(in Song entity, in IArtCallback callback);


    /**
     * Requests the provider to fetch the albums of the provided artist reference. This method
     * is generally only useful for infinite providers where they can't provide a consistent
     * list of albums in getAlbums() (because there are too much of them).
     * If applicable, the provider should send the albums in the onAlbumUpdate callback when
     * the data is available.
     *
     * @param artistRef The reference of the artist whom we want the albums
     * @return true if there are albums loading, false if none is available or everything is loaded
     *         (this is used by the app's UI to display a loading spinner)
     */
    boolean fetchArtistAlbums(String artistRef);

    /**
     * Requests the provider to fetch the tracks of the provided album reference. This method is
     * generally only useful for infinite provider where they can't provide all the tracks of all
     * the albums loaded (because there are too much of them, or they would cause an infinite load
     * loop).
     * If applicable, the provider should send the tracks in the onAlbumUpdate callback when
     * the data is available.
     *
     * @param albumRef The reference of the album whom we want the tracks
     * @return true if there are tracks loading, false if all the tracks are already populated
     *              (this is used by the app's UI to display a loading spinner)
     */
    boolean fetchAlbumTracks(String albumRef);


    /**
     * Tells the provider the name of the local audio socket to use to push data. This string
     * should be passed to AudioSocket in order to push audio to the proper location. The app
     * manages audio crossfading and properly locks each socket to ensure a smooth playback
     * between the various providers.
     * The audio socket may change between the playback sessions, so the provider must handle
     * switching audio socket on the fly, and re-send the format data before audio data. See
     * the com.fastbootmobile.encore.providers.AudioSocket class for more information.
     *
     * @param socketName The name of the socket to use
     */
    void setAudioSocketName(String socketName);

    /**
     * Returns the time, in milliseconds, the providers needs a call to prefetchSong() before the
     * end of the current song.
     * For instance, a cloud provider might need more time to prepare a song than a local provider
     * to ensure smooth and gapless playback.
     */
    long getPrefetchDelay();

    /**
     * Requests the provider to prepare the playback of a song (ie. start downloading it and/or
     * caching it in RAM), as it is likely the next song to be played.
     * Providers may choose to implement or not this method - it is called by the app so that
     * the provider can prepare the next song, but no particular result is expected.
     *
     * @param ref The unique reference of the song to prepare
     */
    void prefetchSong(String ref);

    /**
     * Requests the provider to play the song referenced by the provided ref string.
     *
     * @param ref The unique reference of the song
     * @return false in case of error, otherwise true
     */
    boolean playSong(String ref);

    /**
     * Requests the provider to pause the currently playing song
     */
    void pause();

    /**
     * Requests the provider to resume playing the song that was paused, if any
     */
    void resume();

    /**
     * Seek the currently playing track to the specified position
     */
    void seek(long timeMs);

    /**
     * Requests the provider to update an existing playlist with the songs provided by the songlist
     *
     * @param ref the unique reference of the playlist
     * @return true if the change is saved
     */
    boolean onUserSwapPlaylistItem(int oldPosition, int newPosition, String playlistRef);

    /**
     * Requests the provider to delete a playlist
     * @param ref the unique reference of the playlist
     * @return true if the playlist is deleted
     */
    boolean deletePlaylist(String playlistRef);

    /**
     * Requests the provider to rename a playlist
     * @param ref The unique reference of the playlist
     * @param title The new title for the playlist
     * @return true if rename was successful
     */
    boolean renamePlaylist(String ref, String title);

    /**
     * Requests the provider to delete a song of a playlist
     * @param ref the unique reference of the playlist
     * @return true if the song is deleted
     */
    boolean deleteSongFromPlaylist(int songPosition,String playlistRef);

    /**
     * Requests the provider to delete a song of a playlist
     * @param ref the unique reference of the playlist
     * @return true if the song is deleted
     */
    boolean addSongToPlaylist(String songRef,String playlistRef,in ProviderIdentifier providerIdentifier);

    /**
     * Requests the provider to add a playlist
     * @param playlistName the name of the playlist
     * @return the unique reference to the playlist if it has been added, false if it has not
     */
    String addPlaylist(String playlistName);

    /**
     * Request the provider to search songs, playlists, artists and albums. Results must be provided
     * in "onSearchResult" callback.
     * @param query the name to search
     */
    void startSearch(String query);

    /**
     * Returns the logo Bitmap associated to the provided reference. The logo reference is stored
     * in bound entities (Song, Artist, Album, ...) and this logo is used to show the user where
     * this entity came from.
     * A 300x300 pixels logo is large enough for the app, regardless of the DPI.
     * Also, the provider should not return a default logo in all cases, only when a specific
     * reference matches. Otherwise, it should return null.
     * @param ref The song reference string
     */
    Bitmap getLogo(String ref);

    /**
     * Returns the list of Project Rosetta Stone prefixes supported by this provider. See
     * http://developer.echonest.com/docs/v4/index.html#rosetta for more details about the IDs. This
     * is used when querying EchoNest and external links to easily play through the proper provider.
     * Here are the supported prefixes:
     * "7digital-US", "7digital-AU", "7digital-UK", "facebook", "fma", "emi_open_collection",
     * "emi_bluenote", "emi_artists", "twitter", "spotify-WW", "seatwave", "lyricfind-US",
     * "jambase", "musixmatch-WW", "rdio-US", "rdio-AT", "rdio-AU", "rdio-BR", "rdio-CA", "rdio-CH",
     * "rdio-DE", "rdio-DK", "rdio-ES", "rdio-FI", "rdio-FR", "rdio-IE", "rdio-IT", "rdio-NL",
     * "rdio-NO", "rdio-NZ", "rdio-PT", "rdio-SE", "emi_electrospective", "rdio-WW", "rdio-EE",
     * "rdio-LT", "rdio-LV", "rdio-IS", "rdio-BE", "rdio-MX", "seatgeek", "rdio-GB", "rdio-CZ",
     * "rdio-CO", "rdio-PL", "rdio-MY", "rdio-HK", "rdio-CL", "twitter_numeric", "7digital-ES",
     * "openaura", "spotifyv2", "spotifyv2-ZZ"
     */
    List<String> getSupportedRosettaPrefix();

    /**
     * Sets whether or not the provided playlist should be downloaded for offline playback or not.
     * This will be called accordingly with the playlist's "isOfflineCapable" result (ie. this
     * method will never be called with a playlist for which isOfflineCapable returns false).
     * Please note that only playlists can be set to offline mode, and not individual tracks.
     * However, the provider MUST set the offline status for each synchronized track, as well as
     * the playlist itself (through setOfflineStatus).
     */
    void setPlaylistOfflineMode(String ref, boolean offline);

    /**
     * Sets whether or not the app operates in offline mode (ie. simulates no network connection).
     * Only entities for which getOfflineStatus() returns OFFLINE_STATUS_READY will be able to
     * be played (ie. the provider will only get play() requests for these songs).
     */
    void setOfflineMode(boolean offline);
}

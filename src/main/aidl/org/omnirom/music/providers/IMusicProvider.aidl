package org.omnirom.music.providers;

import org.omnirom.music.model.Song;
import org.omnirom.music.model.Album;
import org.omnirom.music.model.Playlist;
import org.omnirom.music.model.Artist;
import org.omnirom.music.model.Genre;
import org.omnirom.music.providers.IProviderCallback;
import org.omnirom.music.providers.ProviderIdentifier;

/**
 * This interface represents a music provider and must be implemented as the binder of a service
 * implementing the PICK_PROVIDER action.
 * Please note that all the classes of the Model package (Album, Artist, Playlist, Song, ...) take
 * a "ref" String in their constructor. This reference MUST be unique for ALL providers. It is
 * strongly recommended to prefix it with the name of your provider service. A good example of
 * reference format is: "spotify:user:xplodwild:playlist:5oT5U5svjYPQZewzscRIQW"
 */
interface IMusicProvider {
    /**
     * Returns the API Version of this provider.
     * The current API version is: 1
     */
    int getVersion();

    /**
     * Sets the Provider Identifier for this provider. The service must retain this identifier
     * and pass it on every callback
     */
    void setIdentifier(in ProviderIdentifier identifier);

    /**
     * Register a callback for the app to be notified of events. Remember that the providers calls
     * should all be asynchronous (every request must return immediately, and the result be posted
     * later on to all the callbacks registered here).
     */
    void registerCallback(IProviderCallback cb);

    /**
     * Removes a registered callback
     */
    void unregisterCallback(IProviderCallback cb);

    /**
     * Returns whether or not the provider is fully setup and ready to use (for example, if the
     * user entered his login and password to authenticate to the service in the configuration
     * activity).
     * As long as this returns false, the app won't try to login or do any action on the provider.
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
     * This method call is only valid when isInfinite returns false
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
     * This method call is only valid when isInfinite returns false
     *
     * @return A list of all songs available on the provider
     */
    List<Song> getSongs();

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
     * It must not return null however.
     *
     * @param ref The reference of the song
     */
    Song getSong(String ref);

    /**
     * Returns a bitmap for the song given
     * @param song the song for which we want the bitmap
     */
    Bitmap getSongArt(in Song song);


    /**
     * Tells the provider the name of the local audio socket to use to push data. This string
     * should be passed to AudioSocket in order to push audio to the proper location. The app
     * manages audio crossfading and properly locks each socket to ensure a smooth playback
     * between the various providers.
     * The audio socket may change between the playback sessions, so the provider must handle
     * switching audio socket on the fly, and re-send the format data before audio data. See
     * the org.omnirom.music.providers.AudioSocket class for more information.
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
     * @param ref The unique reference of the song
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
}

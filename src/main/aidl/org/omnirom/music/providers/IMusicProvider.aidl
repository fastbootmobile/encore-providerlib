package org.omnirom.music.providers;

import org.omnirom.music.model.Song;
import org.omnirom.music.model.Album;
import org.omnirom.music.model.Playlist;
import org.omnirom.music.model.Artist;
import org.omnirom.music.providers.IProviderCallback;

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


}

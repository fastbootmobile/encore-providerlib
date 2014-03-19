package org.omnirom.music.providers;

import org.omnirom.music.model.Song;
import org.omnirom.music.model.Album;
import org.omnirom.music.model.Playlist;
import org.omnirom.music.model.Artist;

interface IMusicProvider {
    /**
     * Authenticates the user against the provider.
     *
     * @param username Login username
     * @param password Login password
     * @return true if authentication succeeded, false otherwise
     */
    boolean login(String username, String password);

    /**
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

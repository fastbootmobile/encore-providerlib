package org.omnirom.music.providers;

import org.omnirom.music.model.Song;
import org.omnirom.music.model.Album;
import org.omnirom.music.model.Playlist;
import org.omnirom.music.model.Artist;

import org.omnirom.music.providers.IMusicProvider;

interface IProviderCallback {

    /**
     * Called by the provider when a feedback is available about a login request
     *
     * @param success Whether or not the login succeeded
     */
    void onLoggedIn(IMusicProvider provider, boolean success);

    /**
     * Called by the provider when the user login has expired, or has been kicked.
     */
    void onLoggedOut(IMusicProvider provider);

    /**
     * Called by the provider when a Playlist has been added or updated. The app's provider
     * syndicator will automatically update the local cache of playlists based on the playlist
     * name.
     */
    void onPlaylistAddedOrUpdated(IMusicProvider provider, in Playlist p);

    /**
     * Called by the provider when the details of a song have been updated.
     */
    void onSongUpdate(IMusicProvider provider, in Song s);

    /**
     * Called by the provider when the details of an album have been updated.
     */
    void onAlbumUpdate(IMusicProvider provider, in Album a);

    /**
     * Called by the provider when the details of an artist have been updated.
     */
    void onArtistUpdate(IMusicProvider provider, in Artist a);

}

package org.omnirom.music.providers;

import org.omnirom.music.model.Song;
import org.omnirom.music.model.Album;
import org.omnirom.music.model.Playlist;
import org.omnirom.music.model.Artist;
import org.omnirom.music.model.Genre;


import org.omnirom.music.providers.ProviderIdentifier;

interface IProviderCallback {

    /**
     * Called by the provider when a feedback is available about a login request
     *
     * @param success Whether or not the login succeeded
     */
    void onLoggedIn(in ProviderIdentifier provider, boolean success);

    /**
     * Called by the provider when the user login has expired, or has been kicked.
     */
    void onLoggedOut(in ProviderIdentifier provider);

    /**
     * Called by the provider when a Playlist has been added or updated. The app's provider
     * syndicator will automatically update the local cache of playlists based on the playlist
     * name.
     * @param p The playlist that has been updated
     */
    void onPlaylistAddedOrUpdated(in ProviderIdentifier provider, in Playlist p);

    /**
     * Called by the provider when the details of a song have been updated.
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
     * Called by the provider when a song starts playing
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

}

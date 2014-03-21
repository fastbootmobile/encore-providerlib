package org.omnirom.music.providers;

import org.omnirom.music.model.Song;
import org.omnirom.music.model.Album;
import org.omnirom.music.model.Playlist;
import org.omnirom.music.model.Artist;

interface IProviderCallback {

    /**
     * Called by the provider when a feedback is available about a login request
     *
     * @param success Whether or not the login succeeded
     */
    void onLoggedIn(boolean request);

    void onLoggedOut();

    void onSongUpdate(in Song s);

    void onAlbumUpdate(in Album a);

    void onPlaylistUpdate(in Playlist p);

    void onArtistUpdate(in Artist a);

}

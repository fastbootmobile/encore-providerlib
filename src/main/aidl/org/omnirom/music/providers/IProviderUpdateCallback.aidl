package org.omnirom.music.providers;

import org.omnirom.music.model.Song;
import org.omnirom.music.model.Album;
import org.omnirom.music.model.Playlist;
import org.omnirom.music.model.Artist;

interface IProviderUpdateCallback {

    void onSongUpdate(in Song s);

    void onAlbumUpdate(in Album a);

    void onPlaylistUpdate(in Playlist p);

    void onArtistUpdate(in Artist a);

}

package org.omnirom.music.providers;

import org.omnirom.music.model.Song;
import org.omnirom.music.model.Album;
import org.omnirom.music.model.Playlist;
import org.omnirom.music.model.Artist;
import org.omnirom.music.model.Genre;
import org.omnirom.music.providers.IProviderCallback;
import org.omnirom.music.providers.ProviderIdentifier;

/**
 * This interface represents a way to interact with a sound effects provider
 */
interface IDSPProvider {
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

}

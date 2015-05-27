package omnimusic.googleplaymusicprovider;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.fastbootmobile.encore.model.Album;
import com.fastbootmobile.encore.model.Artist;
import com.fastbootmobile.encore.model.Genre;
import com.fastbootmobile.encore.model.Playlist;
import com.fastbootmobile.encore.model.Song;
import com.fastbootmobile.encore.providers.Constants;
import com.fastbootmobile.encore.providers.IArtCallback;
import com.fastbootmobile.encore.providers.IMusicProvider;
import com.fastbootmobile.encore.providers.IProviderCallback;
import com.fastbootmobile.encore.providers.ProviderIdentifier;

import java.util.ArrayList;
import java.util.List;

public class PluginService extends Service {
    private static final String TAG = "PluginService";

    private ProviderIdentifier mIdentifier;
    private final List<IProviderCallback> mCallbacks;

    /**
     * Default constructor
     */
    public PluginService() {
        mCallbacks = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (mCallbacks) {
            mCallbacks.clear();
        }
    }

    @Override
    public void onDestroy() {
        // Clear up the remaining callbacks
        synchronized (mCallbacks) {
            mCallbacks.clear();
        }

        // And destroy the service!
        super.onDestroy();
    }

    /**
     * Called when the service is getting bound to another service or activity
     *
     * @param intent The intent requested
     * @return An RPC binder, in our case an IMusicProvider Stub
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Binder Stub implementation
     */
    private IMusicProvider.Stub mBinder = new IMusicProvider.Stub() {
        @Override
        public int getVersion() {
            return Constants.API_VERSION;
        }

        @Override
        public void setIdentifier(ProviderIdentifier identifier) throws RemoteException {
            mIdentifier = identifier;
        }

        @Override
        public void registerCallback(final IProviderCallback cb) throws RemoteException {
            if (cb == null) {
                Log.e(TAG, "Trying to register null callback!");
                throw new IllegalArgumentException("Trying to register null callback!");
            }

            boolean contains = false;
            try {
                final int cbId = cb.getIdentifier();

                synchronized (mCallbacks) {
                    for (IProviderCallback callback : mCallbacks) {
                        if (callback.getIdentifier() == cbId) {
                            contains = true;
                            break;
                        }
                    }
                }

                if (!contains) {
                    mCallbacks.add(cb);
                }
            } catch (Exception e) {
                Log.e(TAG, "Cannot add callback, object is dead");
            }
        }

        @Override
        public void unregisterCallback(IProviderCallback cb) {
            synchronized (mCallbacks) {
                try {
                    final int cbId = cb.getIdentifier();
                    for (IProviderCallback callback : mCallbacks) {
                        if (callback.getIdentifier() == cbId) {
                            Log.e(TAG, "Found callback with identifier " + cbId);
                            mCallbacks.remove(callback);
                            return;
                        }
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "Remote exception while getting identifier", e);
                }

                Log.e(TAG, "Can't find callback for unregistering!");
            }
        }

        @Override
        public boolean isSetup() throws RemoteException {
            return false;
        }

        @Override
        public boolean login() throws RemoteException {
            return true;
        }

        @Override
        public boolean isAuthenticated() throws RemoteException {
            return false;
        }

        @Override
        public boolean isInfinite() throws RemoteException {
            return false;
        }

        @Override
        public List<Album> getAlbums() throws RemoteException {
            return null;
        }

        @Override
        public List<Artist> getArtists() throws RemoteException {
            return null;
        }

        @Override
        public List<Song> getSongs(int offset, int limit) throws RemoteException {
            return null;
        }

        @Override
        public List<Playlist> getPlaylists() throws RemoteException {
            return null;
        }

        @Override
        public List<Genre> getGenres() throws RemoteException {
            return null;
        }

        @Override
        public Song getSong(String ref) throws RemoteException {
            return null;
        }

        @Override
        public Artist getArtist(String ref) throws RemoteException {
            return null;
        }

        @Override
        public Album getAlbum(String ref) throws RemoteException {
            return null;
        }

        @Override
        public Playlist getPlaylist(String ref) throws RemoteException {
            return null;
        }

        @Override
        public boolean getArtistArt(Artist entity, IArtCallback callback) throws RemoteException {
            return false;
        }

        @Override
        public boolean getAlbumArt(Album entity, IArtCallback callback) throws RemoteException {
            return false;
        }

        @Override
        public boolean getPlaylistArt(Playlist entity, IArtCallback callback) throws RemoteException {
            return false;
        }

        @Override
        public boolean getSongArt(Song entity, IArtCallback callback) throws RemoteException {
            return false;
        }

        @Override
        public boolean fetchArtistAlbums(String artistRef) throws RemoteException {
            return false;
        }

        @Override
        public boolean fetchAlbumTracks(String albumRef) throws RemoteException {
            return false;
        }

        @Override
        public void setAudioSocketName(String socketName) throws RemoteException {

        }

        @Override
        public long getPrefetchDelay() throws RemoteException {
            return 0;
        }

        @Override
        public void prefetchSong(String ref) throws RemoteException {

        }

        @Override
        public boolean playSong(String ref) throws RemoteException {
            return false;
        }

        @Override
        public void pause() throws RemoteException {

        }

        @Override
        public void resume() throws RemoteException {

        }

        @Override
        public void seek(long timeMs) throws RemoteException {

        }

        @Override
        public boolean onUserSwapPlaylistItem(int oldPosition, int newPosition, String playlistRef) throws RemoteException {
            return false;
        }

        @Override
        public boolean deletePlaylist(String playlistRef) throws RemoteException {
            return false;
        }

        @Override
        public boolean renamePlaylist(String ref, String title) throws RemoteException {
            return false;
        }

        @Override
        public boolean deleteSongFromPlaylist(int songPosition, String playlistRef) throws RemoteException {
            return false;
        }

        @Override
        public boolean addSongToPlaylist(String songRef, String playlistRef, ProviderIdentifier providerIdentifier) throws RemoteException {
            return false;
        }

        @Override
        public String addPlaylist(String playlistName) throws RemoteException {
            return null;
        }

        @Override
        public void startSearch(String query) throws RemoteException {

        }

        @Override
        public Bitmap getLogo(String ref) throws RemoteException {
            return null;
        }

        @Override
        public List<String> getSupportedRosettaPrefix() throws RemoteException {
            return null;
        }

        @Override
        public void setPlaylistOfflineMode(String ref, boolean offline) throws RemoteException {

        }

        @Override
        public void setOfflineMode(boolean offline) throws RemoteException {

        }

    };
}

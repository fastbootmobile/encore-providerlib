package org.omnirom.music.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing an Artist (bands, etc).
 */
public class Artist extends BoundEntity {
    /**
     * The name of the artist
     */
    private String mName;

    /**
     * The list of albums of this artist
     */
    private List<String> mAlbums;

    public static final Creator<Artist> CREATOR = new
            Creator<Artist>() {
                public Artist createFromParcel(Parcel in) {
                    return new Artist(in);
                }

                public Artist[] newArray(int size) {
                    return new Artist[size];
                }
            };

    public Artist(final Parcel in) {
        super(in);
    }

    public Artist(final String ref) {
        super(ref);
        mAlbums = new ArrayList<String>();
    }

    /**
     * Sets the name of this artist
     * @param name The name of the artist
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Returns the name of the artist
     * @return The name of the artist, or null if none has been set
     */
    public String getName() {
        return mName;
    }

    /**
     * Adds an album that belongs to this artist
     * Note that the application matches the albums' songs artist automatically in onAlbumUpdated
     * callback so the provider isn't required to explicitly call addAlbum and maintain it.
     * @param a The string reference of the album to add
     */
    public void addAlbum(String a) {
        if (!mAlbums.contains(a)) {
            mAlbums.add(a);
        }
    }

    public Iterator<String> albums() {
        return mAlbums.iterator();
    }

    @Override
    public boolean isIdentical(Object other) {
        if (other instanceof Artist) {
            Artist remote = (Artist) other;
            if (remote.getName().equals(getName()) && remote.getRef().equals(getRef())) {
                Iterator<String> remoteSongs = remote.albums();
                Iterator<String> ourSongs = albums();

                while (remoteSongs.hasNext()) {
                    String next = remoteSongs.next();

                    if (!ourSongs.hasNext()) {
                        return false;
                    } else if (!ourSongs.next().equals(next)) {
                        return false;
                    }
                }
            } else {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(mName);
        out.writeList(mAlbums);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mName = in.readString();
        mAlbums = in.readArrayList(String.class.getClassLoader());
    }
}

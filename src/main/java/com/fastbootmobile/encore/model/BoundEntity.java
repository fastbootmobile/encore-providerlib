/*
 * Copyright (C) 2014 Fastboot Mobile, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fastbootmobile.encore.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastbootmobile.encore.providers.ProviderIdentifier;

/**
 * <p>Represents an abstract entity. This class must never be instantiated directly (see
 * {@link com.fastbootmobile.encore.model.Song}, {@link com.fastbootmobile.encore.model.Album},
 * {@link com.fastbootmobile.encore.model.Artist} and {@link com.fastbootmobile.encore.model.Song}.</p>
 *
 * <p><b>IMPORTANT NOTE:</b> You MUST make sure that the provider identifier is set FOR ALL ENTITIES
 * THAT ARE SENT TO THE APP! Failure to do so will result in content from your provider not
 * working properly.</p>
 */
public abstract class BoundEntity implements Parcelable {
    /**
     * The entity has not been marked for offline usage
     */
    public static final int OFFLINE_STATUS_NO = 0;

    /**
     * The entity has been marked for offline usage, but the download is pending
     */
    public static final int OFFLINE_STATUS_PENDING = 1;

    /**
     * The entity has been marked for offline usage, but it is currently downloading
     */
    public static final int OFFLINE_STATUS_DOWNLOADING = 2;

    /**
     * The entity has been marked for offline usage, but an error occurred during download
     */
    public static final int OFFLINE_STATUS_ERROR = 3;

    /**
     * The entity is marked for offline usage and is available
     */
    public static final int OFFLINE_STATUS_READY = 4;


    private String mRef;
    private boolean mIsLoaded;
    private ProviderIdentifier mProvider;
    private String mSourceLogo;

    BoundEntity(final String ref) {
        mRef = ref;
    }

    BoundEntity(final Parcel in) {
        readFromParcel(in);
    }

    /**
     * Returns the unique reference to this entity
     * @return A unique string representing this entity
     */
    public String getRef() {
        return mRef;
    }

    /**
     * Sets whether or not all the data for this entity is loaded. This must be set to true when
     * all the fields of the entity are filled with the final data.
     * @param isLoaded True if all the data is loaded, false otherwise.
     */
    public void setIsLoaded(boolean isLoaded) {
        mIsLoaded = isLoaded;
    }

    /**
     * Returns whether or not this entity is fully loaded (all the fields are filled with the final
     * data).
     * @return True if all the data is loaded, false otherwise.
     */
    public boolean isLoaded() {
        return mIsLoaded;
    }

    /**
     * Sets the ProviderIdentifier that provides this entity. This must be set by the Provider
     * before passing the entity to the app. Likewise, the app must never change this value.
     * @param id The identifier associated to the provider that provided this entity
     */
    public void setProvider(ProviderIdentifier id) {
        mProvider = id;
    }

    /**
     * Returns the identifier of the provider that created this entity
     * @return A ProviderIdentifier representing the source of this entity
     */
    public ProviderIdentifier getProvider() {
        return mProvider;
    }

    /**
     * Sets the reference of the logo of the source. The app will then request that reference to
     * the provider in order to display a logo next to the entity, in order to inform the user where
     * it came from. The image is then cached by the app to avoid requesting the Bitmap every time.
     * @param ref A reference to the image to display.
     */
    public void setSourceLogo(String ref) {
        mSourceLogo = ref;
    }

    /**
     * Returns the reference of the logo to display, that provided this entity. The actual Bitmap
     * may be obtained by calling getLogo(String) on {@link com.fastbootmobile.encore.providers.IMusicProvider}
     * @return A reference string for getLogo
     */
    public String getLogo() {
        return mSourceLogo;
    }

    @Override
    public int describeContents() {
        return mRef.hashCode();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mRef);
        out.writeInt(mIsLoaded ? 1 : 0);
        out.writeParcelable(mProvider, 0);
        out.writeString(mSourceLogo);
    }

    public void readFromParcel(Parcel in) {
        mRef = in.readString();
        mIsLoaded = in.readInt() == 1;
        mProvider = in.readParcelable(ProviderIdentifier.class.getClassLoader());
        mSourceLogo = in.readString();
    }

    /**
     * Returns whether or not the entities are identical, in that the content of the entity is
     * exactly the same (ie. has exactly the same contents) as the one passed in parameter.
     *
     * @param other The other object to compare with
     * @return true if both objects are exactly the same
     */
    public abstract boolean isIdentical(Object other);

    /**
     * Returns whether or not the entities are similar in that their reference is identical.
     * To check for a more in-depth identity, use {@link #isIdentical(Object)}
     *
     * @param o The object to compare this instance with.
     * @return true if the references are identical
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof BoundEntity) {
            BoundEntity ent = (BoundEntity) o;
            if (mRef != null) {
                return mRef.equals(ent.getRef());
            } else {
                return null == ent.getRef();
            }
        } else {
            return false;
        }

    }
}

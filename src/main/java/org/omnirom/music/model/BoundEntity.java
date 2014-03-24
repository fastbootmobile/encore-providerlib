package org.omnirom.music.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public abstract class BoundEntity implements Parcelable {

    private String mRef;
    private boolean mIsLoaded;

    public BoundEntity(final String ref) {
        mRef = ref;
    }

    public BoundEntity(final Parcel in) {
        readFromParcel(in);
    }

    public String getRef() {
        return mRef;
    }

    public void setIsLoaded(boolean isLoaded) {
        mIsLoaded = isLoaded;
    }

    public boolean isLoaded() {
        return mIsLoaded;
    }

    @Override
    public int describeContents() {
        return mRef.hashCode();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mRef);
        out.writeInt(mIsLoaded ? 1 : 0);
    }

    public void readFromParcel(Parcel in) {
        mRef = in.readString();
        mIsLoaded = in.readInt() == 1;
    }

    /**
     * Returns whether or not the entities are identical, in that the content of the entity is
     * exactly the same (ie. has exactly the same contents) as the one passed in parameter.
     *
     * @param other The other object to compare with
     *
     * @return true if both objects are exactly the same
     */
    public abstract boolean isIdentical(Object other);

    /**
     * Returns whether or not the entities are similar in that their reference is identical.
     * To check for a more in-depth identity, use {@link #isIdentical(Object)}
     * @param o
     *            the object to compare this instance with.
     * @return true if the references are identical
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof BoundEntity) {
            BoundEntity ent = (BoundEntity) o;
            return mRef.equals(ent.getRef());
        } else {
            return false;
        }

    }
}

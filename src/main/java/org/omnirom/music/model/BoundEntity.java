package org.omnirom.music.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class BoundEntity implements Parcelable {

    private String mRef;
    private boolean mIsLoaded;

    public static final Creator<BoundEntity> CREATOR = new
            Creator<BoundEntity>() {
                public BoundEntity createFromParcel(Parcel in) {
                    return new BoundEntity(in);
                }

                public BoundEntity[] newArray(int size) {
                    return new BoundEntity[size];
                }
            };

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
}

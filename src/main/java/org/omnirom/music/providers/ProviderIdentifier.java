package org.omnirom.music.providers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ProviderIdentifier implements Parcelable {

    private static final String TAG = "ProviderIdentifier";

    public String mPackage;
    public String mService;
    public String mName;

    public static final Parcelable.Creator<ProviderIdentifier> CREATOR = new
            Parcelable.Creator<ProviderIdentifier>() {
                public ProviderIdentifier createFromParcel(Parcel in) {
                    return new ProviderIdentifier(in);
                }

                public ProviderIdentifier[] newArray(int size) {
                    return new ProviderIdentifier[size];
                }
            };

    public ProviderIdentifier(Parcel in) {
        readFromParcel(in);
    }

    public ProviderIdentifier(final String pck, final String service, final String name) {
        mPackage = pck;
        mService = service;
        mName = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProviderIdentifier) {
            ProviderIdentifier o = (ProviderIdentifier) obj;
            return (this.mPackage.equals(o.mPackage) && this.mName.equals(o.mName)
                    && this.mService.equals(o.mService));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ProviderIdentifier[package=" + mPackage + ", service=" + mService + ", name=" + mName + "]";
    }

    @Override
    public int describeContents() {
        return mPackage.hashCode() + mService.hashCode() + mName.hashCode();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mPackage);
        out.writeString(mService);
        out.writeString(mName);
    }


    public void readFromParcel(Parcel in) {
        mPackage = in.readString();
        mService = in.readString();
        mName = in.readString();
    }
}

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

package com.fastbootmobile.encore.providers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ProviderIdentifier implements Parcelable {

    private static final String TAG = "ProviderIdentifier";

    public String mPackage;
    public String mService;
    public String mName;
    public Boolean mIsMultiProviderIdentifier;

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
        mIsMultiProviderIdentifier = false;
    }

    public void setMultiProviderMode(){
        mIsMultiProviderIdentifier = true;
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

    public String serialize() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("package", mPackage);
            obj.put("service", mService);
            obj.put("name", mName);
        } catch (JSONException e) {
            // why should this even happen?
            Log.e(TAG, "Unable to serialize the element", e);
            throw new RuntimeException(e);
        }

        return obj.toString();
    }

    public static ProviderIdentifier fromSerialized(String input) {
        try {
            JSONObject obj = new JSONObject(input);
            return new ProviderIdentifier(obj.getString("package"), obj.getString("service"),
                    obj.getString("name"));
        } catch (JSONException e) {
            Log.e(TAG, "Cannot un-serialize", e);
            return null;
        }
    }
}

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

public class Constants {

    /**
     * Returns this value in getVersion
     */
    public static final int API_VERSION = 1;

    /**
     * When the app will look for providers, it will pick-up all services that implements
     * the PICK_PROVIDER action.
     */
    public static final String ACTION_PICK_PROVIDER = "com.fastbootmobile.encore.action.PICK_PROVIDER";

    /**
     * When the app will look for DSP effect providers, it will pick-up all services that implements
     * the PICK_DSP_PROVIDER action.
     */
    public static final String ACTION_PICK_DSP_PROVIDER = "com.fastbootmobile.encore.action.PICK_DSP_PROVIDER";

    /**
     * For a provider to be valid, it MUST have a meta-data presenting the name of the providers
     * it represents (e.g. "Deezer", "Spotify", etc in case of a music provider, or "Bass Boost",
     * "10-Band Equalizer", etc for a DSP provider).
     */
    public static final String METADATA_PROVIDER_NAME = "com.fastbootmobile.encore.metadata.PROVIDER_NAME";

    /**
     * For a provider to be valid, it MUST have a meta-data presenting the author of the plug-in,
     * such as the company name or the author of the source code (e.g. "The OmniROM Project", or
     * "XpLoDWilD").
     */
    public static final String METADATA_PROVIDER_AUTHOR = "com.fastbootmobile.encore.metadata.PROVIDER_AUTHOR";

    /**
     * A provider MAY have a configuration activity to setup the user account (e.g. in case the
     * providers needs a login/password) for a music provider, or to setup the audio effect
     * parameter in case of a DSP provider. This meta-data value should be the activity class name.
     */
    public static final String METADATA_CONFIG_CLASS = "com.fastbootmobile.encore.metadata.CONFIG_CLASS";
}

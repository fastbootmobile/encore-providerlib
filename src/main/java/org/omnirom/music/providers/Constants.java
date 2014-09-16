package org.omnirom.music.providers;

public class Constants {

    /**
     * Returns this value in getVersion
     */
    public static final int API_VERSION = 1;

    /**
     * When the app will look for providers, it will pick-up all services that implements
     * the PICK_PROVIDER action.
     */
    public static final String ACTION_PICK_PROVIDER = "org.omnirom.music.action.PICK_PROVIDER";

    /**
     * When the app will look for DSP effect providers, it will pick-up all services that implements
     * the PICK_DSP_PROVIDER action.
     */
    public static final String ACTION_PICK_DSP_PROVIDER = "org.omnirom.music.action.PICK_DSP_PROVIDER";

    /**
     * For a provider to be valid, it MUST have a meta-data presenting the name of the providers
     * it represents (e.g. "Deezer", "Spotify", etc in case of a music provider, or "Bass Boost",
     * "10-Band Equalizer", etc for a DSP provider).
     */
    public static final String METADATA_PROVIDER_NAME = "org.omnirom.music.metadata.PROVIDER_NAME";

    /**
     * For a provider to be valid, it MUST have a meta-data presenting the author of the plug-in,
     * such as the company name or the author of the source code (e.g. "The OmniROM Project", or
     * "XpLoDWilD").
     */
    public static final String METADATA_PROVIDER_AUTHOR = "org.omnirom.music.metadata.PROVIDER_AUTHOR";

    /**
     * A provider MAY have a configuration activity to setup the user account (e.g. in case the
     * providers needs a login/password) for a music provider, or to setup the audio effect
     * parameter in case of a DSP provider. This meta-data value should be the activity class name.
     */
    public static final String METADATA_CONFIG_CLASS = "org.omnirom.music.metadata.CONFIG_CLASS";
}

# Encore Provider Library

Encore's Provider Library, internally referred to as "providerlib", is the library allowing
third-party developers to create music sources ("providers") and sound processing ("DSP") plug-ins
for Encore.

## Overview
### Working
Plug-ins can be made in either full Java, or with 99% of native (C++) code. Plug-in discovery is
made thanks to AndroidManifest attributes, and plug-in initialization is done via AIDL
(service interface) calls in Java. Music sources plug-ins will need to implement a full set of
AIDL methods and callbacks, while DSP plugins only need a few methods.

Music providers will need to implement the ``IMusicProvider`` AIDL, whereas the DSP will need to
implement ``IDSPProvider`` AIDL.

App can register one or more callbacks to the provider, so it must register them all locally, and
when calling a callback method, it must be done on all registered callbacks.

### Entities
The major part of data transferred through AIDL requires two important information: the plug-in's
``ProviderIdentifier`` and an entity unique reference (a ``String``). The former is assigned by
the main application and must be retained by the provider and passed along callbacks, while the
second is assigned by the plug-in to uniquely identify an entity (a song, an artist, an album,
etc). It is strongly recommended to make sure entities references are unique across all providers,
hence why you should have your service's name at the beginning of the reference. A good example
of reference is ``spotify:album:abcdefghiklmnopq``.

The main app builds itself an entity cache that is cleared when the app is killed. Calls won't
be made to ``getSong``, ``getAlbum``, ``getArtist``, etc if the app has the entity in its cache.
If the entity might have changed, it is up to the provider to call relevant callbacks to notify
the app that the entity has changed (using ``onSongUpdated``, ``onArtistUpdated``, etc).

It is strongly recommended to make your service return as soon as possible, updating the entity
later via callbacks if needed. If your service needs to make potentially lengthy operations
(network, calculations, heavy loading), it is advised to return an "empty" entity, by only
setting the entity's reference and ``ProviderIdentifier``, and setting the isLoaded flag to
false using ``entity.setIsLoaded(false)``. Later, when the data has been fetched in a separate
thread, you can call the relevant method (``onSongUpdated``, etc) on the registered callbacks to
notify of the full entity data.

> Important note: When fetching a set of tracks, for example a playlist, the app will request
> the track information using getSong() if the track isn't already known. If the track is known,
> but not loaded (isLoaded() returns false and setIsLoaded(true) hasn't been called), the app
> won't request getSong again and relies on onSongUpdate() callback.


### Audio data
#### General workflow
The actual audio data is transferred through a local domain UNIX socket, which is assigned by the
main application when it initializes plug-ins. It is then up to the plug-in to transfer data using
our Java classes or our C++ classes (see ``src/main/jni/nativesocket`` or
``src/main/java/com/fastbootmobile/encore/providers`` for C++ and Java code).

In the case of a music provider, the audio must be decoded (PCM) by the provider itself and
transmitted to the main app using the socket. Here's the typical flow for music playback:

- The main app initializes the plug-in on boot, calling ``setAudioSocketName`` with the audio
socket name to use. The provider will initialize an ``AudioClientSocket`` (Java) or ``SocketClient``
(C++) with the provided name. These classes will keep the connection open and idle.
- The main app calls ``playSong(String ref)`` on the provider, which will validate the URI and
  return true. Considering the positive return code, the app will set itself in "buffering" state.
- The provider will initialize decoding and will start fetching the requested track, then start
  decoding samples.
- When the first samples are decoded and ready to be sent to the main app, the provider must call
  the ``onSongPlaying`` callback to notify buffering is done and playback will start. The app will
  then hide the buffering animation and consider music is playing.
- On the first played samples, you must send the format information using
  ``AudioSocket.writeFormatInfo`` / ``SocketClient::writeFormatInfo``. By default, the app will
  setup a 44.1kHz, 16 bits, stereo playback engine, so if your sample rate changes, you must notify
  the app using this call.
- The decoded samples must then be sent to the main app using the audio socket. If you're using
  Java, using ``AudioSocket.writeAudioData(short[], int)``, or in C++
  ``SocketClient::writeAudioData(uint8_t*, int)``. Once the samples have been processed, the app
  will reply with the number of bytes written. Zero bytes written indicates the audio buffers are
  full and the plug-in must retry to send the same samples later. Otherwise, the number of bytes
  written are returned.  In Java, the response is asynchronous and returned in
  ``AudioSocket.ISocketCallback.onAudioResponse``, so it is up to you to implement proper locking.
  In C++, you can either get the response asynchronously in your ``SocketCallbacks`` implementation,
  or synchronously by setting ``writeAudioData``'s last parameter to ``true`` which will make the
  method return the number of bytes replied by the main app.
- When the track is done (e.g. when EOF has been seen), the provider will call ``onTrackEnded`` to
  notify the app that the next track can be played.

> Note that in the current implementation, the audio engine will only queue either the full
> sent audio data array or nothing. This means that the number of bytes returned by a
> ``writeAudioData`` call will either return the same number of bytes that were sent, or zero.
> It is however possible that in the future, audio data may be split and only part of the
> transmitted audio data will be stored in the audio playback engine buffers. It is recommended
> to implement support for this behavior.

#### DSP specifics
DSPs work in a similar fashion to music providers, to the difference they're not decoding or
generating music. When the user puts your DSP plugin in its processing chain, the app will
automatically feed you with the audio data coming either from the provider, or the previous plugin
in the chain. No action is required in either Java or C++, as long as the DSP opened the connection
to the socket. This data will arrive in the ``onAudioData`` callback. The provider must then process
the sample, and return them to the app by calling ``writeAudioData``, just like a music provider.
DSPs however don't need to care about the number of bytes written, as space is guaranteed by the
app if you got audio data.

### Album art
Encore supports fetching album art from various web sources (MusicBrainz, Google Images, etc),
but these services might sometimes be slower than a local album art, or an album art provided
directly by the service. On calls to ``getSongArt``, ``getAlbumArt``, ``getArtistArt`` or
``getPlaylistArt``, providers may return true or false depending on their capability to provide an
image for the specified entity. When returning true, the app won't fetch the art itself, and will
wait on the provider to provide an image.

All illustrations are cached by the main app.

The app will wait up to 6 seconds for the art to be downloaded until cancelling the load to free up
processing time for other illustrations. If the provider returns an art after the timeout is
expired, it will still be cached by the app and will show up on the next display request (e.g.
after scrolling down and back up, or when closing and reopening a view).

### Project Rosetta-stone support
Encore's main app gather some information from the
[EchoNest API](http://developer.echonest.com/index.html), which understands services' URIs directly
in their database.

If you want to enable support for Rosetta-stone in your provider and offer the user additional
services (similar artists, and Automix support), you must:

- Make sure your references are the actual service's URIs (e.g. a Spotify provider would use
  Spotify's URIs as references)
- Make sure you return the Rosetta-stone ID space in ``getSupportedRosettaStonePrefixes()`` API
  method (see the [list of ID spaces](http://developer.echonest.com/docs/v4#project-rosetta-stone))

## SDK Contents
The providerlib comes with two parts: a native (C++) part, and a Java part. Plug-ins can be made in
both full Java, or fully native code.

- The Java part contains multiple types of files:
  - The AIDL files allows for main service calls (get songs information, get playlists, etc) and app
    callbacks methods (playback started, track ended, etc).
  - The Java files contains two packages, a ``model`` package and a ``providers`` package
    - The ``model`` package contains the shared structures between the main app and the providers
      (``Song``, ``Album``, ``Artist``, ``Playlist`` and ``SearchResult``). All of these classes
      inherit from the common class ``BoundEntity`` which commonizes the reference parameter as
      well as the provider identifier. When sending data to the main app, you MUST use these classes.
    - The ``providers`` package, which contains the constants and the audio socket classes if you
      wish to handle the audio data purely in Java.
    - There is a separate ``Encore.Plugin`` class which contains generated code for the socket's
      protobuf protocol. You should not need to deal with this class directly, besides the fields
      used in ``ISocketCallbacks``.
- The JNI folder contains the native implementation of the audio socket communication protocol.
  In a similar way to Java, there is a ``SocketClient`` and a ``SocketCallbacks`` to communicate
  with the main app and get responses.

## Getting started

Feel free to check out the open-sourced providers to get started, as well as the boilerplate
AndroidManifest and Plugin Service java files. Here are a few steps to guide you through:

- Create a project and import the ``providerlib`` module in one of these ways:
  - copy the folder and add the module to your ``settings.gradle`` file
  - **or** in Android Studio: download the library (using ``Download ZIP``), use ``File -> New -> Import Module`` to import the downloaded folder, go to ``build.gradle`` file and add under the ``dependecies{}`` section: ``compile project( ':encore-providerlib' )``
- Add the required meta-data and actions in your AndroidManifest.xml
  (see ``com.fastbootmobile.encore.providers.Constants``)
- Create a Service and implement either ``IMusicProvider`` or ``IDSPProvider`` depending on
  whether you're working on a music provider, or a DSP processor
- Create the Audio socket in either Java or Native code in ``setAudioSocketName``
- Do whatever you need



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

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.UninitializedMessageException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import omnimusic.Plugin;

/**
 * Audio client socket for plugins to communicate with the main app (transmit audio data, etc).
 * <p/>
 * This class is the java equivalent of the native "nativesocket" implementation found in the
 * jni directory. Both use protobuf to serialize the data, and use the Plugin.proto declarations.
 * Both native and java implementation are interoperable, so you're free to implement your plugin
 * either fully native, or fully java (but remember, native is faster!).
 * <p/>
 * Some notes about the implementation:
 * - All packets are prefixed with an integer (4 bytes) representing the message size, followed
 * by a byte representing the message type (opcode). The message size MUST include the opcode
 * byte.
 * - The ability to push both short[] and byte[] audio data is provided. The pipeline works
 * better with byte, and short[] methods are provided for convenience only (to reduce the
 * amount of work needed to convert). Whenever possible, prefer the byte[] methods.
 */
public abstract class AudioSocket {

    private static final String TAG = "AudioSocket";

    // Keep in sync with Plugin.proto and nativesocket
    protected static final int OPCODE_AUDIODATA         = 1;
    protected static final int OPCODE_AUDIORESPONSE     = 2;
    protected static final int OPCODE_REQUEST           = 3;
    protected static final int OPCODE_FORMATINFO        = 4;
    protected static final int OPCODE_BUFFERINFO        = 5;
    protected static final int OPCODE_SHUTDOWN          = 6;

    private final ByteBuffer mIntBuffer;
    private ByteBuffer mSamplesBuffer;
    private byte[] mInputBuffer = new byte[44100];
    private ISocketCallback mCallback;
    private String mSocketName;

    // Re-use messages
    private Plugin.AudioData.Builder mRcvAudioDataBuilder;
    private Plugin.AudioData.Builder mSndAudioDataBuilder;
    private Plugin.AudioResponse.Builder mAudioResponseBuilder;
    private Plugin.FormatInfo.Builder mFormatInfoBuilder;
    private Plugin.BufferInfo.Builder mBufferInfoBuilder;
    private Plugin.Request.Builder mRequestBuilder;

    private CodedInputStream mCodedInputStream;
    private CodedOutputStream mCodedOutputStream;

    public AudioSocket() {
        mIntBuffer = ByteBuffer.allocateDirect(4);
        mSamplesBuffer = ByteBuffer.allocateDirect(8192);
    }

    /**
     * Connects the audio socket to the main app. In case the main app is down or hasn't
     * opened the socket yet, an IOException may be thrown. You must then retry to connect later.
     *
     * @throws IOException
     */
    public void connect(final String socketName) throws IOException {
        if (!socketName.equals(mSocketName)) {
            // Preventive disconnection
            disconnectSocket();
            mSocketName = socketName;
            initializeSocket(socketName);
        }
    }

    /**
     * Initializes the socket with the provided socketName.
     *
     * @param socketName The name of the UNIX-domain socket file
     * @throws IOException
     */
    protected abstract void initializeSocket(final String socketName) throws IOException;

    /**
     * Disconnects the socket
     */
    protected abstract void disconnectSocket();

    /**
     * Returns the socket's output stream
     */
    protected abstract OutputStream getOutputStream();

    /**
     * Returns the socket's input stream
     */
    protected abstract InputStream getInputStream();

    /**
     * @return The name of the socket (or null if connect hasn't been called)
     */
    public String getSocketName() {
        return mSocketName;
    }

    /**
     * Sets the callback that should be called when socket event occurs (ie. incoming data)
     *
     * @param callback The callback to use
     */
    public void setCallback(ISocketCallback callback) {
        mCallback = callback;
    }

    /**
     * Notifies the main app of the format data. This should be called once per song (on playback
     * start for instance), to make sure the main app is running the output at the proper format.
     * In the case of DSP providers, this metadata is ignored as the music provider gives the source
     * clock and the format data is broadcast from the provider to the DSP effect (tl;dr the DSP
     * has to adapt to the input format data given by the provider).
     *
     * @param channels   The number of audio channels (generally 2 for stereo)
     * @param sampleRate The sample rate of the audio (generally 44100)
     * @throws IOException
     */
    public void writeFormatData(int channels, int sampleRate) throws IOException {
        Plugin.FormatInfo msg =
                Plugin.FormatInfo.newBuilder()
                        .setChannels(channels)
                        .setSamplingRate(sampleRate)
                        .build();

        final OutputStream outStream = getOutputStream();
        outStream.write(intToByte(msg.getSerializedSize() + 1));
        outStream.write(OPCODE_FORMATINFO);
        outStream.write(msg.toByteArray());
    }

    /**
     * This method is used by audio sinks. It is used when a client sends a request for buffer info
     * to the main app, and will reply with the current buffering info from the currently active
     * audio sink.
     *
     * @param samples Current number of samples in the buffer
     * @param stutter Stutter or dropout events that occurred
     * @throws IOException
     */
    public void writeBufferInfo(int samples, int stutter) throws IOException {
        Plugin.BufferInfo msg =
                Plugin.BufferInfo.newBuilder()
                        .setSamples(samples)
                        .setStutter(stutter)
                        .build();

        final OutputStream outStream = getOutputStream();
        outStream.write(intToByte(msg.getSerializedSize() + 1));
        outStream.write(OPCODE_BUFFERINFO);
        outStream.write(msg.toByteArray());
    }

    /**
     * Requests some metadata from the other end. Generally, this is used by provider plugins to
     * query buffer stats from the main app, but it could be possible in the future that the app
     * may require to pull format info from a plugin (both DSP and Music), so it is a good idea to
     * implement a reply. It is however invalid for a music provider to request FORMAT_INFO, as the
     * plugin must set it.
     * Two requests exist:
     * - BUFFER_INFO: Requests buffer stats
     * - FORMAT_INFO: Requests current playback format
     * The replies will be send (if implemented and valid) with their respective opcode packets in
     * the callbacks.
     *
     * @param request The type of request to query
     * @throws IOException
     */
    public void writeRequest(Plugin.Request.RequestType request) throws IOException {
        Plugin.Request msg =
                Plugin.Request.newBuilder()
                        .setRequest(request)
                        .build();

        final OutputStream outStream = getOutputStream();
        outStream.write(intToByte(msg.getSerializedSize() + 1));
        outStream.write(OPCODE_REQUEST);
        outStream.write(msg.toByteArray());
    }

    /**
     * This method is used by audio sinks to reply to AUDIO_DATA messages, indicating the number
     * of samples that were written.
     *
     * @param written The number of samples written
     * @throws IOException
     */
    public void writeAudioResponse(int written) throws IOException {
        Plugin.AudioResponse msg =
                Plugin.AudioResponse.newBuilder()
                        .setWritten(written)
                        .build();

        final OutputStream outStream = getOutputStream();
        outStream.write(intToByte(msg.getSerializedSize() + 1));
        outStream.write(OPCODE_AUDIORESPONSE);
        outStream.write(msg.toByteArray());
    }

    /**
     * Writes audio data to the socket. The frames must be short samples (INT16 LITTLE ENDIAN),
     * and a specific number of frames may be written using numFrames (if you want to write
     * everything, simply pass frames.length as numFrames).
     * The main app puts some of the data in buffer to avoid stuttering, but the amount of cached
     * buffers is very limited. Extra buffers will be discarded, so it is good practice to
     * constantly write a small amount of data, for instance continuous buffers of 2048 or 4096
     * samples, and wait the proper amount of time. This will also allow you to have less delay
     * between the play command and the actual playback on the device audio output, without using
     * up the device's memory.
     *
     * @param frames    The array of samples
     * @param numFrames The number of samples to write, from offset zero
     * @throws IOException
     */
    public void writeAudioData(short[] frames, int numFrames) throws IOException {
        final int numBytes = numFrames * 2;
        if (numBytes > mSamplesBuffer.capacity() || mSamplesBuffer == null) {
            // Allow maximum 500KB audio at a time
            if (numBytes < 524288) {
                mSamplesBuffer = ByteBuffer.allocateDirect(numBytes);
            } else {
                throw new IllegalArgumentException("You must not pass more than 524288 " +
                        "bytes of audio at a time");
            }
        }

        if (numFrames > 0) {
            mSamplesBuffer.rewind();
            mSamplesBuffer.asShortBuffer().put(frames, 0, numFrames);

            if (mSndAudioDataBuilder == null) {
                mSndAudioDataBuilder = Plugin.AudioData.newBuilder();
            }
            Plugin.AudioData msg = mSndAudioDataBuilder
                    .setSamples(ByteString.copyFrom(mSamplesBuffer.array(), 0, numFrames * 2))
                    .build();

            final OutputStream outStream = getOutputStream();
            outStream.write(intToByte(msg.getSerializedSize() + 1));
            outStream.write(OPCODE_AUDIODATA);
            outStream.write(msg.toByteArray());
        }
    }

    /**
     * Writes audio data to the socket. The format of the data must match the format you passed
     * in your FORMAT_INFO (writeFormatInfo) message.
     * The main app puts some of the data in buffer to avoid stuttering, but the amount of cached
     * buffers is very limited. Extra buffers will be discarded, so it is good practice to
     * constantly write a small amount of data, for instance continuous buffers of 2048 or 4096
     * samples, and wait the proper amount of time. This will also allow you to have less delay
     * between the play command and the actual playback on the device audio output, without using
     * up the device's memory.
     *
     * @param frames The array of samples
     * @param offset The offset within the array
     * @param length The number of samples to write, from offset
     * @throws IOException
     */
    public void writeAudioData(byte[] frames, int offset, int length) throws IOException {
        if (length > 0) {
            if (mSndAudioDataBuilder == null) {
                mSndAudioDataBuilder = Plugin.AudioData.newBuilder();
            }

            Plugin.AudioData msg =
                    mSndAudioDataBuilder
                            .setSamples(ByteString.copyFrom(frames, offset, length))
                            .build();

            if (mCodedOutputStream == null) {
                mCodedOutputStream = CodedOutputStream.newInstance(getOutputStream());
            }

            final int msgSize = msg.getSerializedSize();
            final OutputStream outStream = getOutputStream();
            if (outStream != null) {
                outStream.write(intToByte(msgSize + 1));
                outStream.write(OPCODE_AUDIODATA);
                msg.writeTo(mCodedOutputStream);
                mCodedOutputStream.flush();
            } else {
                throw new IOException("Cannot write audio data, socket hasn't been opened");
            }
        }
    }

    /**
     * Process the input stream
     *
     * @param length Length of the message (with the opcode)
     */
    protected boolean processInputStream(int length) throws IOException {
        final InputStream inStream = getInputStream();
        int opcode = inStream.read();

        if (opcode == OPCODE_SHUTDOWN) {
            return false;
        }

        if (mCodedInputStream == null) {
            mCodedInputStream = CodedInputStream.newInstance(inStream);
        }

        if (length > 10 * 1024 * 1024) {
            Log.e(TAG, "Message length is too large (" + (length / 1024 / 1024) + "MB > 10MB!), closing socket");
            return false;
        }

        if (length > mInputBuffer.length) {
            Log.w(TAG, "Message length is larger than input buffer! Resizing buffer");
            try {
                mInputBuffer = new byte[length];
            } catch (OutOfMemoryError e) {
                // This might happen when the app is freezing for too long. Providers might
                // accumulate buffers and send them through the stream, causing a way too
                // big message to process.
                // We skip this message.
                return false;
            }
        }

        int msgBytes = 0;
        while (msgBytes >= 0 && msgBytes < length - 1) {
            msgBytes += inStream.read(mInputBuffer, msgBytes, length - msgBytes - 1);
        }

        if (msgBytes < 0) {
            Log.w(TAG, "Socket broke while reading input stream");
            return false;
        }

        switch (opcode) {
            case OPCODE_AUDIODATA:
                handleAudioData(mInputBuffer, msgBytes);
                break;

            case OPCODE_FORMATINFO:
                handleFormatInfo(mInputBuffer, msgBytes);
                break;

            case OPCODE_AUDIORESPONSE:
                handleAudioResponse(mInputBuffer, msgBytes);
                break;

            case OPCODE_BUFFERINFO:
                handleBufferInfo(mInputBuffer, msgBytes);
                break;

            case OPCODE_REQUEST:
                handleRequest(mInputBuffer, msgBytes);
                break;

            default:
                Log.e(TAG, "Invalid opcode " + opcode + "! Kicking out socket");
                return false;
        }

        return true;
    }

    /**
     * Process an AUDIO_DATA packet
     *
     * @param buffer The message data buffer
     * @param length The length of the message
     */
    private void handleAudioData(byte[] buffer, int length) {
        if (mCallback != null) {
            try {
                if (mRcvAudioDataBuilder == null) {
                    mRcvAudioDataBuilder = Plugin.AudioData.newBuilder();
                }

                mRcvAudioDataBuilder.mergeFrom(buffer, 0, length);
                mCallback.onAudioData(this, mRcvAudioDataBuilder);
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "Invalid AUDIO_DATA message", e);
            } catch (UninitializedMessageException e) {
                Log.e(TAG, "Invalid AUDIO_DATA message", e);
            }
        }
    }

    /**
     * Process a FORMAT_INFO packet
     *
     * @param buffer The message data buffer
     * @param length The length of the message
     */
    private void handleFormatInfo(byte[] buffer, int length) {
        if (mCallback != null) {
            try {
                if (mFormatInfoBuilder == null) {
                    mFormatInfoBuilder = Plugin.FormatInfo.newBuilder();
                }

                mFormatInfoBuilder.mergeFrom(buffer, 0, length);
                mCallback.onFormatInfo(this, mFormatInfoBuilder);
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "Invalid FORMAT_INFO message", e);
            } catch (UninitializedMessageException e) {
                Log.e(TAG, "Invalid FORMAT_INFO message, dropped", e);
            }
        }
    }

    /**
     * Process an AUDIO_RESPONSE packet
     *
     * @param buffer The message data buffer
     * @param length The length of the message
     */
    private void handleAudioResponse(byte[] buffer, int length) {
        if (mCallback != null) {
            try {
                if (mAudioResponseBuilder == null) {
                    mAudioResponseBuilder = Plugin.AudioResponse.newBuilder();
                }

                mAudioResponseBuilder.mergeFrom(buffer, 0, length);
                mCallback.onAudioResponse(this, mAudioResponseBuilder);
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "Invalid AUDIO_RESPONSE message", e);
            } catch (UninitializedMessageException e) {
                Log.e(TAG, "Invalid AUDIO_RESPONSE message", e);
            }
        }
    }

    /**
     * Process a BUFFER_INFO packet
     *
     * @param buffer The message data buffer
     * @param length The length of the message
     */
    private void handleBufferInfo(byte[] buffer, int length) {
        if (mCallback != null) {
            try {
                if (mBufferInfoBuilder == null) {
                    mBufferInfoBuilder = Plugin.BufferInfo.newBuilder();
                }
                mBufferInfoBuilder.mergeFrom(buffer, 0, length);
                mCallback.onBufferInfo(this, mBufferInfoBuilder);
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "Invalid AUDIO_RESPONSE message", e);
            } catch (UninitializedMessageException e) {
                Log.e(TAG, "Invalid AUDIO_RESPONSE message", e);
            }
        }
    }

    /**
     * Process a REQUEST packet
     *
     * @param buffer The message data buffer
     * @param length The length of the message
     */
    private void handleRequest(byte[] buffer, int length) {
        if (mCallback != null) {
            try {
                if (mRequestBuilder == null) {
                    mRequestBuilder = Plugin.Request.newBuilder();
                }

                mRequestBuilder.mergeFrom(buffer, 0, length);
                mCallback.onRequest(this, mRequestBuilder);
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "Invalid REQUEST message", e);
            } catch (UninitializedMessageException e) {
                Log.e(TAG, "Invalid REQUEST message", e);
            }
        }
    }

    protected byte[] intToByte(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    protected int byteToInt(byte[] data) {
        synchronized (mIntBuffer) {
            ByteBuffer buf = ByteBuffer.wrap(data);
            return buf.getInt(0);
        }
    }

    /**
     * Interface allowing the plugin to be notified when an incoming message arrives
     */
    public interface ISocketCallback {
        /**
         * Called when an AUDIO_DATA message has been received (ie. when audio data arrived)
         *
         * @param message The protobuf message
         */
        public void onAudioData(AudioSocket socket, Plugin.AudioData.Builder message);

        /**
         * Called when an AUDIO_RESPONSE message has been received (ie. when audio data has
         * been delivered and the remote end sent a confirmation)
         *
         * @param message The protobuf message
         */
        public void onAudioResponse(AudioSocket socket, Plugin.AudioResponse.Builder message);

        /**
         * Called when a REQUEST message has been received. The remote end is expecting the response
         * to its request (see RequestType) as soon as possible.
         *
         * @param message The protobuf message
         */
        public void onRequest(AudioSocket socket, Plugin.Request.Builder message);

        /**
         * Called when a FORMAT_INFO message has been received.
         *
         * @param message The protobuf message
         */
        public void onFormatInfo(AudioSocket socket, Plugin.FormatInfo.Builder message);

        /**
         * Called when a BUFFER_INFO message has been received.
         *
         * @param message The protobuf message
         */
        public void onBufferInfo(AudioSocket socket, Plugin.BufferInfo.Builder message);
    }
}

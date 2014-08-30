package org.omnirom.music.providers;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.SystemClock;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import omnimusic.Plugin;

/**
 * Audio client socket for plugins to communicate with the main app (transmit audio data, etc).
 *
 * This class is the java equivalent of the native "nativesocket" implementation found in the
 * jni directory. Both use protobuf to serialize the data, and use the Plugin.proto declarations.
 * Both native and java implementation are interoperable, so you're free to implement your plugin
 * either fully native, or fully java (but remember, native is faster!).
 *
 * Some notes about the implementation:
 *  - All packets are prefixed with an integer (4 bytes) representing the message size, followed
 *    by a byte representing the message type (opcode). The message size MUST include the opcode
 *    byte.
 *  - The ability to push both short[] and byte[] audio data is provided. The pipeline works
 *    better with byte, and short[] methods are provided for convenience only (to reduce the
 *    amount of work needed to convert). Whenever possible, prefer the byte[] methods.
 */
public abstract class AudioSocket {

    private static final String TAG = "AudioSocket";

    // Keep in sync with Plugin.proto and nativesocket
    protected static final int OPCODE_AUDIODATA = 1;
    protected static final int OPCODE_AUDIORESPONSE = 2;
    protected static final int OPCODE_REQUEST = 3;
    protected static final int OPCODE_FORMATINFO = 4;
    protected static final int OPCODE_BUFFERINFO = 5;

    private final ByteBuffer mIntBuffer;
    private ByteBuffer mSamplesBuffer;
    private short[] mSamplesShortBuffer = new short[262144];
    private byte[] mInputBuffer = new byte[128000];
    private ISocketCallback mCallback;
    private String mSocketName;

    public AudioSocket() {
        mIntBuffer = ByteBuffer.allocateDirect(4);
        mSamplesBuffer = ByteBuffer.allocateDirect(262144 * 2);
    }

    /**
     * Connects the audio socket to the main app. In case the main app is down or hasn't
     * opened the socket yet, an IOException may be thrown. You must then retry to connect later.
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
     * @param callback The callback to use
     */
    public void setCallback(ISocketCallback callback) {
        mCallback = callback;
    }

    /**
     * Notifies the main app of the format data. This should be called once per song (on playback
     * start for instance), to make sure the main app is running the output at the proper format.
     * @param channels The number of audio channels (generally 2 for stereo)
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
     * @param frames The array of samples
     * @param numFrames The number of samples to write, from offset zero
     * @throws IOException
     */
    public void writeAudioData(short[] frames, int numFrames) throws IOException {
        if (numFrames > mSamplesBuffer.capacity()) {
            throw new IllegalArgumentException("You must not pass more than " +
                    mSamplesBuffer.capacity() + " samples at a time");
        }

        if (numFrames > 0) {
            mSamplesBuffer.rewind();
            mSamplesBuffer.asShortBuffer().put(frames, 0, numFrames);

            Plugin.AudioData msg =
                    Plugin.AudioData.newBuilder()
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
            Plugin.AudioData msg =
                    Plugin.AudioData.newBuilder()
                            .setSamples(ByteString.copyFrom(frames, offset, length))
                            .build();

            final OutputStream outStream = getOutputStream();
            outStream.write(intToByte(msg.getSerializedSize() + 1));
            outStream.write(OPCODE_AUDIODATA);
            outStream.write(msg.toByteArray());
        }
    }

    /**
     * Process the input stream
     * @param length Length of the message (with the opcode)
     */
    protected void processInputStream(int length) throws IOException {
        final InputStream inStream = getInputStream();
        int opcode = inStream.read();

        int msgBytes = 0;
        while (msgBytes < length - 1) {
            msgBytes += inStream.read(mInputBuffer, msgBytes, length - msgBytes - 1);
        }

        //Log.d(TAG, "Read message opcode=" + opcode + " length=" + length + " msgbytes=" + msgBytes);
        switch (opcode) {
            case OPCODE_AUDIODATA:
                handleAudioData(mInputBuffer, msgBytes);
                break;
            case OPCODE_FORMATINFO:
                handleFormatInfo(mInputBuffer, msgBytes);
                break;
        }
    }

    /**
     * Process an AUDIO_DATA packet
     * @param buffer The message data buffer
     */
    private void handleAudioData(byte[] buffer, int length) {
        Plugin.AudioData message;
        try {
            message = Plugin.AudioData.newBuilder().mergeFrom(buffer, 0, length).build();

            if (mCallback != null) {
                mCallback.onAudioData(this, message);
            }
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG, "Invalid AUDIO_DATA message", e);
        }
    }

    /**
     * Process a FORMAT_INFO packet
     * @param buffer The message data buffer
     */
    private void handleFormatInfo(byte[] buffer, int length) {
        Plugin.FormatInfo message;
        try {
            message = Plugin.FormatInfo.newBuilder().mergeFrom(buffer, 0, length).build();

            if (mCallback != null) {
                mCallback.onFormatInfo(this, message);
            }
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG, "Invalid FORMAT_INFO message", e);
        }
    }

    /**
     * Read data from the audio socket. This method is blocking and will wait until data is
     * available.
     *
     * @return An array of short samples
     * @throws IOException
     */
    /*public short[] readAudioFrames() throws IOException {
        if (mInStream.read(mIntBuffer.array(), 0, 4) != 4) {
            Log.e(TAG, "Reading an int but read didn't return 4 bytes!");
            throw new IOException("Invalid read count, stream will be off!");
        }
        final int messageSize = mIntBuffer.getInt(0);

        int opcode = mInStream.read();

        if (opcode == OPCODE_AUDIODATA) {
            final int totalToRead = messageSize - 1; // 1 short = 2 bytes
            int totalRead = 0;
            int sizeToRead = totalToRead;

            while (totalRead < totalToRead) {
                int read = mInStream.read(mSamplesBuffer.array(), totalRead, sizeToRead);
                if (read >= 0) {
                    totalRead += read;
                }
                sizeToRead = totalToRead - totalRead;
            }

            Plugin.AudioData msg = Plugin.AudioData.parseFrom(mSamplesBuffer.array());
            msg.getSamples().copyTo(mSamplesBuffer.array(), 0);
            mSamplesBuffer.asShortBuffer().get(mSamplesShortBuffer);

            short[] output = new short[totalRead / 2];
            System.arraycopy(mSamplesShortBuffer, 0, output, 0, totalRead / 2);

            return output;
        }

        return null;
    }*/

    protected byte[] intToByte(int value) {
        synchronized (mIntBuffer) {
            mIntBuffer.rewind();
            mIntBuffer.putInt(value);
            return mIntBuffer.array();
        }
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
         * @param message The protobuf message
         */
        public void onAudioData(AudioSocket socket, Plugin.AudioData message);

        /**
         * Called when an AUDIO_RESPONSE message has been received (ie. when audio data has
         * been delivered and the remote end sent a confirmation)
         * @param message The protobuf message
         */
        public void onAudioResponse(AudioSocket socket, Plugin.AudioResponse message);

        /**
         * Called when a REQUEST message has been received. The remote end is expecting the response
         * to its request (see RequestType) as soon as possible.
         * @param message The protobuf message
         */
        public void onRequest(AudioSocket socket, Plugin.Request message);

        /**
         * Called when a FORMAT_INFO message has been received.
         * @param message The protobuf message
         */
        public void onFormatInfo(AudioSocket socket, Plugin.FormatInfo message);

        /**
         * Called when a BUFFER_INFO message has been received.
         * @param message The protobuf message
         */
        public void onBufferInfo(AudioSocket socket, Plugin.BufferInfo message);
    }
}

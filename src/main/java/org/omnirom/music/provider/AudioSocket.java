package org.omnirom.music.provider;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 *
 */
public class AudioSocket {

    private static final String TAG = "AudioSocket";

    public static final int OPCODE_FORMAT = 1;
    public static final int OPCODE_DATA = 2;

    private LocalSocket mSocket;
    private OutputStream mOutStream;
    private ByteBuffer mIntBuffer;
    private ByteBuffer mSamplesBuffer;

    /**
     * Creates and connect the audio socket to the main app. In case the main app is down or hasn't
     * opened the socket yet, an IOException may be thrown. You must then retry to connect later.
     * @throws IOException
     */
    public AudioSocket(final String socketName) throws IOException {
        mIntBuffer = ByteBuffer.allocateDirect(4);
        mSamplesBuffer = ByteBuffer.allocateDirect(65535 * 2);

        mSocket = new LocalSocket();
        mSocket.connect(new LocalSocketAddress(socketName));
        mOutStream = mSocket.getOutputStream();
    }

    @Override
    protected void finalize() throws Throwable {
        // Close the socket
        mOutStream.close();
        mSocket.close();

        super.finalize();
    }

    /**
     * Notifies the main app of the format data. This should be called once per song (on playback
     * start for instance), to make sure the main app is running the output at the proper format.
     * @param channels
     * @param sampleRate
     * @throws IOException
     */
    public void writeFormatData(int channels, int sampleRate) throws IOException {
        /**
         * Format data packet:
         * OPCODE_FORMAT    [byte]
         * CHANNELS         [byte]  (1 == mono, 2 == stereo)
         * SAMPLE RATE      [int]   (generally 44100)
         */
        mOutStream.write(OPCODE_FORMAT);
        mOutStream.write(channels);
        mIntBuffer.rewind();
        mIntBuffer.putInt(sampleRate);
        mOutStream.write(mIntBuffer.array());
    }

    /**
     * Writes audio data to the socket. The frames must be short samples (INT16 LITTLE ENDIAN),
     * and a specific number of frames may be written using numFrames (if you want to write
     * everything, simply pass frames.length as numFrames).
     * The main app can handle up to 262144 samples in its buffer. Any extra sample will be
     * discarded, so it is good practice to constantly write a small amount of data, for instance
     * continuous buffers of 2048 or 4096 samples. This will also allow you to have less delay
     * between the play command and the actual playback on the device audio output.
     *
     * @param frames The array of samples
     * @param numFrames The number of samples to write, from offset zero
     * @throws IOException
     */
    public void writeAudioData(short[] frames, int numFrames) throws IOException {
        /**
         * Audio data packet:
         * OPCODE_DATA      [byte]
         * NUM_FRAMES       [int] (number of short values to read)
         * SAMPLES          [short[]]
         */
        if (numFrames > 0) {
            mOutStream.write(OPCODE_DATA);
            mIntBuffer.rewind();
            mIntBuffer.putInt(numFrames);
            mOutStream.write(mIntBuffer.array());

            mSamplesBuffer.rewind();
            for (int i = 0; i < numFrames; i++) {
                mSamplesBuffer.putShort(frames[i]);
            }
            mOutStream.write(mSamplesBuffer.array(), 0, numFrames * 2);

            mOutStream.flush();
        }
    }
}

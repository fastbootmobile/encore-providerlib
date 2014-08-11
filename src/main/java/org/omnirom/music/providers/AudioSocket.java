package org.omnirom.music.providers;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 *
 */
public class AudioSocket {

    private static final String TAG = "AudioSocket";

    public static final int OPCODE_FORMAT = 1;
    public static final int OPCODE_DATA = 2;

    private LocalSocket mSocket;
    private OutputStream mOutStream;
    private InputStream mInStream;
    private ByteBuffer mIntBuffer;
    private ByteBuffer mSamplesBuffer;
    private short[] mSamplesShortBuffer = new short[262144];

    /**
     * Creates and connect the audio socket to the main app. In case the main app is down or hasn't
     * opened the socket yet, an IOException may be thrown. You must then retry to connect later.
     * @throws IOException
     */
    public AudioSocket(final String socketName) throws IOException {
        mIntBuffer = ByteBuffer.allocateDirect(4);
        mSamplesBuffer = ByteBuffer.allocateDirect(262144 * 2);

        mSocket = new LocalSocket();
        mSocket.connect(new LocalSocketAddress(socketName));
        mOutStream = mSocket.getOutputStream();
        mInStream = mSocket.getInputStream();
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
     * @param channels The number of audio channels (generally 2 for stereo)
     * @param sampleRate The sample rate of the audio (generally 44100)
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
        if (numFrames > mSamplesBuffer.capacity()) {
            throw new IllegalArgumentException("You must not pass more than " +
                    mSamplesBuffer.capacity() + " samples at a time");
        }
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
            mSamplesBuffer.asShortBuffer().put(frames, 0, numFrames);

            mOutStream.write(mSamplesBuffer.array(), 0, numFrames * 2);

            mOutStream.flush();
        }
    }

    /**
     * Read data from the audio socket. This method is blocking and will wait until data is
     * available.
     *
     * @return An array of short samples
     * @throws IOException
     */
    public short[] readAudioFrames() throws IOException {
        int opcode = mInStream.read();

        if (mInStream.read(mIntBuffer.array(), 0, 4) != 4) {
            Log.e(TAG, "Reading an int but read didn't return 4 bytes!");
            throw new IOException("Invalid read count, stream will be off!");
        }
        final int numFrames = mIntBuffer.getInt(0);

        final int totalToRead = numFrames * 2; // 1 short = 2 bytes
        int totalRead = 0;
        int sizeToRead = totalToRead;

        while (totalRead < totalToRead) {
            int read = mInStream.read(mSamplesBuffer.array(), totalRead, sizeToRead);
            if (read >= 0) {
                totalRead += read;
            }
            sizeToRead = totalToRead - totalRead;
        }

        mSamplesBuffer.asShortBuffer().get(mSamplesShortBuffer);

        short[] output = new short[numFrames];
        System.arraycopy(mSamplesShortBuffer, 0, output, 0, numFrames);

        return output;
    }
}

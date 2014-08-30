package org.omnirom.music.providers;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Guigui on 30/08/2014.
 */
public class AudioHostSocket extends AudioSocket {
    private static final String TAG = "AudioHostSocket";

    private LocalServerSocket mSocket;
    private InputStream mInStream;
    private OutputStream mOutStream;
    private boolean mLoopRun;
    private Thread mLoopThread;
    private byte[] mIntBuffer = new byte[4];

    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            int readDecay = 0;

            while (mLoopRun) {
                try {
                    LocalSocket client = mSocket.accept();
                    mInStream = client.getInputStream();
                    mOutStream = client.getOutputStream();

                    Log.d(TAG, "Client connected on socket " + mSocket.getLocalSocketAddress().getName());

                    while (mLoopRun) {
                        int readLen = mInStream.read(mIntBuffer, readDecay, 4 - readDecay);

                        // The length
                        if (readLen < 4 && readDecay + readLen < 4) {
                            readDecay += readLen;
                        } else {
                            readDecay = 0;
                            int msgSize = byteToInt(mIntBuffer);
                            processInputStream(msgSize);
                        }
                    }
                } catch (IOException e) {
                    Log.w(TAG, "Exception in the socket host loop", e);
                }
            }
        }
    };

    public AudioHostSocket() {
        super();
    }

    @Override
    protected void initializeSocket(String socketName) throws IOException {
        mSocket = new LocalServerSocket(socketName);
        Log.d(TAG, "Server socket " + socketName + " created");

        mLoopRun = true;
        mLoopThread = new Thread(mLoop);
        mLoopThread.start();
    }

    @Override
    protected void disconnectSocket() {
        if (mSocket != null) {
            mLoopRun = false;
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing socket", e);
            }
            mSocket = null;

            try {
                mLoopThread.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    @Override
    protected OutputStream getOutputStream() {
        return mOutStream;
    }

    @Override
    protected InputStream getInputStream() {
        return mInStream;
    }
}

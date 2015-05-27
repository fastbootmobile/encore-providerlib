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

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Audio Socket subclass handling host sockets (generally done by the main app only)
 */
public class AudioHostSocket extends AudioSocket {
    private static final String TAG = "AudioHostSocket";

    private LocalServerSocket mSocket;
    private InputStream mInStream;
    private OutputStream mOutStream;
    private boolean mLoopRun;
    private Thread mLoopThread;
    private byte[] mIntBuffer = new byte[4];
    private String mName;
    private AudioHostSocketListener mListener;

    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            int readDecay = 0;
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

            while (mLoopRun) {
                try {
                    LocalSocket client = mSocket.accept();

                    // In case we terminated the loop thread, the socket will be null.
                    if (mSocket == null || mSocket.getLocalSocketAddress() == null) {
                        mLoopRun = false;
                        return;
                    }

                    mInStream = client.getInputStream();
                    mOutStream = client.getOutputStream();

                    try {
                        Log.d(TAG, "Client connected on socket " + mSocket.getLocalSocketAddress().getName());
                    } catch (NullPointerException e) {
                        // I'm not sure why this happens - probably a thread race-condition, in that the socket
                        // workaround isn't disconnecting fast enough thus an NPE occurs even if we check
                        // for null just before getting the streams. We cut out here anyway.
                        mLoopRun = false;
                        return;
                    }

                    while (mLoopRun) {
                        if (mInStream.available() >= 4 - readDecay) {
                            int readLen = mInStream.read(mIntBuffer, readDecay, 4 - readDecay);

                            if (readLen < 0) {
                                // Socket broke
                                mLoopRun = false;
                                notifySocketError();
                                break;
                            }

                            // The length
                            if (readLen < 4 && readDecay + readLen < 4) {
                                readDecay += readLen;
                            } else {
                                readDecay = 0;
                                int msgSize = byteToInt(mIntBuffer);
                                if (!processInputStream(msgSize)) {
                                    mLoopRun = false;
                                    notifySocketError();
                                }
                            }
                        } else {
                            Thread.sleep(5);
                        }
                    }
                } catch (IOException e) {
                    Log.w(TAG, "Exception in the socket host loop", e);
                } catch (InterruptedException e) {
                    mLoopRun = false;
                    notifySocketError();
                }
            }
        }
    };

    public AudioHostSocket() {
        super();
    }

    @Override
    protected void finalize() throws Throwable {
        if (mSocket != null) {
            disconnectSocket();
        }

        super.finalize();
    }

    @Override
    protected void initializeSocket(String socketName) throws IOException {
        mSocket = new LocalServerSocket(socketName);
        mName = socketName;
        Log.d(TAG, "Server socket " + socketName + " created");

        mLoopRun = true;
        mLoopThread = new Thread(mLoop, socketName);
        mLoopThread.start();
    }

    @Override
    protected void disconnectSocket() {
        if (mSocket != null) {
            mLoopRun = false;
            try {
                mSocket.close();

                // Workaround for Android bug 29939
                // https://code.google.com/p/android/issues/detail?id=29939
                LocalSocket tmp = new LocalSocket();
                try {
                    tmp.connect(new LocalSocketAddress(mName));
                    tmp.close();
                } catch (IOException e) { /* ignore */ }
                ///////////////////////////////////////////////////////////

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

    public void setListener(AudioHostSocketListener listener) {
        mListener = listener;
    }

    private void notifySocketError() {
        if (mListener != null) {
            mListener.onSocketError();
        }
    }

    public interface AudioHostSocketListener {
        public void onSocketError();
    }
}

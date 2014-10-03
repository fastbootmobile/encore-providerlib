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

package org.omnirom.music.providers;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.*;
import android.os.Process;
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
    private String mName;

    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            int readDecay = 0;
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

            while (mLoopRun) {
                try {
                    LocalSocket client = mSocket.accept();

                    // In case we terminated the loop thread, the socket will be null.
                    if (mSocket == null) {
                        mLoopRun = false;
                        return;
                    }

                    mInStream = client.getInputStream();
                    mOutStream = client.getOutputStream();

                    if (mSocket == null || mSocket.getLocalSocketAddress() == null) {
                        mLoopRun = false;
                        return;
                    }

                    Log.d(TAG, "Client connected on socket " + mSocket.getLocalSocketAddress().getName());

                    while (mLoopRun) {
                        if (mInStream.available() >= 4 - readDecay) {
                            int readLen = mInStream.read(mIntBuffer, readDecay, 4 - readDecay);

                            if (readLen < 0) {
                                // Socket broke
                                mLoopRun = false;
                                break;
                            }

                            // The length
                            if (readLen < 4 && readDecay + readLen < 4) {
                                readDecay += readLen;
                            } else {
                                readDecay = 0;
                                int msgSize = byteToInt(mIntBuffer);
                                processInputStream(msgSize);
                            }
                        } else {
                            Thread.sleep(50);
                        }
                    }
                } catch (IOException e) {
                    Log.w(TAG, "Exception in the socket host loop", e);
                } catch (InterruptedException e) {
                    return;
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
}

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

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Guigui on 30/08/2014.
 */
public class AudioClientSocket extends AudioSocket {
    private static final String TAG = "AudioClientSocket";

    private LocalSocket mSocket;
    private OutputStream mOutStream;
    private InputStream mInStream;
    private Thread mLoopThread;
    private boolean mLoopRun;
    private byte[] mIntBuffer = new byte[4];

    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            int readDecay = 0;
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

            while (mLoopRun) {
                try {
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
                        if (!processInputStream(msgSize)) {
                            mLoopRun = false;
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Exception while reading from socket", e);
                    mLoopRun = false;
                }
            }
        }
    };

    public AudioClientSocket() {
        super();
    }

    @Override
    protected void initializeSocket(final String socketName) throws IOException {
        mSocket = new LocalSocket();
        mSocket.connect(new LocalSocketAddress(socketName));

        Log.d(TAG, "Client connected to " + socketName);

        mOutStream = mSocket.getOutputStream();
        mInStream = mSocket.getInputStream();

        mLoopRun = true;
        mLoopThread = new Thread(mLoop);
        mLoopThread.start();
    }

    @Override
    protected void disconnectSocket() {
        if (mSocket != null) {
            mLoopRun = false;
            try {
                mInStream.close();
                mOutStream.close();
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing socket", e);
            }
            mSocket = null;

            try {
                mLoopThread.interrupt();
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

    @Override
    protected void finalize() throws Throwable {
        mSocket.close();
        mOutStream.close();
        super.finalize();
    }
}

/*
 *     This file is part of ArucoAndroidServer.
 *
 *     ArucoAndroidServer is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.eziosoft.arucomqtt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private String message = "";
    private int socketServerPORT = 5000;
    private Socket socket;
    private EventListener eventListener;
    private boolean newMessage = false;
    private boolean isRunning = false;


    public int getServerSocketPort() {
        return socketServerPORT;
    }

    interface EventListener {
        void onEvent(String s);
    }

    Server(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void start() {
        isRunning = true;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }


    public void stop() {
        isRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(socketServerPORT);
                while (isRunning) {
                    socket = serverSocket.accept();
                    eventListener.onEvent("Client connected");
                    new Thread(r).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    InputStream inputStream = socket.getInputStream();

                    while (isRunning) {
                        if (inputStream.available() > 0) {
//                            while (inputStream.available() > 0) {
//                                inputStream.read();
//                            }

                            int b = inputStream.read();
                            if (b == 'g') {
                                synchronized (message) {
                                    if (newMessage) {
                                        printStream.print(message);
                                        newMessage = false;
                                    }
                                }
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }


    public void setMessage(String message) {
        synchronized (this.message) {
            this.message = message;
            newMessage = true;
        }
    }
}

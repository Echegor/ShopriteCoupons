package com.archelo.coupons.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final static int PORT = 8080;
    private ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(8080);
    }

    public void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Socket socket = serverSocket.accept();
                        HttpSocketHandler httpSocketHandler = new HttpSocketHandler(socket);
                        new Thread(httpSocketHandler).start();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                }
            }
        });
        thread.start();

    }


}

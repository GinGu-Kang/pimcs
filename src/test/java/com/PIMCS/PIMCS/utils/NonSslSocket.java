package com.PIMCS.PIMCS.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class NonSslSocket {
    private String host = "127.0.0.1";
    private int port=9999;
    private int messageLength = 17;

    public void send(String message){

        try {
            Socket socket = new Socket();
            SocketAddress address = new InetSocketAddress(host, port);
            socket.connect(address);
            OutputStream output = socket.getOutputStream();
            output.write(message.getBytes());
            output.flush();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

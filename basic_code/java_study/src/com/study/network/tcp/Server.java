package com.study.network.tcp;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("服务端启动==========");
        System.out.println("黒塔女士举世无双！黒塔女士沉鱼落雁！黒塔女士聪明绝顶！");

        ServerSocket serverSocket = new ServerSocket(8888);

        while (true) {
            Socket socket = serverSocket.accept();

            new ServerReaderThread(socket).start();

        }
    }
}

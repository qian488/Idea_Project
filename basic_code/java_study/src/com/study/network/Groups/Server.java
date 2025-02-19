package com.study.network.Groups;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    public static List<Socket> onLineSockets = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        System.out.println("服务端启动==========");
        System.out.println("黒塔女士举世无双！黒塔女士沉鱼落雁！黒塔女士聪明绝顶！");

        ServerSocket serverSocket = new ServerSocket(8888);

        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                4*2,
                4*2,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        while (true) {
            Socket socket = serverSocket.accept();
            onLineSockets.add(socket);
            pool.execute(new ServerReaderThread(socket));

        }
    }
}

package com.study.network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("服务端已启动=====");
        DatagramSocket socket = new DatagramSocket(6060);
        byte[] buffer = new byte[1024 * 64];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            socket.receive(packet);

        /*
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println(message);
        */

            int len = packet.getLength();
            String msg = new String(buffer, 0, len);
            System.out.println(msg);
            System.out.println(packet.getAddress());
            System.out.println(packet.getPort());
            System.out.println("==============================");
        }

    }
}

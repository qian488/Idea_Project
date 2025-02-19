package com.study.network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入：");
            String msg = scanner.nextLine();

            if("exit".equals(msg)||"quit".equals(msg)) {
                socket.close();
                break;
            }

            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getLocalHost(),
                    6060);
            socket.send(packet);
        }

    }
}

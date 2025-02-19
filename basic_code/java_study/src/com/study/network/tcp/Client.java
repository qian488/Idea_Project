package com.study.network.tcp;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1",8888);

        OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);

        Scanner scanner = new Scanner(System.in);
        while(true) {
            String line = scanner.nextLine();

            if("exit".equals(line) || "quit".equals(line)) {
                System.out.println("退出成功");
                dos.close();
                socket.close();
                break;
            }

            dos.writeUTF(line);
            dos.flush();
        }

    }
}

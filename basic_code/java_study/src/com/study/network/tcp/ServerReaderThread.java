package com.study.network.tcp;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.Socket;

public class ServerReaderThread extends Thread {
    private Socket socket;
    public ServerReaderThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try{
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            while(true){
                try{
                    String msg = dis.readUTF();
                    System.out.println(msg);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

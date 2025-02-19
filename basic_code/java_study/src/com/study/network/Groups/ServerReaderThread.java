package com.study.network.Groups;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
                    sendMsgToAll(msg);
                }catch(Exception e){
                    Server.onLineSockets.remove(socket);
                    dis.close();
                    socket.close();
                    break;
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendMsgToAll(String msg) throws Exception {
        for(Socket socket : Server.onLineSockets){
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(msg);
            dos.flush();
        }
    }
}
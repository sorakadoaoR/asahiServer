package com.sorakadoao.asahiServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;

public class Server implements Runnable {
    ServerSocket serverSocket;
    HashSet<Thread> clientSet = new HashSet<>();
    public Server(int port,int backlog)throws Exception{
        serverSocket = new ServerSocket(port,backlog);

    }

    @Override
    public void run() {
        while(true){
            try {
                ConnectionHandler s = (new ConnectionHandler(serverSocket.accept()));
                Thread t = new Thread(s);
                clientSet.add(t);
                t.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.sorakadoao.asahiServer;


import com.sorakadoao.asahiServer.response.ConnectResponse;
import com.sorakadoao.asahiServer.response.TcpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

//communicates with the real server
public class RemoteSocket implements Runnable{
    Socket socket;
    InputStream input;
    OutputStream output;

    int requestId;
    boolean ioErrorOccurred = false;
    ConnectionHandler connectionHandler;
    public RemoteSocket(int requestId,ConnectionHandler client,InetAddress address,int port){
        this.requestId = requestId;
        this.connectionHandler = client;
        try {
            socket = new Socket(address,port);
            input = socket.getInputStream();
            output = socket.getOutputStream();
            ConnectResponse response = new ConnectResponse(requestId,client, (byte) 0x0);
            connectionHandler.addToDataPool(response);
        } catch (IOException e) {
            ConnectResponse response = new ConnectResponse(requestId,client, (byte) 0x3);
            connectionHandler.addToDataPool(response);
            return;
        }
    }

    @Override
    public void run() {
        try {
            while(true){
                byte[] data = input.readNBytes(4096);
                if(data.length==0) {
                    socket.close();
                    return;
                }
                TcpResponse tcpResponse = new TcpResponse(requestId,connectionHandler,data);
                connectionHandler.addToDataPool(tcpResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToRemoteServer(byte[] data){  //send data to real server
        try {
            output.write(data);
        } catch (IOException e) {
            ioErrorOccurred = true;
            e.printStackTrace();
        }
    }
}

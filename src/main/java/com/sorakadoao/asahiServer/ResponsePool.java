package com.sorakadoao.asahiServer;

import com.sorakadoao.asahiServer.response.Response;

import java.util.ArrayList;

import java.util.Random;

//Class that stores data that are to be sent to the client
//ConnectResponse data have top priority and are always sent first
//data are sent block by block.
//before sent, a length of rubbish will be added to the end of data and a data head will be added before the data.
//data blocks are then encrypted ,added rubbish and sent to the client.
public class ResponsePool implements Runnable {
    private ArrayList<Response> responseList = new ArrayList<>();
    private ConnectionHandler connectionHandler;
    private int timeTillNextSend = 0;
    Random random = new Random();
    public ResponsePool(ConnectionHandler connectionHandler){
        this.connectionHandler = connectionHandler;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        while(true){
            //TODO detailed packet sending rules
            for(Response response:responseList){
                connectionHandler.sendData(response.buildEncryptedPacket());
            }

            try {
                Thread.sleep(random.nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void queueResponse(Response response){
        responseList.add(response);
    }
}

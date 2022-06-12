package com.sorakadoao.asahiServer;

import com.sorakadoao.asahiServer.response.PendingResponse;
import com.sorakadoao.asahiServer.response.Response;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class ResponsePool implements Runnable {
    private HashSet<PendingResponse> responseList = new HashSet<>();
    private ConnectionHandler connectionHandler;
    private int timeTillNextSend = 0;
    Random random = new Random();
    /**Class that stores data that are to be sent to the client
     * data are sent block by block.
     * before sent, a length of rubbish will be added to the end of data and a data head will be added before the data.
     * data blocks are then encrypted ,added rubbish and sent to the client.
     */
    public ResponsePool(ConnectionHandler connectionHandler){
        this.connectionHandler = connectionHandler;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        while(true){
            //TODO detailed packet sending rules
            synchronized (responseList){
                Iterator<PendingResponse> it = responseList.iterator();
                while(it.hasNext()){
                    PendingResponse pr = it.next();
                    if(pr.send()) it.remove();
                }
            }

            try {
                Thread.sleep(random.nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将响应加入到发送池，设为同步以防止多个请求同时到达
     * @param response
     */
    public void queueResponse(Response response){
        synchronized (responseList){
            responseList.add(new PendingResponse(response));
        }

    }
    public int getNextResponseLength(Response response){
        return 4096;
    }

}

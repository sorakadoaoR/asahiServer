package com.sorakadoao.asahiServer.response;

import com.sorakadoao.asahiServer.ConnectionHandler;

public abstract class Response {
    public abstract byte getPacketType();
    public ResponseInfo responseInfo;
    public int requestId;
    public abstract byte[] build();
    private static int packetIdMost = 0;
    protected Response(int clientConnectionHandlerId, ConnectionHandler connectionHandler){
        requestId = getNextResponseId();
        responseInfo = new ResponseInfo(clientConnectionHandlerId,this,connectionHandler);
    }


    private static int getNextResponseId(){
        packetIdMost++;
        return  packetIdMost;
    }

}

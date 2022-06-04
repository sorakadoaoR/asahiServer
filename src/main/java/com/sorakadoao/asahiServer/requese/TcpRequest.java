package com.sorakadoao.asahiServer.requese;

import com.sorakadoao.asahiServer.RemoteSocket;

public class TcpRequest extends Request{
    public byte[] requestData;
    public RemoteSocket remoteSocket;
    public TcpRequest(RequestInfo requestInfo,byte[] input){
        super(requestInfo);
        //?:data
        remoteSocket = requestInfo.connectionHandler.remoteSockets.get(requestInfo.requestId);
        requestData = input;

    }
    public void resolve() {
        remoteSocket.sendDataToRemoteServer(requestData);
    }
}

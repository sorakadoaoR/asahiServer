package com.sorakadoao.asahiServer.request;

import com.sorakadoao.asahiServer.RemoteSocket;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.zz.gmhelper.SM3Util;

public class TcpRequest extends Request{
    public byte[] requestData;
    public RemoteSocket remoteSocket;
    public TcpRequest(RequestInfo requestInfo,byte[] input){
        super(requestInfo);
        //?:data
        remoteSocket = requestInfo.connectionHandler.remoteSockets.get(requestInfo.clientConnectionId);
        requestData = input;
    }
    public void resolve() {
        remoteSocket.sendDataToRemoteServer(requestData);
    }
}

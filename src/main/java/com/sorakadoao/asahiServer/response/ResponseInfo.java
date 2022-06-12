package com.sorakadoao.asahiServer.response;

import com.sorakadoao.asahiServer.ConnectionHandler;
import com.sorakadoao.asahiServer.MemoryStream;
import com.sorakadoao.asahiServer.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ResponseInfo {
    public byte requestType;
    public int requestId;
    public int clientConnectionHandlerId;
    //TODO change this
    ConnectionHandler connectionHandler;

    public ResponseInfo(int clientConnectionHandlerId,Response response, ConnectionHandler connectionHandler){
        this.connectionHandler = connectionHandler;
        this.requestId = response.requestId;
        this.clientConnectionHandlerId = clientConnectionHandlerId;
        this.requestType = response.getPacketType();
    }

    public byte[] buildResponseHeader(byte[] encryptedData,int rubbishLength,boolean isDataEnded){
        //
        //31-byte response header
        //4:requestId 4:EncryptedDataLength 1:requestType 1:isDataEnded 2:rubbishLength 4:connectionId 15:RSV
        //
        MemoryStream memoryStream = new MemoryStream(32);
        memoryStream.writeInt(requestId);
        memoryStream.writeInt(encryptedData.length);
        memoryStream.write(requestType);
        memoryStream.write(isDataEnded?1:0);
        try {
            Utils.write2ByteInt(memoryStream,rubbishLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        memoryStream.writeInt(clientConnectionHandlerId);

        return memoryStream.toByteArray();
    }
}

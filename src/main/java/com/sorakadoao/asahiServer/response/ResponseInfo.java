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
    //TODO change this
    public boolean isDataEnded = true;
    ConnectionHandler connectionHandler;

    public ResponseInfo(int requestId,Response response, ConnectionHandler connectionHandler){
        this.connectionHandler = connectionHandler;
        this.requestId = requestId;
        this.requestType = response.getPacketType();
    }

    public byte[] buildResponseHeader(byte[] encryptedData,int rubbishLength){
        //
        //16-byte response header
        //4:requestId 4:EncryptedDataLength 1:requestType 1:isDataEnded 2:rubbishLength 4:RSV
        //
        MemoryStream memoryStream = new MemoryStream(16);
        memoryStream.writeInt(requestId);
        memoryStream.writeInt(encryptedData.length);
        memoryStream.write(requestType);
        memoryStream.write(isDataEnded?1:0);
        try {
            Utils.write2ByteInt(memoryStream,rubbishLength);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return memoryStream.toByteArray();
    }
}

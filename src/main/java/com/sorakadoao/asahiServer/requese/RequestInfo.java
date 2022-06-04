package com.sorakadoao.asahiServer.requese;

import com.sorakadoao.asahiServer.ConnectionHandler;
import com.sorakadoao.asahiServer.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RequestInfo {
    public boolean isDataEnded ;
    public byte requestType;
    public int encryptedDataLength;
    public int rubbishLength;
    public int requestId;
    ConnectionHandler connectionHandler;

    public RequestInfo(byte[] decryptedHeader, ConnectionHandler connectionHandler) throws IOException, TimeoutException {
        this.connectionHandler = connectionHandler;
        //
        //read the 16-byte request header
        //4:requestId 4:EncryptedDataLength 1:requestType 1:isDataCompleted 2:rubbishLength 4:RSV
        //
        ByteArrayInputStream headerStream = new ByteArrayInputStream(decryptedHeader);
        requestId = Utils.readInt(headerStream);
        encryptedDataLength = Utils.readInt(headerStream);
        requestType = (byte) headerStream.read();
        isDataEnded = headerStream.read()==1;
        rubbishLength = Utils.read2byte(headerStream);
    }
}

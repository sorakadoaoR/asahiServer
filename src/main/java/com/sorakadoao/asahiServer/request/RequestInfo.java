package com.sorakadoao.asahiServer.request;

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
    //客户端的connectionId
    public int clientConnectionId;
    ConnectionHandler connectionHandler;

    /**
     * read the 31-byte request header
     * @param decryptedHeader 4:requestId 4:EncryptedDataLength 1:requestType 1:isDataCompleted 2:rubbishLength 4:connectionId 15:RSV
     */
    public RequestInfo(byte[] decryptedHeader, ConnectionHandler connectionHandler) throws IOException, TimeoutException {
        this.connectionHandler = connectionHandler;

        ByteArrayInputStream headerStream = new ByteArrayInputStream(decryptedHeader);
        requestId = Utils.readInt(headerStream);
        encryptedDataLength = Utils.readInt(headerStream);
        requestType = (byte) headerStream.read();
        isDataEnded = headerStream.read()==1;
        rubbishLength = Utils.read2byte(headerStream);
        clientConnectionId = Utils.readInt(headerStream);
    }
}

package com.sorakadoao.asahiServer.response;

import com.sorakadoao.asahiServer.ConnectionHandler;
import com.sorakadoao.asahiServer.MemoryStream;

public class TcpResponse extends Response {
    byte[] data;
    public TcpResponse(int requestId, ConnectionHandler connectionHandler,byte[] data) {
        super(requestId,connectionHandler);
        this.data = data;
    }

    public byte getPacketType(){
        return 0x2;
    }
    public byte[] build(){
        MemoryStream memoryStream = new MemoryStream();
        memoryStream.writeInt(responseInfo.requestId);
        memoryStream.write(getPacketType());
        memoryStream.writeBytes(data);
        return memoryStream.toByteArray();
    }

}

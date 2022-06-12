package com.sorakadoao.asahiServer.response;

import com.sorakadoao.asahiServer.ConnectionHandler;
import com.sorakadoao.asahiServer.MemoryStream;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.zz.gmhelper.SM3Util;

public class TcpResponse extends Response {
    byte[] data;
    public TcpResponse(int clientConnectionHandlerId, ConnectionHandler connectionHandler,byte[] data) {
        super(clientConnectionHandlerId,connectionHandler);
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

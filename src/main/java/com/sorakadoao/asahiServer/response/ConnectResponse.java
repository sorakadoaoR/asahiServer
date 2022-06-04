package com.sorakadoao.asahiServer.response;

import com.sorakadoao.asahiServer.ConnectionHandler;
import com.sorakadoao.asahiServer.MemoryStream;

public class ConnectResponse extends Response{
    //status: X'00' succeeded
    //        X'01' general SOCKS server failure
    //        X'02' connection not allowed by ruleset
    //        X'03' Network unreachable
    //        X'04' Host unreachable
    //        X'05' Connection refused
    //        X'06' TTL expired
    //        X'07' Command not supported
    //        X'08' Address type not supported
    public byte status;
    @Override
    public byte getPacketType() {
        return 0x1;
    }
    //decrypted connectResponsePacket:
    //4:requestId 4:clientConnectionId 1:packetType,0x1 1:status
    @Override
    public byte[] build() {
        return new byte[]{status};
    }

    public ConnectResponse(int requestId, ConnectionHandler connectionHandler, byte status){
        super(requestId, connectionHandler);
        this.status = status;
    }
}

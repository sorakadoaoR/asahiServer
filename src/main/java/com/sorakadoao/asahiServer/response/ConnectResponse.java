package com.sorakadoao.asahiServer.response;

import com.sorakadoao.asahiServer.ConnectionHandler;
import com.sorakadoao.asahiServer.MemoryStream;
import com.sorakadoao.asahiServer.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

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
    public InetAddress address;
    public int port;
    @Override
    public byte getPacketType() {
        return 0x1;
    }
    //decrypted connectResponsePacket:
    //1:status
    @Override
    public byte[] build() {
        ByteArrayOutputStream a = new ByteArrayOutputStream(300);
        a.write(status);
        a.write(0);
        if(address ==null){
            a.write(0);
        }else if (address instanceof Inet4Address){
            try {
                a.write(1);
                a.write(address.getAddress());
                Utils.write2ByteInt(a,port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (address instanceof Inet6Address){
            try {
                a.write(4);
                a.write(address.getAddress());
                Utils.write2ByteInt(a,port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return a.toByteArray();
    }

    public ConnectResponse(int clientConnectionHandlerId, ConnectionHandler connectionHandler, byte status, InetAddress address,int port){
        super(clientConnectionHandlerId, connectionHandler);
        this.address = address;
        this.status = status;
    }
}

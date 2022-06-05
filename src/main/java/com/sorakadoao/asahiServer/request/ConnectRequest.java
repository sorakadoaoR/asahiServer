package com.sorakadoao.asahiServer.request;

import com.sorakadoao.asahiServer.RemoteSocket;
import com.sorakadoao.asahiServer.response.ConnectResponse;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectRequest extends Request implements Runnable{
    public byte[] address;
    public byte addressType;
    public int port;
    public int requestId;
    public ConnectRequest(RequestInfo requestInfo,ByteArrayInputStream input){
        super(requestInfo);
        this.requestId = requestInfo.requestId;
        //1:addressType ?:address 2:port
        try {
            addressType = (byte) input.read();
            switch (addressType) {
                case 1 ->//ipv4
                        address = input.readNBytes(4);
                case 3 -> {//domainName
                    int length = input.read();
                    address = input.readNBytes(length);
                }
                case 4 -> {//ipv6
                    address = new byte[16];
                    input.readNBytes(16);
                }
            }
            port = (input.read()<<8) + input.read();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void run(){
        InetAddress remoteAddress;
        try {
            remoteAddress = switch (addressType) {//ipv4
                case 1, 4 ->//ipv6
                        InetAddress.getByAddress(address);
                case 3 ->//domainName
                        InetAddress.getByName(new String(address));
                default -> throw new UnknownHostException();
            };
            RemoteSocket remoteSocket = new RemoteSocket(requestInfo.requestId,requestInfo.connectionHandler,remoteAddress,port);
            requestInfo.connectionHandler.remoteSockets.put(requestId,remoteSocket);
            remoteSocket.run();
        } catch (UnknownHostException e) {
            ConnectResponse response = new ConnectResponse(requestInfo.requestId,requestInfo.connectionHandler, (byte) 0x4);
            requestInfo.connectionHandler.addToDataPool(response);
        }
    }

    public void resolve() {
        Thread crt = new Thread(this);
        crt.start();
    }
}

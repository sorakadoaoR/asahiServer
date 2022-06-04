package com.sorakadoao.asahiServer.requese;


import java.util.LinkedList;


//this class stores requests that have not been completely transferred to the server.
public class IncompleteRequest extends Request{
    LinkedList<byte[]> incompleteDecryptedData = new LinkedList<>();
    public IncompleteRequest(RequestInfo requestInfo,byte[] data) {
        super(requestInfo);
        incompleteDecryptedData.add(data);
    }
    public void append(byte[] data){
        incompleteDecryptedData.add(data);
    }
    public byte[] flush(){
        int length = 0;
        for(byte[] bytes:incompleteDecryptedData){
            length = bytes.length;
        }
        byte[] ans = new byte[length];
        int nowIndex = 0;
        for(byte[] bytes:incompleteDecryptedData){
            System.arraycopy(bytes,0,ans,nowIndex,bytes.length);
            nowIndex++;
        }
        return ans;
    }

    public void resolve() {
        //Nothing to do since it's incomplete
    }
}

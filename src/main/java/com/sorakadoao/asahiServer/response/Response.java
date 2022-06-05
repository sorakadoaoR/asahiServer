package com.sorakadoao.asahiServer.response;

import com.sorakadoao.asahiServer.ConnectionHandler;
import org.zz.gmhelper.SM4Util;

public abstract class Response {
    public abstract byte getPacketType();
    public ResponseInfo responseInfo;
    public abstract byte[] build();
    protected Response(int requestId, ConnectionHandler connectionHandler){
        responseInfo = new ResponseInfo(requestId,this,connectionHandler);
    }
    public byte[] buildEncryptedPacket(){
        byte[] ans = null;
        try {
            byte[] encryptedContent = SM4Util.encrypt_ECB_Padding(responseInfo.connectionHandler.sm4key, build());
            byte[] rubbish = responseInfo.connectionHandler.generateRubbish(encryptedContent.length + 16);
            byte[] encryptedHeader = SM4Util.encrypt_ECB_Padding(responseInfo.connectionHandler.sm4key, responseInfo.buildResponseHeader(encryptedContent, rubbish.length));
            ans = new byte[16+ rubbish.length+ encryptedContent.length];
            System.arraycopy(encryptedHeader,0,ans,0,16);
            System.arraycopy(encryptedContent,0,ans,16,encryptedContent.length);
            System.arraycopy(rubbish,0,ans,16+encryptedContent.length,rubbish.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ans;
    }

}

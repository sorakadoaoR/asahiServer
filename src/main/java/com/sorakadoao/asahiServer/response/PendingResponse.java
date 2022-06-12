package com.sorakadoao.asahiServer.response;

import com.sorakadoao.asahiServer.ConnectionHandler;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.zz.gmhelper.SM3Util;
import org.zz.gmhelper.SM4Util;

import java.io.ByteArrayInputStream;


public class PendingResponse {
    public Response response;
    private ByteArrayInputStream responseStream;
    private ConnectionHandler ch;

    /**
     * 已加入响应池的响应数据包
     */
    public PendingResponse(Response response){
        this.response = response;
        responseStream = new ByteArrayInputStream(response.build());
        ch = response.responseInfo.connectionHandler;
    }

    /**
     * 构建最终发送的数据包
     * @return 包含头部和尾部垃圾数据的加密包
     */
    private byte[] buildPartialPacket(){

        byte[] ans = null;
        try {
            //TODO
            byte[] rubbish = ch.generateRubbish(114514);
            int length = ch.responsePool.getNextResponseLength(response);
            byte[] rawContent = responseStream.readNBytes((length- rubbish.length)&0x7fffff00 );
            byte[] encryptedContent = SM4Util.encrypt_ECB_Padding(ch.sm4key, rawContent);
            //System.out.println(response.responseInfo.requestId +" (Partial)Down: " +  ByteUtils.toHexString(SM3Util.hash(rawContent)));
            byte[] encryptedHeader = SM4Util.encrypt_ECB_Padding(ch.sm4key, response.responseInfo.buildResponseHeader(encryptedContent, rubbish.length, responseStream.available()==0));
            ans = new byte[32+ rubbish.length+ encryptedContent.length];
            System.arraycopy(encryptedHeader,0,ans,0,32);
            System.arraycopy(encryptedContent,0,ans,32,encryptedContent.length);
            System.arraycopy(rubbish,0,ans,32+encryptedContent.length,rubbish.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ans;
    }

    /**
     * 将下一份数据包发送到客户端
     * @return 标志着包是否已经发完
     */
    public boolean send(){
        byte[] a = buildPartialPacket();
        System.out.println(response.requestId +" Down: " + a.length +" "+ ByteUtils.toHexString(SM3Util.hash(a)));
        ch.sendData(a);
        return responseStream.available()==0;
    }
}


package com.sorakadoao.asahiServer.request;

import com.sorakadoao.asahiServer.response.Response;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.zz.gmhelper.SM3Util;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public abstract class Request {
    public RequestInfo requestInfo;
    private static HashMap<Integer,IncompleteRequest> incompleteRequestMap = new HashMap<>();
    Request(RequestInfo requestInfo){
        this.requestInfo = requestInfo;
    }

    /**method to resolve the request
     * called after request is instantiated
     */
    public abstract void resolve();

    /**
     * 根据请求头分析请求数据，并返回一个请求（可能不完整）
     * @param requestInfo 请求头
     * @param decryptedData 已经解密的请求数据
     * @return 一个可能不完整的请求
     */
    public static Request analyzer(RequestInfo requestInfo,byte[] decryptedData){
        //merge coming request with existing incomplete request
        IncompleteRequest incompleteRequest = incompleteRequestMap.get(requestInfo.requestId);
        if(!requestInfo.isDataEnded){
            System.out.println(requestInfo.requestId +"  (Partial)UP: "  + decryptedData.length+" "+  ByteUtils.toHexString(SM3Util.hash(decryptedData)));
            if(incompleteRequest ==null) {
                incompleteRequest = new IncompleteRequest(requestInfo, decryptedData);
                incompleteRequestMap.put(requestInfo.requestId, incompleteRequest);
            }else{
                incompleteRequest.append(decryptedData);
            }
            return incompleteRequest;
        }else if(incompleteRequest != null){
            incompleteRequest.append(decryptedData);
            decryptedData = incompleteRequest.flush();
            incompleteRequestMap.remove(requestInfo.requestId);
            System.out.println(requestInfo.requestId +"  (Complete)UP: " + decryptedData.length+" "+ ByteUtils.toHexString(SM3Util.hash(decryptedData)));
        }else{
            System.out.println(requestInfo.requestId +"  (Once)UP: "  + decryptedData.length+" "+  ByteUtils.toHexString(SM3Util.hash(decryptedData)));
        }


        Request request = switch (requestInfo.requestType) {
            case 1 -> new ConnectRequest(requestInfo, new ByteArrayInputStream(decryptedData));
            case 2 -> new TcpRequest(requestInfo, decryptedData);
            default -> null;
        };
        return request;
    }


}

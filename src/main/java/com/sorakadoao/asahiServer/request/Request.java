package com.sorakadoao.asahiServer.request;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public abstract class Request {
    public RequestInfo requestInfo;
    private static HashMap<Integer,IncompleteRequest> incompleteRequestMap = new HashMap<>();
    Request(RequestInfo requestInfo){
        this.requestInfo = requestInfo;
    }

    //method to resolve the request
    public abstract void resolve();

    public static Request analyzer(RequestInfo requestInfo,byte[] decryptedData){
        //merge coming request with existing incomplete request
        IncompleteRequest incompleteRequest = incompleteRequestMap.get(requestInfo.requestId);
        if(!requestInfo.isDataEnded){

            if(incompleteRequest ==null) {
                incompleteRequestMap.put(requestInfo.requestId, new IncompleteRequest(requestInfo, decryptedData));
            }else{
                incompleteRequest.append(decryptedData);
            }
            return incompleteRequest;
        }else if(incompleteRequest != null){
            incompleteRequest.append(decryptedData);
            decryptedData = incompleteRequest.flush();
            incompleteRequestMap.remove(requestInfo.requestId);
        }


        Request request = switch (requestInfo.requestType) {
            case 1 -> new ConnectRequest(requestInfo, new ByteArrayInputStream(decryptedData));
            case 2 -> new TcpRequest(requestInfo, decryptedData);
            default -> null;
        };
        return request;
    }


}

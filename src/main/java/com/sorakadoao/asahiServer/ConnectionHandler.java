package com.sorakadoao.asahiServer;

import com.sorakadoao.asahiServer.config.UserInfo;
import com.sorakadoao.asahiServer.request.Request;
import com.sorakadoao.asahiServer.request.RequestInfo;
import com.sorakadoao.asahiServer.response.Response;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.zz.gmhelper.SM2Util;
import org.zz.gmhelper.SM4Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeoutException;

//One connection means one client connection, while a request is sent by the client.
public class ConnectionHandler implements Runnable{
    Socket client;
    public InputStream input;
    public OutputStream output;
    UserInfo user;
    public byte[] sm4key;
    long lastSeen;
    Random random;
    MemoryStream memoryStream = new MemoryStream(1024);
    private ResponsePool responsePool;
    public HashMap<Integer,RemoteSocket> remoteSockets = new HashMap<>(); //key: requestId
    public ConnectionHandler(Socket client){
        random = new Random();
        this.client = client;
        System.out.println("New connection from "+client.getRemoteSocketAddress());
        responsePool = new ResponsePool(this);
    }
    @Override
    public void run() {
        try {

            input = client.getInputStream();
            output = client.getOutputStream();

            lastSeen = System.currentTimeMillis();
            if(!authenticate() || !sendSM4Key()){
                closeConnection();
                return;
            }
            listen();

        }catch(TimeoutException e){
            e.printStackTrace();
            System.out.println("Connection time out");
            closeConnection();
            return;
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Connection Closed");
            closeConnection();
            return;
        }
    }

    private boolean authenticate() throws TimeoutException,IOException{
        //Authenticate:
        //
        //Authenticate Msg Packet
        // 16:uuid | 4: authenticate Msg length |?: authenticate Msg
        //
        //
        byte[] uuid = new byte[16];
        boolean isPacketEnded = false;
        int byteCount = 0;
        Utils.readByteFromInput(input,uuid, 16);
        String uuidStr = Utils.byteArrayToUUID(uuid);
        user = Main.userInfoHashMap.get(uuidStr);
        if (user == null) {
            System.out.println("User Not Found");
            closeConnection();
            return false;
        }
        int msgLength = Utils.readInt(input);
        byte[] authenticateMsg = new byte[msgLength];
        ByteArrayInputStream decryptedStream;
        Utils.readByteFromInput(input,authenticateMsg, msgLength);
        try {
            decryptedStream= new ByteArrayInputStream(SM2Util.decrypt(Main.serverPrivateKey, authenticateMsg));
        } catch (InvalidCipherTextException e) {
            System.out.println("Authentication failed : Failed to decrypt authenticate message");
            return false;
        }
        byte[] passwordBytes = new byte[Utils.readInt(decryptedStream)];
        byte[] userNameBytes = new byte[Utils.readInt(decryptedStream)];
        Utils.readByteFromInput(decryptedStream,passwordBytes,passwordBytes.length);
        String password = new String(passwordBytes);
        Utils.readByteFromInput(decryptedStream,userNameBytes,userNameBytes.length);
        String userName = new String(userNameBytes);
        if (!user.password.equals(password) || !user.name.equals(userName)) {
            System.out.println("Authentication failed : Incorrect username or password");
            return false;
        }
        //Authenticate complete!
        System.out.println("Authentication succeeded : ".concat(user.name));

        return true;
    }

    private boolean sendSM4Key(){
        //generate & send SM4 key

        try {
            sm4key = SM4Util.generateKey();
            memoryStream.write(sm4key);
            int rubbishLength = random.nextInt()&0xff;
            memoryStream.write(rubbishLength);
            byte[] encryptedSM4Key = SM2Util.encrypt(user.publicKey, memoryStream.getAndReset());
            System.out.println(encryptedSM4Key.length);
            memoryStream.write(encryptedSM4Key);
            memoryStream.write(Utils.generateRandomBytes(rubbishLength,random));
            output.write(memoryStream.getAndReset());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void listen(){
        //start communication, listen to client information
        try{
            while(true){
                //read 16 bytes requestHeader
                byte[] encryptedHeader = input.readNBytes(16);
                if(encryptedHeader.length!=16){
                    System.out.println("Client Stream Closed.");
                    return;
                }
                byte[] decryptedHeader = SM4Util.decrypt_ECB_Padding(sm4key,encryptedHeader);
                RequestInfo requestInfo = new RequestInfo(decryptedHeader,this);

                byte[] decryptedData = SM4Util.decrypt_ECB_Padding(sm4key,input.readNBytes(requestInfo.encryptedDataLength));
                input.skipNBytes(requestInfo.rubbishLength);
                Request request = Request.analyzer(requestInfo,decryptedData);
                request.resolve();
            }
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
    }

    public void sendData(byte[] data){
        try {
            output.write(data);
        } catch (IOException e) {
            closeConnection();
            return;
        }
    }
    public void closeConnection(){
        try {
            client.close();
            Thread.currentThread().stop();
        } catch (IOException ex) {
        }
    }


    public synchronized void addToDataPool(Response data){
        this.responsePool.queueResponse(data);
    }

    public byte[] generateRubbish(int encryptedDataLength){
        //TODO a batter rubbish generator should be made
        return  Utils.generateRandomBytes(random.nextInt(10,200),random);
    }

}

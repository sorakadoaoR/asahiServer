package com.sorakadoao.asahisocks;

import com.sorakadoao.asahisocks.config.UserInfo;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.zz.gmhelper.SM2Util;
import org.zz.gmhelper.SM4Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

public class ConnectionHandler implements Runnable{
    Socket client;
    InputStream input;
    OutputStream output;
    UserInfo user;
    byte[] sm4key;
    long lastSeen;
    public ConnectionHandler(Socket client){
        this.client = client;
        System.out.println("New connection from "+client.getRemoteSocketAddress());

    }
    @Override
    public void run() {
        try {
            try {
                input = client.getInputStream();
                output = client.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                closeConnection();
                return;
            }
            lastSeen = System.currentTimeMillis();
            if(!authenticate() || !sendSM4Key()){
                closeConnection();
                return;
            }
            //start communication

        }catch(TimeoutException e){
            e.printStackTrace();
            System.out.println("Connection time out");
            closeConnection();
            return;
        }
    }

    public boolean authenticate() throws TimeoutException{
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

    public boolean sendSM4Key(){
        //generate & send SM4 key
        try {
            sm4key = SM4Util.generateKey();
            byte[] encryptedSM4Key = SM2Util.encrypt(user.publicKey, sm4key);
            output.write(encryptedSM4Key);
            System.out.println("SM4 key : ".concat(ByteUtils.toHexString(sm4key)));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void closeConnection(){
        try {
            client.close();
            Thread.currentThread().stop();
        } catch (IOException ex) {
        }
    }

}

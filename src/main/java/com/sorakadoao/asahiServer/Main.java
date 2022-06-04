package com.sorakadoao.asahiServer;

import com.sorakadoao.asahiServer.config.Config;
import com.sorakadoao.asahiServer.config.UserInfo;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;

import java.util.HashMap;


public class Main {
    public static HashMap<String, UserInfo> userInfoHashMap;
    public static BCECPrivateKey serverPrivateKey;

    public static int port;
    public static void main(String[] args) throws Exception {

        Config.loadServerConfig();
        userInfoHashMap = Config.loadUser();
        Thread remoteServerThread = new Thread(new Server(port,100));
        remoteServerThread.start();

        //KeyPair kp = SM2Util.generateKeyPair();
        //System.out.println(ByteUtils.toHexString(kp.getPrivate().getEncoded()));
        //System.out.println(ByteUtils.toHexString(kp.getPublic().getEncoded()));

        //EncryptUtils.saveKey(kp.getPublic(),"public.txt");
        //EncryptUtils.saveKey(kp.getPrivate(),"private.txt");
        //BCECPublicKey pubk = EncryptUtils.loadPublicKey("public.txt");
        //BCECPrivateKey prik = EncryptUtils.loadPrivateKey("private.txt");
        //System.out.println(ByteUtils.toHexString(pubk.getEncoded()));
        //System.out.println(ByteUtils.toHexString(prik.getEncoded()));
        //byte[] a = SM4Util.generateKey();
        //byte[] b = SM2Util.encrypt(BCECUtil.convertPublicKeyToParameters((BCECPublicKey) kp.getPublic()),a);
        //System.out.println(b.length);
        //System.out.println(ByteUtils.toHexString(b));
        //String text = "NMSL2333";
        //byte[] test = SM2Util.encrypt(pubk,text.getBytes());
        //System.out.println(test.length);
        //byte[] dec = SM2Util.decrypt(prik,test);
        //System.out.println(new String(dec));

    }


}

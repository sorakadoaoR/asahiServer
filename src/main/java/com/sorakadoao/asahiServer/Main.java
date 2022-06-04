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

    }


}

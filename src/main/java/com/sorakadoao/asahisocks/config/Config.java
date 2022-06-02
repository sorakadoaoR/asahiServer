package com.sorakadoao.asahisocks.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sorakadoao.asahisocks.Main;
import com.sorakadoao.asahisocks.config.JsonClass.JsonServerConfig;
import com.sorakadoao.asahisocks.config.JsonClass.JsonUserInfo;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.zz.gmhelper.BCECUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Config {
    public static HashMap<String,UserInfo> loadUser(){
        JsonUserInfo[] jsonUserInfos;
        HashMap<String,UserInfo> userInfos = new HashMap<>();
        Gson gson = new Gson();
        try {
            JsonReader a = gson.newJsonReader(new FileReader("userdb.json"));
            jsonUserInfos = gson.fromJson(a,JsonUserInfo[].class);
            for(JsonUserInfo jui:jsonUserInfos){
                userInfos.put(jui.uuid,new UserInfo(jui));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfos;
    }
    public static void loadServerConfig(){
        JsonServerConfig jsonServerConfig;
        Gson gson = new Gson();
        try {
            JsonReader a = gson.newJsonReader(new FileReader("server-config.json"));
            jsonServerConfig = gson.fromJson(a,JsonServerConfig.class);
            Main.port = jsonServerConfig.port;
            Main.serverPrivateKey = BCECUtil.convertPKCS8ToECPrivateKey(ByteUtils.fromHexString(jsonServerConfig.server_sm2_private_key));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.sorakadoao.asahisocks.config;

import com.sorakadoao.asahisocks.Utils;
import com.sorakadoao.asahisocks.config.JsonClass.JsonUserInfo;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.zz.gmhelper.BCECUtil;


public class UserInfo {
    public String name;
    public String password;
    public byte[] uuid;
    public BCECPublicKey publicKey;
    public UserInfo(String uuid, String name, String password,String sm2_public_key){
        this.uuid = Utils.uuidToByteArray(uuid);
        this.name = name;
        this.password = password;
        try {
            this.publicKey = BCECUtil.convertX509ToECPublicKey(ByteUtils.fromHexString(sm2_public_key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public UserInfo(JsonUserInfo jsonUserInfo){
        this(jsonUserInfo.uuid,jsonUserInfo.name,jsonUserInfo.password,jsonUserInfo.sm2_public_key);
    }
}

package com.sorakadoao.asahisocks;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.zz.gmhelper.BCECUtil;
import org.zz.gmhelper.SM2Util;
import org.zz.gmhelper.cert.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

public class Utils {

    public static void saveKey(Key key,String fileName)
    {
        File file = new File(fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(key.getEncoded());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BCECPublicKey loadPublicKey(String fileName)
    {
        File file = new File(fileName);
        BCECPublicKey key = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] content = fileInputStream.readAllBytes();
            key = BCECUtil.convertX509ToECPublicKey(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public static BCECPrivateKey loadPrivateKey(String fileName)
    {
        File file = new File(fileName);
        BCECPrivateKey key = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] content = fileInputStream.readAllBytes();
            key = BCECUtil.convertPKCS8ToECPrivateKey(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public static byte[] uuidToByteArray(String str){
        return ByteUtils.fromHexString(str.replace("-","").toLowerCase(Locale.ROOT));
    }

    public static String byteArrayToUUID(byte[] uuidByte){
        char s[] = ByteUtils.toHexString(uuidByte).toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<32;i++){
            if(i==8 || i==12 ||i==16 || i==20) stringBuilder.append('-');
            stringBuilder.append(s[i]);
        }
        return stringBuilder.toString();
    }

    public static void readByteFromInput(InputStream inputStream, byte[] bytes,int length) throws TimeoutException {
        try{
            int byteCount = 0;
            while(true){
                int nowByte = 0;
                nowByte = inputStream.read();
                bytes[byteCount] = (byte)nowByte;
                byteCount++;
                if(byteCount>length-1) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static int readInt(InputStream inputStream) throws TimeoutException{
        int ans = 0;
        byte[] bs = new byte[4];
        readByteFromInput(inputStream,bs,4);
        for(byte b:bs){
            ans<<=8;
            ans+=b;
        }
        return ans;
    }
    public void writeInt(OutputStream outputStream,int i) {
        try {
            outputStream.write(i>>24);
            outputStream.write((i<<8)>>24);
            outputStream.write((i<<16)>>24);
            outputStream.write((i<<24)>>24);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.sorakadoao.asahiServer;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.zz.gmhelper.BCECUtil;

import java.io.*;
import java.security.*;
import java.util.Locale;
import java.util.Random;
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

    public static void readByteFromInput(InputStream inputStream, byte[] bytes,int length) throws TimeoutException,IOException{
        int byteCount = 0;
        while(true){
            int nowByte = 0;
            nowByte = inputStream.read();
            bytes[byteCount] = (byte)nowByte;
            byteCount++;
            if(byteCount>length-1) break;
        }
    }



    public static int readInt(InputStream inputStream) throws IOException{
        byte[] bs = inputStream.readNBytes(4);
        return ((bs[0]&0xff)<<24)+((bs[1]&0xff)<<16)+((bs[2]&0xff)<<8)+(bs[3]&0xff);
    }

    public static int read2byte(InputStream inputStream) throws TimeoutException,IOException{
        return (inputStream.read()<<8) + inputStream.read();
    }

    public static void write2ByteInt(OutputStream outputStream, int i)throws IOException{
        outputStream.write((i&0xff00)>>8);
        outputStream.write(i&0xff);
    }

    public static int convertByteToInt(byte[] b,int i){
        return (b[i]<<24)+(b[i+1]<<8)+(b[i+2]<<16)+(b[i+3]);
    }

    public static void writeInt(OutputStream outputStream, int i) {
        try {
            outputStream.write(i>>24);
            outputStream.write((i&0xff0000)>>16);
            outputStream.write((i&0xff00)>>8);
            outputStream.write(i&0xff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] generateRandomBytes(int count, Random random){
        byte[] ans = new byte[count];
        random.nextBytes(ans);
        return ans;
    }
}

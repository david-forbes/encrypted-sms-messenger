package com.example.smsmessaging;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class EncryptionHelper {


    public static PublicKey GetPublicKey() {

        try {
            ContextWrapper contextWrapper = new ContextWrapper(MyApplication.getAppContext());
            File directory = contextWrapper.getDir(MyApplication.getAppContext().getFilesDir().getName(), Context.MODE_PRIVATE);

            File publicKeyFile = new File(directory, "public.key");

            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);


            return publicKey;


        } catch (Exception e) {

            return null;
        }

    }

    public static PublicKey PublicKeyFromString(String publicKeyString) {

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        try {
            KeyFactory keyFactoryTwo = KeyFactory.getInstance("RSA");

            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            PublicKey publicKey = keyFactoryTwo.generatePublic(publicKeySpec);
            return publicKey;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] encrypt(PublicKey publicKey, byte[] message){
        try {
            byte[] messageBytes = message;
            Log.d("GenKeypair", "encrypt: "+messageBytes);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(message);


        return encryptedBytes;
    }
    catch(Exception e){ return null;}

    }
    public static byte[] decrypt(PrivateKey privateKey, byte[] message){
        try{

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedBytes = cipher.doFinal(message);

        return decryptedBytes;
    }catch(Exception e){
            Log.d("EncryptionHelper.decrypt()", "decrypt:"+e);
            return null;
        }
    }
    public static PrivateKey GetPrivateKey(){

        try {

            File directory = MyApplication.getAppContext().getDir(MyApplication.getAppContext().getFilesDir().getName(), Context.MODE_PRIVATE);





            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            File privateKeyFile = new File(directory,"private.key");
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);


            return privateKey;


        } catch (Exception e) {

            return null;
        }

    }
    }





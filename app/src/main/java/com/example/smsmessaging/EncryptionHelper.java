package com.example.smsmessaging;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.ContextWrapper;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class EncryptionHelper {

    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        dateInMilliseconds = dateInMilliseconds.replaceAll("[^\\d.]", "");
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String SanitizePhoneNumber(String phoneNumber){
        phoneNumber = phoneNumber.replaceAll("[^\\d.]", "");
        if(phoneNumber.substring(0,3).equals("353")){
            phoneNumber = phoneNumber.substring(3);
        }
        return phoneNumber;
    }
    public static void GenKeypair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();


            File directory = MyApplication.getAppContext().getDir(MyApplication.getAppContext().getFilesDir().getName(), Context.MODE_PRIVATE);

            File file = new File(directory, "public.key");
            FileOutputStream fosPublic = new FileOutputStream(file, false);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            fosPublic.write(publicKey.getEncoded());
            fosPublic.close();

            File file2 = new File(directory, "private.key");
            FileOutputStream fosPrivate = new FileOutputStream(file2, false);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            fosPrivate.write(privateKey.getEncoded());
            fosPrivate.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


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

    public static PublicKey GetKey(String file) {

        try {
            ContextWrapper contextWrapper = new ContextWrapper(MyApplication.getAppContext());
            File directory = contextWrapper.getDir(MyApplication.getAppContext().getFilesDir().getName(), Context.MODE_PRIVATE);

            File publicKeyFile = new File(directory, file);

            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());


            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);


            return publicKey;


        } catch (Exception e) {

            return null;
        }

    }


    public static String GetBase64PublicKey() {
        PublicKey publicKey = GetPublicKey();

        String publicKeyBase64 = GetBase64FromPublicKey(publicKey);


        return publicKeyBase64;


    }

    public static String GetBase64FromPublicKey(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();

        String publicKeyBase64 = new String(Base64.getEncoder().encode(publicKeyBytes), StandardCharsets.UTF_8);


        return publicKeyBase64;
    }

    public static PublicKey GetPublicKeyFromBase64(String publicKeyBase64) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64.getBytes(StandardCharsets.UTF_8));
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

    public static byte[] encrypt(PublicKey publicKey, byte[] message) {
        try {
            byte[] messageBytes = message;
            Log.d("GenKeypair", "encrypt: " + messageBytes);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(message);


            return encryptedBytes;
        } catch (Exception e) {
            return null;
        }

    }

    public static String EncryptFromStringToBase64(PublicKey publicKey, String message) {
        byte[] encryptedMessage = EncryptFromString(publicKey, message);
        Log.d(TAG, "EncryptFromStringToBase64: " + new String(encryptedMessage, StandardCharsets.UTF_8));

        byte[] base64EncryptedMessage = Base64.getEncoder().encode(encryptedMessage);
        return new String(base64EncryptedMessage, StandardCharsets.UTF_8);

    }

    public static String DecryptFromBase64ToString(PrivateKey privateKey, String message) {

        try {
            byte[] messageBytes = Base64.getDecoder().decode(message.getBytes(StandardCharsets.UTF_8));
            String decryptedMessage = DecryptToString(privateKey, messageBytes);
            return decryptedMessage;


        } catch (Exception e) {

            return null;
        }

    }

    public static byte[] EncryptFromString(PublicKey publicKey, String message) {
        byte[] encrypted = encrypt(publicKey, message.getBytes(StandardCharsets.UTF_8));
        Log.d(TAG, "EncryptFromString: " + encrypted);
        return encrypted;
    }

    public static byte[] decrypt(PrivateKey privateKey, byte[] message) {
        try {

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = cipher.doFinal(message);

            return decryptedBytes;
        } catch (Exception e) {
            Log.d("EncryptionHelper.decrypt()", "decrypt:" + e);
            return null;
        }
    }

    public static String DecryptToString(PrivateKey privateKey, byte[] message) {
        byte[] bytes = decrypt(privateKey, message);
        String decryptedString = new String(bytes, StandardCharsets.UTF_8);
        return decryptedString;
    }

    public static PrivateKey GetPrivateKey() {

        try {

            ContextWrapper contextWrapper = new ContextWrapper(MyApplication.getAppContext());
            File directory = contextWrapper.getDir(MyApplication.getAppContext().getFilesDir().getName(), Context.MODE_PRIVATE);


            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            File privateKeyFile = new File(directory, "private.key");
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);


            return privateKey;


        } catch (Exception e) {

            return null;
        }

    }
}





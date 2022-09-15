package com.example.smsmessaging;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import android.app.Activity;
import android.util.Log;

public class EncryptionUnitTest {


    @Test

    public void encrypt_decrypt() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            String string = "hello";
            byte[] encryptedBytes = EncryptionHelper.EncryptFromString(publicKey, string);
            String unencryptedString = EncryptionHelper.DecryptToString(privateKey, encryptedBytes);
            assertEquals(string, unencryptedString);
        } catch (Exception e) {
            return;
        }
    }

    @Test

    public void encrypt_decrypt_base64() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            String string = "hello";
            String encryptedString = EncryptionHelper.EncryptFromStringToBase64(publicKey, string);
            String unencryptedString = EncryptionHelper.DecryptFromBase64ToString(privateKey, encryptedString);
            assertEquals(string, unencryptedString);
        } catch (Exception e) {
            return;
        }
    }

    @Test

    public void encrypt_decrypt_public_key() {
        String TAG = "encrypt_decrypt_public_key";
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            PublicKey publicKey = pair.getPublic();

            String publicKeyBase64 = EncryptionHelper.GetBase64FromPublicKey(publicKey);
            Log.d(TAG, "***"+publicKeyBase64);
            PublicKey publicKeyUnencrypted = EncryptionHelper.GetPublicKeyFromBase64(publicKeyBase64);
            assertEquals(publicKey, publicKeyUnencrypted);
        } catch (Exception e) {
            return;
        }
    }
    @Test
    public void phone_number_sanitize() {
        String phoneNumber = "353-382934-384723";
        assertEquals("382934384723",EncryptionHelper.SanitizePhoneNumber(phoneNumber));
    }



}


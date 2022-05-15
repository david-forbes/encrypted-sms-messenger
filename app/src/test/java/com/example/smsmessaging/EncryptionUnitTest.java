package com.example.smsmessaging;

import java.security.PrivateKey;
import java.security.PublicKey;
import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

public class EncryptionUnitTest {
    @Test

    public void encrypt_decrypt(){
        PublicKey publicKey = EncryptionHelper.GetPublicKey();
        PrivateKey privateKey = EncryptionHelper.GetPrivateKey();

        assertNotNull(publicKey);
        assertNotNull(privateKey);

    }
}

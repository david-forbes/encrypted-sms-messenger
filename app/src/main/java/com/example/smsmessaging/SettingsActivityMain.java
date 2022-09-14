package com.example.smsmessaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.smsmessaging.R;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class SettingsActivityMain extends AppCompatActivity {
    String phoneString;

    public TextView textView;
    public TextView textView2;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    String TAG = "SettingsActivityMain";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar4);
        Intent intent = getIntent();
        //phoneString=intent.getStringExtra("phoneString");


        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();


        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        textView = findViewById(R.id.textViewGenPub);
        textView2 = findViewById(R.id.textViewGenPriv);


    }

    public void GenKeypairView(View v) {
        GenKeypair();
        textView.setText(publicKey.toString());
        textView2.setText(privateKey.toString());
    }

    public void GenKeypair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();


            byte[] encrypted = EncryptionHelper.EncryptFromString(publicKey, "hello");

            Log.d(TAG, "GenKeypair: " + encrypted);
            String decryptedString = EncryptionHelper.decryptToString(privateKey, encrypted);

            Log.d(TAG, "GenKeypair: " + decryptedString);

            String secretMessage = "Baeldung secret message";

            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);



            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);


            byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
            String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
            Log.d(TAG, "GenKeypair: "+decryptedMessage);

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

    public void GetKeypairView(View v) {

        publicKey = EncryptionHelper.GetPublicKey();
        privateKey = EncryptionHelper.GetPrivateKey();

        byte[] encrypted = EncryptionHelper.EncryptFromString(publicKey, "hello");


        String decryptedString = EncryptionHelper.decryptToString(privateKey, encrypted);

        Log.d(TAG, "GetKeypair: " + decryptedString);


        Log.d(TAG, "GetKeypairView: " + privateKey);
        textView.setText(publicKey.toString());
        textView2.setText(privateKey.toString());
    }

}
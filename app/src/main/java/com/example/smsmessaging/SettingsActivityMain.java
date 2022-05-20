package com.example.smsmessaging;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.smsmessaging.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.CryptoPrimitive;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SettingsActivityMain extends AppCompatActivity {
    String phoneString;

    public TextView textView;
    public TextView textView2;
    private  PrivateKey privateKey;
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

public void GenKeypairView(View v){GenKeypair();
    textView.setText(publicKey.toString());
    textView2.setText(privateKey.toString());}

public void GenKeypair(){
        try{
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();

            byte[] message = "hello".getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = EncryptionHelper.encrypt(publicKey,message);
            Log.d(TAG, "GenKeypair: "+encrypted);
            byte[] decrypted = EncryptionHelper.decrypt(privateKey,encrypted);
            Log.d(TAG, "GenKeypair: "+decrypted);


            File directory = MyApplication.getAppContext().getDir(MyApplication.getAppContext().getFilesDir().getName(), Context.MODE_PRIVATE);

            File file =  new File(directory,"public.key");
            FileOutputStream fosPublic = new FileOutputStream(file, false);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            fosPublic.write(publicKey.getEncoded());
            fosPublic.close();

            File file2 =  new File(directory,"private.key");
            FileOutputStream fosPrivate = new FileOutputStream(file2, false);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            fosPrivate.write(privateKey.getEncoded());
            fosPrivate.close();

    } catch (Exception e) {
        e.printStackTrace();

    }
}
public void GetKeypairView(View v){
        publicKey = EncryptionHelper.GetPublicKey();
        privateKey =EncryptionHelper.GetPrivateKey();
    Log.d(TAG, "GetKeypairView: "+privateKey);
    textView.setText(publicKey.toString());
    textView2.setText(privateKey.toString());
    }

}
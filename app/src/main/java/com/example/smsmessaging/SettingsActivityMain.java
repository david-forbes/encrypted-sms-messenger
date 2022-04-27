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
    private PrivateKey privateKey;
    private PublicKey publicKey;

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
    generator.initialize(2048);
    KeyPair pair = generator.generateKeyPair();
    privateKey = pair.getPrivate();
    publicKey = pair.getPublic();
            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File directory = contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);
            File file =  new File(directory,"public.key");
            File file2 =  new File(directory,"private.key");





        FileOutputStream fos = new FileOutputStream(file, false);
        fos.write(publicKey.getEncoded());


        FileOutputStream fospriv = new FileOutputStream(file2, false);
            fospriv.write(privateKey.getEncoded());
        fos.close();
        fospriv.close();
            Toast.makeText(getBaseContext(), "fidn", Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
        e.printStackTrace();

    }
}
public void GetKeypairView(View v){
        GetKeypair();
    textView.setText(publicKey.toString());
    textView2.setText(privateKey.toString());}
public void GetKeypair(){
        try {
            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File directory = contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);

            File publicKeyFile = new File(directory,"public.key");
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

            File privateKeyFile = new File(directory,"private.key");
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());



            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            publicKey = keyFactory.generatePublic(publicKeySpec);

            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            privateKey = keyFactory.generatePrivate(privateKeySpec);





            Toast.makeText(getBaseContext(), "fn", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.d("noneed",e.toString());
        }

}

public void GetPublicKey(){
        String d = "";
        try {
            ContextWrapper contextWrapper = new ContextWrapper(MyApplication.getAppContext());
            File directory = contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);

            File publicKeyFile = new File(directory, "public.key");
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        }catch (Exception e){ Log.d("noneed",e.toString());}

    }
}
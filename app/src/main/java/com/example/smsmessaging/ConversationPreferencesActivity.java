package com.example.smsmessaging;


import static com.example.smsmessaging.EncryptionHelper.GetPublicKeyFromBase64;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.smsmessaging.R;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class ConversationPreferencesActivity extends AppCompatActivity {
    public String phoneString;
    public String address;
    public String message;
    public PublicKey publicKey;
    public PrivateKey privateKey;
    public BroadcastReceiver broadcastReceiver;
    public String SENT;
    public static Queue<String> messages;
    public String TAG = ConversationPreferencesActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_settings);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar3);
        Intent intent = getIntent();
        phoneString = intent.getStringExtra("phoneString");


        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        setTitle("+" + phoneString + " Settings");
        messages = new LinkedList<>();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                intent.removeCategory(SENT);
                intent.removeExtra(SENT);
                String messagecur = messages.remove();
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();


                        ContentResolver contentResolver = getBaseContext().getContentResolver();
                        ContentValues values = new ContentValues();

                        values.put("body", messagecur);
                        values.put("address", address.replaceAll("[^\\d.]", ""));



                        String currentTime = Long.toString(java.lang.System.currentTimeMillis());
                        values.put("date", currentTime);

                        Uri uriSMSURI = Uri.parse("content://sms/sent");
                        contentResolver.insert(uriSMSURI, values);


                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.d(TAG, messagecur);
                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }

            }


        };
        SENT = "SMS_SENT_SETTINGS";
        registerReceiver(broadcastReceiver, new IntentFilter(SENT));

    }

    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);

        } catch (IllegalArgumentException e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {

            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {

        }
    }


    @Override
    protected void onResume() {


        super.onResume();
        try {
            registerReceiver(broadcastReceiver, new IntentFilter(SENT));

        } catch (IllegalArgumentException e) {
        }

    }


    public void smsSendPublic(View view) {

        //get public key from file

        String publicKeyString = EncryptionHelper.GetBase64PublicKey();



        //construct public key object from public key

        //RSAPublicKey publicKeyRsa = (RSAPublicKey) publicKey;

        //Send rsa public key over sms(base64 encoded)
        Log.d(TAG, "smsSendPublic: "+publicKeyString);
        Log.d(TAG, "smsSendPublic: "+GetPublicKeyFromBase64(publicKeyString));

        SmsSplit(phoneString, "***" + publicKeyString);


    }

    public void SmsSplit(String address, String message) {
        while (!message.isEmpty()) {
            if (message.length() > 150) {
                messages.add(message.substring(0, 150));
                SendSms(address, message.substring(0, 150));
                Log.d(TAG, "SmsSplit: " + message.substring(0, 150));

                message = message.substring(150);
            } else {
                messages.add(message);
                SendSms(address, message);
                Log.d(TAG, "SmsSplit: " + message);

                message = "";
            }

        }

    }

    public void SendSms(String smsAddress, String smsMessage) {
        address = smsAddress;
        message = smsMessage;
        try {

            Thread.sleep(500);
        } catch (Exception e) {
        }

        String scAddress = null;

        SENT = "SMS_SENT_SETTINGS";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(address.replaceAll("[^\\d.]", ""), null, message,
                sentPI, null);


    }

    public void GetPublicKey() {
        String d = "";
        try {
            ContextWrapper contextWrapper = new ContextWrapper(MyApplication.getAppContext());
            File directory = contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);

            File publicKeyFile = new File(directory, "public.key");
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            publicKey = keyFactory.generatePublic(publicKeySpec);

        } catch (Exception e) {
            Log.d("noneed", e.toString());
        }

    }
}



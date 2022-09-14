/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.smsmessaging;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.android.smsmessaging.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class MySmsReceiver extends BroadcastReceiver {
    private static final String TAG = MySmsReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";

    public List<String> getMessageText() {
        return messageText;
    }

    public void setMessageText(List<String> messageText) {
        this.messageText = messageText;
    }

    public String CHANNEL_ID;
    public List<String> messageText = null;
    public List<String> phoneNumber;

    public void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent received.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    /*
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get;
            ContentResolver contentResolver = context.getContentResolver();
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";

                /*creating the content values to pass to inbox
                ContentValues values = new ContentValues();
                values.put("address",address);
                values.put("body",smsBody);

                Uri uriSMSURI = Uri.parse("content://sms/inbox");
                //contentResolver.insert(uriSMSURI, values);

                /*push to inbo
                context.getContentResolver().insert(uriSMSURI,values);

            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
        }
    }}
*/
    public void onReceive(Context context, Intent intent) {


/*
        ContentValues values = new ContentValues();
        values.put("address", number);
        values.put("body", message);
        values.put("read", readState);
        values.put("date", dateTime);
        mActivity.getContentResolver().insert(Uri.parse("content://sms/sent"), values);*/

        ContentResolver contentResolver = context.getContentResolver();


        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        Hashtable<String, List<SmsMessage>> hashMap = new Hashtable<>();


        String strMessage = "";
        String format = bundle.getString("format");

        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {

            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {

                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                Log.d(TAG, msgs[i].getOriginatingAddress());
                if (hashMap.containsKey(msgs[i].getOriginatingAddress().toString())) {
                    hashMap.get(msgs[i].getOriginatingAddress()).add(msgs[i]);
                    Log.d(TAG, "success");
                } else {
                    List<SmsMessage> smsList;
                    smsList = new ArrayList<SmsMessage>();
                    smsList.add(msgs[i]);
                    hashMap.put(msgs[i].getOriginatingAddress().toString(), smsList);

                }

            }
            Log.d(TAG, "onReceive: " + hashMap);

            Object[] keys = hashMap.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {


                String message = "";
                for (int j = 0; j < hashMap.get(keys[i].toString()).size(); j++) {
                    message = message + hashMap.get(keys[i].toString()).get(j).getMessageBody();
                    Log.d(TAG, "onReceive: " + message);
                }
                Toast.makeText(context.getApplicationContext(), message.substring(0, 3), Toast.LENGTH_SHORT).show();
                if (message.substring(0, 3).equals("***")) {
                    SharedPreferences sharedPref1 = MyApplication.getAppContext().getSharedPreferences(
                            "public_key", Context.MODE_PRIVATE);
                    sharedPref1.edit().putString(Base64.getDecoder().decode(keys[i].toString().replaceAll("[^\\d.]", "")).toString(), message.substring(3)).apply();


                    Toast.makeText(MyApplication.getAppContext(), "new encryption key added", Toast.LENGTH_SHORT).show();
                } else if (message.substring(0, 2).equals("**")) {

                }


                ContentValues values = new ContentValues();


                values.put("address", (keys[i].toString().replaceAll("[^\\d.]", "")));
                values.put("body", message);

                String currentTime = Long.toString(java.lang.System.currentTimeMillis());


                //SimpleDateFormat sdf = new SimpleDateFormat("YY/MM/ddHH:mm:ss");
                //String currentTime = sdf.format(new Date());

                values.put("date", currentTime);


                Uri uriSMSURI = Uri.parse("content://sms/inbox");


                context.getContentResolver().insert(uriSMSURI, values);

                Intent intent3 = new Intent(context, ConversationActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent3.putExtra("phoneNumber", keys[i].toString().replaceAll("[^\\d.]", ""));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent3, 0);

                CHANNEL_ID = "1234597";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(keys[i].toString().replaceAll("[^\\d.]", ""))
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


                createNotificationChannel(context);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                int notificationId = 2134;
// notificationId is a unique int for each notification that you must define
                notificationManager.notify(notificationId, builder.build());


                // Log and display the SMS message.
                Log.d(TAG, "onReceive: " + strMessage);
                //Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent();
                intent2.setAction("newsms");
                intent2.putExtra("phoneNumber", keys[i].toString().replaceAll("[^\\d.]", ""));
                intent2.putExtra("message", message);
                intent2.putExtra("date", currentTime);
                context.sendBroadcast(intent2);
            }


        }

    }
}



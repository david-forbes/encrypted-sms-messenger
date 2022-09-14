package com.example.smsmessaging;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony.Mms;
import android.util.Log;
import android.widget.Toast;


/**
 * MmsSystemEventReceiver receives the
 * and performs a series of operations which may include:
 * <ul>
 * <li>Show/hide the icon in notification area which is used to indicate
 * whether there is new incoming message.</li>
 * <li>Resend the MM's in the outbox.</li>
 * </ul>
 */
public class MyMmsReceiver extends BroadcastReceiver {
    private static final String TAG = MyMmsReceiver.class.getSimpleName();
    private static ConnectivityManager mConnMgr = null;


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(MyApplication.getAppContext(), "MMS Received", Toast.LENGTH_SHORT).show();
            /*
            "transactionId" - (Integer) The WAP transaction ID
            "pduType" - (Integer) The WAP PDU type
            "header" - (byte[]) The header of the message
            "data" - (byte[]) The data payload of the message
            */

        try {
            Bundle bundle = intent.getExtras();
            Integer transactionId = bundle.getInt("transactionId");
            Integer pduType = bundle.getInt("pduType");
            byte[] header = bundle.getByteArray("header");
            byte[] data = bundle.getByteArray("data");

            Log.d("TAG", "---> MMS | transactionId: " + transactionId.toString());
            Log.d("TAG", "---> MMS | pduType: " + pduType.toString());
            Log.d("TAG", "---> MMS | header: " + header.toString());
            Log.d("TAG", "---> MMS | data: " + data.toString());

        } catch (Exception e) {
            Log.d(TAG, "---> ERROR EXTRACTING MMS: " + e.getLocalizedMessage());
        }
    }
}
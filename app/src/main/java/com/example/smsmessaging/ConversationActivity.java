package com.example.smsmessaging;

import static com.example.smsmessaging.EncryptionHelper.convertDate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.SwitchCompat;

import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.example.android.smsmessaging.R;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ConversationActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    public int MY_PERMISSIONS_REQUEST_SEND_SMS;
    int color;
    public List<Sms> textMessageList;
    public BroadcastReceiver broadcastReceiver;

    public ArrayList<RecyclerData> getRecyclerDataArrayList() {
        return recyclerDataArrayList;
    }

    public String phoneString;
    public String TAG = "SecondActivity.java";
    public Boolean permission;
    public String newTextFromNumber;
    static int teal;
    public static Queue<String> messages;
    public String smsMessage;
    public String destinationAddress;
    public String SENT;
    public SwitchCompat togSwitch;
    public BroadcastReceiver myReceiver;
    public RecyclerViewAdapter adapter;
    public String rec_data;
    public EditText smsEditText;
    public static final String BROADCAST = "PACKAGE_NAME.android.action.broadcast";

    public void setRecyclerDataArrayList(ArrayList<RecyclerData> recyclerDataArrayList) {
        this.recyclerDataArrayList = recyclerDataArrayList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar2);
        setSupportActionBar(myToolbar);

        //togSwitch = findViewById(R.layout);
        // inflate the layout


// load the text view


        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        smsEditText = (EditText) findViewById(R.id.editTextMessage);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        MY_PERMISSIONS_REQUEST_SEND_SMS = 39847023;
        if (intent.hasExtra("phoneNumber")) {
            phoneString = intent.getStringExtra("phoneNumber").replaceAll("[^\\d.]", "");
        } else {

            phoneString = sharedPref.getString("phoneString", "Fail");
        }
        String string = sharedPref.getString(phoneString + "DRAFT", null);
        if (string != null) {
            smsEditText.setText(string.toString());
        }
        setTitle(phoneString);
        //phoneString="6505551212";
        recyclerView = findViewById(R.id.idCourseRV);
        recyclerDataArrayList = new ArrayList<>();





        messages = new LinkedList<>();
        final String myPackageName = getPackageName();
        final String defaultSms = Telephony.Sms.getDefaultSmsPackage(this);
        if ((defaultSms == null) || !(defaultSms.equals(myPackageName))) {
            //Toast.makeText(this, "working", Toast.LENGTH_SHORT);
            Intent intent2 = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent2.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
            startActivityForResult(intent2, 1);
        } else {
            recyclerDataArrayList = (ArrayList<RecyclerData>) createList();

        }


        adapter = new RecyclerViewAdapter(recyclerDataArrayList, this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        color = ContextCompat.getColor(this, R.color.teal);
        // at last set adapter to recycler view.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerViewAdapter.ClickListener() {

            @Override
            public void onItemClick(int position, View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", recyclerDataArrayList.get(position).getTitle());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getBaseContext(), "Message copied to clipboard", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onItemLongClick(int position, View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ConversationActivity.this);
                builder.setMessage("Delete Message?");
                builder.setCancelable(true);


                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                adapter.notifyItemRemoved(position);
                                Uri uriSMSURI = Uri.parse("content://sms/");
                                String[] projection = null;
                                String selectionClause = "date = ? AND body = ?";
                                String sortOrder = null;
                                String[] selectionArgs = {recyclerDataArrayList.get(position).getDate(), recyclerDataArrayList.get(position).getTitle()};
                                ContentResolver contentResolver = getBaseContext().getContentResolver();
                                getBaseContext().getContentResolver().delete(uriSMSURI, selectionClause, selectionArgs);
                                recyclerDataArrayList.remove(position);

                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder.create();
                alert11.show();

            }
        });

        //BroadcastReceiver myReceiver;
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String messagePhoneNumber = intent.getStringExtra("phoneNumber").replaceAll("[^\\d.]", "");
                String messageDate = intent.getStringExtra("date");
                Log.d(TAG, messagePhoneNumber + " " + phoneString);

                if (messagePhoneNumber.toString().equals(phoneString.toString().replaceAll("[^\\d.]", ""))) {
                    //Toast.makeText(context, "new message",Toast.LENGTH_SHORT);
                    String message = intent.getStringExtra("message");
                    recyclerDataArrayList.add(recyclerDataArrayList.size(), new RecyclerData(message, 1, convertDate(messageDate,"hh:mm"), 0));
                    adapter.notifyItemInserted(recyclerDataArrayList.size());
                    recyclerView.scrollToPosition(recyclerDataArrayList.size() - 1);
                }
            }
        };


        IntentFilter intentFilter = new IntentFilter("newsms");
        registerReceiver(myReceiver, intentFilter);

        //Toast.makeText(this,phoneString, Toast.LENGTH_SHORT).show();
        recyclerView.scrollToPosition(recyclerDataArrayList.size() - 1);


        smsEditText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString(phoneString + "DRAFT", smsEditText.getText().toString());
                editor.apply();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                        Log.d(TAG, smsMessage + destinationAddress);
                        Log.d(TAG, "onReceive: " + getResultCode());
                        values.put("body", messagecur);
                        values.put("address", destinationAddress.replaceAll("[^\\d.]", ""));




                        String currentTime = Long.toString(java.lang.System.currentTimeMillis());
                        values.put("date", currentTime);

                        Uri uriSMSURI = Uri.parse("content://sms/sent");
                        //contentResolver.insert(uriSMSURI, values);

                        recyclerDataArrayList.add(recyclerDataArrayList.size(), new RecyclerData(messagecur, 0, convertDate(currentTime,"hh:mm"), 0));
                        contentResolver.insert(uriSMSURI, values);
                        adapter.notifyItemInserted(recyclerDataArrayList.size() - 1);
                        recyclerView.scrollToPosition(recyclerDataArrayList.size() - 1);

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
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
        SENT = "SMS_SENT";
        registerReceiver(broadcastReceiver, new IntentFilter(SENT));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tog, menu);
        View item = menu.findItem(R.id.mySwitch).getActionView();
        togSwitch = item.findViewById(R.id.switch1);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                goSetting();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            /*case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

             */

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void goSetting() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("phoneString", phoneString);
        editor.apply();


        Intent intent4 = new Intent(getBaseContext(), ConversationPreferencesActivity.class);
        intent4.putExtra("phoneString", phoneString);
        startActivity(intent4);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(myReceiver);
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {

        }
    }


    @Override
    protected void onResume() {


        super.onResume();
        try {
            registerReceiver(broadcastReceiver, new IntentFilter(SENT));
            registerReceiver(myReceiver, new IntentFilter("newsms"));
        } catch (IllegalArgumentException e) {
        }
    }


    public List<RecyclerData> createList() {
        textMessageList = getAllSms();
        List<RecyclerData> newTextList = new ArrayList<>();
        if (textMessageList.size() > 0) {
            for (int i = 0; i < textMessageList.size(); i++) {
                if (textMessageList.get(i).getFolderName() == "sent") {
                    color = 0;//ContextCompat.getColor(this, R.color.white);
                } else {
                    color = 1;//ContextCompat.getColor(this, R.color.teal);
                }
                recyclerDataArrayList.add(new RecyclerData(textMessageList.get(i).getMsg(), color, convertDate(textMessageList.get(i).getTime(),"hh:mm"), 0));
                newTextList.add(0, new RecyclerData(textMessageList.get(i).getMsg(), color, convertDate(textMessageList.get(i).getTime(),"hh:mm"), 0));
            }
        }
        //return recyclerDataArrayList;
        return recyclerDataArrayList;


    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (permissions.length > 0 && grantResults.length > 0) {
                if (grantResults[0] == (PackageManager.PERMISSION_GRANTED)) {
                    permission = true;
                    //Toast.makeText(this, "approved",
                    //      Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private Boolean checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            return (true);
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.


        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);

            // Permission already granted. Enable the SMS button.
        }
        if (permission) return (true);
        return false;
    }

    public void smsSendMessage(View view) {
        destinationAddress = phoneString;
                /*
                SharedPreferences sharedPref1 =getBaseContext().getSharedPreferences(
                        "com.example.smsmessaging", Context.MODE_PRIVATE);
                sharedPref1.edit().putString(phoneString+"PUBLICKEY","test123").apply();
*/


        smsMessage = smsEditText.getText().toString();
        smsEditText.setText("");
        if (smsMessage.isEmpty()) {
            Toast.makeText(getBaseContext(), "Empty Message", Toast.LENGTH_SHORT).show();
            return;

        }
        if (togSwitch.isChecked()) {
/*
                    SharedPreferences sharedPref =MyApplication.getAppContext().getSharedPreferences(
                            "com.example.smsmessaging", Context.MODE_PRIVATE);
                    String pubKey = sharedPref.getString(phoneString+"PUBLICKEY","");

 */
            PublicKey phoneNumberPublicKey = EncryptionHelper.GetKey(destinationAddress+".key");
            if(phoneNumberPublicKey==null){
                Toast.makeText(this, "No saved encryption key for this number", Toast.LENGTH_SHORT).show();
                return;

            }

            PublicKey standInPubKey = EncryptionHelper.GetPublicKey();
            String messageString = EncryptionHelper.EncryptFromStringToBase64(standInPubKey, smsMessage);
            PrivateKey privateKey = EncryptionHelper.GetPrivateKey();
            Log.d(TAG, "smsSendMessage: "+EncryptionHelper.DecryptFromBase64ToString(privateKey, messageString));







            SmsSplit(destinationAddress, "**"+messageString);
        } else {
            SmsSplit(destinationAddress, smsMessage);
        }
    }

    public void SmsSplit(String address, String message1) {
        while (!message1.isEmpty()) {
            if (message1.length() > 150) {
                messages.add(message1.substring(0, 150));
                SendSmsSecond(address, message1.substring(0, 150));
                Log.d(TAG, "SmsSplit: " + message1.substring(0, 150));

                message1 = message1.substring(150);
            } else {
                messages.add(message1);
                SendSmsSecond(address, message1);
                Log.d(TAG, "SmsSplit: " + message1);

                message1 = "";
            }

        }

    }


    public void SendSmsSecond(String address, String message) {
        smsMessage = message;
        destinationAddress = address;
        String scAddress = null;
        SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationAddress.replaceAll("[^\\d.]", ""), scAddress, smsMessage,
                sentPI, null);


        /*


         */


    }


    @SuppressLint("Range")
    public List<Sms> getAllSms() {


        //String phoneString = "6505551212";

        String TAG = "SecondActivity";
        List<Sms> lstSms = new ArrayList<Sms>();
        List<String> smsTxt = new ArrayList<String>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(message, null, null, null, "date ASC");
        this.startManagingCursor(c);
        //int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            c.moveToPrevious();

            //for (int i = 0; i < totalSMS; i++) {
            while ((c.moveToNext())) {
                if (c.getString(c.getColumnIndexOrThrow("address")).equals(phoneString)) {

                    Log.d(TAG, "sms found" + phoneString);
                    objSms = new Sms();

                    try {
                        Log.d("heow", c.getString(c.getColumnIndexOrThrow("_id")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                    objSms.setAddress(c.getString(c
                            .getColumnIndexOrThrow("address")));
                    objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                    objSms.setReadState(c.getString(c.getColumnIndex("read")));
                    objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        objSms.setFolderName("inbox");
                    } else {
                        objSms.setFolderName("sent");
                    }
                    //smsTxt.add(c.getString(c.getColumnIndexOrThrow("body")));


                    lstSms.add(objSms);
                }
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();
        for (int i = 0; i < lstSms.size(); i++) {
            Log.d(TAG, lstSms.get(i).getMsg() + "coe370");
        }

        Log.d(TAG, lstSms + "code349");
        //return smsTxt;
        return lstSms;
    }


    public void onButtonTwoClick(View view) {


        Intent replyIntent = new Intent();
        setResult(RESULT_OK, replyIntent);
        finish();

    }


}

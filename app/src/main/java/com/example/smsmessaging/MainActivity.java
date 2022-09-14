package com.example.smsmessaging;

import android.app.role.RoleManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.smsmessaging.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public int color;
    public List<String> phoneNumbers;
    public String phoneNumber;
    public BroadcastReceiver myReceiver;
    private RecyclerView recyclerView;
    public ArrayList<RecyclerDataMain> recyclerDataArrayList;

    public ArrayList<String> messages;
    public ArrayList<String> date;
    public ArrayList<String> time;
    public Thread thread;

    public SearchView searchView;
    public String messageTime;
    public int requestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_new);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Log.d(TAG, "onCreate: help");
        Intent intent = getIntent();
        searchView = findViewById(R.id.editTextSendMessage);
        searchView.setVisibility(View.GONE);
        searchView.setIconifiedByDefault(false);
        recyclerView = findViewById(R.id.recyclerViewList);
        recyclerDataArrayList = new ArrayList<>();
        messages = new ArrayList<>();
        date = new ArrayList<>();
        time = new ArrayList<>();
        color = (ContextCompat.getColor(this, R.color.white));

        RecyclerViewAdapterMain adapter = new RecyclerViewAdapterMain(recyclerDataArrayList, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        requestCode = 82347;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                phoneNumbers = getNumbers();


                for (int i = 0; i < phoneNumbers.size(); i++) {
                    recyclerDataArrayList.add(new RecyclerDataMain(phoneNumbers.get(i), (ContextCompat.getColor(getBaseContext(), R.color.white)), messages.get(i), date.get(i), null));
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyYourDataSetChanged(recyclerDataArrayList);



                    }
                });
            }
        });

        Log.d(TAG, recyclerDataArrayList.toString());
        if (isDefaultSmsApp(getBaseContext())) {

            thread.start();

        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                RoleManager roleManager = getBaseContext().getSystemService(RoleManager.class);

                boolean isRoleAvailable = roleManager.isRoleAvailable(RoleManager.ROLE_SMS);
                if (isRoleAvailable) {

                    boolean isRoleHeld = roleManager.isRoleHeld(RoleManager.ROLE_SMS);
                    if (!isRoleHeld) {
                        Intent roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS);
                        startActivityForResult(roleRequestIntent, requestCode);
                    }
                }
            } else {
                Intent intent2 = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent2.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getApplicationContext().getPackageName());
                startActivityForResult(intent, requestCode);
            }


        }


        adapter.setOnItemClickListener(new RecyclerViewAdapterMain.ClickListener() {


            @Override
            public void onItemClick(int position, View v) {

                Log.d(TAG, "Button clicked");
                phoneNumber = recyclerDataArrayList.get(position).getTitle();
                Intent intent = new Intent(v.getContext(), ConversationActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(int position, View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Delete Conversation?");
                builder1.setCancelable(true);

                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        adapter.notifyYourItemRemoved(position);
                        Uri uriSMSURI = Uri.parse("content://sms/");

                        String selectionClause = "address = ?";

                        String[] selectionArgs = {recyclerDataArrayList.get(position).getTitle()};

                        getBaseContext().getContentResolver().delete(uriSMSURI, selectionClause, selectionArgs);
                        recyclerDataArrayList.remove(position);
                        dialog.cancel();
                    }
                });

                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                /*


                 */
            }
        });

        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String messagePhoneNumber = intent.getStringExtra("phoneNumber");
                String message = intent.getStringExtra("message");
                String messageTime = intent.getStringExtra("date");



                if (!phoneNumbers.contains(messagePhoneNumber.replaceAll("[^\\d.]", ""))) {
                    phoneNumbers.add(messagePhoneNumber.replaceAll("[^\\d.]", ""));
                    recyclerDataArrayList.add(0, new RecyclerDataMain(messagePhoneNumber.replaceAll("[^\\d.]", ""), color, message, messageTime, null));
                    adapter.notifyYourItemInserted(0);
                    adapter.notifyYourDataSetChanged(recyclerDataArrayList);
                    recyclerView.scrollToPosition(0);


                } else {
                    for (int i = 0; i < recyclerDataArrayList.size(); i++) {
                        if (recyclerDataArrayList.get(i).getTitle().equals(messagePhoneNumber)) {
                            recyclerDataArrayList.remove(i);
                            recyclerDataArrayList.add(0, new RecyclerDataMain(messagePhoneNumber, color, message, messageTime, null));
                            adapter.notifyYourItemInserted(0);
                            adapter.notifyYourItemRemoved(i + 1);
                            adapter.notifyYourDataSetChanged(recyclerDataArrayList);
                            recyclerView.scrollToPosition(0);

                            break;
                        }
                    }
                }


                adapter.updateCopy(recyclerDataArrayList);
            }
        };
        IntentFilter intentFilter = new IntentFilter("newsms");
        registerReceiver(myReceiver, intentFilter);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String openPhoneNumber = searchView.getQuery().toString().replaceAll("[^\\d.]", "");
                if (openPhoneNumber.length() < 7) {
                    Toast.makeText(getBaseContext(), "Phone number too short", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getBaseContext(), ConversationActivity.class);
                    intent.putExtra("phoneNumber", openPhoneNumber);
                    startActivity(intent);
                }



                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.filter(newText);
                return true;
            }
        });

    }

    public boolean isDefaultSmsApp(Context context) {
        return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                goSettingMain();

                return true;
            case R.id.show_search:
                if (searchView.getVisibility() == View.GONE) {
                    searchView.setVisibility(View.VISIBLE);
                } else {
                    searchView.setVisibility(View.GONE);
                }



            default:

                return super.onOptionsItemSelected(item);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isDefaultSmsApp(getBaseContext())) {

            thread.start();

        }
    }

    public void goSettingMain() {


        Intent intent = new Intent(getBaseContext(), com.example.smsmessaging.SettingsActivityMain.class);

        startActivity(intent);
    }

    public ArrayList<RecyclerDataMain> getRecyclerDataArrayList() {
        return recyclerDataArrayList;
    }

    public void setRecyclerDataArrayList(ArrayList<RecyclerDataMain> recyclerDataArrayList) {
        this.recyclerDataArrayList = recyclerDataArrayList;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {

        }
    }


    @Override
    protected void onResume() {


        super.onResume();
        try {
            registerReceiver(myReceiver, new IntentFilter("newsms"));
        } catch (IllegalArgumentException e) {
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bar, menu);
        return true;
    }

    public List<String> getNumbers() {




        String TAG = "ThirdActivity";
        List<String> phoneNumbers = new ArrayList<String>();

        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getBaseContext().getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        this.startManagingCursor(c);


        if (c.moveToFirst()) {
            c.moveToPrevious();


            while ((c.moveToNext())) {
                if (!phoneNumbers.contains(c.getString(c.getColumnIndexOrThrow("address")).replaceAll("[^\\d.]", ""))) {


                    messages.add(c.getString(c.getColumnIndexOrThrow("body")));


                    messageTime = (c.getString(c.getColumnIndexOrThrow("date")));
                    date.add(messageTime);
/*

 */


                    phoneNumbers.add(c.getString(c.getColumnIndexOrThrow("address")).replaceAll("[^\\d.]", ""));
                }
            }
        }

        c.close();

        return phoneNumbers;
    }


    }









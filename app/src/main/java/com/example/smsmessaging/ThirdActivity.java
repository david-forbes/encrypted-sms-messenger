package com.example.smsmessaging;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.smsmessaging.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;



public class ThirdActivity extends AppCompatActivity{
/*    private static final String TAG = ThirdActivity.class.getSimpleName();
        public List<String> phoneNumbers;
        public String phoneNumber;
        private RecyclerView recyclerView;
        private ArrayList<RecyclerData> recyclerDataArrayList;
        public List<Sms> textMessageList;
        public ArrayList<RecyclerData> getRecyclerDataArrayList() {
            return recyclerDataArrayList;
        }

        public void setRecyclerDataArrayList(ArrayList<RecyclerData> recyclerDataArrayList) {
            this.recyclerDataArrayList = recyclerDataArrayList;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_new);
            Log.d(TAG, "onCreate: help");
            Intent intent = getIntent();
            recyclerView = findViewById(R.id.recyclerViewList);
            recyclerDataArrayList = new ArrayList<>();
            phoneNumbers=getNumbers();

            for(int i=0;i< phoneNumbers.size();i++){
                recyclerDataArrayList.add(new RecyclerData(phoneNumbers.get(i),R.drawable.ic_launcher_background));
            }
            recyclerDataArrayList.add(new RecyclerData("hello", R.drawable.ic_launcher_background));

            // created new array list..



            // added data to array list





            // added data from arraylist to adapter class.
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerDataArrayList, this);

            // setting grid layout manager to implement grid view.
            // in this method '2' represents number of columns to be displayed in grid view.
            GridLayoutManager layoutManager = new GridLayoutManager(this, 1);

            // at last set adapter to recycler view.
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);


            adapter.setOnItemClickListener(new RecyclerViewAdapter.ClickListener() {

        @Override
        public void onItemClick(int position,View v){

            Log.d(TAG, "Button clicked");
            phoneNumber=phoneNumbers.get(position);
            Intent intent = new Intent(v.getContext(), com.example.smsmessaging.SecondActivity.class);
            intent.putExtra("phoneNumber",phoneNumber);
            startActivity(intent);

        }

        @Override
        public void onItemLongClick(int position, View v) {


            Snackbar snackbar = Snackbar
                    .make(v, "wwlong", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    });
}
    public List<String> getNumbers() {


        //String phoneString = "6505551212";

        String TAG = "ThirdActivity";
        List<String> phoneNumbers = new ArrayList<String>();

        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        this.startManagingCursor(c);
        //int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            c.moveToPrevious();

            //for (int i = 0; i < totalSMS; i++) {
            while((c.moveToNext())){
                if (!phoneNumbers.contains(c.getString(c.getColumnIndexOrThrow("address")))) {


                    phoneNumbers.add(c.getString(c.getColumnIndexOrThrow("address")));
                }
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();

        return phoneNumbers;
    }
*/
}
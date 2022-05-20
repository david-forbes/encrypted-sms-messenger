package com.example.smsmessaging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.smsmessaging.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewAdapterMain extends RecyclerView.Adapter<RecyclerViewAdapterMain.RecyclerViewHolder> {

    public ArrayList<RecyclerDataMain> courseDataArrayList;
    public ArrayList<RecyclerDataMain> courseDataArrayListCopy;
    private Context mcontext;
    private static ClickListener clickListener;



    public RecyclerViewAdapterMain(ArrayList<RecyclerDataMain> recyclerDataArrayList, Context mcontext) {
        Log.d("once", "RecyclerViewAdapterMain: ");
        this.courseDataArrayList = recyclerDataArrayList;
        this.courseDataArrayListCopy= (ArrayList<RecyclerDataMain>) recyclerDataArrayList.clone();


        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_main, parent, false);

        return new RecyclerViewHolder(view);

    }
    public void notifyYourItemInserted(int i) {
        //... your custom logic
        notifyItemInserted(i);

    }
    public void notifyYourItemRemoved(int i) {
        //... your custom logic
        notifyItemRemoved(i);
    }
    public void notifyYourDataSetChanged(ArrayList<RecyclerDataMain> recyclerDataArrayList) {
        courseDataArrayListCopy = (ArrayList<RecyclerDataMain>) recyclerDataArrayList.clone();
        notifyDataSetChanged();
    }
    public void updateCopy(ArrayList<RecyclerDataMain> recyclerDataArrayList){
        this.courseDataArrayListCopy= (ArrayList<RecyclerDataMain>) recyclerDataArrayList.clone();
    }
    public void filter(String text) {




        courseDataArrayList.clear();
        if(text.equals("")){
            if (!courseDataArrayListCopy.isEmpty()) {
                courseDataArrayList.addAll(courseDataArrayListCopy);
            }
            notifyDataSetChanged();
        } else{
            text = text.toLowerCase();
            for(int i = 0;i<courseDataArrayListCopy.size();i++){
                RecyclerDataMain item = courseDataArrayListCopy.get(i);

                if(item.getTitle().toString().toLowerCase().contains(text) || item.getMessage().toString().toLowerCase().contains(text)){

                    courseDataArrayList.add(item);
                }
            }
        }
        
        //Toast.makeText(mcontext, item.getTitle(),Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
        RecyclerDataMain recyclerDataMain = courseDataArrayList.get(position);
        holder.courseTV.setText(recyclerDataMain.getTitle());
        holder.textView2.setText(recyclerDataMain.getMessage());
        holder.textViewTime.setText(recyclerDataMain.getTime());


    }


    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return courseDataArrayList.size();
    }
    
    // View Holder Class to handle Recycler View.
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView courseTV;
        private CardView cardViewId;
        private TextView textView2;
        private TextView textViewTime;



        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            courseTV = itemView.findViewById(R.id.idTVCourse);
            cardViewId = itemView.findViewById(R.id.cardViewId);
            textView2 = itemView.findViewById(R.id.textViewSecond);
            textViewTime=itemView.findViewById(R.id.textViewTime);
        }

        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);


        }


        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(),v);
            return false;



}}
        public void setOnItemClickListener(ClickListener clickListener) {
            RecyclerViewAdapterMain.clickListener = clickListener;
        }
        public interface ClickListener {
            void onItemClick(int position, View v);
            void onItemLongClick(int position, View v);
        }
    }

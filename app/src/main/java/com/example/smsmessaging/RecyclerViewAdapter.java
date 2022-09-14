package com.example.smsmessaging;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.smsmessaging.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<RecyclerData> courseDataArrayList;
    private Context mcontext;
    private static ClickListener clickListener;


    public RecyclerViewAdapter(ArrayList<RecyclerData> recyclerDataArrayList, Context mcontext) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.cardViewDate.setVisibility(View.GONE);
        holder.cardViewImage.setVisibility(View.GONE);

        int top = 0;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.cardViewId.getLayoutParams();

        // Set the data to textview and imageview.
        RecyclerData recyclerData = courseDataArrayList.get(position);
        //if (position - 1 <= 0 || !recyclerData.getDate().substring(0, 13).equals(courseDataArrayList.get(position - 1).getDate().substring(0, 13))) {

        //String text = recyclerData.getDate().substring(3, 8) + " " + recyclerData.getDate().substring(8, 13);
        String text = recyclerData.getDate();
        holder.textViewDate.setText(text);

        holder.cardViewDate.setVisibility(View.VISIBLE);


        //}

        if (recyclerData.getImage() != 0) {
            ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) holder.cardViewImage.getLayoutParams();

            holder.cardViewImage.setVisibility(View.VISIBLE);

            holder.cardViewId.setVisibility(View.GONE);

            if (recyclerData.getFolder() == 1) {
                params2.horizontalBias = 0.1f;
                holder.cardViewImage.setLayoutParams(params2);


            } else if (recyclerData.getFolder() == 0) {
                params2.horizontalBias = 0.9f;
                holder.cardViewImage.setLayoutParams(params2);


            }
        } else {

            holder.courseTV.setText(recyclerData.getTitle());

            //holder.courseIV.setImageResource(recyclerData.getFolder());


            // params2.horizontalBias=(float)0.5;


            //holder.cardViewId.setCardBackgroundColor(recyclerData.getFolder());
            if (recyclerData.getFolder() == 1) {
                params.horizontalBias = 0.25f;
                holder.cardViewId.setLayoutParams(params);


                holder.cardViewId.setCardBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.teal));
            } else if (recyclerData.getFolder() == 0) {
                params.horizontalBias = 0.75f;
                holder.cardViewId.setLayoutParams(params);

                holder.cardViewId.setCardBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.white));

            }
            holder.cardViewId.setRadius(30);


        }
    }


    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return courseDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView courseTV;
        private CardView cardViewId;
        private TextView textViewDate;
        private CardView cardViewDate;
        private CardView cardViewMain;
        private ImageView imageView2;
        private CardView cardViewImage;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.findViewById(R.id.cardViewId).setOnClickListener(this);
            itemView.findViewById(R.id.cardViewId).setOnLongClickListener(this);
            courseTV = itemView.findViewById(R.id.idTVCourse);
            cardViewId = itemView.findViewById(R.id.cardViewId);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            cardViewDate = itemView.findViewById(R.id.cardViewDate);
            imageView2 = itemView.findViewById(R.id.imageView2);
            cardViewMain = itemView.findViewById(R.id.cardViewMain);
            cardViewImage = itemView.findViewById(R.id.cardViewImage);
        }

        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);


        }


        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return true;


        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }
}

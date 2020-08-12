package com.example.myplayer;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CustomHolder extends RecyclerView.ViewHolder {
    CardView cardView;
    ImageView custom_img;
    TextView custom_title;
    TextView custom_writer;

    public CustomHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.cardView);
        custom_img = (ImageView)itemView.findViewById(R.id.custom_img);
        custom_title = (TextView)itemView.findViewById(R.id.custom_title);
        custom_writer = (TextView)itemView.findViewById(R.id.custom_writer);
    }
}

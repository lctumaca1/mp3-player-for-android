package com.example.myplayer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomHolder> {


    Context context = null;
    int resId;
    int cardPos;
    ArrayList<SongDetail> datas;

    public CustomAdapter(Context context, int resId, ArrayList<SongDetail> datas) {
        this.context = context;
        this.resId = resId;
        this.datas = datas;
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resId, parent, false );
        final CustomHolder holder = new CustomHolder(view);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cardPos = holder.getBindingAdapterPosition();
                Log.d("LOG_ME", cardPos + "");

                MyPlayer.getInstance().reset();

                if(context instanceof MainActivity) {
                    ((MainActivity) context).setPlayer(cardPos);
                    ((MainActivity) context).play();
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView image = holder.custom_img;
        TextView title = holder.custom_title;
        TextView writer = holder.custom_writer;

        SongDetail vo = datas.get(position);

        title.setText(vo.getSong_name());
        writer.setText(vo.getSong_writer());
        Bitmap bmp = BitmapFactory.decodeFile(vo.getImg_path());
        image.setImageBitmap(bmp);


        setAnimation(holder.cardView, position);
    }

    private void setAnimation(CardView cardView, int position) {
        Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        cardView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


}


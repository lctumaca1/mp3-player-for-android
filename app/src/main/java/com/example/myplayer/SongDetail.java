package com.example.myplayer;

import android.net.Uri;

import androidx.annotation.NonNull;

public class SongDetail {
    private String song_name;
    private String song_path;
    private String song_writer;
    private String img_path;

    public String getSong_writer() {
        return song_writer;
    }

    public void setSong_writer(String song_writer) {
        this.song_writer = song_writer;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getSong_path() {
        return song_path;
    }

    public void setSong_path(String song_path) {
        this.song_path = song_path;
    }

    @NonNull
    @Override
    public String toString() {
        return "[ writer: " + getSong_writer() +  ", song name: " + getSong_name() + ", song path:" + getSong_path() + ", img path: " + getImg_path() + "]";
    }
}

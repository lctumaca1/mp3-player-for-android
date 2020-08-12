package com.example.myplayer;

import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

public class MyPlayer extends AppCompatActivity {

    private static MediaPlayer mediaPlayer = new MediaPlayer();

    private static MyPlayer instance = new MyPlayer();

    private MyPlayer() {

    }

    public static MyPlayer getInstance() {
        return instance;
    }



    public MyPlayer setSource(String mediaSource) {
        try {
            this.mediaPlayer.setDataSource(mediaSource);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return getInstance();
    }

    public MyPlayer prepare() {
        try {
            this.mediaPlayer.prepare();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return getInstance();
    }

    public MyPlayer start() {
        try {
            this.mediaPlayer.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return getInstance();
    }

    public MyPlayer pauseMedia() {
        try {
            this.mediaPlayer.pause();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return getInstance();
    }

    public MyPlayer playMedia() {
        try {
            this.mediaPlayer.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return getInstance();
    }

    public MyPlayer release() {
        try {
            this.mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getInstance();
    }

    public MyPlayer reset() {
        try {
            this.mediaPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getInstance();
    }

    public int getCurrentPosition() {
        int position = 0;
        try {
            position = this.mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }

    public MyPlayer seekTo(int position) {
        try {
            this.mediaPlayer.seekTo(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getInstance();
    }

    public MyPlayer stop() {
        try {
            this.mediaPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getInstance();
    }

    public int getDuration() {
        int d = 0;
        try {
            d = this.mediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }



}

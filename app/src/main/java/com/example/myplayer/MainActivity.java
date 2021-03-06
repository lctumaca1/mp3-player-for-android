package com.example.myplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView play_btn, skip_next, skip_prev, shuffle, repeat;
    TextView song_title, song_writer, start_time, end_time;
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    SeekBar seekbar;
    //?
    private static ArrayList<SongDetail> mediaList = new ArrayList<SongDetail>();
    private static ArrayList<SongDetail> shuffleList = new ArrayList<SongDetail>();
    private boolean isPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    int idx = 0;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == 0) {
                start_time.setText(String.format("%02d:%02d", (MyPlayer.getInstance().getCurrentPosition()  % (1000 * 60 * 60)) / (1000 * 60),  (((MyPlayer.getInstance().getCurrentPosition() % (1000 * 60 * 60)) % (1000 * 60)) / 1000)   ));
            }
        }
    };


    class MyThread extends Thread {

        @Override
        public void run() {
            while(isPlaying) {
                handler.sendEmptyMessage(0);
                seekbar.setProgress(MyPlayer.getInstance().getCurrentPosition());
                Log.d("LOG_ME_THREAD", seekbar.getMax() + ", " + seekbar.getProgress());
                try {
                    MyThread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }




    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play_btn = (ImageView)findViewById(R.id.play_btn);
        skip_next = (ImageView)findViewById(R.id.skip_next_btn);
        skip_prev = (ImageView)findViewById(R.id.skip_prev_btn);
        shuffle = (ImageView)findViewById(R.id.shuffle);
        repeat = (ImageView)findViewById(R.id.repeat);
        song_title = (TextView)findViewById(R.id.song_title);
        song_writer = (TextView)findViewById(R.id.song_writer);
        seekbar = (SeekBar)findViewById(R.id.seekbar);
        start_time = (TextView)findViewById(R.id.startTime);
        end_time = (TextView)findViewById(R.id.endTime);



        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        if(isPlaying) {
            play_btn.setImageDrawable(getDrawable(R.drawable.pause));
        } else {
            play_btn.setImageDrawable(getDrawable(R.drawable.play));
        }

        play_btn.setOnClickListener(this);
        skip_next.setOnClickListener(this);
        skip_prev.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if((progress) >= (seekBar.getMax() - 100)) {
                    pause();

                    if(isRepeat) {
                        do {
                            skipNext();
                        } while(false);
                    } else {
                        pause();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isPlaying = false;

                MyPlayer.getInstance().pauseMedia();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                play();
                int touch = seekBar.getProgress();
                MyPlayer.getInstance().seekTo(touch).start();
                new MyThread().start();

            }
        });


        checkSelfPermission();
        fillData();
        fillShuffleList();

        for(SongDetail a : shuffleList) {
            Log.d("LOG_ME_SHUFFLE", a.toString());
        }

        //

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        customAdapter = new CustomAdapter(this, R.layout.custom_item, mediaList);
        recyclerView.setAdapter(customAdapter);

        //

        if(mediaList.size() > 0) {
            setPlayer(idx);
        }
    }

    public void setPlayer(int idx) {
        if(isShuffle) {
            MyPlayer.getInstance().setSource(shuffleList.get(idx).getSong_path()).prepare();
            song_title.setText(shuffleList.get(idx).getSong_name());
            song_writer.setText(shuffleList.get(idx).getSong_writer());
            end_time.setText(String.format("%02d:%02d", (MyPlayer.getInstance().getDuration()  % (1000 * 60 * 60)) / (1000 * 60),  (((MyPlayer.getInstance().getDuration() % (1000 * 60 * 60)) % (1000 * 60)) / 1000)   ));
            Log.d("LOG_ME_IDX", "SHUFFLE -> curr idx : " + idx + ", max idx : " + (shuffleList.size() - 1) + ", song : " + (shuffleList.get(idx).getSong_name()));
        } else {
            MyPlayer.getInstance().setSource(mediaList.get(idx).getSong_path()).prepare();
            song_title.setText(mediaList.get(idx).getSong_name());
            song_writer.setText(mediaList.get(idx).getSong_writer());
            end_time.setText(String.format("%02d:%02d", (MyPlayer.getInstance().getDuration()  % (1000 * 60 * 60)) / (1000 * 60),  (((MyPlayer.getInstance().getDuration() % (1000 * 60 * 60)) % (1000 * 60)) / 1000)   ));
            Log.d("LOG_ME_IDX", "GENERATE -> curr idx : " + idx + ", max idx : " + (mediaList.size() - 1) + ", song : " + (mediaList.get(idx).getSong_name()));
        }

    }

    public void releaseShuffle() {
        isShuffle = false;
        shuffle.setImageDrawable(getDrawable(R.drawable.shuffle_no));
    }


    public static void fillData() {
        String sdPath;

        String externalState = Environment.getExternalStorageState();
        if (externalState.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            sdPath = Environment.MEDIA_UNMOUNTED;
        }

        try {
            File dir = new File(sdPath + "/Music/");
            File[] files = dir.listFiles();
            SongDetail obj = null;
            Log.d("LOG_ME", dir.exists() + "");
            for(File song : files) {
                if(!song.getName().substring(0, 1).equals(".")) {
                    Log.d("LOG_ME_HOHO", song.toString());
                    obj = new SongDetail();
                    if(song.isDirectory()) {
                        String[] str = song.getName().split(" - ");
                        obj.setSong_writer(str[0]);
                        obj.setSong_name(str[1]);
                        File[] items = song.listFiles();
                        for(File f : items) {
                            if(f.isFile() && f.getName().indexOf(".webp") != -1) {
                                obj.setImg_path(f.getPath());
                            } else if(f.isFile() && f.getName().indexOf(".mp3") != -1) {
                                obj.setSong_path(f.getPath());
                            }
                        }
                    }
                    mediaList.add(obj);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }


    public void checkSelfPermission() {
        String temp = new String("");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        } //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }

        if (TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        }else {
            // 모두 허용 상태
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        if(v == play_btn) {
            if(isPlaying) {
                pause();
            } else {
                play();
            }
        } else if(v == skip_next) {
            skipNext();
        } else if(v == skip_prev) {
            skipPrev();
        } else if(v == shuffle) {
            if(isShuffle) { //no 셔플
                isShuffle = false;
                shuffle.setImageDrawable(getDrawable(R.drawable.shuffle_no));
            } else { //셔플
                isShuffle = true;
                shuffle.setImageDrawable(getDrawable(R.drawable.shuffle));
            }
        } else if(v == repeat) {
            if(isRepeat) { //no repeat
                isRepeat = false;
                repeat.setImageDrawable(getDrawable(R.drawable.repeat_no));
            } else { //repeat
                isRepeat = true;

                repeat.setImageDrawable(getDrawable(R.drawable.repeat));
            }
        }
    }

    public void pause() {
        isPlaying = false;
        play_btn.setImageDrawable(getDrawable(R.drawable.play));
        MyPlayer.getInstance().pauseMedia();
    }

    public void play() {
        new MyThread().start();
        isPlaying = true;
        play_btn.setImageDrawable(getDrawable(R.drawable.pause));
        MyPlayer.getInstance().playMedia();
        int d = MyPlayer.getInstance().getDuration();
        seekbar.setMax(d);
    }

    public void skipNext() {
        Log.d("LOG_ME_TYPE", "shuffle : " + isShuffle + ", repeat : " + isRepeat);
        this.idx += 1;
        if(idx >= mediaList.size()) {
            idx = 0;
        }
        MyPlayer.getInstance().reset();
        setPlayer(idx);
        play();


    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void skipPrev() {

        if(idx -1 < 0) {
            idx = mediaList.size() - 1;
        } else {
            --idx;

        }
        MyPlayer.getInstance().reset();
        setPlayer(idx);
        play();


    }

    public void fillShuffleList() {
        for(int i = 0; i < mediaList.size(); i++) {
            shuffleList.add(mediaList.get(i));
        }
        long seed = System.nanoTime();
        Collections.shuffle(shuffleList, new Random(seed));


    }



    @Override
    protected void onResume() {
        super.onResume();
        mediaList.clear();
        fillData();
        customAdapter.notifyDataSetChanged();
    }




}
package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.Player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ActionBar actionBar;
    TextView tv_songName;
    TextView tv_seekBarHint;
    TextView tv_duration;
    SeekBar seekBar;
    ImageButton btn_play;
    ImageButton btn_pre;
    ImageButton btn_next;
    ImageButton btn_playList;
    ImageButton btn_playWay;
    ListView listView;
    ArrayAdapter<String> adpter;
    List<String> music_list = new ArrayList<>();
    ConstraintLayout layout;
    MusicService musicService;
    private boolean isServiceBound = false;
    private Timer timer; //定时器

    // 更新播放进度和歌曲总时长
    private class ProgressUpdate extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    long position = musicService.getContentPosition();
                    long duration = musicService.getDuration();

                    // 更新当前进度文本
                    tv_seekBarHint.setText(format(position));
                    // 更新歌曲总时长文本
                    tv_duration.setText(format(duration));

                    // 更新进度条进度
                    seekBar.setMax((int) duration);
                    seekBar.setProgress((int) position);
                }
            });
        }
    }

    public String format(long position) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss"); // "分:秒"格式
        String timeStr = sdf.format(position); //会自动将时长(毫秒数)转换为分秒格式
        return timeStr;
    }


    // 绑定 MusicService
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isServiceBound = false;
        }
    };
    View.OnClickListener listener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, MusicService.class);
            switch (v.getId()) {
                case R.id.btn_playList:
                    showListView();
                    break;
                case R.id.btn_play:
                    playOrPauseMusic();
                    break;
                case R.id.btn_pre:
                    playPreviousTrack();
                    break;
                case R.id.btn_next:
                    playNextTrack();
                    break;
                case R.id.btn_playWay:
                    changePlayMode();
                    break;
            }
        }
    };

    // ListView 显示音乐列表 start
    public void showListView() {
        music_list = getMusic(); // 获取音乐列表
        adpter = new ArrayAdapter<String>( // 创建适配器
                MainActivity.this,
                android.R.layout.simple_list_item_single_choice,
                music_list
        );
        listView.setAdapter(adpter); // 设置适配器
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 设置选择模式

        listView.setOnItemClickListener(listener2); // 设置监听器
        listView.setVisibility(View.VISIBLE); // 设置可见
    }

    List<String> getMusic() {
        List<String> mList = new ArrayList<>();
        try {
            String[] fNames = getAssets().list("music");
            for (String fn : fNames) {
                mList.add(fn);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mList.add("返回");
        return mList;
    }

    AdapterView.OnItemClickListener listener2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lv = (ListView) parent;
            lv.setSelector(R.color.purple_200); //设置高亮背景色 purple_200颜色值见原来项目
            String musicName = parent.getItemAtPosition(position).toString(); //获得选中项图片名称，需要toString

            if (musicName.equals("返回")) {
                // 获取ListView的布局参数
                listView.setVisibility(View.GONE);
            } else {
                playMusic(musicName);
            }
        }
    };

    public void playMusic(String musicName) {
        updateSongName(musicName);
        if (isServiceBound && musicService != null) {
            timer = new Timer();
            timer.schedule(new ProgressUpdate(), 0, 1000);
            musicService.playMusic(musicName);
            btn_play.setImageResource(R.drawable.pause);
        }
    }
    // ListView 显示音乐列表 end

    public void playOrPauseMusic() {
        if (musicService.isPlaying()) {
            btn_play.setImageResource(R.drawable.play);
            musicService.pause();
        } else {
            // 创建一个 Timer 对象，用于定时更新进度条和文本
            timer = new Timer();
            // 将 ProgressUpdate 任务调度到 Timer 中，每隔一定时间执行一次
            timer.schedule(new ProgressUpdate(), 0, 1000);
            btn_play.setImageResource(R.drawable.pause);

            musicService.play();

            String songName = musicService.getCurrentSongName();
            updateSongName(songName);
        }
    }

    // 播放上一首音乐
    public void playPreviousTrack() {
        if (isServiceBound && musicService != null) {
            timer = new Timer();
            timer.schedule(new ProgressUpdate(), 0, 1000);
            btn_play.setImageResource(R.drawable.pause);
            musicService.playPreviousTrack();
            String songName = musicService.getCurrentSongName();
            updateSongName(songName);
        }
    }

    // 播放下一首音乐
    public void playNextTrack() {
        if (isServiceBound && musicService != null) {
            timer = new Timer();
            timer.schedule(new ProgressUpdate(), 0, 1000);
            btn_play.setImageResource(R.drawable.pause);
            musicService.playNextTrack();
            String songName = musicService.getCurrentSongName();
            updateSongName(songName);
        }
    }

    public void updateSongName(String songName) {
        // 去掉文件名后缀
        songName = songName.substring(0, songName.lastIndexOf("."));
        tv_songName.setText(songName);
    }

    // 更换播放模式
    private void changePlayMode() {
        if (musicService != null) {
            int currentMode = musicService.getPlayMode();
            if (currentMode == Player.REPEAT_MODE_ALL) {
                musicService.setPlayMode(Player.REPEAT_MODE_ONE); // 切换到单曲循环
                btn_playWay.setImageResource(R.drawable.single_cycle); // 更新按钮图标
            } else if (currentMode == Player.REPEAT_MODE_ONE) {
                musicService.setPlayMode(Player.REPEAT_MODE_ALL); // 切换到顺序播放
                btn_playWay.setImageResource(R.drawable.play_in_order); // 更新按钮图标
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initView() {
        tv_songName = (TextView) findViewById(R.id.tv_songName);
        tv_seekBarHint = (TextView) findViewById(R.id.tv_seekBarHint);
        tv_duration = (TextView) findViewById(R.id.tv_duration);

        btn_play = (ImageButton) findViewById(R.id.btn_play);
        btn_pre = (ImageButton) findViewById(R.id.btn_pre);
        btn_next = (ImageButton) findViewById(R.id.btn_next);
        btn_playList = (ImageButton) findViewById(R.id.btn_playList);
        btn_playWay = (ImageButton) findViewById(R.id.btn_playWay);

        btn_play.setOnClickListener(listener1);
        btn_pre.setOnClickListener(listener1);
        btn_next.setOnClickListener(listener1);
        btn_playList.setOnClickListener(listener1);
        btn_playWay.setOnClickListener(listener1);

        listView = (ListView) findViewById(R.id.lv_music);

        layout = (ConstraintLayout) findViewById(R.id.constrainLayout);

        layout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // 获取 ListView 的位置信息
                int[] listViewLocation = new int[2];
                listView.getLocationOnScreen(listViewLocation);

                // 判断触摸事件的坐标是否在 ListView 区域内
                if (event.getRawX() < listViewLocation[0] ||
                        event.getRawX() > listViewLocation[0] + listView.getWidth() ||
                        event.getRawY() < listViewLocation[1] ||
                        event.getRawY() > listViewLocation[1] + listView.getHeight()) {
                    // 如果触摸事件不在 ListView 区域内，则隐藏 ListView
                    listView.setVisibility(View.GONE);
                    return true; // 表示触摸事件已经被处理
                }
                return false; // 表示触摸事件未被处理
            }
        });

        // SeekBar
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_seekBarHint = (TextView) findViewById(R.id.tv_seekBarHint);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // SeekBar 的进度发生改变时触发的操作
                if (fromUser) {
                    timer = new Timer();
                    timer.schedule(new ProgressUpdate(), 0, 1000);
                    tv_seekBarHint.setText(format(progress));
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 用户开始滑动 SeekBar 时触发的操作
                btn_play.setImageResource(R.drawable.play);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 用户停止滑动 SeekBar 时触发的操作
                btn_play.setImageResource(R.drawable.pause);
                updateSongName(musicService.getCurrentSongName());
                musicService.play();
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 设置全屏
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // 设置 ActionBar
        actionBar = getSupportActionBar();
        actionBar.setTitle("MusicPlayer");
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();


        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        musicService.onDestroy();
    }
}
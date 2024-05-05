package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// MusicService.java
public class MusicService extends Service {
    ExoPlayer player;
    List<MediaItem> mediaItems = new ArrayList<>();
    int currentTrackIndex = 0;
    int playMode = Player.REPEAT_MODE_ALL; // 默认为顺序播放
    boolean prepared = false;



    public void init() {
        if (player == null) {
            player = new ExoPlayer.Builder(MusicService.this).build(); // 创建播放器

            try {
                // 获取 assets/music 文件夹中的所有音乐文件
                String[] musicFiles = getAssets().list("music");
                for (String musicFile : musicFiles) {
                    // 构建音乐文件的 URI
                    Uri uri = Uri.parse("asset:///music/" + musicFile);
                    // 创建 MediaItem 并添加到播放列表
                    mediaItems.add(MediaItem.fromUri(uri));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 将所有音乐文件添加到播放器的播放列表中
            player.addMediaItems(mediaItems);
            player.setRepeatMode(playMode); // 顺序播放
            player.prepare();
        }
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public void play() {
        if (player != null) {
            player.play();
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    public void playMusic(String name) {
        if (player != null) {
            for (int i = 0; i < mediaItems.size(); i++) {
                MediaItem mediaItem = mediaItems.get(i);
                String fileName = getFileNameFromMediaItem(mediaItem);
                if (fileName.equals(name)) {
                    player.seekTo(i, 0);
                    player.play();
                    break;
                }
            }
        }

    }

    public String getFileNameFromMediaItem(MediaItem mediaItem) {
        Uri uri = mediaItem.playbackProperties.uri;
        return uri.getLastPathSegment();
    }

    public void playNextTrack() {
        currentTrackIndex++;
        if (currentTrackIndex >= mediaItems.size()) {
            currentTrackIndex = 0; // 如果超出了列表范围，回到第一首
        }
        player.seekToDefaultPosition(currentTrackIndex);
        player.play();
    }

    public void playPreviousTrack() {
        currentTrackIndex--;
        if (currentTrackIndex < 0) {
            currentTrackIndex = mediaItems.size() - 1; // 如果低于列表范围，回到最后一首
        }
        player.seekToDefaultPosition(currentTrackIndex);
        player.play();
    }

    public void seekTo(long position) {
        if (player != null) {
            player.seekTo(position);
        }
    }
    // 获取当前播放进度
    public long getContentPosition() {
        if (player != null) {
            return  player.getCurrentPosition();
        }
        return 0;
    }

    // 获取歌曲总时长
    public long getDuration() {
        if (player != null) {
            return player.getDuration();
        }
        return 0;
    }

    // 获取当前正在播放的歌曲名
    public String getCurrentSongName() {
        if (currentTrackIndex >= 0 && currentTrackIndex < mediaItems.size()) {
            MediaItem mediaItem = mediaItems.get(currentTrackIndex);
            return getFileNameFromMediaItem(mediaItem);
        }
        return "";
    }

    // 获取当前播放模式
    public int getPlayMode() {
        return playMode;
    }

    // 设置播放模式
    public void setPlayMode(int mode) {
        playMode = mode;
        player.setRepeatMode(playMode); // 更新播放模式
    }


    // 绑定 MusicService
    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}

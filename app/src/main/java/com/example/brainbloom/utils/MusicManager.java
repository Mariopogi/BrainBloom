package com.example.brainbloom.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private static MusicManager instance;
    private final Context appContext;
    private MediaPlayer mediaPlayer;
    private int musicLevel = 5;

    private MusicManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static synchronized MusicManager getInstance(Context context) {
        if (instance == null) {
            instance = new MusicManager(context);
        }
        return instance;
    }

    public void playMusic(int rawResId, boolean loop) {
        stopMusic();
        mediaPlayer = MediaPlayer.create(appContext, rawResId);
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setLooping(loop);
        applyVolume();
        mediaPlayer.start();
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void setMusicLevel(int level) {
        musicLevel = Math.max(0, Math.min(10, level));
        applyVolume();
    }

    public int getMusicLevel() {
        return musicLevel;
    }

    private void applyVolume() {
        if (mediaPlayer != null) {
            float volume = musicLevel / 10.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }
}

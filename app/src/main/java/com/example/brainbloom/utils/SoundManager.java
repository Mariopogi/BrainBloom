package com.example.brainbloom.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    private static SoundManager instance;
    private final Context appContext;
    private int soundLevel = 5;

    private SoundManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    public void playSound(int rawResId) {
        if (soundLevel <= 0) {
            return;
        }

        MediaPlayer player = MediaPlayer.create(appContext, rawResId);
        if (player == null) {
            return;
        }

        float volume = soundLevel / 10.0f;
        player.setVolume(volume, volume);
        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }

    public void setSoundLevel(int level) {
        soundLevel = Math.max(0, Math.min(10, level));
    }

    public int getSoundLevel() {
        return soundLevel;
    }
}

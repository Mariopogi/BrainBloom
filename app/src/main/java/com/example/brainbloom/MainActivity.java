package com.example.brainbloom;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainbloom.database.BrainBloomDatabaseHelper;
import com.example.brainbloom.utils.MusicManager;

public class MainActivity extends AppCompatActivity {

    private MusicManager musicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        BrainBloomDatabaseHelper.getInstance(this).getReadableDatabase();

        musicManager = MusicManager.getInstance(this);
        musicManager.playMusic(R.raw.bg_music_mainmenu, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            MusicManager.getInstance(this).stopMusic();
        }
    }
}

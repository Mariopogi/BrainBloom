package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.utils.MusicManager;
import com.example.brainbloom.utils.SoundManager;

public class SettingsFragment extends Fragment {

    private TextView textSoundLevel;
    private TextView textMusicLevel;
    private SoundManager soundManager;
    private MusicManager musicManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        soundManager = SoundManager.getInstance(requireContext());
        musicManager = MusicManager.getInstance(requireContext());

        textSoundLevel = view.findViewById(R.id.textSoundLevel);
        textMusicLevel = view.findViewById(R.id.textMusicLevel);
        updateLabels();

        view.findViewById(R.id.buttonSettingsBack).setOnClickListener(v -> {
            soundManager.playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_settings_to_mainMenu);
        });

        view.findViewById(R.id.buttonSoundMinus).setOnClickListener(v -> {
            soundManager.setSoundLevel(soundManager.getSoundLevel() - 1);
            updateLabels();
        });

        view.findViewById(R.id.buttonSoundPlus).setOnClickListener(v -> {
            soundManager.setSoundLevel(soundManager.getSoundLevel() + 1);
            soundManager.playSound(R.raw.button_click);
            updateLabels();
        });

        view.findViewById(R.id.buttonMusicMinus).setOnClickListener(v -> {
            musicManager.setMusicLevel(musicManager.getMusicLevel() - 1);
            updateLabels();
        });

        view.findViewById(R.id.buttonMusicPlus).setOnClickListener(v -> {
            musicManager.setMusicLevel(musicManager.getMusicLevel() + 1);
            soundManager.playSound(R.raw.button_click);
            updateLabels();
        });

        view.findViewById(R.id.buttonExportLeaderboard).setOnClickListener(v -> {
            soundManager.playSound(R.raw.button_click);
            Toast.makeText(requireContext(), "Export placeholder ready for later file export.", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateLabels() {
        textSoundLevel.setText("SOUNDS " + soundManager.getSoundLevel());
        textMusicLevel.setText("MUSIC " + musicManager.getMusicLevel());
    }
}

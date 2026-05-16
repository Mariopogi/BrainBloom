package com.example.brainbloom.fragments;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.example.brainbloom.database.BrainBloomDatabaseHelper;
import com.example.brainbloom.game.GameConstants;
import com.example.brainbloom.models.LeaderboardRecord;
import com.example.brainbloom.utils.MusicManager;
import com.example.brainbloom.utils.SoundManager;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    private TextView textSoundLevel;
    private TextView textMusicLevel;
    private SoundManager soundManager;
    private MusicManager musicManager;
    private BrainBloomDatabaseHelper databaseHelper;

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
            exportLeaderboard();
        });
    }

    private void exportLeaderboard() {
        databaseHelper = BrainBloomDatabaseHelper.getInstance(requireContext());
        String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String fileName = "leaderboards-brainbloom-" + dateString + ".txt";
        String content = generateLeaderboardContent();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = requireContext().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri)) {
                        if (outputStream != null) {
                            outputStream.write(content.getBytes(StandardCharsets.UTF_8));
                            Toast.makeText(requireContext(), "Exported to Downloads: " + fileName, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } else {
                java.io.File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                java.io.File file = new java.io.File(downloadsDir, fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(content.getBytes(StandardCharsets.UTF_8));
                    Toast.makeText(requireContext(), "Exported to Downloads: " + fileName, Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String generateLeaderboardContent() {
        StringBuilder sb = new StringBuilder();
        String dateHeader = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        sb.append("BRAIN BLOOM LEADERBOARD EXPORT\n");
        sb.append("Generated on: ").append(dateHeader).append("\n\n");

        appendModeRecords(sb, GameConstants.MODE_SINGLE_PLAYER);
        sb.append("\n");
        appendModeRecords(sb, GameConstants.MODE_TWO_PLAYER);

        return sb.toString();
    }

    private void appendModeRecords(StringBuilder sb, String mode) {
        sb.append("--- ").append(mode.toUpperCase()).append(" ---\n");
        List<LeaderboardRecord> records = databaseHelper.getTopRecords(mode, 100);
        if (records.isEmpty()) {
            sb.append("No records found.\n");
        } else {
            for (int i = 0; i < records.size(); i++) {
                LeaderboardRecord r = records.get(i);
                sb.append(i + 1).append(". ").append(r.getPlayerName())
                        .append(" | Score: ").append(r.getFinalScore())
                        .append(" | Diff: ").append(r.getDifficulty())
                        .append(" | Date: ").append(r.getDatePlayed());

                if (mode.equals(GameConstants.MODE_TWO_PLAYER)) {
                    sb.append(" | Winner: ").append(r.getWinnerName());
                }
                sb.append("\n");
            }
        }
    }

    private void updateLabels() {
        textSoundLevel.setText("SOUNDS " + soundManager.getSoundLevel());
        textMusicLevel.setText("MUSIC " + musicManager.getMusicLevel());
    }
}

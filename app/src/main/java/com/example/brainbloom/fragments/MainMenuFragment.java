package com.example.brainbloom.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.game.GameSession;
import com.example.brainbloom.utils.MusicManager;
import com.example.brainbloom.utils.SoundManager;

public class MainMenuFragment extends Fragment {

    private Typeface arcadeFont;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        arcadeFont = ResourcesCompat.getFont(requireContext(), R.font.arcade);

        MusicManager.getInstance(requireContext()).playMusic(R.raw.bg_music_mainmenu, true);

        view.findViewById(R.id.buttonStartAdventure).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            GameSession.getInstance().resetAdventure();
            NavHostFragment.findNavController(this).navigate(R.id.action_mainMenu_to_story);
        });

        view.findViewById(R.id.buttonTwoPlayer).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_mainMenu_to_twoPlayerSetup);
        });

        view.findViewById(R.id.buttonLeaderboard).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_mainMenu_to_leaderboard);
        });

        view.findViewById(R.id.buttonSettings).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_mainMenu_to_settings);
        });

        view.findViewById(R.id.buttonExit).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            showExitConfirmationDialog();
        });
    }

    private void showExitConfirmationDialog() {
        LinearLayout dialogLayout = new LinearLayout(requireContext());
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(48, 36, 48, 24);

        TextView titleText = new TextView(requireContext());
        titleText.setText("EXIT GAME?");
        titleText.setTextColor(getResources().getColor(R.color.bb_black));
        titleText.setTextSize(22);
        titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleText.setGravity(android.view.Gravity.CENTER);
        titleText.setTypeface(arcadeFont, Typeface.BOLD);
        titleText.setIncludeFontPadding(false);

        TextView messageText = new TextView(requireContext());
        messageText.setText("Are you sure you want to exit Brain Bloom?");
        messageText.setTextColor(getResources().getColor(R.color.bb_black));
        messageText.setTextSize(14);
        messageText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        messageText.setGravity(android.view.Gravity.CENTER);
        messageText.setTypeface(arcadeFont, Typeface.NORMAL);
        messageText.setIncludeFontPadding(true);
        messageText.setPadding(0, 24, 0, 0);

        dialogLayout.addView(titleText);
        dialogLayout.addView(messageText);

        AlertDialog exitDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogLayout)
                .setPositiveButton("YES", (dialog, which) -> {
                    SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
                    requireActivity().finish();
                })
                .setNegativeButton("NO", (dialog, which) -> {
                    SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
                    dialog.dismiss();
                })
                .create();

        exitDialog.setOnShowListener(dialog -> {
            exitDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.bb_red));
            exitDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.bb_black));

            exitDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(14);
            exitDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(14);

            exitDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(arcadeFont, Typeface.BOLD);
            exitDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(arcadeFont, Typeface.BOLD);
        });

        exitDialog.show();
    }
}
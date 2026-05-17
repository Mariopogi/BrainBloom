package com.example.brainbloom.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.game.GameSession;
import com.example.brainbloom.utils.MusicManager;
import com.example.brainbloom.utils.SoundManager;

public class GameOverFragment extends Fragment {

    private final GameSession session = GameSession.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_over, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MusicManager.getInstance(requireContext()).playMusic(R.raw.bg_music_result_screen, true);
        SoundManager.getInstance(requireContext()).playSound(R.raw.game_over);

        TextView textGameOverScore = view.findViewById(R.id.textGameOverScore);
        textGameOverScore.setText(String.valueOf(session.getLastAttemptScore()));

        view.findViewById(R.id.buttonTryAgain).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            session.resetBookScore(session.getSelectedBookNumber());
            NavHostFragment.findNavController(this).navigate(R.id.action_gameOver_to_bookSelection);
        });

        view.findViewById(R.id.buttonGameOverMainMenu).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            showLeaveWarning();
        });
    }

    private void showLeaveWarning() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_confirm_navigation, null);

        TextView txtConfirmTitle = dialogView.findViewById(R.id.txtConfirmTitle);
        TextView txtConfirmMessage = dialogView.findViewById(R.id.txtConfirmMessage);
        TextView btnConfirmAction = dialogView.findViewById(R.id.btnConfirmAction);
        TextView btnCancelConfirmNavigation = dialogView.findViewById(R.id.btnCancelConfirmNavigation);

        txtConfirmTitle.setText("Are you sure you want to go to Main Menu?");
        txtConfirmMessage.setText(R.string.warning_exit_progress);
        btnConfirmAction.setText("GO TO MAIN MENU");

        btnConfirmAction.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            dialog.dismiss();
            session.resetAdventure();
            NavHostFragment.findNavController(this).navigate(R.id.action_gameOver_to_mainMenu);
        });

        btnCancelConfirmNavigation.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            dialog.dismiss();
        });

        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        dialog.setOnShowListener(dialogInterface -> {
            Window window = dialog.getWindow();

            if (window == null) {
                return;
            }

            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);

            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.0f;

            window.setAttributes(params);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        });

        dialog.show();
    }
}

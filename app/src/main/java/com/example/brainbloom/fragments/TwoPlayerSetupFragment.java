package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.game.GameSession;
import com.example.brainbloom.utils.MusicManager;
import com.example.brainbloom.utils.SoundManager;

public class TwoPlayerSetupFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two_player_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MusicManager.getInstance(requireContext()).playMusic(R.raw.bg_music_before_quiz, true);

        EditText editPlayerOne = view.findViewById(R.id.editPlayerOneName);
        EditText editPlayerTwo = view.findViewById(R.id.editPlayerTwoName);

        view.findViewById(R.id.buttonTwoPlayerBack).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_twoPlayerSetup_to_mainMenu);
        });

        view.findViewById(R.id.buttonStartTwoPlayerMatch).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);

            String player1Name = editPlayerOne.getText().toString().trim();
            String player2Name = editPlayerTwo.getText().toString().trim();

            if (player1Name.isEmpty()) {
                player1Name = "Player 1";
            }
            if (player2Name.isEmpty()) {
                player2Name = "Player 2";
            }

            String difficulty = "Easy";
            if (((RadioButton) view.findViewById(R.id.radioMedium)).isChecked()) {
                difficulty = "Medium";
            } else if (((RadioButton) view.findViewById(R.id.radioHard)).isChecked()) {
                difficulty = "Hard";
            }

            GameSession.getInstance().resetTwoPlayer(player1Name, player2Name, difficulty);

            Bundle bundle = new Bundle();
            bundle.putString("PLAYER_1_NAME", player1Name);
            bundle.putString("PLAYER_2_NAME", player2Name);
            bundle.putString("DIFFICULTY", difficulty);

            MusicManager.getInstance(requireContext()).playMusic(R.raw.bg_music_quiz_screen_1, true);
            NavHostFragment.findNavController(this).navigate(R.id.action_twoPlayerSetup_to_twoPlayerQuiz, bundle);
        });
    }
}
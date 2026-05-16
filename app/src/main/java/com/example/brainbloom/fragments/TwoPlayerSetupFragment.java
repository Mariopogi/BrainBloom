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

        EditText playerOne = view.findViewById(R.id.editPlayerOneName);
        EditText playerTwo = view.findViewById(R.id.editPlayerTwoName);

        view.findViewById(R.id.buttonTwoPlayerBack).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_twoPlayerSetup_to_mainMenu);
        });

        view.findViewById(R.id.buttonStartTwoPlayerMatch).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);

            String difficulty = "Easy";
            if (((RadioButton) view.findViewById(R.id.radioMedium)).isChecked()) {
                difficulty = "Medium";
            } else if (((RadioButton) view.findViewById(R.id.radioHard)).isChecked()) {
                difficulty = "Hard";
            }

            GameSession.getInstance().resetTwoPlayer(
                    playerOne.getText().toString(),
                    playerTwo.getText().toString(),
                    difficulty
            );

            MusicManager.getInstance(requireContext()).playMusic(R.raw.bg_music_quiz_screen_1, true);
            NavHostFragment.findNavController(this).navigate(R.id.action_twoPlayerSetup_to_twoPlayerQuiz);
        });
    }
}

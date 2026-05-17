package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.database.BrainBloomDatabaseHelper;
import com.example.brainbloom.game.GameConstants;
import com.example.brainbloom.game.GameSession;
import com.example.brainbloom.models.LeaderboardRecord;
import com.example.brainbloom.utils.DateTimeUtils;
import com.example.brainbloom.utils.MusicManager;
import com.example.brainbloom.utils.SoundManager;

public class FinalSummaryFragment extends Fragment {

    private final GameSession session = GameSession.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_final_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MusicManager.getInstance(requireContext()).playMusic(R.raw.bg_music_result_screen, true);

        int finalScore = session.getOverallScore();
        int totalTime = session.getTotalTimeLeft();
        int highestCombo = session.getHighestCombo();

        ((TextView) view.findViewById(R.id.textBooksRestoredValue)).setText(
                session.getCompletedBookCount() + "/4"
        );
        ((TextView) view.findViewById(R.id.textOverallScoreValue)).setText(
                String.valueOf(finalScore)
        );
        ((TextView) view.findViewById(R.id.textTotalTimeLeftValue)).setText(
                totalTime + "s"
        );
        ((TextView) view.findViewById(R.id.textHighestComboValue)).setText(
                highestCombo + "x"
        );

        if (session.allBooksCompleted() && !session.isFinalSinglePlayerSaved()) {
            LeaderboardRecord record = new LeaderboardRecord(
                    0,
                    session.getSinglePlayerName(),
                    GameConstants.MODE_SINGLE_PLAYER,
                    4,
                    finalScore,
                    totalTime,
                    highestCombo,
                    session.getDifficulty(),
                    "",
                    DateTimeUtils.now()
            );
            BrainBloomDatabaseHelper.getInstance(requireContext()).saveLeaderboardRecord(record);
            session.setFinalSinglePlayerSaved(true);
        }

        view.findViewById(R.id.buttonFinalPlayAgain).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            session.resetAdventure();
            NavHostFragment.findNavController(this).navigate(R.id.action_finalSummary_to_bookSelection);
        });

        view.findViewById(R.id.buttonFinalMainMenu).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            session.resetAdventure();
            NavHostFragment.findNavController(this).navigate(R.id.action_finalSummary_to_mainMenu);
        });
    }
}

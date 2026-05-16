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
import com.example.brainbloom.models.Player;
import com.example.brainbloom.utils.DateTimeUtils;
import com.example.brainbloom.utils.MusicManager;
import com.example.brainbloom.utils.SoundManager;

public class WinnerResultFragment extends Fragment {

    private final GameSession session = GameSession.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_winner_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MusicManager.getInstance(requireContext()).playMusic(R.raw.bg_music_result_screen, true);
        SoundManager.getInstance(requireContext()).playSound(R.raw.winner_victory);

        String player1Name = "Player 1";
        String player2Name = "Player 2";
        int player1Score = 0;
        int player2Score = 0;
        int player1Time = 0;
        int player2Time = 0;
        int player1Combo = 0;
        int player2Combo = 0;
        String difficulty = "Easy";
        String winner = "";

        if (getArguments() != null) {
            player1Name = getArguments().getString("PLAYER_1_NAME", "Player 1");
            player2Name = getArguments().getString("PLAYER_2_NAME", "Player 2");
            player1Score = getArguments().getInt("PLAYER_1_SCORE", 0);
            player2Score = getArguments().getInt("PLAYER_2_SCORE", 0);
            player1Time = getArguments().getInt("PLAYER_1_TIME", 0);
            player2Time = getArguments().getInt("PLAYER_2_TIME", 0);
            player1Combo = getArguments().getInt("PLAYER_1_COMBO", 0);
            player2Combo = getArguments().getInt("PLAYER_2_COMBO", 0);
            difficulty = getArguments().getString("DIFFICULTY", "Easy");
            winner = getArguments().getString("WINNER", "");
        } else {
            Player p1 = session.getPlayerOne();
            Player p2 = session.getPlayerTwo();
            player1Name = p1.getName();
            player2Name = p2.getName();
            player1Score = p1.getScore();
            player2Score = p2.getScore();
            player1Time = p1.getTotalTimeLeft();
            player2Time = p2.getTotalTimeLeft();
            player1Combo = p1.getHighestCombo();
            player2Combo = p2.getHighestCombo();
            difficulty = session.getDifficulty();
            winner = session.getWinnerName();
        }

        // Title
        String titleText;
        if ("Tie".equals(winner)) {
            titleText = "DRAW!!";
        } else {
            titleText = winner.toUpperCase() + " WINS!!";
        }
        ((TextView) view.findViewById(R.id.textWinnerTitle)).setText(titleText);

        // Player 1 stats
        ((TextView) view.findViewById(R.id.textWinnerP1Name)).setText(player1Name);
        ((TextView) view.findViewById(R.id.textWinnerP1Stats)).setText(
                "Score: " + player1Score + "   Time: " + player1Time + "s   Combo: " + player1Combo + "x"
        );

        // Player 2 stats
        ((TextView) view.findViewById(R.id.textWinnerP2Name)).setText(player2Name);
        ((TextView) view.findViewById(R.id.textWinnerP2Stats)).setText(
                "Score: " + player2Score + "   Time: " + player2Time + "s   Combo: " + player2Combo + "x"
        );

        // Save leaderboard records
        saveTwoPlayerRecord(player1Name, player1Score, player1Time, player1Combo, difficulty, winner);
        saveTwoPlayerRecord(player2Name, player2Score, player2Time, player2Combo, difficulty, winner);

        view.findViewById(R.id.buttonWinnerPlayAgain).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_winnerResult_to_twoPlayerSetup);
        });

        view.findViewById(R.id.buttonWinnerMainMenu).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_winnerResult_to_mainMenu);
        });
    }

    private void saveTwoPlayerRecord(String playerName, int score, int timeLeft, int highestCombo,
                                     String difficulty, String winner) {
        LeaderboardRecord record = new LeaderboardRecord(
                0,
                playerName,
                GameConstants.MODE_TWO_PLAYER,
                0,
                score,
                timeLeft,
                highestCombo,
                difficulty,
                winner,
                DateTimeUtils.now()
        );
        BrainBloomDatabaseHelper.getInstance(requireContext()).saveLeaderboardRecord(record);
    }
}
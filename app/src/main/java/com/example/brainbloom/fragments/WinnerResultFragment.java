package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private boolean dropdownVisible = false;

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

        String player1Name;
        String player2Name;
        int player1Score;
        int player2Score;
        int player1Time;
        int player2Time;
        int player1Combo;
        int player2Combo;
        String difficulty;
        String winner;

        if (getArguments() != null) {
            player1Name  = getArguments().getString("PLAYER_1_NAME", "Player 1").trim();
            player2Name  = getArguments().getString("PLAYER_2_NAME", "Player 2").trim();
            player1Score = getArguments().getInt("PLAYER_1_SCORE", 0);
            player2Score = getArguments().getInt("PLAYER_2_SCORE", 0);
            player1Time  = getArguments().getInt("PLAYER_1_TIME", 0);
            player2Time  = getArguments().getInt("PLAYER_2_TIME", 0);
            player1Combo = getArguments().getInt("PLAYER_1_COMBO", 0);
            player2Combo = getArguments().getInt("PLAYER_2_COMBO", 0);
            difficulty   = getArguments().getString("DIFFICULTY", "Easy");
            winner       = getArguments().getString("WINNER", "").trim();
        } else {
            Player p1 = session.getPlayerOne();
            Player p2 = session.getPlayerTwo();
            player1Name  = p1.getName().trim();
            player2Name  = p2.getName().trim();
            player1Score = p1.getScore();
            player2Score = p2.getScore();
            player1Time  = p1.getTotalTimeLeft();
            player2Time  = p2.getTotalTimeLeft();
            player1Combo = p1.getHighestCombo();
            player2Combo = p2.getHighestCombo();
            difficulty   = session.getDifficulty();
            winner       = session.getWinnerName().trim();
        }

        String titleText;
        if ("Tie".equalsIgnoreCase(winner) || winner.isEmpty()) {
            titleText = "DRAW!!";
        } else if (winner.equalsIgnoreCase(player1Name)) {
            titleText = "PLAYER 1 WINS!";
        } else if (winner.equalsIgnoreCase(player2Name)) {
            titleText = "PLAYER 2 WINS!";
        } else {
            titleText = winner.toUpperCase() + " WINS!";
        }
        ((TextView) view.findViewById(R.id.textWinnerTitle)).setText(titleText);

        ImageView crownP1 = view.findViewById(R.id.imageCrownP1);
        ImageView crownP2 = view.findViewById(R.id.imageCrownP2);
        View cardP1 = view.findViewById(R.id.cardPlayer1);
        View cardP2 = view.findViewById(R.id.cardPlayer2);

        int colorWinner = 0xFFFFD700;
        int colorLoser = 0xFFF0EDD0;

        if ("Tie".equalsIgnoreCase(winner) || winner.isEmpty()) {
            crownP1.setVisibility(View.INVISIBLE);
            crownP2.setVisibility(View.INVISIBLE);
            cardP1.setBackgroundColor(colorLoser);
            cardP2.setBackgroundColor(colorLoser);
        } else if (winner.equalsIgnoreCase(player1Name)) {
            crownP1.setVisibility(View.VISIBLE);
            crownP2.setVisibility(View.INVISIBLE);
            cardP1.setBackgroundColor(colorWinner);
            cardP2.setBackgroundColor(colorLoser);
        } else if (winner.equalsIgnoreCase(player2Name)) {
            crownP1.setVisibility(View.INVISIBLE);
            crownP2.setVisibility(View.VISIBLE);
            cardP1.setBackgroundColor(colorLoser);
            cardP2.setBackgroundColor(colorWinner);
        } else {
            crownP1.setVisibility(View.INVISIBLE);
            crownP2.setVisibility(View.INVISIBLE);
            cardP1.setBackgroundColor(colorLoser);
            cardP2.setBackgroundColor(colorLoser);
        }

        ((TextView) view.findViewById(R.id.textWinnerP1Name)).setText(player1Name);
        ((TextView) view.findViewById(R.id.textP1Score)).setText(String.valueOf(player1Score));
        ((TextView) view.findViewById(R.id.textP1Time)).setText(player1Time + "s");
        ((TextView) view.findViewById(R.id.textP1Combo)).setText(player1Combo + "x");

        ((TextView) view.findViewById(R.id.textWinnerP2Name)).setText(player2Name);
        ((TextView) view.findViewById(R.id.textP2Score)).setText(String.valueOf(player2Score));
        ((TextView) view.findViewById(R.id.textP2Time)).setText(player2Time + "s");
        ((TextView) view.findViewById(R.id.textP2Combo)).setText(player2Combo + "x");

        saveTwoPlayerLeaderboard(player1Name, player1Score, player2Name, player2Score, difficulty, winner);

        LinearLayout hamburgerButton = view.findViewById(R.id.hamburgerButton);
        LinearLayout dropdownMenu = view.findViewById(R.id.dropdownMenu);

        hamburgerButton.setOnClickListener(v -> {
            dropdownVisible = !dropdownVisible;
            dropdownMenu.setVisibility(dropdownVisible ? View.VISIBLE : View.GONE);
        });

        view.setOnClickListener(v -> {
            if (dropdownVisible) {
                dropdownVisible = false;
                dropdownMenu.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.buttonWinnerPlayAgain).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_winnerResult_to_twoPlayerSetup);
        });

        view.findViewById(R.id.buttonWinnerMainMenu).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_winnerResult_to_mainMenu);
        });
    }

    private void saveTwoPlayerLeaderboard(String p1, int s1, String p2, int s2, String diff, String winner) {
        String matchup = p1 + " vs " + p2;
        String result = s1 + " - " + s2;
        
        // Final score for DB sort can be the winning score or max score
        int topScore = Math.max(s1, s2);

        LeaderboardRecord record = new LeaderboardRecord(
                0,
                matchup,
                GameConstants.MODE_TWO_PLAYER,
                0,
                topScore,
                0,
                0,
                diff,
                winner, // winnerName field stores winner (e.g. "Mario" or "Tie")
                result  // datePlayed field is a TEXT, we'll hijack it or better yet, winnerName is result?
                // Let's use winnerName for winner name, and datePlayed for the actual date.
                // We don't have a 'result string' field. 
                // Let's use high combo or something? No.
                // Re-hijacking: winnerName = winner, timeLeft = p1score, highestCombo = p2score?
                // No, let's just stick to a consistent hijacking if we can't change model.
                // Actually, let's use playerName = Matchup, winnerName = winner, finalScore = Result (wait finalScore is int).
                // Okay, let's use: winnerName = winner, and we will format result string manually in adapter using finalScore if it was single value.
                // But we need BOTH scores.
                // Let's use: highestCombo = p1Score, timeLeft = p2Score.
        );
        
        // Actually, the simplest is to store "P1 - P2" string in the record somehow.
        // Let's use the 'difficulty' field to store "DIFF | P1-P2"? No.
        // Let's just use the 'datePlayed' field for the result string for now if we must, 
        // OR better: use 'winnerName' for "WINNER | RESULT".
        
        String winnerAndResult = winner + " (" + result + ")";
        
        LeaderboardRecord finalRecord = new LeaderboardRecord(
            0, matchup, GameConstants.MODE_TWO_PLAYER, 0, topScore, 0, 0, diff, winnerAndResult, DateTimeUtils.now()
        );

        BrainBloomDatabaseHelper.getInstance(requireContext()).saveLeaderboardRecord(finalRecord);
    }
}

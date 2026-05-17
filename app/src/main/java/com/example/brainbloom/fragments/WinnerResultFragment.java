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

        // --- Retrieve data from bundle or session fallback ---
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

        // --- Winner title ---
        // winner string is the actual player NAME (e.g. "Mario") or "Tie"
        String titleText;
        if ("Tie".equalsIgnoreCase(winner) || winner.isEmpty()) {
            titleText = "DRAW!!";
        } else if (winner.equalsIgnoreCase(player1Name)) {
            titleText = "PLAYER 1 WINS!";
        } else if (winner.equalsIgnoreCase(player2Name)) {
            titleText = "PLAYER 2 WINS!";
        } else {
            // Fallback: just show the name
            titleText = winner.toUpperCase() + " WINS!";
        }
        ((TextView) view.findViewById(R.id.textWinnerTitle)).setText(titleText);

        // --- Crown & Background logic ---
        ImageView crownP1 = view.findViewById(R.id.imageCrownP1);
        ImageView crownP2 = view.findViewById(R.id.imageCrownP2);
        View cardP1 = view.findViewById(R.id.cardPlayer1);
        View cardP2 = view.findViewById(R.id.cardPlayer2);

        int colorWinner = 0xFFFFD700; // Gold/Yellow
        int colorLoser = 0xFFF0EDD0;  // Cream/Beige

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
            // Fallback
            crownP1.setVisibility(View.INVISIBLE);
            crownP2.setVisibility(View.INVISIBLE);
            cardP1.setBackgroundColor(colorLoser);
            cardP2.setBackgroundColor(colorLoser);
        }

        // --- Player 1 card ---
        ((TextView) view.findViewById(R.id.textWinnerP1Name)).setText(player1Name);
        ((TextView) view.findViewById(R.id.textP1Score)).setText(String.valueOf(player1Score));
        ((TextView) view.findViewById(R.id.textP1Time)).setText(player1Time + "s");
        ((TextView) view.findViewById(R.id.textP1Combo)).setText(player1Combo + "x");

        // --- Player 2 card ---
        ((TextView) view.findViewById(R.id.textWinnerP2Name)).setText(player2Name);
        ((TextView) view.findViewById(R.id.textP2Score)).setText(String.valueOf(player2Score));
        ((TextView) view.findViewById(R.id.textP2Time)).setText(player2Time + "s");
        ((TextView) view.findViewById(R.id.textP2Combo)).setText(player2Combo + "x");

        // --- Save leaderboard records ---
        saveCombinedTwoPlayerRecord(player1Name, player1Score, player2Name, player2Score, difficulty);

        // --- Hamburger dropdown ---
        LinearLayout hamburgerButton = view.findViewById(R.id.hamburgerButton);
        LinearLayout dropdownMenu = view.findViewById(R.id.dropdownMenu);

        hamburgerButton.setOnClickListener(v -> {
            dropdownVisible = !dropdownVisible;
            dropdownMenu.setVisibility(dropdownVisible ? View.VISIBLE : View.GONE);
        });

        // Dismiss dropdown when tapping outside (tap on root)
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

    private void saveCombinedTwoPlayerRecord(String p1Name, int p1Score, String p2Name, int p2Score, String difficulty) {
        String combinedNames = p1Name + " vs " + p2Name;
        String combinedScores = p1Score + " - " + p2Score;
        int topScore = Math.max(p1Score, p2Score);

        LeaderboardRecord record = new LeaderboardRecord(
                0,
                combinedNames,
                GameConstants.MODE_TWO_PLAYER,
                0,
                topScore,
                0,
                0,
                difficulty,
                combinedScores, // Using winnerName field to store the score string "X - Y"
                DateTimeUtils.now()
        );
        BrainBloomDatabaseHelper.getInstance(requireContext()).saveLeaderboardRecord(record);
    }
}
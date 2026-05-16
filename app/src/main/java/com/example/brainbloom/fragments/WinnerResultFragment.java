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

        Player p1 = session.getPlayerOne();
        Player p2 = session.getPlayerTwo();
        String winner = session.getWinnerName();

        ((TextView) view.findViewById(R.id.textWinnerTitle)).setText(winner.equals("Tie") ? "MATCH TIE" : winner + " WON");
        ((TextView) view.findViewById(R.id.textWinnerStats)).setText(
                p1.getName() + "\nScore: " + p1.getScore() + "   Time: " + p1.getTotalTimeLeft() + "s   Combo: " + p1.getHighestCombo() + "x\n\n" +
                        p2.getName() + "\nScore: " + p2.getScore() + "   Time: " + p2.getTotalTimeLeft() + "s   Combo: " + p2.getHighestCombo() + "x"
        );

        saveTwoPlayerRecord(p1, winner);
        saveTwoPlayerRecord(p2, winner);

        view.findViewById(R.id.buttonWinnerPlayAgain).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_winnerResult_to_twoPlayerSetup);
        });

        view.findViewById(R.id.buttonWinnerMainMenu).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_winnerResult_to_mainMenu);
        });
    }

    private void saveTwoPlayerRecord(Player player, String winner) {
        LeaderboardRecord record = new LeaderboardRecord(
                0,
                player.getName(),
                GameConstants.MODE_TWO_PLAYER,
                0,
                player.getScore(),
                player.getTotalTimeLeft(),
                player.getHighestCombo(),
                session.getDifficulty(),
                winner,
                DateTimeUtils.now()
        );

        BrainBloomDatabaseHelper.getInstance(requireContext()).saveLeaderboardRecord(record);
    }
}

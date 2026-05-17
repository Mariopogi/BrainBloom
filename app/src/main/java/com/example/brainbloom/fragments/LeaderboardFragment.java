package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.adapters.LeaderboardTextAdapter;
import com.example.brainbloom.database.BrainBloomDatabaseHelper;
import com.example.brainbloom.game.GameConstants;
import com.example.brainbloom.utils.SoundManager;

public class LeaderboardFragment extends Fragment {

    private final LeaderboardTextAdapter adapter = new LeaderboardTextAdapter();
    private BrainBloomDatabaseHelper databaseHelper;
    private LinearLayout singleLayout;
    private LinearLayout twoPlayerLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        databaseHelper = BrainBloomDatabaseHelper.getInstance(requireContext());
        singleLayout = view.findViewById(R.id.layoutSingleLeaderboard);
        twoPlayerLayout = view.findViewById(R.id.layoutTwoPlayerLeaderboard);

        loadLeaderboards();

        view.findViewById(R.id.buttonLeaderboardBack).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_leaderboard_to_mainMenu);
        });

        view.findViewById(R.id.buttonResetSingle).setOnClickListener(v -> confirmReset(GameConstants.MODE_SINGLE_PLAYER));
        view.findViewById(R.id.buttonResetTwoPlayer).setOnClickListener(v -> confirmReset(GameConstants.MODE_TWO_PLAYER));
    }

    private void loadLeaderboards() {
        adapter.render(requireContext(), singleLayout, databaseHelper.getTopRecords(GameConstants.MODE_SINGLE_PLAYER, 3), GameConstants.MODE_SINGLE_PLAYER);
        adapter.render(requireContext(), twoPlayerLayout, databaseHelper.getTopRecords(GameConstants.MODE_TWO_PLAYER, 3), GameConstants.MODE_TWO_PLAYER);
    }

    private void confirmReset(String mode) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Reset " + mode + " records?")
                .setMessage("This will clear saved leaderboard records for this mode.")
                .setPositiveButton("Reset", (dialog, which) -> {
                    databaseHelper.clearLeaderboard(mode);
                    loadLeaderboards();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.game.GameSession;
import com.example.brainbloom.utils.SoundManager;

public class EnterNameFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enter_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editPlayerName = view.findViewById(R.id.editPlayerName);
        Button buttonProceed = view.findViewById(R.id.buttonProceed);
        Button buttonBack = view.findViewById(R.id.buttonBack);

        buttonProceed.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            String name = editPlayerName.getText().toString().trim();
            
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
            } else {
                GameSession.getInstance().setSinglePlayerName(name);
                NavHostFragment.findNavController(this).navigate(R.id.action_enterName_to_story);
            }
        });

        buttonBack.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).popBackStack();
        });
    }
}

package com.example.brainbloom.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.brainbloom.R;

public class PauseDialogFragment extends DialogFragment {

    public interface PauseActionListener {
        void onResumeQuiz();
        void onRestartQuiz();
        void onReturnToMainMenu();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pause, null);

        view.findViewById(R.id.buttonPauseResume).setOnClickListener(v -> {
            dismiss();
            if (getParentFragment() instanceof PauseActionListener) {
                ((PauseActionListener) getParentFragment()).onResumeQuiz();
            }
        });

        view.findViewById(R.id.buttonPauseRestart).setOnClickListener(v -> {
            dismiss();
            if (getParentFragment() instanceof PauseActionListener) {
                ((PauseActionListener) getParentFragment()).onRestartQuiz();
            }
        });

        view.findViewById(R.id.buttonPauseMainMenu).setOnClickListener(v -> {
            dismiss();
            if (getParentFragment() instanceof PauseActionListener) {
                ((PauseActionListener) getParentFragment()).onReturnToMainMenu();
            }
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }
}

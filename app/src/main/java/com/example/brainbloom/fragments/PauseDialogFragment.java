package com.example.brainbloom.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.brainbloom.R;
import com.example.brainbloom.utils.MusicManager;

public class PauseDialogFragment extends DialogFragment {

    public interface PauseActionListener {
        void onResumeQuiz();
        void onRestartQuiz();
        void onReturnToMainMenu();
    }

    private PauseActionListener getPauseActionListener() {
        Fragment parentFragment = getParentFragment();

        if (parentFragment instanceof PauseActionListener) {
            return (PauseActionListener) parentFragment;
        }

        if (requireActivity() instanceof PauseActionListener) {
            return (PauseActionListener) requireActivity();
        }

        return null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MusicManager.getInstance(requireContext()).pauseMusic();

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pause, null);
        dialog.setContentView(view);

        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        view.findViewById(R.id.buttonPauseResume).setOnClickListener(v -> {
            MusicManager.getInstance(requireContext()).resumeMusic();
            PauseActionListener listener = getPauseActionListener();
            dismiss();

            if (listener != null) {
                listener.onResumeQuiz();
            }
        });

        view.findViewById(R.id.buttonPauseRestart).setOnClickListener(v -> {
            PauseActionListener listener = getPauseActionListener();
            dismiss();

            if (listener != null) {
                listener.onRestartQuiz();
            }
        });

        view.findViewById(R.id.buttonPauseMainMenu).setOnClickListener(v -> {
            PauseActionListener listener = getPauseActionListener();
            dismiss();

            if (listener != null) {
                listener.onReturnToMainMenu();
            }
        });

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog == null) {
            return;
        }

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);
        window.setDimAmount(0.55f);

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        params.dimAmount = 0.55f;

        window.setAttributes(params);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
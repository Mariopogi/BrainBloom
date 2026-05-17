package com.example.brainbloom.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.game.GameSession;
import com.example.brainbloom.utils.SoundManager;

public class BookSelectionFragment extends Fragment {

    private final GameSession session = GameSession.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateBookImages(view);

        view.findViewById(R.id.buttonBookSelectionBack).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            showLeaveWarning();
        });

        setupBookButton(view, R.id.buttonBook1, 1);
        setupBookButton(view, R.id.buttonBook2, 2);
        setupBookButton(view, R.id.buttonBook3, 3);
        setupBookButton(view, R.id.buttonBook4, 4);
    }

    private void setupBookButton(View view, int buttonId, int bookNumber) {
        view.findViewById(buttonId).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);

            if (!session.isBookUnlocked(bookNumber)) {
                showLockedBookDialog();
                return;
            }

            session.setSelectedBookNumber(bookNumber);
            NavHostFragment.findNavController(this).navigate(R.id.action_bookSelection_to_bookDetails);
        });
    }

    private void updateBookImages(View view) {
        ImageButton book1 = view.findViewById(R.id.buttonBook1);
        ImageButton book2 = view.findViewById(R.id.buttonBook2);
        ImageButton book3 = view.findViewById(R.id.buttonBook3);
        ImageButton book4 = view.findViewById(R.id.buttonBook4);

        book1.setImageResource(R.drawable.ic_button_book1);
        book2.setImageResource(session.isBookUnlocked(2) ? R.drawable.ic_button_book2 : R.drawable.ic_book2_locked);
        book3.setImageResource(session.isBookUnlocked(3) ? R.drawable.ic_button_book3 : R.drawable.ic_book3_locked);
        book4.setImageResource(session.isBookUnlocked(4) ? R.drawable.ic_button_book4 : R.drawable.ic_book4_locked);
    }

    private void showLeaveWarning() {
        if (session.allBooksCompleted()) {
            NavHostFragment.findNavController(this).navigate(R.id.action_bookSelection_to_mainMenu);
            return;
        }

        showCustomDialog(
                "Are you sure you want to go back?",
                getString(R.string.warning_exit_progress),
                "GO BACK",
                true,
                () -> {
                    session.resetAdventure();
                    NavHostFragment.findNavController(this).navigate(R.id.action_bookSelection_to_mainMenu);
                }
        );
    }

    private void showLockedBookDialog() {
        showCustomDialog(
                "This book is still locked!",
                "Complete the previous book first to unlock this book.",
                "OK",
                false,
                null
        );
    }

    private void showCustomDialog(String title,
                                  String message,
                                  String actionText,
                                  boolean showCancelButton,
                                  @Nullable Runnable confirmAction) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_confirm_navigation, null);

        TextView txtConfirmTitle = dialogView.findViewById(R.id.txtConfirmTitle);
        TextView txtConfirmMessage = dialogView.findViewById(R.id.txtConfirmMessage);
        TextView btnConfirmAction = dialogView.findViewById(R.id.btnConfirmAction);
        TextView btnCancelConfirmNavigation = dialogView.findViewById(R.id.btnCancelConfirmNavigation);

        txtConfirmTitle.setText(title);
        txtConfirmMessage.setText(message);
        btnConfirmAction.setText(actionText);

        if (!showCancelButton) {
            ViewGroup buttonRow = (ViewGroup) btnConfirmAction.getParent();

            for (int i = 0; i < buttonRow.getChildCount(); i++) {
                View child = buttonRow.getChildAt(i);

                if (child != btnConfirmAction) {
                    child.setVisibility(View.GONE);
                }
            }
        }

        btnConfirmAction.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            dialog.dismiss();

            if (confirmAction != null) {
                confirmAction.run();
            }
        });

        btnCancelConfirmNavigation.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            dialog.dismiss();
        });

        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        dialog.setOnShowListener(dialogInterface -> {
            Window window = dialog.getWindow();

            if (window == null) {
                return;
            }

            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);

            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.0f;

            window.setAttributes(params);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        });

        dialog.show();
    }
}
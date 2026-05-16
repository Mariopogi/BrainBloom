package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

        view.findViewById(R.id.buttonBookSelectionBack).setOnClickListener(v -> showLeaveWarning());

        setupBookButton(view, R.id.buttonBook1, 1);
        setupBookButton(view, R.id.buttonBook2, 2);
        setupBookButton(view, R.id.buttonBook3, 3);
        setupBookButton(view, R.id.buttonBook4, 4);
    }

    private void setupBookButton(View view, int buttonId, int bookNumber) {
        view.findViewById(buttonId).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);

            if (!session.isBookUnlocked(bookNumber)) {
                Toast.makeText(requireContext(), "This book is still locked.", Toast.LENGTH_SHORT).show();
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

        book1.setImageResource(session.isBookCompleted(1) ? R.drawable.ic_completed_badge : R.drawable.ic_button_book1);
        book2.setImageResource(session.isBookUnlocked(2) ? R.drawable.ic_button_book2 : R.drawable.ic_book2_locked);
        book3.setImageResource(session.isBookUnlocked(3) ? R.drawable.ic_button_book3 : R.drawable.ic_book3_locked);
        book4.setImageResource(session.isBookUnlocked(4) ? R.drawable.ic_button_book4 : R.drawable.ic_book4_locked);
    }

    private void showLeaveWarning() {
        if (session.allBooksCompleted()) {
            NavHostFragment.findNavController(this).navigate(R.id.action_bookSelection_to_mainMenu);
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Are you sure you want to go back?")
                .setMessage(R.string.warning_exit_progress)
                .setPositiveButton("Go Back", (dialog, which) -> {
                    session.resetAdventure();
                    NavHostFragment.findNavController(this).navigate(R.id.action_bookSelection_to_mainMenu);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

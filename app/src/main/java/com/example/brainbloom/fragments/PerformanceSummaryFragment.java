package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.game.BookRepository;
import com.example.brainbloom.game.GameSession;
import com.example.brainbloom.models.Book;
import com.example.brainbloom.models.QuizResult;
import com.example.brainbloom.utils.MusicManager;
import com.example.brainbloom.utils.SoundManager;

public class PerformanceSummaryFragment extends Fragment {

    private final GameSession session = GameSession.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_performance_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MusicManager.getInstance(requireContext()).playMusic(R.raw.bg_music_result_screen, true);

        Book book = BookRepository.getBook(session.getSelectedBookNumber());
        QuizResult result = session.getCurrentBookResult();

        FrameLayout root = view.findViewById(R.id.performanceRoot);
        root.setBackgroundResource(book.getBackgroundDrawableRes());

        ((TextView) view.findViewById(R.id.textPerformanceTitle)).setText(book.getTitle() + " COMPLETED");
        ((ImageView) view.findViewById(R.id.imagePerformanceObject)).setImageResource(book.getObjectDrawableRes());

        if (result != null) {
            ((TextView) view.findViewById(R.id.textPerformanceStats)).setText(
                    "Time Left: " + result.getTimeLeft() + "s\n" +
                            "Correct Answers: " + result.getCorrectCount() + "\n" +
                            "Highest Combo: " + result.getHighestCombo() + "x\n" +
                            "Score: " + result.getScore()
            );
        }

        if (book.getBookNumber() == 4) {
            ((TextView) view.findViewById(R.id.buttonNextBook)).setText(R.string.book_completed);
        }

        view.findViewById(R.id.buttonNextBook).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            if (book.getBookNumber() == 4) {
                NavHostFragment.findNavController(this).navigate(R.id.action_performance_to_allBooksRestored);
            } else {
                session.setSelectedBookNumber(book.getBookNumber() + 1);
                NavHostFragment.findNavController(this).navigate(R.id.action_performance_to_bookSelection);
            }
        });

        view.findViewById(R.id.buttonPlayAgain).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            NavHostFragment.findNavController(this).navigate(R.id.action_performance_to_quiz);
        });

        view.findViewById(R.id.buttonPerformanceBack).setOnClickListener(v -> showLeaveWarning());
    }

    private void showLeaveWarning() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Are you sure you want to go back?")
                .setMessage(R.string.warning_exit_progress)
                .setPositiveButton("Go Back", (dialog, which) ->
                        NavHostFragment.findNavController(this).navigate(R.id.action_performance_to_bookSelection))
                .setNegativeButton("Cancel", null)
                .show();
    }
}

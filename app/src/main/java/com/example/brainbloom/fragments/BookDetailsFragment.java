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
import com.example.brainbloom.utils.MusicManager;
import com.example.brainbloom.utils.SoundManager;

public class BookDetailsFragment extends Fragment {

    private final GameSession session = GameSession.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Book book = BookRepository.getBook(session.getSelectedBookNumber());

        FrameLayout root = view.findViewById(R.id.bookDetailsRoot);
        root.setBackgroundResource(book.getBackgroundDrawableRes());

        ((TextView) view.findViewById(R.id.textBookTitle)).setText(book.getTitle());
        ((TextView) view.findViewById(R.id.textBookCategory)).setText("CATEGORY: " + book.getCategory());
        ((TextView) view.findViewById(R.id.textBookGoal)).setText("GOAL: Restore the garden by answering questions.");
        ((TextView) view.findViewById(R.id.textBookQuestions)).setText("QUESTIONS: " + book.getQuestionCount());
        ((TextView) view.findViewById(R.id.textBookReward)).setText("REWARD: " + book.getReward());
        ((ImageView) view.findViewById(R.id.imageBookObject)).setImageResource(book.getObjectDrawableRes());

        view.findViewById(R.id.buttonStartBookQuiz).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            MusicManager.getInstance(requireContext()).playMusic(getQuizMusic(book.getBookNumber()), true);
            NavHostFragment.findNavController(this).navigate(R.id.action_bookDetails_to_quiz);
        });

        view.findViewById(R.id.buttonBookDetailsBack).setOnClickListener(v -> showBackWarning());
    }

    private int getQuizMusic(int bookNumber) {
        switch (bookNumber) {
            case 1:
                return R.raw.bg_music_quiz_screen_1;
            case 2:
                return R.raw.bg_music_quiz_screen_2;
            case 3:
                return R.raw.bg_music_quiz_screen_3;
            case 4:
            default:
                return R.raw.bg_music_quiz_screen_4;
        }
    }

    private void showBackWarning() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Are you sure you want to go back?")
                .setMessage(R.string.warning_exit_progress)
                .setPositiveButton("Go Back", (dialog, which) ->
                        NavHostFragment.findNavController(this).navigate(R.id.action_bookDetails_to_bookSelection))
                .setNegativeButton("Cancel", null)
                .show();
    }
}

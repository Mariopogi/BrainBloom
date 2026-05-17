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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        TextView textBookTitle = view.findViewById(R.id.textBookTitle);
        TextView textBookCategory = view.findViewById(R.id.textBookCategory);
        TextView textBookGoal = view.findViewById(R.id.textBookGoal);
        TextView textBookQuestions = view.findViewById(R.id.textBookQuestions);
        TextView textBookReward = view.findViewById(R.id.textBookReward);
        ImageView imageBookObject = view.findViewById(R.id.imageBookObject);

        textBookTitle.setText(book.getTitle());
        textBookCategory.setText("CATEGORY: " + book.getCategory());
        textBookGoal.setText("GOAL: Restore the garden by answering questions.");
        textBookQuestions.setText("QUESTIONS: " + book.getQuestionCount());
        textBookReward.setText("REWARD: " + book.getReward());
        imageBookObject.setImageResource(book.getObjectDrawableRes());

        view.findViewById(R.id.buttonStartBookQuiz).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            MusicManager.getInstance(requireContext()).playMusic(getQuizMusic(book.getBookNumber()), true);
            NavHostFragment.findNavController(this).navigate(R.id.action_bookDetails_to_quiz);
        });

        view.findViewById(R.id.buttonBookDetailsBack).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            showGoBackConfirmDialog();
        });
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

    private void showGoBackConfirmDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_confirm_navigation, null);

        TextView txtConfirmTitle = dialogView.findViewById(R.id.txtConfirmTitle);
        TextView txtConfirmMessage = dialogView.findViewById(R.id.txtConfirmMessage);
        TextView btnConfirmAction = dialogView.findViewById(R.id.btnConfirmAction);
        TextView btnCancelConfirmNavigation = dialogView.findViewById(R.id.btnCancelConfirmNavigation);

        txtConfirmTitle.setText("Are you sure you want to go back?");
        txtConfirmMessage.setText(getString(R.string.warning_exit_progress));
        btnConfirmAction.setText("GO BACK");

        btnConfirmAction.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            dialog.dismiss();
            NavHostFragment.findNavController(this).navigate(R.id.action_bookDetails_to_bookSelection);
        });

        btnCancelConfirmNavigation.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            dialog.dismiss();
        });

        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setOnShowListener(dialogInterface -> {
            Window shownWindow = dialog.getWindow();

            if (shownWindow == null) {
                return;
            }

            shownWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            shownWindow.setGravity(Gravity.CENTER);

            WindowManager.LayoutParams params = shownWindow.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.0f;

            shownWindow.setAttributes(params);
            shownWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        });

        dialog.show();
    }
}
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
        TextView textPerformanceTitle = view.findViewById(R.id.textPerformanceTitle);
        TextView buttonNextBook = view.findViewById(R.id.buttonNextBook);
        ImageView imagePerformanceObject = view.findViewById(R.id.imagePerformanceObject);

        root.setBackgroundResource(book.getBackgroundDrawableRes());
        textPerformanceTitle.setText(book.getTitle() + " COMPLETED!");
        imagePerformanceObject.setImageResource(book.getObjectDrawableRes());

        if (result != null) {
            ((TextView) view.findViewById(R.id.textTimeLeftValue)).setText(result.getTimeLeft() + "s");
            ((TextView) view.findViewById(R.id.textCorrectValue)).setText(String.valueOf(result.getCorrectCount()));
            ((TextView) view.findViewById(R.id.textComboValue)).setText(result.getHighestCombo() + "x");
            ((TextView) view.findViewById(R.id.textScoreValue)).setText(String.valueOf(result.getScore()));
        }

        if (book.getBookNumber() == 4) {
            buttonNextBook.setText(R.string.book_completed);
        } else {
            buttonNextBook.setText("NEXT BOOK");
        }

        buttonNextBook.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);

            if (book.getBookNumber() == 4) {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_performance_to_allBooksRestored);
            } else {
                session.setSelectedBookNumber(book.getBookNumber() + 1);
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_performance_to_bookSelection);
            }
        });

        view.findViewById(R.id.buttonPerformanceBack).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            showLeaveWarningDialog();
        });
    }

    private void showLeaveWarningDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_confirm_navigation, null);

        TextView txtConfirmTitle = dialogView.findViewById(R.id.txtConfirmTitle);
        TextView txtConfirmMessage = dialogView.findViewById(R.id.txtConfirmMessage);
        TextView btnConfirmAction = dialogView.findViewById(R.id.btnConfirmAction);
        TextView btnCancelConfirmNavigation = dialogView.findViewById(R.id.btnCancelConfirmNavigation);

        txtConfirmTitle.setText("Are you sure you want to go back?");
        txtConfirmMessage.setText(R.string.warning_exit_progress);
        btnConfirmAction.setText("GO BACK");

        btnConfirmAction.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playSound(R.raw.button_click);
            dialog.dismiss();

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_performance_to_bookSelection);
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
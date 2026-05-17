package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.database.BrainBloomDatabaseHelper;
import com.example.brainbloom.game.BookRepository;
import com.example.brainbloom.game.GameConstants;
import com.example.brainbloom.game.GameSession;
import com.example.brainbloom.game.ScoreCalculator;
import com.example.brainbloom.models.Book;
import com.example.brainbloom.models.Question;
import com.example.brainbloom.models.QuizResult;
import com.example.brainbloom.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment implements PauseDialogFragment.PauseActionListener {

    private final GameSession session = GameSession.getInstance();
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private List<Question> questions = new ArrayList<>();

    private int currentIndex = 0;
    private int score = 0;
    private int correctCount = 0;
    private int lives = GameConstants.STARTING_LIVES;
    private int currentCombo = 0;
    private int highestCombo = 0;
    private int totalTimeLeft = 0;
    private int timeLeft = GameConstants.QUESTION_TIME_SECONDS;

    private CountDownTimer timer;

    private FrameLayout quizRoot;
    private View currentFeedbackPopup;

    private TextView textQuizBook;
    private TextView textTimer;
    private TextView textCombo;
    private TextView textQuestionNumber;
    private TextView textQuestion;
    private TextView textProgress;

    private ImageView imageHearts;

    private Button buttonA;
    private Button buttonB;
    private Button buttonC;
    private Button buttonD;

    private boolean feedbackShowing = false;
    private boolean quizFinished = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindViews(view);

        Book book = BookRepository.getBook(session.getSelectedBookNumber());
        quizRoot.setBackgroundResource(book.getBackgroundDrawableRes());

        questions = BrainBloomDatabaseHelper.getInstance(requireContext())
                .getRandomQuestions(
                        book.getBookNumber(),
                        session.getDifficulty(),
                        GameConstants.QUESTION_COUNT_PER_BOOK
                );

        if (questions.isEmpty()) {
            session.setLastAttemptScore(0);
            NavHostFragment.findNavController(this).navigate(R.id.action_quiz_to_gameOver);
            return;
        }

        view.findViewById(R.id.buttonPauseQuiz).setOnClickListener(v -> pauseQuiz());

        showQuestion();
    }

    private void bindViews(View view) {
        quizRoot = view.findViewById(R.id.quizRoot);

        textQuizBook = view.findViewById(R.id.textQuizBook);
        textTimer = view.findViewById(R.id.textTimer);
        textCombo = view.findViewById(R.id.textCombo);
        textQuestionNumber = view.findViewById(R.id.textQuestionNumber);
        textQuestion = view.findViewById(R.id.textQuestion);
        textProgress = view.findViewById(R.id.textProgress);

        imageHearts = view.findViewById(R.id.imageHearts);

        buttonA = view.findViewById(R.id.buttonChoiceA);
        buttonB = view.findViewById(R.id.buttonChoiceB);
        buttonC = view.findViewById(R.id.buttonChoiceC);
        buttonD = view.findViewById(R.id.buttonChoiceD);
    }

    private void showQuestion() {
        if (quizFinished) {
            return;
        }

        if (currentIndex >= questions.size()) {
            finishQuiz();
            return;
        }

        removeFeedbackPopup();
        feedbackShowing = false;
        setAnswerButtonsEnabled(true);

        Book book = BookRepository.getBook(session.getSelectedBookNumber());
        Question question = questions.get(currentIndex);

        textQuizBook.setText("PLAYER: " + session.getSinglePlayerName().toUpperCase() + "\n" +
                "BOOK " + book.getBookNumber() + ": " + book.getArea());
        textQuestionNumber.setText("QUESTION " + (currentIndex + 1) + " OUT " + questions.size());
        textQuestion.setText(question.getQuestionText());

        buttonA.setText("A.   " + question.getChoiceA());
        buttonB.setText("B.   " + question.getChoiceB());
        buttonC.setText("C.   " + question.getChoiceC());
        buttonD.setText("D.   " + question.getChoiceD());

        buttonA.setOnClickListener(v -> answerQuestion("A"));
        buttonB.setOnClickListener(v -> answerQuestion("B"));
        buttonC.setOnClickListener(v -> answerQuestion("C"));
        buttonD.setOnClickListener(v -> answerQuestion("D"));

        updateProgress();
        updateHearts();
        startTimer(GameConstants.QUESTION_TIME_SECONDS);
    }

    private void startTimer(int seconds) {
        cancelTimer();

        if (quizFinished || feedbackShowing) {
            return;
        }

        timeLeft = Math.max(seconds, 1);
        textTimer.setText(timeLeft + "s");

        timer = new CountDownTimer(timeLeft * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = (int) (millisUntilFinished / 1000L);
                textTimer.setText(timeLeft + "s");
            }

            @Override
            public void onFinish() {
                timeLeft = 0;
                answerWrong(true);
            }
        };

        timer.start();
    }

    private void answerQuestion(String selectedAnswer) {
        if (feedbackShowing || quizFinished) {
            return;
        }

        cancelTimer();
        setAnswerButtonsEnabled(false);

        Question question = questions.get(currentIndex);

        if (question.isCorrect(selectedAnswer)) {
            answerCorrect();
        } else {
            answerWrong(false);
        }
    }

    private void answerCorrect() {
        if (feedbackShowing || quizFinished) {
            return;
        }

        feedbackShowing = true;

        SoundManager.getInstance(requireContext()).playSound(R.raw.correct_answer);

        correctCount++;
        currentCombo++;

        if (currentCombo > highestCombo) {
            highestCombo = currentCombo;
        }

        int points = scoreCalculator.calculateCorrectAnswerScore(timeLeft, currentCombo);
        score += points;
        totalTimeLeft += timeLeft;

        updateProgress();

        showFeedbackPopup(R.layout.popup_correct_answer, () -> {
            currentIndex++;
            showQuestion();
        });
    }

    private void answerWrong(boolean isTimeUp) {
        if (feedbackShowing || quizFinished) {
            return;
        }

        feedbackShowing = true;

        cancelTimer();
        setAnswerButtonsEnabled(false);

        SoundManager.getInstance(requireContext()).playSound(R.raw.wrong_answer);

        lives--;
        currentCombo = 0;

        updateHearts();
        updateProgress();

        int popupLayout = isTimeUp ? R.layout.popup_times_up : R.layout.popup_wrong_answer;

        if (lives <= 0) {
            session.setLastAttemptScore(score);

            showFeedbackPopup(popupLayout, () -> {
                quizFinished = true;
                NavHostFragment.findNavController(this).navigate(R.id.action_quiz_to_gameOver);
            });
        } else {
            showFeedbackPopup(popupLayout, () -> {
                currentIndex++;
                showQuestion();
            });
        }
    }

    private void showFeedbackPopup(int layoutResId, Runnable afterPopup) {
        removeFeedbackPopup();

        if (!isAdded() || getView() == null) {
            return;
        }

        currentFeedbackPopup = LayoutInflater.from(requireContext())
                .inflate(layoutResId, quizRoot, false);

        currentFeedbackPopup.setClickable(true);
        currentFeedbackPopup.setFocusable(true);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        quizRoot.addView(currentFeedbackPopup, params);

        handler.postDelayed(() -> {
            removeFeedbackPopup();
            feedbackShowing = false;

            if (isAdded() && getView() != null && !quizFinished) {
                afterPopup.run();
            } else if (isAdded() && getView() != null) {
                afterPopup.run();
            }
        }, 900);
    }

    private void removeFeedbackPopup() {
        if (currentFeedbackPopup != null && currentFeedbackPopup.getParent() == quizRoot) {
            quizRoot.removeView(currentFeedbackPopup);
        }

        currentFeedbackPopup = null;
    }

    private void finishQuiz() {
        if (quizFinished) {
            return;
        }

        quizFinished = true;
        cancelTimer();
        removeFeedbackPopup();

        boolean restored = correctCount >= GameConstants.RESTORE_TARGET;

        QuizResult result = new QuizResult(
                session.getSelectedBookNumber(),
                score,
                correctCount,
                totalTimeLeft,
                highestCombo,
                restored
        );

        session.setLastAttemptScore(score);

        if (restored) {
            session.saveTemporaryBookResult(result);
            SoundManager.getInstance(requireContext()).playSound(R.raw.book_complete);
            NavHostFragment.findNavController(this).navigate(R.id.action_quiz_to_performanceSummary);
        } else {
            SoundManager.getInstance(requireContext()).playSound(R.raw.game_over);
            NavHostFragment.findNavController(this).navigate(R.id.action_quiz_to_gameOver);
        }
    }

    private void updateProgress() {
        textCombo.setText("COMBO\n" + currentCombo + "x");

        textProgress.setText(
                "PROGRESS " + correctCount + "/" + GameConstants.QUESTION_COUNT_PER_BOOK
                        + "\nANSWER 7 CORRECTLY TO RESTORE THE BOOK"
        );
    }

    private void updateHearts() {
        if (lives >= 3) {
            imageHearts.setImageResource(R.drawable.ic_3_hp);
        } else if (lives == 2) {
            imageHearts.setImageResource(R.drawable.ic_2_hp);
        } else if (lives == 1) {
            imageHearts.setImageResource(R.drawable.ic_1_hp);
        } else {
            imageHearts.setImageResource(R.drawable.ic_no_hp);
        }
    }

    private void setAnswerButtonsEnabled(boolean enabled) {
        buttonA.setEnabled(enabled);
        buttonB.setEnabled(enabled);
        buttonC.setEnabled(enabled);
        buttonD.setEnabled(enabled);
    }

    private void pauseQuiz() {
        if (feedbackShowing || quizFinished) {
            return;
        }

        cancelTimer();

        PauseDialogFragment dialog = new PauseDialogFragment();
        dialog.show(getChildFragmentManager(), "pause_dialog");
    }

    @Override
    public void onResumeQuiz() {
        if (!quizFinished && !feedbackShowing) {
            startTimer(timeLeft > 0 ? timeLeft : GameConstants.QUESTION_TIME_SECONDS);
        }
    }

    @Override
    public void onRestartQuiz() {
        cancelTimer();
        handler.removeCallbacksAndMessages(null);
        removeFeedbackPopup();

        NavHostFragment.findNavController(this).navigate(R.id.action_quiz_to_quiz);
    }

    @Override
    public void onReturnToMainMenu() {
        cancelTimer();
        handler.removeCallbacksAndMessages(null);
        removeFeedbackPopup();

        session.resetAdventure();
        NavHostFragment.findNavController(this).navigate(R.id.action_quiz_to_mainMenu);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        cancelTimer();
        handler.removeCallbacksAndMessages(null);
        removeFeedbackPopup();

        currentFeedbackPopup = null;
        quizRoot = null;
    }
}
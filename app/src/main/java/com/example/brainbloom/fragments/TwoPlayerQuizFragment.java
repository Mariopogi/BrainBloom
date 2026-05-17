package com.example.brainbloom.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.brainbloom.R;
import com.example.brainbloom.database.BrainBloomDatabaseHelper;
import com.example.brainbloom.game.GameConstants;
import com.example.brainbloom.game.GameSession;
import com.example.brainbloom.game.ScoreCalculator;
import com.example.brainbloom.models.Player;
import com.example.brainbloom.models.Question;
import com.example.brainbloom.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class TwoPlayerQuizFragment extends Fragment implements PauseDialogFragment.PauseActionListener {

    private final GameSession session = GameSession.getInstance();
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private List<Question> questions = new ArrayList<>();

    private int currentIndex = 0;
    private int activePlayerNumber = 1;
    private int timeLeft = GameConstants.QUESTION_TIME_SECONDS;
    private int lives = GameConstants.STARTING_LIVES;

    private CountDownTimer timer;

    private boolean acceptingAnswers = true;
    private boolean feedbackShowing = false;
    private boolean quizFinished = false;

    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private String difficulty = "Easy";

    private ViewGroup quizRoot;
    private View currentFeedbackPopup;

    private TextView textQuizBook;
    private TextView textTimer;
    private TextView textCombo;
    private TextView textQuestionNumber;
    private TextView textQuestion;
    private TextView textProgress;
    private TextView textPlayer1Name;
    private TextView textPlayer2Name;
    private TextView textPlayer1Score;
    private TextView textPlayer2Score;

    private ImageView imageHearts;

    private Button buttonA;
    private Button buttonB;
    private Button buttonC;
    private Button buttonD;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two_player_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        quizRoot = (ViewGroup) view;

        if (getArguments() != null) {
            player1Name = getArguments().getString("PLAYER_1_NAME", "Player 1");
            player2Name = getArguments().getString("PLAYER_2_NAME", "Player 2");
            difficulty = getArguments().getString("DIFFICULTY", "Easy");
        } else {
            player1Name = session.getPlayerOne().getName();
            player2Name = session.getPlayerTwo().getName();
            difficulty = session.getDifficulty();
        }

        bindViews(view);

        textPlayer1Name.setText(player1Name);
        textPlayer2Name.setText(player2Name);
        textQuizBook.setText("DIFFICULTY: " + difficulty.toUpperCase());

        view.findViewById(R.id.buttonPauseQuiz).setOnClickListener(v -> pauseQuiz());

        loadQuestionsForActivePlayer();
        updatePlayerHighlight();
        updateScoreDisplays();
        showQuestion();
    }

    private void bindViews(View view) {
        textQuizBook = view.findViewById(R.id.textQuizBook);
        textTimer = view.findViewById(R.id.textTimer);
        textCombo = view.findViewById(R.id.textCombo);
        textQuestionNumber = view.findViewById(R.id.textQuestionNumber);
        textQuestion = view.findViewById(R.id.textQuestion);
        textProgress = view.findViewById(R.id.textProgress);

        textPlayer1Name = view.findViewById(R.id.textPlayer1Name);
        textPlayer2Name = view.findViewById(R.id.textPlayer2Name);
        textPlayer1Score = view.findViewById(R.id.textPlayer1Score);
        textPlayer2Score = view.findViewById(R.id.textPlayer2Score);

        imageHearts = view.findViewById(R.id.imageHearts);

        buttonA = view.findViewById(R.id.buttonChoiceA);
        buttonB = view.findViewById(R.id.buttonChoiceB);
        buttonC = view.findViewById(R.id.buttonChoiceC);
        buttonD = view.findViewById(R.id.buttonChoiceD);
    }

    private Player getActivePlayer() {
        return activePlayerNumber == 1 ? session.getPlayerOne() : session.getPlayerTwo();
    }

    private void loadQuestionsForActivePlayer() {
        lives = GameConstants.STARTING_LIVES;

        questions = BrainBloomDatabaseHelper.getInstance(requireContext())
                .getRandomQuestions(1, difficulty, GameConstants.QUESTION_COUNT_PER_BOOK);

        if (questions.isEmpty()) {
            for (int book = 2; book <= 4; book++) {
                questions = BrainBloomDatabaseHelper.getInstance(requireContext())
                        .getRandomQuestions(book, difficulty, GameConstants.QUESTION_COUNT_PER_BOOK);

                if (!questions.isEmpty()) {
                    break;
                }
            }
        }

        currentIndex = 0;
        updateHearts();
    }

    private void showQuestion() {
        if (quizFinished) {
            return;
        }

        if (currentIndex >= questions.size()) {
            finishActivePlayerTurn();
            return;
        }

        removeFeedbackPopup();
        feedbackShowing = false;
        acceptingAnswers = true;
        setAnswerButtonsEnabled(true);

        Question question = questions.get(currentIndex);

        textQuestionNumber.setText("QUESTION " + (currentIndex + 1) + " OUT " + questions.size());
        textQuestion.setText(question.getQuestionText());

        buttonA.setText("A  " + question.getChoiceA());
        buttonB.setText("B  " + question.getChoiceB());
        buttonC.setText("C  " + question.getChoiceC());
        buttonD.setText("D  " + question.getChoiceD());

        buttonA.setOnClickListener(v -> answerQuestion("A"));
        buttonB.setOnClickListener(v -> answerQuestion("B"));
        buttonC.setOnClickListener(v -> answerQuestion("C"));
        buttonD.setOnClickListener(v -> answerQuestion("D"));

        updateProgress();
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
        if (!acceptingAnswers || feedbackShowing || quizFinished) {
            return;
        }

        acceptingAnswers = false;
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

        Player player = getActivePlayer();
        player.addCorrectCombo();
        player.addTimeLeft(timeLeft);

        int points = scoreCalculator.calculateCorrectAnswerScore(timeLeft, player.getCurrentCombo());
        player.addScore(points);

        updateScoreDisplays();
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

        getActivePlayer().resetCombo();
        lives--;

        updateHearts();
        updateProgress();

        int popupLayout = isTimeUp ? R.layout.popup_times_up : R.layout.popup_wrong_answer;

        if (lives <= 0) {
            showFeedbackPopup(popupLayout, this::finishActivePlayerTurn);
        } else {
            showFeedbackPopup(popupLayout, () -> {
                currentIndex++;
                showQuestion();
            });
        }
    }

    private void showFeedbackPopup(int layoutResId, Runnable afterPopup) {
        removeFeedbackPopup();

        if (!isAdded() || getView() == null || quizRoot == null) {
            return;
        }

        currentFeedbackPopup = LayoutInflater.from(requireContext())
                .inflate(layoutResId, quizRoot, false);

        currentFeedbackPopup.setClickable(true);
        currentFeedbackPopup.setFocusable(true);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        quizRoot.addView(currentFeedbackPopup, params);

        handler.postDelayed(() -> {
            removeFeedbackPopup();
            feedbackShowing = false;

            if (isAdded() && getView() != null) {
                afterPopup.run();
            }
        }, 900);
    }

    private void removeFeedbackPopup() {
        if (currentFeedbackPopup != null && quizRoot != null && currentFeedbackPopup.getParent() == quizRoot) {
            quizRoot.removeView(currentFeedbackPopup);
        }

        currentFeedbackPopup = null;
    }

    private void finishActivePlayerTurn() {
        cancelTimer();
        removeFeedbackPopup();
        setAnswerButtonsEnabled(false);

        if (quizFinished) {
            return;
        }

        if (activePlayerNumber == 1) {
            activePlayerNumber = 2;
            lives = GameConstants.STARTING_LIVES; // Reset lives for player 2
            currentIndex = 0; // Reset index for player 2
            textProgress.setText(player2Name.toUpperCase() + "'S TURN!");

            loadQuestionsForActivePlayer();
            updatePlayerHighlight();
            updateHearts();
            updateProgress(); // Refresh combo display (Player 2 starts at 0)

            handler.postDelayed(this::showQuestion, 1000);
        } else {
            quizFinished = true;

            session.computeWinner();

            Player p1 = session.getPlayerOne();
            Player p2 = session.getPlayerTwo();

            Bundle bundle = new Bundle();
            bundle.putString("PLAYER_1_NAME", player1Name);
            bundle.putString("PLAYER_2_NAME", player2Name);
            bundle.putInt("PLAYER_1_SCORE", p1.getScore());
            bundle.putInt("PLAYER_2_SCORE", p2.getScore());
            bundle.putInt("PLAYER_1_TIME", p1.getTotalTimeLeft());
            bundle.putInt("PLAYER_2_TIME", p2.getTotalTimeLeft());
            bundle.putInt("PLAYER_1_COMBO", p1.getHighestCombo());
            bundle.putInt("PLAYER_2_COMBO", p2.getHighestCombo());
            bundle.putString("DIFFICULTY", difficulty);
            bundle.putString("WINNER", session.getWinnerName());

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_twoPlayerQuiz_to_winnerResult, bundle);
        }
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

    private void updatePlayerHighlight() {
        if (getView() == null) {
            return;
        }

        ViewGroup column = getView().findViewById(R.id.playerPanelColumn);

        if (column == null || column.getChildCount() < 2) {
            return;
        }

        View playerOneCard = column.getChildAt(0);
        View playerTwoCard = column.getChildAt(1);

        if (activePlayerNumber == 1) {
            playerOneCard.setAlpha(1.0f);
            playerTwoCard.setAlpha(0.45f);
        } else {
            playerOneCard.setAlpha(0.45f);
            playerTwoCard.setAlpha(1.0f);
        }
    }

    private void updateProgress() {
        Player player = getActivePlayer();

        if (textCombo != null) {
            textCombo.setText(player.getCurrentCombo() + "x");
        }

        textProgress.setText(
                "PLAYER " + activePlayerNumber
                        + "   QUESTION " + Math.min(currentIndex + 1, GameConstants.QUESTION_COUNT_PER_BOOK)
                        + "/" + GameConstants.QUESTION_COUNT_PER_BOOK
        );
    }

    private void updateScoreDisplays() {
        textPlayer1Score.setText(String.valueOf(session.getPlayerOne().getScore()));
        textPlayer2Score.setText(String.valueOf(session.getPlayerTwo().getScore()));
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
        dialog.show(getChildFragmentManager(), "two_player_pause_dialog");
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

        // Go back to the setup menu as requested
        NavHostFragment.findNavController(this).navigate(R.id.action_twoPlayerQuiz_to_twoPlayerSetup);
    }

    @Override
    public void onReturnToMainMenu() {
        cancelTimer();
        handler.removeCallbacksAndMessages(null);
        removeFeedbackPopup();

        NavHostFragment.findNavController(this).navigate(R.id.action_twoPlayerQuiz_to_mainMenu);
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
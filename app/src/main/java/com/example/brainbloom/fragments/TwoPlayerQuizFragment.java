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
import android.widget.Toast;

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
    private CountDownTimer timer;
    private boolean acceptingAnswers = true;

    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private String difficulty = "Easy";

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
        buttonA = view.findViewById(R.id.buttonChoiceA);
        buttonB = view.findViewById(R.id.buttonChoiceB);
        buttonC = view.findViewById(R.id.buttonChoiceC);
        buttonD = view.findViewById(R.id.buttonChoiceD);
    }

    private Player getActivePlayer() {
        return activePlayerNumber == 1 ? session.getPlayerOne() : session.getPlayerTwo();
    }

    private void loadQuestionsForActivePlayer() {
        // Use book 1 questions for two-player mode with selected difficulty
        questions = BrainBloomDatabaseHelper.getInstance(requireContext())
                .getRandomQuestions(1, difficulty, GameConstants.QUESTION_COUNT_PER_BOOK);

        // Fallback: try any book if book 1 returns nothing
        if (questions.isEmpty()) {
            for (int book = 2; book <= 4; book++) {
                questions = BrainBloomDatabaseHelper.getInstance(requireContext())
                        .getRandomQuestions(book, difficulty, GameConstants.QUESTION_COUNT_PER_BOOK);
                if (!questions.isEmpty()) break;
            }
        }

        currentIndex = 0;
    }

    private void showQuestion() {
        if (currentIndex >= questions.size()) {
            finishActivePlayerTurn();
            return;
        }

        acceptingAnswers = true;
        setAnswerButtonsEnabled(true);

        Player player = getActivePlayer();
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
        timeLeft = seconds;
        textTimer.setText(timeLeft + "s");

        timer = new CountDownTimer(seconds * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = (int) (millisUntilFinished / 1000L);
                textTimer.setText(timeLeft + "s");
            }

            @Override
            public void onFinish() {
                timeLeft = 0;
                answerWrong();
            }
        };
        timer.start();
    }

    private void answerQuestion(String selectedAnswer) {
        if (!acceptingAnswers) return;
        acceptingAnswers = false;
        cancelTimer();
        setAnswerButtonsEnabled(false);

        Question question = questions.get(currentIndex);
        if (question.isCorrect(selectedAnswer)) {
            answerCorrect();
        } else {
            answerWrong();
        }
    }

    private void answerCorrect() {
        SoundManager.getInstance(requireContext()).playSound(R.raw.correct_answer);

        Player player = getActivePlayer();
        player.addCorrectCombo();
        player.addTimeLeft(timeLeft);

        int points = scoreCalculator.calculateCorrectAnswerScore(timeLeft, player.getCurrentCombo());
        player.addScore(points);

        updateScoreDisplays();
        Toast.makeText(requireContext(), "Correct! +" + points, Toast.LENGTH_SHORT).show();
        nextQuestionDelayed();
    }

    private void answerWrong() {
        SoundManager.getInstance(requireContext()).playSound(R.raw.wrong_answer);
        getActivePlayer().resetCombo();
        Toast.makeText(requireContext(), "Wrong!", Toast.LENGTH_SHORT).show();
        nextQuestionDelayed();
    }

    private void nextQuestionDelayed() {
        updateProgress();
        handler.postDelayed(() -> {
            currentIndex++;
            showQuestion();
        }, 800);
    }

    private void finishActivePlayerTurn() {
        cancelTimer();

        if (activePlayerNumber == 1) {
            Toast.makeText(requireContext(), player2Name + "'s turn!", Toast.LENGTH_LONG).show();
            activePlayerNumber = 2;
            loadQuestionsForActivePlayer();
            handler.postDelayed(this::showQuestion, 1200);
        } else {
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

            NavHostFragment.findNavController(this).navigate(R.id.action_twoPlayerQuiz_to_winnerResult, bundle);
        }
    }

    private void updateProgress() {
        Player player = getActivePlayer();
        String playerLabel = activePlayerNumber == 1 ? player1Name : player2Name;
        textCombo.setText("COMBO\n" + player.getCurrentCombo() + "x");
        textProgress.setText(playerLabel.toUpperCase() + "'S TURN   Q" + (currentIndex + 1) + "/" + questions.size());
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
        cancelTimer();
        PauseDialogFragment dialog = new PauseDialogFragment();
        dialog.show(getChildFragmentManager(), "two_player_pause_dialog");
    }

    @Override
    public void onResumeQuiz() {
        startTimer(timeLeft > 0 ? timeLeft : GameConstants.QUESTION_TIME_SECONDS);
    }

    @Override
    public void onRestartQuiz() {
        activePlayerNumber = 1;
        session.resetTwoPlayer(player1Name, player2Name, difficulty);
        textPlayer1Score.setText("0");
        textPlayer2Score.setText("0");
        loadQuestionsForActivePlayer();
        showQuestion();
    }

    @Override
    public void onReturnToMainMenu() {
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
    }
}
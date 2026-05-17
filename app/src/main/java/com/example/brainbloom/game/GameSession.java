package com.example.brainbloom.game;

import com.example.brainbloom.models.Player;
import com.example.brainbloom.models.QuizResult;

import java.util.HashMap;
import java.util.Map;

public class GameSession {
    private static GameSession instance;

    private int selectedBookNumber = 1;
    private int completedBookCount = 0;
    private final Map<Integer, QuizResult> bookResults = new HashMap<>();

    private String singlePlayerName = "Player";
    private String difficulty = "Easy";

    private Player playerOne = new Player("Player 1");
    private Player playerTwo = new Player("Player 2");
    private String winnerName = "";

    private int lastAttemptScore = 0;
    private boolean finalSinglePlayerSaved = false;

    private GameSession() {
    }

    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }

    public void resetAdventure() {
        selectedBookNumber = 1;
        completedBookCount = 0;
        bookResults.clear();
        lastAttemptScore = 0;
        finalSinglePlayerSaved = false;
    }

    public void resetTwoPlayer(String playerOneName, String playerTwoName, String selectedDifficulty) {
        playerOne = new Player(playerOneName == null || playerOneName.trim().isEmpty() ? "Player 1" : playerOneName.trim());
        playerTwo = new Player(playerTwoName == null || playerTwoName.trim().isEmpty() ? "Player 2" : playerTwoName.trim());
        difficulty = selectedDifficulty;
        winnerName = "";
    }

    public int getSelectedBookNumber() {
        return selectedBookNumber;
    }

    public void setSelectedBookNumber(int selectedBookNumber) {
        this.selectedBookNumber = selectedBookNumber;
    }

    public boolean isBookUnlocked(int bookNumber) {
        return bookNumber == 1 || bookNumber <= completedBookCount + 1;
    }

    public boolean isBookCompleted(int bookNumber) {
        return bookResults.containsKey(bookNumber) && bookResults.get(bookNumber).isRestored();
    }

    public void saveTemporaryBookResult(QuizResult result) {
        bookResults.put(result.getBookNumber(), result);
        if (result.isRestored() && result.getBookNumber() >= completedBookCount) {
            completedBookCount = result.getBookNumber();
        }
    }

    public void resetBookScore(int bookNumber) {
        bookResults.remove(bookNumber);
    }

    public QuizResult getBookResult(int bookNumber) {
        return bookResults.get(bookNumber);
    }

    public QuizResult getCurrentBookResult() {
        return bookResults.get(selectedBookNumber);
    }

    public int getCompletedBookCount() {
        return completedBookCount;
    }

    public boolean allBooksCompleted() {
        return completedBookCount >= 4;
    }

    public int getOverallScore() {
        if (bookResults.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (QuizResult result : bookResults.values()) {
            total += result.getScore();
        }
        return total;
    }

    public int getTotalTimeLeft() {
        int total = 0;
        for (QuizResult result : bookResults.values()) {
            total += result.getTimeLeft();
        }
        return total;
    }

    public int getHighestCombo() {
        int highest = 0;
        for (QuizResult result : bookResults.values()) {
            if (result.getHighestCombo() > highest) {
                highest = result.getHighestCombo();
            }
        }
        return highest;
    }

    public String getSinglePlayerName() {
        return singlePlayerName;
    }

    public void setSinglePlayerName(String singlePlayerName) {
        this.singlePlayerName = singlePlayerName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getLastAttemptScore() {
        return lastAttemptScore;
    }

    public void setLastAttemptScore(int lastAttemptScore) {
        this.lastAttemptScore = lastAttemptScore;
    }

    public boolean isFinalSinglePlayerSaved() {
        return finalSinglePlayerSaved;
    }

    public void setFinalSinglePlayerSaved(boolean finalSinglePlayerSaved) {
        this.finalSinglePlayerSaved = finalSinglePlayerSaved;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void computeWinner() {
        if (playerOne.getScore() > playerTwo.getScore()) {
            winnerName = playerOne.getName();
            return;
        }
        if (playerTwo.getScore() > playerOne.getScore()) {
            winnerName = playerTwo.getName();
            return;
        }
        if (playerOne.getTotalTimeLeft() > playerTwo.getTotalTimeLeft()) {
            winnerName = playerOne.getName();
            return;
        }
        if (playerTwo.getTotalTimeLeft() > playerOne.getTotalTimeLeft()) {
            winnerName = playerTwo.getName();
            return;
        }
        if (playerOne.getHighestCombo() > playerTwo.getHighestCombo()) {
            winnerName = playerOne.getName();
            return;
        }
        if (playerTwo.getHighestCombo() > playerOne.getHighestCombo()) {
            winnerName = playerTwo.getName();
            return;
        }
        winnerName = "Tie";
    }
}

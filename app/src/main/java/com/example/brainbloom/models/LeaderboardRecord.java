package com.example.brainbloom.models;

public class LeaderboardRecord {
    private final int id;
    private final String playerName;
    private final String gameMode;
    private final int bookCompletedCount;
    private final int finalScore;
    private final int timeLeft;
    private final int highestCombo;
    private final String difficulty;
    private final String winnerName;
    private final String datePlayed;

    public LeaderboardRecord(int id, String playerName, String gameMode, int bookCompletedCount,
                             int finalScore, int timeLeft, int highestCombo, String difficulty,
                             String winnerName, String datePlayed) {
        this.id = id;
        this.playerName = playerName;
        this.gameMode = gameMode;
        this.bookCompletedCount = bookCompletedCount;
        this.finalScore = finalScore;
        this.timeLeft = timeLeft;
        this.highestCombo = highestCombo;
        this.difficulty = difficulty;
        this.winnerName = winnerName;
        this.datePlayed = datePlayed;
    }

    public int getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getGameMode() {
        return gameMode;
    }

    public int getBookCompletedCount() {
        return bookCompletedCount;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public int getHighestCombo() {
        return highestCombo;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getDatePlayed() {
        return datePlayed;
    }
}

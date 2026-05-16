package com.example.brainbloom.models;

public class QuizResult {
    private final int bookNumber;
    private final int score;
    private final int correctCount;
    private final int timeLeft;
    private final int highestCombo;
    private final boolean restored;

    public QuizResult(int bookNumber, int score, int correctCount, int timeLeft, int highestCombo, boolean restored) {
        this.bookNumber = bookNumber;
        this.score = score;
        this.correctCount = correctCount;
        this.timeLeft = timeLeft;
        this.highestCombo = highestCombo;
        this.restored = restored;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public int getScore() {
        return score;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public int getHighestCombo() {
        return highestCombo;
    }

    public boolean isRestored() {
        return restored;
    }
}

package com.example.brainbloom.models;

public class Player {
    private String name;
    private int score;
    private int totalTimeLeft;
    private int currentCombo;
    private int highestCombo;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.totalTimeLeft = 0;
        this.currentCombo = 0;
        this.highestCombo = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getTotalTimeLeft() {
        return totalTimeLeft;
    }

    public int getCurrentCombo() {
        return currentCombo;
    }

    public int getHighestCombo() {
        return highestCombo;
    }

    public void addScore(int points) {
        score += points;
    }

    public void addTimeLeft(int seconds) {
        totalTimeLeft += seconds;
    }

    public void addCorrectCombo() {
        currentCombo++;
        if (currentCombo > highestCombo) {
            highestCombo = currentCombo;
        }
    }

    public void resetCombo() {
        currentCombo = 0;
    }
}

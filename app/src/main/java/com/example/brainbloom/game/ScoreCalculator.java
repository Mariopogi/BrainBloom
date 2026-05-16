package com.example.brainbloom.game;

public class ScoreCalculator {

    public int calculateCorrectAnswerScore(int timeLeft, int currentCombo) {
        return GameConstants.CORRECT_BASE_POINTS
                + calculateFastAnswerBonus(timeLeft)
                + calculateComboBonus(currentCombo);
    }

    public int calculateFastAnswerBonus(int timeLeft) {
        if (timeLeft >= 25) {
            return 30;
        }
        if (timeLeft >= 20) {
            return 15;
        }
        return 0;
    }

    public int calculateComboBonus(int currentCombo) {
        if (currentCombo == 10) {
            return 200;
        }
        if (currentCombo == 6) {
            return 50;
        }
        if (currentCombo == 3) {
            return 20;
        }
        return 0;
    }
}

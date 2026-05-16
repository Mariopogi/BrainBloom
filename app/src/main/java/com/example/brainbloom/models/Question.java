package com.example.brainbloom.models;

public class Question {
    private final int id;
    private final int bookNumber;
    private final String category;
    private final String difficulty;
    private final String questionText;
    private final String choiceA;
    private final String choiceB;
    private final String choiceC;
    private final String choiceD;
    private final String correctAnswer;

    public Question(int id, int bookNumber, String category, String difficulty, String questionText,
                    String choiceA, String choiceB, String choiceC, String choiceD, String correctAnswer) {
        this.id = id;
        this.bookNumber = bookNumber;
        this.category = category;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.correctAnswer = correctAnswer;
    }

    public int getId() {
        return id;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public boolean isCorrect(String answerLetter) {
        return correctAnswer.equalsIgnoreCase(answerLetter);
    }
}

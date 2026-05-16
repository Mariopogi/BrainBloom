package com.example.brainbloom.models;

public class Book {
    private final int bookNumber;
    private final String title;
    private final String area;
    private final String category;
    private final int questionCount;
    private final String reward;
    private final int objectDrawableRes;
    private final int backgroundDrawableRes;

    public Book(int bookNumber, String title, String area, String category, int questionCount,
                String reward, int objectDrawableRes, int backgroundDrawableRes) {
        this.bookNumber = bookNumber;
        this.title = title;
        this.area = area;
        this.category = category;
        this.questionCount = questionCount;
        this.reward = reward;
        this.objectDrawableRes = objectDrawableRes;
        this.backgroundDrawableRes = backgroundDrawableRes;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getArea() {
        return area;
    }

    public String getCategory() {
        return category;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public String getReward() {
        return reward;
    }

    public int getObjectDrawableRes() {
        return objectDrawableRes;
    }

    public int getBackgroundDrawableRes() {
        return backgroundDrawableRes;
    }
}

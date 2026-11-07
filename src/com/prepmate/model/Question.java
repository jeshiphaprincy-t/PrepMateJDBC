package com.prepmate.model;

public class Question {

    private int questionId;
    private int topicId;
    private String questionText;
    private String difficulty;
    private String correctAnswer;
    private String explanation;

    public Question() {
    }

    public Question(int topicId, String questionText,
                    String difficulty, String correctAnswer,
                    String explanation) {

        this.topicId = topicId;
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
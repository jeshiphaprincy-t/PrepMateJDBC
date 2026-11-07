package com.prepmate.model;

public class AttemptAnswer {

    private int answerId;
    private int attemptId;
    private int questionId;
    private String userAnswer;
    private boolean isCorrect;
    private int selectedOptionId;

    public AttemptAnswer() {
    }

    public AttemptAnswer(int attemptId, int questionId,
                         String userAnswer, boolean isCorrect) {

        this.attemptId = attemptId;
        this.questionId = questionId;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
    
    public int getSelectedOptionId() {
        return selectedOptionId;
    }
    
    public void setSelectedOptionId(int selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
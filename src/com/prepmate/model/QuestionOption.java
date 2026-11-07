package com.prepmate.model;

public class QuestionOption {

    private int optionId;
    private int questionId;
    private String optionLabel;
    private String optionText;
    private boolean correct;

    public QuestionOption() {
    }

    public QuestionOption(int questionId, String optionLabel,
                          String optionText, boolean correct) {
        this.questionId = questionId;
        this.optionLabel = optionLabel;
        this.optionText = optionText;
        this.correct = correct;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
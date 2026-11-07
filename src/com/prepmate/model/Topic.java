package com.prepmate.model;

public class Topic {

    private int topicId;
    private int subjectId;
    private String topicName;

    public Topic() {
    }

    public Topic(int subjectId, String topicName) {
        this.subjectId = subjectId;
        this.topicName = topicName;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
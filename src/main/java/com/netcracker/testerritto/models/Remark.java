package com.netcracker.testerritto.models;

import java.math.BigInteger;
import java.util.Objects;

public class Remark {

    private BigInteger id;
    private String text;
    private BigInteger userSenderId;
    private BigInteger questionId;

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BigInteger getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public BigInteger getUserSenderId() {
        return userSenderId;
    }

    public void setUserSenderId(BigInteger userSenderId) {
        this.userSenderId = userSenderId;
    }

    public BigInteger getQuestionId() {
        return questionId;
    }

    public void setQuestionId(BigInteger questionId) {
        this.questionId = questionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Remark remark = (Remark) o;
        return id.equals(remark.id) &&
                text.equals(remark.text) &&
                userSenderId.equals(remark.userSenderId) &&
                questionId.equals(remark.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, userSenderId, questionId);
    }

    @Override
    public String toString() {
        return "Remark{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", userId=" + userSenderId +
                ", questionId=" + questionId +
                '}';
    }
}


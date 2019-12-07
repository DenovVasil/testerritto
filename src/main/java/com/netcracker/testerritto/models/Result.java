package com.netcracker.testerritto.models;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Result {

    private BigInteger id;
    private Date date;
    private int score;
    private String status;
    private BigInteger testId;
    private HashMap<Question, Reply> replies;
    private BigInteger userId;

    public Result() {
    }

    public Result(BigInteger id, Date date, int score, String status, BigInteger testId, HashMap<Question, Reply> replies, BigInteger userId) {
        this.id = id;
        this.date = date;
        this.score = score;
        this.status = status;
        this.testId = testId;
        this.replies = replies;
        this.userId = userId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigInteger getTestId() {
        return testId;
    }

    public void setTestId(BigInteger testId) {
        this.testId = testId;
    }

    public HashMap<Question, Reply> getReplies() {
        return replies;
    }

    public void setReplies(HashMap<Question, Reply> replies) {
        this.replies = replies;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return id.equals(result.id) &&
            date.equals(result.date) &&
            status.equals(result.status) &&
            testId.equals(result.testId) &&
            Objects.equals(replies, result.replies) &&
            userId.equals(result.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, status, testId, replies, userId);
    }

    @Override
    public String toString() {
        return "Result{" +
            "id=" + id +
            ", date=" + date +
            ", status=" + status +
            ", testId=" + testId +
            ", replies=" + replies +
            ", userId=" + userId +
            '}';
    }
}

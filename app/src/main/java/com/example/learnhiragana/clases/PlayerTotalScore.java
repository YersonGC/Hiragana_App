// src/main/java/com/example/learnhiragana/models/PlayerTotalScore.java
package com.example.learnhiragana.models;

public class PlayerTotalScore {
    private String email;
    private int totalScore;

    public PlayerTotalScore(String email, int totalScore) {
        this.email = email;
        this.totalScore = totalScore;
    }

    public String getEmail() {
        return email;
    }

    public int getTotalScore() {
        return totalScore;
    }
}

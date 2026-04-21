package FairplayLeagueG18.model;

import java.time.LocalDateTime;

public class LeagueMember {
    private int userId;
    private int leagueId;
    private String username;
    private int totalScore;
    private LocalDateTime joinedAt;

    public LeagueMember() {
    }

    // Skapa ny medlem
    public LeagueMember(int userId, int leagueId) {
        this.userId = userId;
        this.leagueId = leagueId;
        this.totalScore = 0;
        this.joinedAt = LocalDateTime.now();
    }

    // Läsa från databasen
    public LeagueMember(int userId, int leagueId, String username, int totalScore, LocalDateTime joinedAt) {
        this.userId = userId;
        this.leagueId = leagueId;
        this.username = username;
        this.totalScore = totalScore;
        this.joinedAt = joinedAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getLeagueId() { return leagueId; }
    public void setLeagueId(int leagueId) { this.leagueId = leagueId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
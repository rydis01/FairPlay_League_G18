package FairplayLeagueG18.model;

import java.time.LocalDateTime;

/**
 * Representerar en användares medlemskap i en specifik liga.
 * Innehåller information om användarens aktuella poäng och när de gick med i ligan.
 * @author Gustav Johnsson
 */
public class LeagueMember {
    private int userId;
    private int leagueId;
    private String username;
    private int totalScore;
    private LocalDateTime joinedAt;

    /**
     * Standardkonstruktor som skapar ett tomt medlemskap.
     */
    public LeagueMember() {
    }

    /**
     * Skapar ett nytt medlemskap. Används när en användare går med i en liga.
     * Startpoängen sätts automatiskt till 0 och tidpunkten till aktuell tid.
     *
     * @param userId   ID för användaren som går med
     * @param leagueId ID för ligan användaren går med i
     */
    public LeagueMember(int userId, int leagueId) {
        this.userId = userId;
        this.leagueId = leagueId;
        this.totalScore = 0;
        this.joinedAt = LocalDateTime.now();
    }

    /**
     * Skapar ett befintligt medlemskap. Används främst vid inläsning från databasen.
     *
     * @param userId     ID för användaren
     * @param leagueId   ID för ligan
     * @param username   användarens användarnamn (ofta medskickat för enklare visning i leaderboards)
     * @param totalScore användarens totala poäng i denna specifika liga
     * @param joinedAt   tidpunkten då användaren gick med
     */
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
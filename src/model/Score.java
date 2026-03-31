package model;

public class Score {
    private int id;
    private int userId;
    private int leagueId;
    private int roundId;
    private int correctPicksCount;
    private int pointsEarned;

    public Score() {
    }

    // Skapa ny poäng
    public Score(int userId, int leagueId, int roundId, int correctPicksCount, int pointsEarned) {
        this.userId = userId;
        this.leagueId = leagueId;
        this.roundId = roundId;
        this.correctPicksCount = correctPicksCount;
        this.pointsEarned = pointsEarned;
    }

    // Läsa från databasen
    public Score(int id, int userId, int leagueId, int roundId, int correctPicksCount, int pointsEarned) {
        this.id = id;
        this.userId = userId;
        this.leagueId = leagueId;
        this.roundId = roundId;
        this.correctPicksCount = correctPicksCount;
        this.pointsEarned = pointsEarned;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public int getLeagueId() { return leagueId; }

    public void setLeagueId(int leagueId) { this.leagueId = leagueId; }

    public int getRoundId() { return roundId; }

    public void setRoundId(int roundId) { this.roundId = roundId; }

    public int getCorrectPicksCount() { return correctPicksCount; }

    public void setCorrectPicksCount(int correctPicksCount) { this.correctPicksCount = correctPicksCount; }

    public int getPointsEarned() { return pointsEarned; }

    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }
}

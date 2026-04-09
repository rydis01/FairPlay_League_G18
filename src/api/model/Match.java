package api.model;

public class Match {
    private String homeTeam;
    private String awayTeam;
    private String homeScore;
    private String awayScore;
    private String matchStatus; // T.ex. "FT" (Full Time) eller "NS" (Not Started)
    private String matchTime;   // T.ex. "20260404150000"

    // --- Konstruktor ---
    public Match(String homeTeam, String awayTeam, String homeScore, String awayScore, String matchStatus, String matchTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.matchStatus = matchStatus;
        this.matchTime = matchTime;
    }

    // --- Getters (så att vi kan läsa datan senare) ---
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public String getHomeScore() { return homeScore; }
    public String getAwayScore() { return awayScore; }
    public String getMatchStatus() { return matchStatus; }
    public String getMatchTime() { return matchTime; }

    // --- toString (för att snyggt kunna skriva ut matchen i konsolen) ---
    @Override
    public String toString() {
        return homeTeam + " " + homeScore + " - " + awayScore + " " + awayTeam + " (" + matchStatus + ")";
    }
}
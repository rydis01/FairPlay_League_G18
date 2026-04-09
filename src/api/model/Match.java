package api.model;

public class Match {
    private String homeTeam;
    private String awayTeam;
    private String homeScore;
    private String awayScore;
    private String matchStatus;
    private String matchTime;

    public Match(String homeTeam, String awayTeam, String homeScore, String awayScore, String matchStatus, String matchTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.matchStatus = matchStatus;
        this.matchTime = matchTime;
    }

    // Getters behövs för att Gson ska kunna läsa av objekten och göra JSON av dem
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public String getHomeScore() { return homeScore; }
    public String getAwayScore() { return awayScore; }
    public String getMatchStatus() { return matchStatus; }
    public String getMatchTime() { return matchTime; }
}
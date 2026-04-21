package model; // Viktigt att denna nu är 'model' och inte 'api.model'

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

    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public String getHomeScore() { return homeScore; }
    public String getAwayScore() { return awayScore; }
    public String getMatchStatus() { return matchStatus; }
    public String getMatchTime() { return matchTime; }

    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public void setHomeScore(String homeScore) { this.homeScore = homeScore; }
    public void setAwayScore(String awayScore) { this.awayScore = awayScore; }
    public void setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }
    public void setMatchTime(String matchTime) { this.matchTime = matchTime; }
}
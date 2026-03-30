package model;

public class Match {
    private final int matchNumber;
    private final String homeTeam;
    private final String awayTeam;
    private String result;

    public Match(int matchNumber, String homeTeam, String awayTeam) {
        this.matchNumber = matchNumber;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public void setResult(String result) {
        this.result = result; // Kan exemepelvis vara 1X2
    }

    public int getMatchNumber() { return matchNumber; }

    public String getHomeTeam() { return homeTeam; }

    public String getAwayTeam() { return awayTeam; }

    public String getResult() { return result; }
}


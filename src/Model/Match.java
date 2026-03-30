package model;

import java.time.LocalDateTime;

public class Match {
    private Long id;
    private Long roundId;
    private String externalMatchId;
    private int matchNumber;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime kickOff;
    private String result;

    public Match() {
    }

    // Skapa ny match (hämtad från TheSportsDB)
    public Match(Long roundId, String externalMatchId, int matchNumber, String homeTeam, String awayTeam, LocalDateTime kickOff) {
        this.roundId = roundId;
        this.externalMatchId = externalMatchId;
        this.matchNumber = matchNumber;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.kickOff = kickOff;
        this.result = null;
    }

    // Läsa från databasen
    public Match(Long id, Long roundId, String externalMatchId, int matchNumber, String homeTeam, String awayTeam, LocalDateTime kickOff, String result) {
        this.id = id;
        this.roundId = roundId;
        this.externalMatchId = externalMatchId;
        this.matchNumber = matchNumber;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.kickOff = kickOff;
        this.result = result;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getRoundId() { return roundId; }

    public void setRoundId(Long roundId) { this.roundId = roundId; }

    public String getExternalMatchId() { return externalMatchId; }

    public void setExternalMatchId(String externalMatchId) { this.externalMatchId = externalMatchId; }

    public int getMatchNumber() { return matchNumber; }

    public void setMatchNumber(int matchNumber) { this.matchNumber = matchNumber; }

    public String getHomeTeam() { return homeTeam; }

    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

    public String getAwayTeam() { return awayTeam; }

    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

    public LocalDateTime getKickOff() { return kickOff; }

    public void setKickOff(LocalDateTime kickOff) { this.kickOff = kickOff; }

    public String getResult() { return result; }

    public void setResult(String result) { this.result = result; }
}
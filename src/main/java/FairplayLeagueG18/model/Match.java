package FairplayLeagueG18.model;

import java.time.LocalDateTime;

public class Match {

    // Databasfält
    private int id;
    private int roundId;
    private String externalMatchId;
    private int matchNumber;
    private LocalDateTime kickOff;
    private String result;

    // Gemensamma fält
    private String homeTeam;
    private String awayTeam;

    // Fält från API-skrapning
    private String homeScore;
    private String awayScore;
    private String matchStatus;
    private String matchTime;

    // 1. Tom konstruktor
    public Match() {
    }

    // 2. Konstruktor för API-skrapare
    public Match(String homeTeam, String awayTeam, String homeScore, String awayScore, String matchStatus, String matchTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.matchStatus = matchStatus;
        this.matchTime = matchTime;
    }

    // 3. Konstruktor: Skapa ny match
    public Match(int roundId, String externalMatchId, int matchNumber, String homeTeam, String awayTeam, LocalDateTime kickOff) {
        this.roundId = roundId;
        this.externalMatchId = externalMatchId;
        this.matchNumber = matchNumber;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.kickOff = kickOff;
    }

    // 4. Konstruktor: Läsa från databasen
    public Match(int id, int roundId, String externalMatchId, int matchNumber, String homeTeam, String awayTeam, LocalDateTime kickOff, String result) {
        this.id = id;
        this.roundId = roundId;
        this.externalMatchId = externalMatchId;
        this.matchNumber = matchNumber;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.kickOff = kickOff;
        this.result = result;
    }

    // getters and setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoundId() { return roundId; }
    public void setRoundId(int roundId) { this.roundId = roundId; }

    public String getExternalMatchId() { return externalMatchId; }
    public void setExternalMatchId(String externalMatchId) { this.externalMatchId = externalMatchId; }

    public int getMatchNumber() { return matchNumber; }
    public void setMatchNumber(int matchNumber) { this.matchNumber = matchNumber; }

    public LocalDateTime getKickOff() { return kickOff; }
    public void setKickOff(LocalDateTime kickOff) { this.kickOff = kickOff; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

    public String getHomeScore() { return homeScore; }
    public void setHomeScore(String homeScore) { this.homeScore = homeScore; }

    public String getAwayScore() { return awayScore; }
    public void setAwayScore(String awayScore) { this.awayScore = awayScore; }

    public String getMatchStatus() { return matchStatus; }
    public void setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }

    public String getMatchTime() { return matchTime; }
    public void setMatchTime(String matchTime) { this.matchTime = matchTime; }
}
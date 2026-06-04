package FairplayLeagueG18.model;

import java.time.LocalDateTime;

/**
 * Representerar en fotbollsmatch i systemet.
 * Klassen används både för att hantera matchdata från databasen och
 * för att mappa live-data (skrapad via API) från LiveScore.
 * @author Theo Andersson, Carl Rydengård & Gustav Johnsson
 */
public class Match {

    private int id;
    private int gameweekId;
    private String externalMatchId;
    private int matchNumber;
    private LocalDateTime kickOff;
    private String result;

    private String homeTeam;
    private String awayTeam;

    private String homeScore;
    private String awayScore;
    private String matchStatus;
    private String matchTime;

    /**
     * Standardkonstruktor som skapar en tom match.
     */
    public Match() {
    }

    /**
     * Skapar ett match-objekt utifrån data hämtad från LiveScore API.
     * Används främst av API-skraparen för att mappa aktuell matchstatus.
     *
     * @param homeTeam    hemmalagets namn
     * @param awayTeam    bortalagets namn
     * @param homeScore   hemmalagets aktuella målskörd
     * @param awayScore   bortalagets aktuella målskörd
     * @param matchStatus status för matchen (t.ex. "NS" för Not Started, "FT" för Full Time)
     * @param matchTime   matchtid hämtad från API:et
     */
    public Match(String homeTeam, String awayTeam, String homeScore, String awayScore, String matchStatus, String matchTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.matchStatus = matchStatus;
        this.matchTime = matchTime;
    }

    /**
     * Skapar en ny match för att sparas i databasen.
     *
     * @param roundId         ID för omgången (gameweek) matchen tillhör
     * @param externalMatchId match-ID från det externa API:et
     * @param matchNumber     matchens nummer i kupongen (oftast 1-8)
     * @param homeTeam        hemmalagets namn
     * @param awayTeam        bortalagets namn
     * @param kickOff         tidpunkt för avspark
     */
    public Match(int roundId, String externalMatchId, int matchNumber, String homeTeam, String awayTeam, LocalDateTime kickOff) {
        this.gameweekId = roundId;
        this.externalMatchId = externalMatchId;
        this.matchNumber = matchNumber;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.kickOff = kickOff;
    }

    /**
     * Skapar ett befintligt match-objekt. Används vid inläsning från databasen.
     *
     * @param id              matchens unika ID i systemets databas
     * @param roundId         ID för omgången matchen tillhör
     * @param externalMatchId match-ID från det externa API:et
     * @param matchNumber     matchens nummer i kupongen (oftast 1-8)
     * @param homeTeam        hemmalagets namn
     * @param awayTeam        bortalagets namn
     * @param kickOff         tidpunkt för avspark
     * @param result          matchens slutgiltiga resultattecken ("1", "X", eller "2")
     */
    public Match(int id, int roundId, String externalMatchId, int matchNumber, String homeTeam, String awayTeam, LocalDateTime kickOff, String result) {
        this.id = id;
        this.gameweekId = roundId;
        this.externalMatchId = externalMatchId;
        this.matchNumber = matchNumber;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.kickOff = kickOff;
        this.result = result;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGameweekId() { return gameweekId; }
    public void setGameweekId(int gameweekId) { this.gameweekId = gameweekId; }

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
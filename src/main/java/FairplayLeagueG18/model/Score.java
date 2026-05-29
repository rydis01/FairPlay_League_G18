package FairplayLeagueG18.model;

/**
 * Representerar en användares poäng i en specifik liga för en specifik omgång.
 * Innehåller information om antalet rätta tips och hur många poäng det genererade.
 */
public class Score {
    private int id;
    private int userId;
    private int leagueId;
    private int roundId;
    private int correctPicksCount;
    private int pointsEarned;

    /**
     * Standardkonstruktor som skapar ett tomt poängobjekt.
     */
    public Score() {
    }

    /**
     * Skapar en ny poängpost. Används när en omgång rättas och poäng delas ut.
     *
     * @param userId            ID för användaren som poängen tillhör
     * @param leagueId          ID för ligan där poängen delats ut
     * @param roundId           ID för omgången poängen gäller
     * @param correctPicksCount antal rätta tips användaren hade på sin kupong
     * @param pointsEarned      antal intjänade poäng baserat på utdelningsmallen
     */
    public Score(int userId, int leagueId, int roundId, int correctPicksCount, int pointsEarned) {
        this.userId = userId;
        this.leagueId = leagueId;
        this.roundId = roundId;
        this.correctPicksCount = correctPicksCount;
        this.pointsEarned = pointsEarned;
    }

    /**
     * Skapar ett befintligt poängobjekt. Används främst vid inläsning från databasen.
     *
     * @param id                poängpostens unika ID i databasen
     * @param userId            ID för användaren
     * @param leagueId          ID för ligan
     * @param roundId           ID för omgången
     * @param correctPicksCount antal rätta tips
     * @param pointsEarned      antal intjänade poäng
     */
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
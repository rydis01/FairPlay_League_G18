package FairplayLeagueG18.model;

/**
 * Representerar ett specifikt tips (gissning) för en enskild match i systemet.
 */
public class Pick {
    private int id;
    private int userId;
    private int matchId;
    private String guess;

    /**
     * Standardkonstruktor som skapar ett tomt tips.
     */
    public Pick() {
    }

    /**
     * Skapar ett nytt tips. Används när en användare registrerar en ny gissning för en match.
     *
     * @param userId  ID för användaren som gör tipset
     * @param matchId ID för matchen som tipset gäller
     * @param guess   tipstecknet ("1" för hemmavinst, "X" för oavgjort, "2" för bortavinst)
     */
    public Pick(int userId, int matchId, String guess) {
        this.userId = userId;
        this.matchId = matchId;
        this.guess = guess;
    }

    /**
     * Skapar ett befintligt tips. Används främst vid inläsning från databasen.
     *
     * @param id      tipsets unika ID i databasen
     * @param userId  ID för användaren som gjort tipset
     * @param matchId ID för matchen tipset gäller
     * @param guess   tipstecknet ("1" för hemmavinst, "X" för oavgjort, "2" för bortavinst)
     */
    public Pick(int id, int userId, int matchId, String guess) {
        this.id = id;
        this.userId = userId;
        this.matchId = matchId;
        this.guess = guess;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public String getGuess() { return guess; }
    public void setGuess(String guess) { this.guess = guess; }
}
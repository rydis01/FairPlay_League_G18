package FairplayLeagueG18.model;

public class Pick {
    private int id;
    private int userId;
    private int matchId;
    private String guess; // '1' = hemmavinst, 'X' = oavgjort, '2' = bortavinst

    public Pick() {
    }

    // Skapa nytt tips
    public Pick(int userId, int matchId, String guess) {
        this.userId = userId;
        this.matchId = matchId;
        this.guess = guess;
    }

    // Läsa från databasen
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

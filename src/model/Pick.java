package model;

/**
 * En kupong består av ett antal picks (oftast 8)
 *      En coupon kommer alltså vara ett objekt av en lista med antal picks
 * Via denna klassen gör det enkelt för en coupon-klass att hämta picks till en lista
 */
public class Pick {
    private int matchID;
    private String guess;

    public Pick(int matchID, String guess) {
        this.matchID = matchID;
        this.guess = guess;
    }

    public int getMatchID() {
        return matchID;
    }

    public String getGuess() {
        return guess;
    }
}

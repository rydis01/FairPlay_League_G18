package FairplayLeagueG18.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Representerar en användares inlämnade kupong (tips) för en specifik omgång och liga.
 * Innehåller användarens gissningar per match samt resultat efter att omgången rättats.
 */
public class Coupon {
    private int id;
    private int userId;
    private int roundId;
    private int leagueId;
    private Map<Integer, String> tips;
    private int correctCount;
    private boolean graded = false;

    /**
     * Standardkonstruktor som skapar en tom kupong.
     */
    public Coupon() {
        this.tips = new HashMap<>();
    }

    /**
     * Skapar en ny kupong. Används när en användare lämnar in nya tips för en omgång.
     *
     * @param userId   ID för användaren som skapar kupongen
     * @param roundId  ID för omgången (gameweek) kupongen gäller
     * @param leagueId ID för ligan kupongen tillhör
     */
    public Coupon(int userId, int roundId, int leagueId) {
        this.userId = userId;
        this.roundId = roundId;
        this.leagueId = leagueId;
        this.tips = new HashMap<>();
        this.correctCount = 0;
    }

    /**
     * Skapar en befintlig kupong. Används främst vid inläsning från databasen.
     *
     * @param id           kupongens unika ID i databasen
     * @param userId       ID för användaren som äger kupongen
     * @param roundId      ID för omgången kupongen gäller
     * @param leagueId     ID för ligan kupongen tillhör
     * @param tips         en map innehållande match-ID som nyckel och tipstecken som värde
     * @param correctCount antal rätta tips på kupongen
     */
    public Coupon(int id, int userId, int roundId, int leagueId, Map<Integer, String> tips, int correctCount) {
        this.id = id;
        this.userId = userId;
        this.roundId = roundId;
        this.leagueId = leagueId;
        this.tips = tips;
        this.correctCount = correctCount;
    }

    /**
     * Lägger till eller uppdaterar ett tips för en specifik match.
     *
     * @param matchId ID för matchen tipset gäller
     * @param tip     tipstecknet (t.ex. "1", "X", eller "2")
     */
    public void setTip(int matchId, String tip) {
        tips.put(matchId, tip);
    }

    /**
     * Hämtar tipset för en specifik match.
     *
     * @param matchId ID för matchen
     * @return tipstecknet för matchen, eller null om inget tips finns
     */
    public String getTip(int matchId) {
        return tips.get(matchId);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRoundId() { return roundId; }
    public void setRoundId(int roundId) { this.roundId = roundId; }

    public Map<Integer, String> getTips() { return tips; }
    public void setTips(Map<Integer, String> tips) { this.tips = tips; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public boolean getGraded() { return graded; }
    public void setGraded(boolean graded) { this.graded = graded; }

    public int getLeagueId() { return leagueId; }
    public void setLeagueId(int leagueId) { this.leagueId = leagueId; }
}
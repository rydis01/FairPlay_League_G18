package FairplayLeagueG18.model;

import java.util.HashMap;
import java.util.Map;

public class Coupon {
    private int id;
    private int userId;
    private int roundId;
    private int leagueId;
    private Map<Integer, String> tips;
    private int correctCount;
    private boolean graded = false;


    public Coupon() {
        this.tips = new HashMap<>();
    }

    //Skapa ny kupong (en användares tips för en omgång)
    public Coupon(int userId, int roundId, int leaugeId) {
        this.userId = userId;
        this.roundId = roundId;
        this.leagueId = leagueId;
        this.tips = new HashMap<>();
        this.correctCount = 0;
    }

    //läsafrån databasen
    public Coupon(int id, int userId, int roundId, int leagueId, Map<Integer, String> tips, int correctCount) {
        this.id = id;
        this.userId = userId;
        this.roundId = roundId;
        this.leagueId = leagueId;
        this.tips = tips;
        this.correctCount = correctCount;

    }

    public void setTip(int matchNumber, String tip) {
        tips.put(matchNumber, tip);
    }

    public String getTip(int matchNumber) {
        return tips.get(matchNumber);
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
}
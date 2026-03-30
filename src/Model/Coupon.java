package model;

import java.util.HashMap;
import java.util.Map;

public class Coupon {
    private Long id;
    private Long userId;
    private Long roundId;
    private Map<Integer, String> tips;
    private int correctCount;

    public Coupon() {
        this.tips = new HashMap<>();
    }

    //Skapa ny kupong (en användares tips för en omgång)
    public Coupon(Long userId, Long roundId) {
        this.userId = userId;
        this.roundId = roundId;
        this.tips = new HashMap<>();
        this.correctCount = 0;
    }

    //läsafrån databasen
    public Coupon(Long id, Long userId, Long roundId, Map<Integer, String> tips, int correctCount) {
        this.id = id;
        this.userId = userId;
        this.roundId = roundId;
        this.tips = tips;
        this.correctCount = correctCount;
    }

    public void setTip(int matchNumber, String tip) {
        tips.put(matchNumber, tip);
    }

    public String getTip(int matchNumber) {
        return tips.get(matchNumber);
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRoundId() { return roundId; }

    public void setRoundId(Long roundId) { this.roundId = roundId; }

    public Map<Integer, String> getTips() { return tips; }

    public void setTips(Map<Integer, String> tips) { this.tips = tips; }

    public int getCorrectCount() { return correctCount; }

    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
}
package model;

import java.util.*;

public class Coupon {
    private final int couponId;
    private final int userId;
    private final List<Match> matches;
    private final Map<Integer, List<String>> tips = new HashMap<>();

    public Coupon(int couponId, int userId, List<Match> matches) {
        this.couponId = couponId;
        this.userId = userId;
        this.matches = matches;
    }

    public void setTip(int matchNumber, List<String> tip) {
        tips.put(matchNumber, tip);
    }

    public List<Match> getMatches() {
        return matches;
    }

    public int getCouponId() { return couponId; }

    public int getUserId() { return userId; }
}

//Va fan händer med git asså

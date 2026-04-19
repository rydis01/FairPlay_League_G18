package service;

import database.CouponDAO;
import database.MatchDAO;
import model.Coupon;
import java.util.Map;

/**
 * Hanterar poängberäkning och leaderboards.
 * Ansvarar för att räkna ut poäng efter varje omgång och visa topplistan.
 */
public class ScoreService {
    private CouponDAO couponDAO;
    private MatchDAO matchesDAO;

    public ScoreService() {
        this.couponDAO = new CouponDAO();
        this.matchesDAO = new MatchDAO();
    }

    // Rätta en kupong och returnera antalet rätt
    public int gradeCoupon(int userId, int roundId) {
        Coupon userCoupon = couponDAO.getCoupon(userId, roundId);

        String[] correctCoupon = matchesDAO.getMatchresults(roundId);

        // Jämför användarens tips med de rätta resultaten
        int correctCount = 0;
        Map<Integer, String> tips = userCoupon.getTips();

        for (int i = 0; i < correctCoupon.length; i++) {
            String userTip = tips.get(i);
            String correctResult = correctCoupon[i];

            // Om tippet matchar det rätta resultatet, öka räknaren
            if (userTip != null && userTip.equals(correctResult)) {
                correctCount++;
            }
        }

        // Spara det rätta antalet på kupongen
        userCoupon.setCorrectCount(correctCount);
        couponDAO.updateCorrectCountCoupon(userCoupon);

        return correctCount;
    }
}

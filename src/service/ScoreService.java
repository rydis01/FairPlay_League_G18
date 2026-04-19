package service;

import database.CouponDAO;
import database.MatchDAO;
import database.RoundDAO;
import model.Coupon;

import java.util.List;
import java.util.Map;

/**
 * Hanterar poängberäkning och leaderboards.
 * Ansvarar för att räkna ut poäng efter varje omgång och visa topplistan.
 */
public class ScoreService {
    private CouponDAO couponDAO;
    private RoundDAO roundDAO;

    public ScoreService() {
        this.couponDAO = new CouponDAO();
        this.roundDAO = new RoundDAO();
    }

    // Rätta en kupong och returnera antalet rätt
    public void gradeCoupon(int userId, int roundId) {
        Coupon userCoupon = couponDAO.getCoupon(userId, roundId);

        if(userCoupon.getGraded()){
            return;
        }

        List <String> correctCoupon = roundDAO.getResultsFromRound(roundId);

        // Jämför användarens tips med de rätta resultaten
        int correctCount = 0;
        Map<Integer, String> tips = userCoupon.getTips();

        for (int i = 0; i < correctCoupon.size(); i++) {
            String userTip = tips.get(i);
            String correctResult = correctCoupon.get(i);

            // Om tippet matchar det rätta resultatet, öka räknaren
            if (userTip != null && userTip.equals(correctResult)) {
                correctCount++;
            }
        }

        userCoupon.setGraded(true);
        userCoupon.setCorrectCount(correctCount);
        couponDAO.updateCorrectCountCoupon(userCoupon);
    }
}

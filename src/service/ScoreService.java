package service;

import database.CouponDAO;
import database.MatchDAO;
import database.RoundDAO;
import model.Coupon;

import java.util.List;
import java.util.Map;

/**
 * Handles score calculation and leaderboards.
 * Responsible for counting points after each round and displaying the top list.
 *
 * @author Carl
 */
public class ScoreService {
    private CouponDAO couponDAO;
    private RoundDAO roundDAO;

    public ScoreService() {
        this.couponDAO = new CouponDAO();
        this.roundDAO = new RoundDAO();
    }

    // Grade a coupon and return the number of correct guesses

    /**
     * Grades a user's coupon by comparing their tips with the correct results from a round.
     * If the coupon has already been graded, the method returns without making changes.
     *
     * @param userId The ID of the user whose coupon will be graded
     * @param roundId The ID of the round to grade the coupon for
     * @author Carl
     */
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

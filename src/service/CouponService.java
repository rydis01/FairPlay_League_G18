package service;

import database.CouponDAO;
import database.MatchDAO;
import model.Coupon;
import java.util.Map;

public class CouponService {

    private CouponDAO couponDAO;
    private MatchDAO matchesDAO;

    public CouponService() {
        this.couponDAO = new CouponDAO();
        this.matchesDAO = new MatchDAO();
    }

    // Skapa och spara en kupong med alla 8 tips
    public void submitCoupon(int userId, int roundId, Map<Integer, String> tips) {
        //Validera att det är exakt 8 tips
        if (tips.size() != 8) {
            System.out.println("En kupong måste ha exakt 8 tips!");
            return;
        }

        //Validera att varje tips är 1, X eller 2
        for (String tip : tips.values()) {
            if (!tip.equals("1") && !tip.equals("X") && !tip.equals("2")) {
                System.out.println("Ogiltigt tips: " + tip + ". Måste vara 1, X eller 2.");
                return;
            }
        }

        Coupon coupon = new Coupon(userId, roundId);
        coupon.setTips(tips);
        couponDAO.saveCoupon(coupon);
    }
    // Carl
    public int gradeCoupon(int userId, int roundId){
        Coupon userCoupon = getCoupon(userId, roundId);

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

    // Helpmethods

    public Coupon getCoupon(int userId, int roundId) {
        return couponDAO.getCoupon(userId, roundId);
    }
}
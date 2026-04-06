package service;

import database.CouponDAO;
import model.Coupon;
import java.util.Map;

public class CouponService {

    private CouponDAO couponDAO;

    public CouponService() {
        this.couponDAO = new CouponDAO();
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

    // Hämta en användares kupong för en omgång
    public Coupon getCoupon(int userId, int roundId) {
        return couponDAO.getCoupon(userId, roundId);
    }
}
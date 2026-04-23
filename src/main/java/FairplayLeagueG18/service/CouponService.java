package FairplayLeagueG18.service;

import FairplayLeagueG18.database.CouponDAO;
import FairplayLeagueG18.model.Coupon;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CouponService {

    private CouponDAO couponDAO;

    public CouponService() {
        this.couponDAO = new CouponDAO();
    }

    // Skapa och spara en kupong med alla 8 tips
    public void submitCoupon(int userId, int roundId, Map<Integer, String> tips) {
        // F-KUP-1.4, Deadline för inlämning, Must
        // Kupongen ska inte kunna lämnas in eller ändras efter omgångens deadline.


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

    // Helpmethods

    public Coupon getCoupon(int userId, int roundId) {
        return couponDAO.getCoupon(userId, roundId);
    }
}
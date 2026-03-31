package service;

import database.GetDataFromDatabase;
import model.Coupon;

public class CouponService {
    private GetDataFromDatabase couponQuery;

    public CouponService() {
        this.couponQuery = new CouponDAO();
    }

    // Skapa en ny kupong för en användare i en specifik round
    public void createCoupon(int userId, int roundId) {
        Coupon coupon = new Coupon(userId, roundId);
        couponQuery.saveCoupon(coupon);
    }
}

package model;

import java.util.List;
import java.util.ArrayList;

public class User {
    private final int id;
    private final String username;
    private final List<Coupon> coupons = new ArrayList<>();

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public void addCoupon(Coupon coupon) {
        coupons.add(coupon);
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public int getId() { return id; }

    public String getUsername() { return username; }
}

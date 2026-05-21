package FairplayLeagueG18.database;

import FairplayLeagueG18.model.Coupon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouponDAO {

    // Tar emot en kupong och bryter ner den till 8 rader av "picks" till databasen
    public void saveCoupon(Coupon coupon) {

        String insertCouponSql =
                "INSERT INTO Coupons (User_ID, Gameweek_ID, Correct_count) VALUES (?, ?, 0)";

        String insertPickSql =
                "INSERT INTO Picks (Coupon_ID, Match_ID, Guess) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            int couponId;
            try (PreparedStatement stmt = conn.prepareStatement(insertCouponSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, coupon.getUserId());
                stmt.setInt(2, coupon.getRoundId());
                stmt.executeUpdate();

                ResultSet keys = stmt.getGeneratedKeys();
                keys.next();
                couponId = keys.getInt(1);
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertPickSql)) {
                for (Map.Entry<Integer, String> tip : coupon.getTips().entrySet()) {
                    stmt.setInt(1, couponId);
                    stmt.setInt(2, tip.getKey());
                    stmt.setString(3, tip.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            System.out.println("Kupong + picks sparade!");

        } catch (SQLException e) {
            System.out.println("Fel vid sparande: " + e.getMessage());
        }
    }


    // Hämtar en användares kupong för en specifik omgång
    // Hämtar först kupongen, sedan alla tillhörande tips från Picks
    public Coupon getCoupon(int couponId) {

        Coupon coupon = null;

        String couponSql = "SELECT * FROM Coupons WHERE Coupon_ID = ?";
        String picksSql = "SELECT Match_ID, Guess FROM Picks WHERE Coupon_ID = ? ORDER BY Match_ID";

        try (Connection conn = DatabaseManager.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement(couponSql)) {
                stmt.setInt(1, couponId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    coupon = new Coupon(
                            rs.getInt("Coupon_ID"),
                            rs.getInt("User_ID"),
                            rs.getInt("League_ID"),
                            rs.getInt("Gameweek_ID"),
                            new HashMap<>(),
                            rs.getInt("Correct_count")
                    );
                }
            }

            if (coupon != null) {
                try (PreparedStatement stmt = conn.prepareStatement(picksSql)) {
                    stmt.setInt(1, couponId);
                    ResultSet rs = stmt.executeQuery();

                    int matchNumber = 1;
                    while (rs.next()) {
                        coupon.setTip(matchNumber++, rs.getString("Guess"));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Kunde inte hämta kupong. Fel: " + e.getMessage());
        }

        return coupon;
    }

    public Map<Integer, Integer> getCouponIdsForRound(int roundId) {

        Map<Integer, Integer> map = new HashMap<>();

        String sql = "SELECT Coupon_ID, User_ID FROM Coupons WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roundId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                map.put(rs.getInt("User_ID"), rs.getInt("Coupon_ID"));
            }

        } catch (SQLException e) {
            System.out.println("Kunde inte hämta kuponger för omgång: " + e.getMessage());
        }

        return map;
    }

    //Hämtar alla kuponger för en användare
    public List<Coupon> getCouponsByUserId(int userId) {

        List<Coupon> list = new ArrayList<>();

        String sql = "SELECT Coupon_ID FROM Coupons WHERE User_ID = ? ORDER BY Gameweek_ID DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int couponId = rs.getInt("Coupon_ID");
                Coupon c = getCoupon(couponId);
                list.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Kunde inte hämta kuponger: " + e.getMessage());
        }

        return list;
    }



    // Uppdaterar en kupong med rätt antal poäng
    public void updateCorrectCountCoupon(Coupon coupon) {
        String sql = "UPDATE Coupons SET Correct_count = ? WHERE Coupon_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coupon.getCorrectCount());
            stmt.setInt(2, coupon.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Kunde inte uppdatera kupongen. Fel: " + e.getMessage());
        }
    }

}
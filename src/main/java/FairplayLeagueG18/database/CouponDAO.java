package FairplayLeagueG18.database;

import FairplayLeagueG18.model.Coupon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CouponDAO {

    // Tar emot en kupong och bryter ner den till 8 rader av "picks" till databasen
    public void saveCoupon(Coupon coupon) {
        String sql = "INSERT INTO Picks (User_ID, Match_ID, Guess) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Map.Entry<Integer, String> tip : coupon.getTips().entrySet()) {
                stmt.setInt(1, coupon.getUserId());
                stmt.setInt(2, tip.getKey());
                stmt.setString(3, tip.getValue());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            System.out.println("Kupongen sparades i databasen!");

        } catch (SQLException e) {
            System.out.println("Kunde inte spara data i databasen. Fel: " + e.getMessage());
        }
    }

    // Hämtar en användares kupong för en specifik omgång
    // Hämtar först kupongen, sedan alla tillhörande tips från Picks
    public Coupon getCoupon(int userId, int roundId) {
        Coupon coupon = null;

        String couponSql = "SELECT Coupon_ID, User_ID, Round_ID, Correct_count FROM Coupons WHERE User_ID = ? AND Round_ID = ?";
        String picksSql = "SELECT Match_ID, Guess FROM Picks WHERE User_ID = ? AND Match_ID IN (SELECT Match_ID FROM Matches WHERE Gameweek_ID = ?) ORDER BY Match_ID";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return null;

            // Steg 1: Hämta kupongen
            try (PreparedStatement stmt = conn.prepareStatement(couponSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, roundId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    coupon = new Coupon(
                            rs.getInt("Coupon_ID"),
                            rs.getInt("User_ID"),
                            rs.getInt("Round_ID"),
                            new HashMap<>(),
                            rs.getInt("Correct_count")
                    );
                }
            }

            // Steg 2: Hämta tipsen och lägg in dem i kupongen
            if (coupon != null) {
                try (PreparedStatement stmt = conn.prepareStatement(picksSql)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, roundId);
                    ResultSet rs = stmt.executeQuery();

                    int matchNumber = 1;
                    while (rs.next()) {
                        coupon.setTip(matchNumber, rs.getString("Guess"));
                        matchNumber++;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Kunde inte hämta kupong. Fel: " + e.getMessage());
        }

        return coupon;
    }

    // Uppdaterar en kupong med rätt antal poäng
    public void updateCorrectCountCoupon(Coupon coupon) {
        String sql = "UPDATE Coupons SET Correct_count = ? WHERE User_ID = ? AND Round_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coupon.getCorrectCount());
            stmt.setInt(2, coupon.getUserId());
            stmt.setInt(3, coupon.getRoundId());

            stmt.executeUpdate();
            conn.commit();
            System.out.println("Kupongen uppdaterades med " + coupon.getCorrectCount() + " rätt!");

        } catch (SQLException e) {
            System.out.println("Kunde inte uppdatera kupongen. Fel: " + e.getMessage());
        }
    }
}
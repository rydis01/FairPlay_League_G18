package database;

import model.Coupon;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class GetDataFromDatabase {

    /* TODO:
     * Koda kopplingen till databasen
     * Strukturera upp schema för sparande av data
     */

    public void saveCoupon(Coupon coupon) {
        String sql = "INSERT INTO Picks (user_id, match_id, guess) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coupon.getUserId());
            stmt.setInt(2, coupon.getRoundId());
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

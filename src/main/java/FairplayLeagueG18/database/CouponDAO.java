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

/**
 * Hanterar all databasåtkomst för kuponger och tillhörande tips (picks).
 * Kommunicerar med tabellerna Coupons och Picks.
 * @author Hugo Werntoft, Carl Rydengård & Gustav Johnsson
 */
public class CouponDAO {

    /**
     * Sparar en kupong och dess tillhörande tips i databasen som en transaktion.
     * Kupongen sparas först i Coupons, sedan sparas varje tips i Picks.
     *
     * @param coupon kupong-objektet som ska sparas, inklusive alla tips
     * @throws SQLException om transaktionen misslyckas och rollback krävs
     */
    public void saveCoupon(Coupon coupon) {

        String insertCouponSql =
                "INSERT INTO Coupons (User_ID, Gameweek_ID, League_ID, Correct_count) VALUES (?, ?, ?, 0)";

        String insertPickSql =
                "INSERT INTO Picks (Coupon_ID, Match_ID, Guess) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            int couponId;
            try (PreparedStatement stmt = conn.prepareStatement(insertCouponSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, coupon.getUserId());
                stmt.setInt(2, coupon.getRoundId());
                stmt.setInt(3, coupon.getLeagueId());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    keys.next();
                    couponId = keys.getInt(1);
                }
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

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Fel vid sparande av kupong: " + e.getMessage());
        }
    }

    /**
     * Hämtar en kupong med alla tillhörande tips från databasen.
     * Hämtar först kupong-raden från Coupons, sedan alla picks från Picks.
     *
     * @param couponId ID:t för kupongen som ska hämtas
     * @return Coupon-objekt med tips ifyllda, eller null om kupongen inte hittas
     */
    public Coupon getCoupon(int couponId) {

        Coupon coupon = null;

        String couponSql = "SELECT * FROM Coupons WHERE Coupon_ID = ?";
        String picksSql = "SELECT Match_ID, Guess FROM Picks WHERE Coupon_ID = ? ORDER BY Match_ID";

        try (Connection conn = DatabaseManager.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement(couponSql)) {
                stmt.setInt(1, couponId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        coupon = new Coupon(
                                rs.getInt("Coupon_ID"),
                                rs.getInt("User_ID"),
                                rs.getInt("Gameweek_ID"),
                                rs.getInt("League_ID"),
                                new HashMap<>(),
                                rs.getInt("Correct_count")
                        );
                    }
                }
            }

            if (coupon != null) {
                try (PreparedStatement stmt = conn.prepareStatement(picksSql)) {
                    stmt.setInt(1, couponId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            int matchId = rs.getInt("Match_ID");
                            String guess = rs.getString("Guess");
                            coupon.setTip(matchId, guess);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte hämta kupong. Fel: " + e.getMessage());
        }

        return coupon;
    }

    /**
     * Hämtar en mappning från användar-ID till kupong-ID för alla kuponger i en given omgång.
     *
     * @param roundId ID:t för omgången
     * @return en Map där nyckel är User_ID och värde är Coupon_ID
     */
    public Map<Integer, Integer> getCouponIdsForRound(int roundId) {

        Map<Integer, Integer> map = new HashMap<>();

        String sql = "SELECT Coupon_ID, User_ID FROM Coupons WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roundId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("User_ID"), rs.getInt("Coupon_ID"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte hämta kuponger för omgång: " + e.getMessage());
        }

        return map;
    }

    /**
     * Hämtar alla kuponger för en given användare, sorterade med senaste omgången först.
     *
     * @param userId ID:t för användaren
     * @return lista med Coupon-objekt inklusive tips, eller tom lista om inga hittas
     */
    public List<Coupon> getCouponsByUserId(int userId) {

        List<Coupon> list = new ArrayList<>();

        String sql = "SELECT Coupon_ID FROM Coupons WHERE User_ID = ? ORDER BY Gameweek_ID DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int couponId = rs.getInt("Coupon_ID");
                    Coupon c = getCoupon(couponId);
                    list.add(c);
                }
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte hämta kuponger: " + e.getMessage());
        }

        return list;
    }

    /**
     * Kontrollerar om en användare redan har lämnat in en kupong för en specifik omgång och liga.
     * Används för att förhindra dubbelregistrering.
     *
     * @param userId     ID:t för användaren
     * @param gameweekId ID:t för omgången
     * @param leagueId   ID:t för ligan
     * @return true om en kupong redan finns, annars false
     */
    public boolean couponExists(int userId, int gameweekId, int leagueId) {
        String sql = "SELECT 1 FROM Coupons WHERE User_ID = ? AND Gameweek_ID = ? AND League_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, gameweekId);
            stmt.setInt(3, leagueId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte kontrollera kupong: " + e.getMessage());
            return false;
        }
    }

    /**
     * Uppdaterar antalet rätta tips på en kupong i databasen.
     *
     * @param coupon kupong-objektet med uppdaterat correctCount
     */
    public void updateCorrectCountCoupon(Coupon coupon) {
        String sql = "UPDATE Coupons SET Correct_count = ? WHERE Coupon_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coupon.getCorrectCount());
            stmt.setInt(2, coupon.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Kunde inte uppdatera kupongen. Fel: " + e.getMessage());
        }
    }

    /**
     * Hämtar alla unika liga-ID:n som har kuponger inlämnade för en given omgång.
     *
     * @param gameweekId ID:t för omgången
     * @return lista med liga-ID:n, eller tom lista om inga hittas
     */
    public List<Integer> getLeagueIdsForGameweek(int gameweekId) {
        List<Integer> leagueIds = new ArrayList<>();
        String sql = "SELECT DISTINCT League_ID FROM Coupons WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameweekId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    leagueIds.add(rs.getInt("League_ID"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av ligor: " + e.getMessage());
        }

        return leagueIds;
    }
}
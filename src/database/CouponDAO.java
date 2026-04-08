package database;

import model.Coupon;
import model.Pick;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * DAO står för Data Access Object
 *      Namn för systemet där en service-klass skickar en beställning till denna klass för att komma åt data från DB
 */

public class CouponDAO {

    // Tar emot en kopong och bryter ner den till 8 rader av "picks" till databasen
    public void saveCoupon(Coupon coupon) {

        // Lägger in EN i tabellen Picks i DB
        String sql = "INSERT INTO Picks (User_ID, Match_ID, Guess) VALUES (?, ?, ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Vi vill skicka hela batches av gissningar istället för att skicka en i taget, alltså ingen automatisk:
            conn.setAutoCommit(false);

            // Vi loopar igenom kupongens alla gissningar (i vår Map):
            // Map.Entry låter oss plocka ut både Match_ID (Key) och Gissningen (Value) samtidigt
            for (Map.Entry<Integer, String> tip : coupon.getTips().entrySet()) {

                stmt.setInt(1, coupon.getUserId());  // 1:a frågetecknet = Användarens ID
                stmt.setInt(2, tip.getKey());        // 2:a frågetecknet = Match_ID (Nyckeln i Map:en)
                stmt.setString(3, tip.getValue());   // 3:e frågetecknet = Gissningen (Värdet i Map:en)

                // Lägger till "batchen" i bunten
                stmt.addBatch();
            }

            // Skickar iväg batchen till databasen som EN bunt
            stmt.executeBatch();

            // Bekräftar och sparar kupongen
            conn.commit();
            System.out.println("Kupongen sparades i databasen!");
        } catch (SQLException e) {
            System.out.println("Kunde inte spara data i databasen. Fel: " + e.getMessage());
        }
    }
}

package FairplayLeagueG18;

import FairplayLeagueG18.database.DatabaseManager;

import java.sql.Connection;
import java.sql.Statement;

public class TestingDatabase {

    public static void main(String[] args) {

        String[] sqlCommands = {

                // Rensa allt som INTE är Users, Gameweeks eller Matches
                "DELETE FROM Picks;",
                "DELETE FROM Coupons;",
                "DELETE FROM Gameweek_scores;",
                "DELETE FROM User_Leagues;",
                "DELETE FROM Leagues;",

                // Återställ auto-increment (valfritt men snyggt)
                "ALTER SEQUENCE picks_pick_id_seq RESTART WITH 1;",
                "ALTER SEQUENCE coupons_coupon_id_seq RESTART WITH 1;",
                "ALTER SEQUENCE gameweek_scores_score_id_seq RESTART WITH 1;",
                "ALTER SEQUENCE leagues_league_id_seq RESTART WITH 1;"
        };

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : sqlCommands) {
                stmt.execute(sql);
                System.out.println("Körde: " + sql);
            }

        } catch (Exception e) {
            System.out.println("Fel vid ändring: " + e.getMessage());
        }
    }
}

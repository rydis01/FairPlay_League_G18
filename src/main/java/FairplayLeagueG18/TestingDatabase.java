package FairplayLeagueG18;

import FairplayLeagueG18.database.DatabaseManager;

import java.sql.Connection;
import java.sql.Statement;

public class TestingDatabase {

    public static void main(String[] args) {

        String[] sqlCommands = {

                // 🔵 Ändra ALLA matcher
                "UPDATE Matches SET Home_team = 'Hej';"

                // 👉 Lägg fler UPDATE här om du vill
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

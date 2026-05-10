package FairplayLeagueG18;

import FairplayLeagueG18.database.DatabaseManager;

import java.sql.Connection;
import java.sql.Statement;

public class TestingDatabase {

    public static void main(String[] args) {

        String[] sqlCommands = {
                "TRUNCATE TABLE Picks RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE Gameweek_scores RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE Coupons RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE Matches RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE Gameweeks RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE User_Leagues RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE Leagues RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE Users RESTART IDENTITY CASCADE"
        };

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : sqlCommands) {
                stmt.execute(sql);
                System.out.println("Körde: " + sql);
            }

        } catch (Exception e) {
            System.out.println("Fel vid nollställning: " + e.getMessage());
        }
    }
}

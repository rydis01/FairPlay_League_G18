package FairplayLeagueG18;

import FairplayLeagueG18.api.LiveScoreMapper;
import FairplayLeagueG18.api.LiveScoreService;
import FairplayLeagueG18.database.DatabaseManager;
import FairplayLeagueG18.database.MatchDAO;
import FairplayLeagueG18.model.Match;

import java.sql.*;
import java.util.List;

public class TestingDatabase {

    public static void main(String[] args) {


        printTable("SELECT * FROM Picks WHERE Coupon_ID = 15;");
    }

    private static void printTable(String sql) {
        System.out.println("\n--- " + sql + " ---");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();

            int rows = 0;
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
                rows++;
            }

            if (rows == 0) System.out.println("(inga rader)");

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }
}
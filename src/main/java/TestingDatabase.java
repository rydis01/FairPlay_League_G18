package service;

import database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class TestingDatabase {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Vilken omgång vill du hämta? Skriv en siffra (1-30): ");


        int omgangAttHamta = scanner.nextInt();

        System.out.println("\nHämtar matcher för omgång " + omgangAttHamta + "...\n");

        String sql = "SELECT Home_team, Away_team, Actual_result FROM Matches WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, omgangAttHamta);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            System.out.println("--- OMGÅNG " + omgangAttHamta + " ---");

            while (rs.next()) {
                String home = rs.getString("Home_team");
                String away = rs.getString("Away_team");
                String res = rs.getString("Actual_result");

                String displayRes = (res != null) ? res : "Ej spelad";
                System.out.println(home + " vs " + away + " | Resultat: " + displayRes);
                count++;
            }

            if (count == 0) {
                System.out.println("Hittade inga matcher. Finns omgång " + omgangAttHamta + " verkligen i databasen?");
            }

        } catch (Exception e) {
            System.out.println("Ett fel uppstod: " + e.getMessage());
        }

        scanner.close();
    }
}
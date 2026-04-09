package database;

import api.model.Match;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

public class MatchDAO {

    public void saveMatches(List<Match> matches) {
        // SQL för att kolla om matchen redan finns
        String checkSql = "SELECT Match_ID FROM Matches WHERE Home_team = ? AND Away_team = ?";
        // SQL för att uppdatera en befintlig match
        String updateSql = "UPDATE Matches SET Actual_result = ?, Kickoff_time = ? WHERE Match_ID = ?";
        // SQL för att lägga till en helt ny match
        String insertSql = "INSERT INTO Matches (Home_team, Away_team, Kickoff_time, Actual_result) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            // Vi slår av AutoCommit för att spara alla 240 matcher i ett svep
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                for (Match match : matches) {
                    Timestamp kickoff = parseTime(match.getMatchTime());
                    String result = determineResult(match);

                    // 1. Finns matchen redan?
                    checkStmt.setString(1, match.getHomeTeam());
                    checkStmt.setString(2, match.getAwayTeam());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        // 2A. Matchen finns! Vi uppdaterar den (ifall det blivit mål)
                        int matchId = rs.getInt("Match_ID");
                        updateStmt.setString(1, result);
                        updateStmt.setTimestamp(2, kickoff);
                        updateStmt.setInt(3, matchId);
                        updateStmt.addBatch();
                    } else {
                        // 2B. Matchen finns inte! Vi lägger till den.
                        insertStmt.setString(1, match.getHomeTeam());
                        insertStmt.setString(2, match.getAwayTeam());
                        insertStmt.setTimestamp(3, kickoff);
                        insertStmt.setString(4, result);
                        insertStmt.addBatch();
                    }
                }

                // Kör iväg allt till databasen
                updateStmt.executeBatch();
                insertStmt.executeBatch();
                conn.commit();

                System.out.println("-> Databasen är nu synkad med LiveScore!");

            } catch (Exception e) {
                conn.rollback(); // Något gick fel, vi ångrar ändringarna
                System.out.println("Kunde inte spara matcher till DB: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Fel vid databaskoppling: " + e.getMessage());
        }
    }

    // --- Hjälpmetoder ---

    // Konverterar er matchScore (t.ex. "2" och "1") till tipstecken (1, X eller 2)
    private String determineResult(Match match) {
        if ("NS".equals(match.getMatchStatus()) || "-".equals(match.getHomeScore())) {
            return null; // Matchen har inte spelats än
        }
        try {
            int home = Integer.parseInt(match.getHomeScore());
            int away = Integer.parseInt(match.getAwayScore());
            if (home > away) return "1";
            if (home == away) return "X";
            return "2";
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Konverterar LiveScore-tiden "20260404150000" till SQL-Timestamp "2026-04-04 15:00:00"
    private Timestamp parseTime(String timeStr) {
        if (timeStr == null || timeStr.length() != 14) return null;
        String formatted = String.format("%s-%s-%s %s:%s:%s",
                timeStr.substring(0, 4), timeStr.substring(4, 6), timeStr.substring(6, 8),
                timeStr.substring(8, 10), timeStr.substring(10, 12), timeStr.substring(12, 14));
        return Timestamp.valueOf(formatted);
    }
}
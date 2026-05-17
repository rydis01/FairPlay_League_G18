package FairplayLeagueG18.database;

import FairplayLeagueG18.model.Match;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    public void saveMatches(List<Match> matches) {
        // SQL för att kolla om matchen redan finns
        String checkSql = "SELECT Match_ID FROM Matches WHERE Home_team = ? AND Away_team = ?";

        // SQL för att uppdatera en befintlig match
        String updateSql = "UPDATE Matches SET Gameweek_ID = ?, Actual_result = ?, Kickoff_time = ? WHERE Match_ID = ?";

        // SQL för att lägga till en helt ny match
        String insertSql = "INSERT INTO Matches (Gameweek_ID, Home_team, Away_team, Kickoff_time, Actual_result) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            // Vi slår av AutoCommit för att spara alla 240 matcher i ett svep
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                for (Match match : matches) {

                    ensureGameweekExists(conn, match.getGameweekId());

                    Timestamp kickoff = parseTime(match.getMatchTime());
                    String result = determineResult(match);

                    // 1. Finns matchen redan?
                    checkStmt.setString(1, match.getHomeTeam());
                    checkStmt.setString(2, match.getAwayTeam());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        // 2A. Matchen finns! Vi uppdaterar den (ifall det blivit mål)
                        int matchId = rs.getInt("Match_ID");

                        updateStmt.setInt(1, match.getGameweekId());
                        updateStmt.setString(2, result);
                        updateStmt.setTimestamp(3, kickoff);
                        updateStmt.setInt(4, matchId);
                        updateStmt.addBatch();

                    } else {
                        // 2B. Matchen finns inte! Vi lägger till den.
                        insertStmt.setInt(1, match.getGameweekId());
                        insertStmt.setString(2, match.getHomeTeam());
                        insertStmt.setString(3, match.getAwayTeam());
                        insertStmt.setTimestamp(4, kickoff);
                        insertStmt.setString(5, result);
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

    private void ensureGameweekExists(Connection conn, int gameweekId) throws Exception {
        if (gameweekId <= 0) return; // hoppa över ogiltiga omgångar

        String checkSql = "SELECT Gameweek_ID FROM Gameweeks WHERE Gameweek_ID = ?";
        String insertSql = "INSERT INTO Gameweeks (Gameweek_ID, Round_number, Lock_time) VALUES (?, ?, NOW() + INTERVAL '7 days')";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, gameweekId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, gameweekId);
                    insertStmt.setInt(2, gameweekId);
                    insertStmt.executeUpdate();
                    System.out.println("-> Skapade Gameweek " + gameweekId);
                }
            }
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

    public List<Match> getMatchesByGameweek(int gameweekId) {

        List<Match> matches = new ArrayList<>();

        String sql = "SELECT Match_ID, Gameweek_ID, Home_team, Away_team, Kickoff_time, Actual_result " +
                "FROM Matches WHERE Gameweek_ID = ? ORDER BY Match_ID";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameweekId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Match m = new Match();

                m.setId(rs.getInt("Match_ID"));
                m.setGameweekId(rs.getInt("Gameweek_ID"));
                m.setHomeTeam(rs.getString("Home_team"));
                m.setAwayTeam(rs.getString("Away_team"));

                Timestamp kickoff = rs.getTimestamp("Kickoff_time");
                m.setKickOff(kickoff != null ? kickoff.toLocalDateTime() : null);

                m.setResult(rs.getString("Actual_result"));

                matches.add(m);
            }

        } catch (Exception e) {
            System.out.println("Kunde inte hämta matcher för gameweek " + gameweekId + ". Fel: " + e.getMessage());
        }

        return matches;
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

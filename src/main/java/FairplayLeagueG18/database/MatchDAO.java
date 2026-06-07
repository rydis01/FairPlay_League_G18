package FairplayLeagueG18.database;

import FairplayLeagueG18.model.Match;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Hanterar all databasåtkomst för matcher.
 * Kommunicerar med tabellerna Matches och Gameweeks.
 * @author Carl Rydengård & Hugo Bergman Werntoft
 */
public class MatchDAO {

    /**
     * Sparar eller uppdaterar en lista med matcher i databasen som en transaktion.
     * Om en match redan finns (baserat på hemma- och bortalag) uppdateras den,
     * annars läggs den till som ny. Säkerställer även att tillhörande omgång finns.
     *
     * @param matches lista med Match-objekt som ska sparas eller uppdateras
     */
    public void saveMatches(List<Match> matches) {
        String checkSql = "SELECT Match_ID FROM Matches WHERE Home_team = ? AND Away_team = ?";
        String updateSql = "UPDATE Matches SET Gameweek_ID = ?, Actual_result = ?, Kickoff_time = ? WHERE Match_ID = ?";
        String insertSql = "INSERT INTO Matches (Gameweek_ID, Home_team, Away_team, Kickoff_time, Actual_result) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                for (Match match : matches) {

                    ensureGameweekExists(conn, match.getGameweekId());

                    Timestamp kickoff = parseTime(match.getMatchTime());
                    String result = determineResult(match);

                    checkStmt.setString(1, match.getHomeTeam());
                    checkStmt.setString(2, match.getAwayTeam());

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            // Matchen finns redan — uppdatera den
                            int matchId = rs.getInt("Match_ID");
                            updateStmt.setInt(1, match.getGameweekId());
                            updateStmt.setString(2, result);
                            updateStmt.setTimestamp(3, kickoff);
                            updateStmt.setInt(4, matchId);
                            updateStmt.addBatch();
                        } else {
                            // Matchen finns inte — lägg till den
                            insertStmt.setInt(1, match.getGameweekId());
                            insertStmt.setString(2, match.getHomeTeam());
                            insertStmt.setString(3, match.getAwayTeam());
                            insertStmt.setTimestamp(4, kickoff);
                            insertStmt.setString(5, result);
                            insertStmt.addBatch();
                        }
                    }
                }

                updateStmt.executeBatch();
                insertStmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Kunde inte spara matcher till DB: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Fel vid databaskoppling: " + e.getMessage());
        }
    }

    /**
     * Säkerställer att en omgång med givet ID finns i tabellen Gameweeks.
     * Skapar omgången om den inte redan existerar.
     *
     * @param conn       aktiv databasanslutning
     * @param gameweekId ID:t för omgången som ska kontrolleras eller skapas
     * @throws SQLException om ett databasfel uppstår
     */
    private void ensureGameweekExists(Connection conn, int gameweekId) throws SQLException {
        if (gameweekId <= 0) return;

        String checkSql = "SELECT Gameweek_ID FROM Gameweeks WHERE Gameweek_ID = ?";
        String insertSql = "INSERT INTO Gameweeks (Gameweek_ID, Round_number, Lock_time) VALUES (?, ?, NOW() + INTERVAL '7 days')";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, gameweekId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, gameweekId);
                        insertStmt.setInt(2, gameweekId);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }

    /**
     * Hämtar alla matcher för en given omgång, sorterade efter Match_ID.
     *
     * @param gameweekId ID:t för omgången
     * @return lista med Match-objekt, eller tom lista om inga matcher hittas
     */
    public List<Match> getMatchesByGameweek(int gameweekId) {
        List<Match> matches = new ArrayList<>();

        String sql = "SELECT Match_ID, Gameweek_ID, Home_team, Away_team, Kickoff_time, Actual_result " +
                "FROM Matches WHERE Gameweek_ID = ? ORDER BY Match_ID";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameweekId);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte hämta matcher för gameweek " + gameweekId + ". Fel: " + e.getMessage());
        }

        return matches;
    }

    /**
     * Avgör matchresultatet som tipstecken baserat på målskillnad.
     * Returnerar null om matchen inte spelats än.
     *
     * @param match Match-objektet med hemma- och bortapoäng
     * @return "1" för hemmavinst, "X" för oavgjort, "2" för bortavinst, eller null om ej spelad
     */
    private String determineResult(Match match) {
        if ("NS".equals(match.getMatchStatus()) || "-".equals(match.getHomeScore())) {
            return null;
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

    /**
     * Konverterar LiveScore-tidens format "20260404150000" till ett SQL-Timestamp.
     *
     * @param timeStr tidssträng på formatet yyyyMMddHHmmss
     * @return ett Timestamp-objekt, eller null om strängen är ogiltig
     */
    private Timestamp parseTime(String timeStr) {
        if (timeStr == null || timeStr.length() != 14) return null;
        String formatted = String.format("%s-%s-%s %s:%s:%s",
                timeStr.substring(0, 4), timeStr.substring(4, 6), timeStr.substring(6, 8),
                timeStr.substring(8, 10), timeStr.substring(10, 12), timeStr.substring(12, 14));
        return Timestamp.valueOf(formatted);
    }
}
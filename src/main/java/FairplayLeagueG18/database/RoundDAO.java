package FairplayLeagueG18.database;

import FairplayLeagueG18.model.Match;
import FairplayLeagueG18.model.Round;
import FairplayLeagueG18.model.RoundStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Hanterar all databasåtkomst för spelomgångar (gameweeks) och tillhörande matcher.
 * Kommunicerar med tabellerna Gameweeks och Matches.
 * @author Carl Rydengård & Hugo Bergman Werntoft
 */
public class RoundDAO {

    /**
     * Hämtar alla matchresultat för en given omgång, sorterade efter Match_ID.
     *
     * @param roundId ID:t för omgången
     * @return lista med resultattecken ("1", "X", "2") eller null för ej spelade matcher
     */
    public List<String> getResultsFromRound(int roundId) {
        List<String> results = new ArrayList<>();

        String sql = "SELECT Actual_result FROM Matches WHERE Gameweek_ID = ? ORDER BY Match_ID";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roundId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(rs.getString("Actual_result"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte hämta matchresultat för omgång " + roundId + ". Fel: " + e.getMessage());
        }

        return results;
    }

    /**
     * Hämtar en spelomgång med tillhörande matcher baserat på omgångens ID.
     * Sätter status till Open och createdAt till aktuell tid.
     *
     * @param roundId ID:t för omgången
     * @return Round-objekt med matcher ifyllda, eller tomt Round-objekt om omgången inte hittas
     */
    public Round getRound(int roundId) {
        Round round = new Round();

        String sql = "SELECT * FROM Gameweeks WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roundId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    round.setId(rs.getInt("Gameweek_ID"));
                    round.setGameweek(rs.getInt("Round_number"));
                    round.setDeadline(rs.getTimestamp("Lock_time") != null
                            ? rs.getTimestamp("Lock_time").toLocalDateTime()
                            : null);
                    round.setCreatedAt(LocalDateTime.now());
                    round.setStatus(RoundStatus.Open);
                }
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte hämta omgång " + roundId + ". Fel: " + e.getMessage());
        }

        round.setMatches(getMatches(roundId));
        return round;
    }

    /**
     * Kontrollerar om en omgångs deadline har passerat, vilket innebär att den är låst.
     *
     * @param gameweekId ID:t för omgången
     * @return true om deadline har passerat, annars false
     */
    public boolean isLocked(int gameweekId) {
        String sql = "SELECT Lock_time FROM Gameweeks WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameweekId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp lockTime = rs.getTimestamp("Lock_time");
                    if (lockTime == null) return false;
                    return lockTime.toLocalDateTime().isBefore(LocalDateTime.now());
                }
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte kontrollera deadline: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla matcher för en given omgång, sorterade efter Match_ID.
     *
     * @param gameweekId ID:t för omgången
     * @return lista med Match-objekt, eller tom lista om inga matcher hittas
     */
    private List<Match> getMatches(int gameweekId) {
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
     * Markerar en omgång som rättad i databasen.
     * Förhindrar att samma omgång rättas flera gånger.
     *
     * @param gameweekId ID:t för omgången som ska markeras
     */
    public void markAsSettled(int gameweekId) {
        String sql = "UPDATE Gameweeks SET Settled = TRUE WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameweekId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Fel vid markering av settled: " + e.getMessage());
        }
    }

    /**
     * Kontrollerar om en omgång redan är markerad som rättad.
     *
     * @param gameweekId ID:t för omgången
     * @return true om omgången är rättad, annars false
     */
    public boolean isAlreadySettled(int gameweekId) {
        String sql = "SELECT Settled FROM Gameweeks WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameweekId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("Settled");
                }
            }

        } catch (SQLException e) {
            System.err.println("Fel vid kontroll av settled: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar ID:n för alla omgångar där samtliga matcher har ett registrerat resultat.
     *
     * @return lista med gameweek-ID:n för avslutade omgångar
     */
    public List<Integer> getFinishedGameweekIds() {
        List<Integer> ids = new ArrayList<>();

        String sql =
                "SELECT DISTINCT m.Gameweek_ID " +
                        "FROM Matches m " +
                        "WHERE NOT EXISTS (" +
                        "SELECT 1 FROM Matches m2 " +
                        "WHERE m2.Gameweek_ID = m.Gameweek_ID " +
                        "AND m2.Actual_result IS NULL" +
                        ")";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("Gameweek_ID"));
            }

        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av färdiga omgångar: " + e.getMessage());
        }

        return ids;
    }
}
package FairplayLeagueG18.database;

import FairplayLeagueG18.model.Match;
import FairplayLeagueG18.model.Round;
import FairplayLeagueG18.model.RoundStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoundDAO {

    public List<String> getResultsFromRound(int roundId){
        List<String> results = new ArrayList<>();

        String sql = "SELECT Actual_result FROM Matches WHERE Gameweek_ID = ? ORDER BY Match_ID";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roundId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(rs.getString("Actual_result"));
            }

        } catch (Exception e) {
            System.out.println("Kunde inte hämta matchresultat för omgång " + roundId + ". Fel: " + e.getMessage());
        }
        return results;
    }

    public Round getRound(int roundId) {

        Round round = new Round();

        String sql = "SELECT * FROM Gameweeks WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roundId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                round.setId(rs.getInt("Gameweek_ID"));
                round.setGameweek(rs.getInt("Round_number"));
                round.setDeadline(rs.getTimestamp("Lock_time") != null
                        ? rs.getTimestamp("Lock_time").toLocalDateTime()
                        : null);
                round.setCreatedAt(LocalDateTime.now());
                round.setStatus(RoundStatus.Open);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        round.setMatches(getMatches(roundId));

        return round;
    }

    public boolean isLocked(int gameweekId) {
        String sql = "SELECT Lock_time FROM Gameweeks WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameweekId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp lockTime = rs.getTimestamp("Lock_time");
                if (lockTime == null) return false;
                return lockTime.toLocalDateTime().isBefore(LocalDateTime.now());
            }

        } catch (Exception e) {
            System.out.println("Kunde inte kontrollera deadline: " + e.getMessage());
        }

        return false;
    }

    private List<Match> getMatches(int gameweekId) {
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

    // Markerar en omgång som rättad
    public void markAsSettled(int gameweekId) {
        String sql = "UPDATE Gameweeks SET Settled = TRUE WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameweekId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Fel vid markering av settled: " + e.getMessage());
        }
    }

    // Kollar om en omgång är markerad som rättad
    public boolean isAlreadySettled(int gameweekId) {
        String sql = "SELECT Settled FROM Gameweeks WHERE Gameweek_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameweekId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("Settled");
            }

        } catch (SQLException e) {
            System.out.println("Fel vid kontroll av settled: " + e.getMessage());
        }

        return false;
    }

    // Hämtar alla omgångar där ALLA matcher har ett resultat
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
            System.out.println("Fel vid hämtning av färdiga omgångar: " + e.getMessage());
        }

        return ids;
    }
}

package FairplayLeagueG18.database;

import FairplayLeagueG18.model.Match;
import FairplayLeagueG18.model.Round;
import FairplayLeagueG18.model.RoundStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
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
}

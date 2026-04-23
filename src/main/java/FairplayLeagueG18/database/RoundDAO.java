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
    //Jag vill att RoundDAO finns för att vi ska kunna skapa objekt av Round från
    //databasen utan att behöva klydda till det och göra det i MatchDAO. MVH CARL

    /**
     * Fetches a complete Round object with all its data and matches.
     *
     * @param roundId The round ID (Gameweek number or Round.id)
     * @return A Round object containing all round data and its associated matches
     */

    public List<String> getResultsFromRound(int roundId){
        List<String> results = new ArrayList<>();

        String sql = "SELECT Actual_result FROM Matches WHERE Gameweek_ID = ? ORDER BY Match_ID";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roundId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String result = rs.getString("Actual_result");
                results.add(result);
            }

        } catch (Exception e) {
            System.out.println("Kunde inte hämta matchresultat för omgång " + roundId + ". Fel: " + e.getMessage());
        }
        return results;
    }

    public Round getRound(int roundId) {

        Round round = null;
        List<Match> matches = new ArrayList<>();

        // Hämta gameweek
        String roundSql =
                "SELECT gameweek_id, round_number, lock_time " +
                        "FROM gameweeks WHERE gameweek_id = ?";

        // Hämta matcherna
        String matchesSql =
                "SELECT match_id, gameweek_id, home_team, away_team, kickoff_time, actual_result " +
                        "FROM matches WHERE gameweek_id = ? ORDER BY match_id";

        try (Connection conn = DatabaseManager.getConnection()) {

            // 1. Hämta gameweek
            try (PreparedStatement stmt = conn.prepareStatement(roundSql)) {
                stmt.setInt(1, roundId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {

                    int id = rs.getInt("gameweek_id");
                    int roundNumber = rs.getInt("round_number");

                    Timestamp lockTs = rs.getTimestamp("lock_time");
                    LocalDateTime lockTime = lockTs != null ? lockTs.toLocalDateTime() : null;

                    round = new Round(id, 0, roundNumber, RoundStatus.Open, lockTime, null);
                }
            }

            // 2. Hämta matcherna
            if (round != null) {
                try (PreparedStatement stmt = conn.prepareStatement(matchesSql)) {
                    stmt.setInt(1, round.getId());
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {

                        int id = rs.getInt("match_id");
                        int gameweekId = rs.getInt("gameweek_id");
                        String homeTeam = rs.getString("home_team");
                        String awayTeam = rs.getString("away_team");

                        Timestamp kickoffTs = rs.getTimestamp("kickoff_time");
                        LocalDateTime kickOff = kickoffTs != null ? kickoffTs.toLocalDateTime() : null;

                        String result = rs.getString("actual_result");

                        Match match = new Match(
                                id,
                                gameweekId,
                                null, // externalMatchId finns inte
                                0,    // matchNumber finns inte → sätt 0
                                homeTeam,
                                awayTeam,
                                kickOff,
                                result
                        );

                        matches.add(match);
                    }
                }

                round.setMatches(matches);
            }

        } catch (Exception e) {
            System.out.println("Could not fetch round " + roundId + ". Error: " + e.getMessage());
        }

        return round;
    }
}

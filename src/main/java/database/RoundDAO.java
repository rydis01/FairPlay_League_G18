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

        // Fetch round data from Rounds table
        String roundSql = "SELECT id, League_ID, Gameweek, Status, Deadline, Created_At FROM Rounds WHERE id = ?";

        // Fetch matches for this round
        String matchesSql = "SELECT Match_ID, Gameweek_ID, External_Match_ID, Match_Number, Home_team, Away_team, Kickoff_time, Actual_result " +
                            "FROM Matches WHERE Gameweek_ID = ? ORDER BY Match_Number";

        try (Connection conn = DatabaseManager.getConnection()) {

            // 1. Fetch round data
            try (PreparedStatement roundStmt = conn.prepareStatement(roundSql)) {
                roundStmt.setInt(1, roundId);
                ResultSet rs = roundStmt.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("id");
                    int leagueId = rs.getInt("League_ID");
                    int gameweek = rs.getInt("Gameweek");
                    RoundStatus status = RoundStatus.valueOf(rs.getString("Status"));
                    Timestamp deadlineTs = rs.getTimestamp("Deadline");
                    Timestamp createdAtTs = rs.getTimestamp("Created_At");

                    LocalDateTime deadline;
                    if (deadlineTs != null) {
                        deadline = deadlineTs.toLocalDateTime();
                    } else {
                        deadline = null;
                    }

                    LocalDateTime createdAt;
                    if (createdAtTs != null) {
                        createdAt = createdAtTs.toLocalDateTime();
                    } else {
                        createdAt = null;
                    }

                    round = new Round(id, leagueId, gameweek, status, deadline, createdAt);
                }
            }

            // 2. Fetch all matches for this round
            if (round != null) {
                try (PreparedStatement matchesStmt = conn.prepareStatement(matchesSql)) {
                    matchesStmt.setInt(1, round.getGameweek());
                    ResultSet rs = matchesStmt.executeQuery();

                    while (rs.next()) {
                        int id = rs.getInt("Match_ID");
                        int gameweekId = rs.getInt("Gameweek_ID");
                        String externalMatchId = rs.getString("External_Match_ID");
                        int matchNumber = rs.getInt("Match_Number");
                        String homeTeam = rs.getString("Home_team");
                        String awayTeam = rs.getString("Away_team");
                        Timestamp kickoffTime = rs.getTimestamp("Kickoff_time");
                        String result = rs.getString("Actual_result");

                        LocalDateTime kickOff;
                        if (kickoffTime != null) {
                            kickOff = kickoffTime.toLocalDateTime();
                        } else {
                            kickOff = null;
                        }

                        Match match = new Match(id, gameweekId, externalMatchId, matchNumber, homeTeam, awayTeam, kickOff, result);
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

package database;

import model.League;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object för att hantera Ligor (Leagues).
 * Sköter all kommunikation med tabellerna Leagues och User_Leagues.
 */
public class LeagueDAO {

    /**
     * Skapar och sparar en ny liga i databasen och lägger automatiskt till skaparen (Admin)
     * som medlem i kopplingstabellen User_Leagues.
     */
    public void createLeague(String leagueName, int adminUserId, String inviteCode) {
        String insertLeagueSql = "INSERT INTO Leagues (League_Name, Admin_User, Invite_Code) VALUES (?, ?, ?)";
        String insertMemberSql = "INSERT INTO User_Leagues (User_ID, League_ID) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {

            if (conn == null) {
                System.out.println("Kunde inte ansluta till databasen!");
                return;
            }

            int generatedLeagueId = -1;

            // 1. Skapar själva ligan. Statement.RETURN_GENERATED_KEYS ger oss det nya ID:t direkt
            try (PreparedStatement leagueStmt = conn.prepareStatement(insertLeagueSql, Statement.RETURN_GENERATED_KEYS)) {
                leagueStmt.setString(1, leagueName);
                leagueStmt.setInt(2, adminUserId);
                leagueStmt.setString(3, inviteCode);
                leagueStmt.executeUpdate();

                // Hämtar det nya League_Id som databasen precis skapade med SERIAL
                ResultSet rs = leagueStmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedLeagueId = rs.getInt(1);
                }
            }

            // 2. Lägger in admin som första spelaren i ligan via kopplingstabellen
            if (generatedLeagueId != -1) {
                try (PreparedStatement memberStmt = conn.prepareStatement(insertMemberSql)) {
                    memberStmt.setInt(1, adminUserId);
                    memberStmt.setInt(2, generatedLeagueId);
                    memberStmt.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("Ligan '" + leagueName + "' har skapats!");

        } catch (SQLException e) {
            System.out.println("Kunde inte skapa ligan. Fel: " + e.getMessage());
        }
    }

    /**
     * Hämtar en specifik liga från databasen.
     */
    public League getLeagueById(int leagueId) {
        League league = null;

        String sql =
                "SELECT League_Id, League_Name, Admin_User, Invite_Code, Created_at " +
                        "FROM Leagues " +
                        "WHERE League_Id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {

            if (conn == null) {
                System.out.println("Kunde inte ansluta till databasen!");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, leagueId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    league = new League(
                            rs.getInt("League_Id"),
                            rs.getString("League_Name"),
                            rs.getString("Invite_Code"),
                            rs.getInt("Admin_User"),
                            rs.getTimestamp("Created_at").toLocalDateTime()
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Kunde inte hämta ligan. Fel: " + e.getMessage());
        }

        return league;
    }

    /**
     * Hämtar en lista på alla ligor som en specifik användare är medlem i.
     */
    public List<League> getLeaguesByUserId(int userId) {
        List<League> userLeagues = new ArrayList<>();

        String sql =
                "SELECT l.League_Id, l.League_Name, l.Admin_User, l.Invite_Code, l.Created_at " +
                        "FROM Leagues l " +
                        "JOIN User_Leagues ul ON l.League_Id = ul.League_ID " +
                        "WHERE ul.User_ID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {

            if (conn == null) {
                System.out.println("Kunde inte ansluta till databasen!");
                return userLeagues;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    League league = new League(
                            rs.getInt("League_Id"),
                            rs.getString("League_Name"),
                            rs.getString("Invite_Code"),
                            rs.getInt("Admin_User"),
                            rs.getTimestamp("Created_at").toLocalDateTime()
                    );
                    userLeagues.add(league);
                }
            }

        } catch (SQLException e) {
            System.out.println("Kunde inte hämta användarens ligor. Fel: " + e.getMessage());
        }

        return userLeagues;
    }
}

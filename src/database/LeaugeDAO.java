package database;

import model.League;
import model.User;
import model.Role;
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
public class LeaugeDAO {

    /**
     * Skapar och sparar en ny liga i databasen och lägger automatiskt till skaparen (Admin)
     * som medlem i kopplingstabellen User_Leagues.
     */
    public void createLeague(String leagueName, Long adminUserId, String inviteCode) {
        String insertLeagueSql = "INSERT INTO Leagues (League_Name, Admin_User, Invite_Code) VALUES (?, ?, ?)";
        String insertMemberSql = "INSERT INTO User_Leagues (User_ID, League_ID) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {

            // AutoCommit stängs av då två saker saker måste lyckas TILLSAMMANS
            conn.setAutoCommit(false);

            long generatedLeagueId = -1;

            // 1. Skapar själva ligan. Statement.RETURN_GENERATED_KEYS ger oss det nya ID:t direkt
            try (PreparedStatement leagueStmt = conn.prepareStatement(insertLeagueSql, Statement.RETURN_GENERATED_KEYS)) {
                leagueStmt.setString(1, leagueName);
                leagueStmt.setLong(2, adminUserId);
                leagueStmt.setString(3, inviteCode);
                leagueStmt.executeUpdate();

                // Hämtar det nya League_Id som databasen precis skapade med SERIAL
                ResultSet rs = leagueStmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedLeagueId = rs.getLong(1);
                }
            }

            // 2. Lägger in admin som första spelaren i ligan via kopplingstabellen
            if (generatedLeagueId != -1) {
                try (PreparedStatement memberStmt = conn.prepareStatement(insertMemberSql)) {
                    memberStmt.setLong(1, adminUserId);
                    memberStmt.setLong(2, generatedLeagueId);
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
     * Hämtar en specifik liga och en lista på ALLA dess medlemmar med hjälp av en LEFT JOIN.
     */
    public League getLeagueById(Long leagueId) {
        League league = null;
        List<User> members = new ArrayList<>();

        String sql =
                "SELECT l.*, u.User_ID AS Member_ID, u.Username, u.Email, u.Role " +
                        "FROM Leagues l " +
                        "LEFT JOIN User_Leagues ul ON l.League_Id = ul.League_ID " +
                        "LEFT JOIN Users u ON ul.User_ID = u.User_ID " +
                        "WHERE l.League_Id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, leagueId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Skapar League-objektet på första varvet
                if (league == null) {
                    league = new League(
                            rs.getLong("League_Id"),
                            rs.getString("League_Name"),
                            rs.getLong("Admin_User"),
                            rs.getString("Invite_Code"),
                            members
                    );
                }

                // Bygg ihop medlemmarna om det finns några
                if (rs.getLong("Member_ID") != 0) {
                    User member = new User(
                            rs.getLong("Member_ID"),
                            rs.getString("Username"),
                            rs.getString("Email"),
                            null,
                            Role.valueOf(rs.getString("Role")),
                            null
                    );
                    members.add(member);
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
    public List<League> getLeaguesByUserId(Long userId) {
        List<League> userLeagues = new ArrayList<>();

        String sql =
                "SELECT l.* " +
                        "FROM Leagues l " +
                        "JOIN User_Leagues ul ON l.League_Id = ul.League_ID " +
                        "WHERE ul.User_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                League league = new League(
                        rs.getLong("League_Id"),
                        rs.getString("League_Name"),
                        rs.getLong("Admin_User"),
                        rs.getString("Invite_Code"),
                        new ArrayList<>() // Tom lista för medlemmar
                );
                userLeagues.add(league);
            }

        } catch (SQLException e) {
            System.out.println("Kunde inte hämta användarens ligor. Fel: " + e.getMessage());
        }

        return userLeagues;
    }
}

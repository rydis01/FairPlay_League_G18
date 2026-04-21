package database;

import model.League;
import model.LeagueMember;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Hanterar all kommunikation med tabellerna Leagues och User_Leagues
public class LeagueDAO {

    // Skapar en liga och lägger till skaparen som första medlem
    public void createLeague(String leagueName, int adminUserId, String inviteCode) {
        String insertLeagueSql = "INSERT INTO Leagues (League_Name, Admin_User, Invite_Code) VALUES (?, ?, ?)";
        String insertMemberSql = "INSERT INTO User_Leagues (User_ID, League_ID, Total_Score) VALUES (?, ?, 0)";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) {
                System.out.println("Kunde inte ansluta till databasen!");
                return;
            }

            int generatedLeagueId = -1;

            // Skapa ligan och hämta det autogenererade id:t
            try (PreparedStatement leagueStmt = conn.prepareStatement(insertLeagueSql, Statement.RETURN_GENERATED_KEYS)) {
                leagueStmt.setString(1, leagueName);
                leagueStmt.setInt(2, adminUserId);
                leagueStmt.setString(3, inviteCode);
                leagueStmt.executeUpdate();

                ResultSet rs = leagueStmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedLeagueId = rs.getInt(1);
                }
            }

            // Lägg till skaparen som medlem
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

    // Hämtar en liga baserat på id
    public League getLeagueById(int leagueId) {
        League league = null;

        String sql =
                "SELECT League_Id, League_Name, Admin_User, Invite_Code, Created_at " +
                        "FROM Leagues " +
                        "WHERE League_Id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return null;

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

    // Hämtar en liga baserat på invite code — returnerar null om koden inte finns
    public League getLeagueByInviteCode(String inviteCode) {
        League league = null;

        String sql =
                "SELECT League_Id, League_Name, Admin_User, Invite_Code, Created_at " +
                        "FROM Leagues " +
                        "WHERE Invite_Code = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return null;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, inviteCode);
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
            System.out.println("Kunde inte hämta liga med inbjudningskod. Fel: " + e.getMessage());
        }

        return league;
    }

    // Hämtar alla ligor som en användare är med i via JOIN
    public List<League> getLeaguesByUserId(int userId) {
        List<League> userLeagues = new ArrayList<>();

        String sql =
                "SELECT l.League_Id, l.League_Name, l.Admin_User, l.Invite_Code, l.Created_at " +
                        "FROM Leagues l " +
                        "JOIN User_Leagues ul ON l.League_Id = ul.League_ID " +
                        "WHERE ul.User_ID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return userLeagues;

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

    // Lägger till en användare som medlem med 0 poäng
    public void addMember(int leagueId, int userId) {
        String sql = "INSERT INTO User_Leagues (User_ID, League_ID, Total_Score) VALUES (?, ?, 0)";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, leagueId);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            System.out.println("Kunde inte lägga till medlem. Fel: " + e.getMessage());
        }
    }

    // Tar bort en användare från en liga
    public void removeMember(int leagueId, int userId) {
        String sql = "DELETE FROM User_Leagues WHERE League_ID = ? AND User_ID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, leagueId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            System.out.println("Kunde inte ta bort medlem. Fel: " + e.getMessage());
        }
    }

    // Kollar om en användare redan är med i en liga — förhindrar dubbelmedlemskap
    public boolean isMember(int leagueId, int userId) {
        String sql = "SELECT 1 FROM User_Leagues WHERE League_ID = ? AND User_ID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return false;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, leagueId);
                stmt.setInt(2, userId);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Kunde inte kontrollera medlemskap. Fel: " + e.getMessage());
        }

        return false;
    }

    // Räknar antal medlemmar i en liga — används för att beräkna potten
    public int countMembersByLeagueId(int leagueId) {
        String sql = "SELECT COUNT(*) FROM User_Leagues WHERE League_ID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return 0;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, leagueId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Kunde inte räkna medlemmar. Fel: " + e.getMessage());
        }

        return 0;
    }

    // Hämtar leaderboard — alla medlemmar sorterade på poäng, högst först
    public List<LeagueMember> getMembersByLeagueIdSortedByScore(int leagueId) {
        List<LeagueMember> members = new ArrayList<>();

        String sql =
                "SELECT ul.User_ID, u.Username, ul.Total_Score " +
                        "FROM User_Leagues ul " +
                        "JOIN Users u ON ul.User_ID = u.User_Id " +
                        "WHERE ul.League_ID = ? " +
                        "ORDER BY ul.Total_Score DESC";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return members;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, leagueId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    LeagueMember member = new LeagueMember();
                    member.setUserId(rs.getInt("User_ID"));
                    member.setUsername(rs.getString("Username"));
                    member.setTotalScore(rs.getInt("Total_Score"));
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            System.out.println("Kunde inte hämta leaderboard. Fel: " + e.getMessage());
        }

        return members;
    }

    // Adderar poäng till en spelares total — skriver inte över befintlig poäng
    public void addScoreToMember(int leagueId, int userId, int pointsToAdd) {
        String sql = "UPDATE User_Leagues SET Total_Score = Total_Score + ? WHERE League_ID = ? AND User_ID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, pointsToAdd);
                stmt.setInt(2, leagueId);
                stmt.setInt(3, userId);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            System.out.println("Kunde inte uppdatera poäng. Fel: " + e.getMessage());
        }
    }
}
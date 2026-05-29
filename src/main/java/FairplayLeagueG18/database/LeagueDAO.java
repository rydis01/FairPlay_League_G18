package FairplayLeagueG18.database;

import FairplayLeagueG18.model.League;
import FairplayLeagueG18.model.LeagueMember;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Hanterar all databasåtkomst för ligor och ligamedlemskap.
 * Kommunicerar med tabellerna Leagues och User_Leagues.
 */
public class LeagueDAO {

    /**
     * Skapar en ny liga och lägger till skaparen som första medlem.
     * Utförs som två separata INSERT-operationer mot Leagues och User_Leagues.
     *
     * @param leagueName  namnet på den nya ligan
     * @param adminUserId ID:t för användaren som skapar ligan
     * @param inviteCode  den unika invite-koden för ligan
     */
    public void createLeague(String leagueName, int adminUserId, String inviteCode) {
        String insertLeagueSql = "INSERT INTO Leagues (League_Name, Admin_User, Invite_Code) VALUES (?, ?, ?)";
        String insertMemberSql = "INSERT INTO User_Leagues (User_ID, League_ID, Total_Score) VALUES (?, ?, 0)";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) {
                System.err.println("Kunde inte ansluta till databasen!");
                return;
            }

            int generatedLeagueId = -1;

            try (PreparedStatement leagueStmt = conn.prepareStatement(insertLeagueSql, Statement.RETURN_GENERATED_KEYS)) {
                leagueStmt.setString(1, leagueName);
                leagueStmt.setInt(2, adminUserId);
                leagueStmt.setString(3, inviteCode);
                leagueStmt.executeUpdate();

                try (ResultSet rs = leagueStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedLeagueId = rs.getInt(1);
                    }
                }
            }

            if (generatedLeagueId != -1) {
                try (PreparedStatement memberStmt = conn.prepareStatement(insertMemberSql)) {
                    memberStmt.setInt(1, adminUserId);
                    memberStmt.setInt(2, generatedLeagueId);
                    memberStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte skapa ligan. Fel: " + e.getMessage());
        }
    }

    /**
     * Hämtar alla ligor i systemet.
     *
     * @return lista med alla League-objekt, eller tom lista om inga finns
     */
    public List<League> getAllLeagues() {
        List<League> leagues = new ArrayList<>();

        String sql =
                "SELECT League_Id, League_Name, Admin_User, Invite_Code, Created_at " +
                        "FROM Leagues";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return leagues;

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    League league = new League(
                            rs.getInt("League_Id"),
                            rs.getString("League_Name"),
                            rs.getString("Invite_Code"),
                            rs.getInt("Admin_User"),
                            rs.getTimestamp("Created_at").toLocalDateTime()
                    );
                    leagues.add(league);
                }
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte hämta ligorna. Fel: " + e.getMessage());
        }

        return leagues;
    }

    /**
     * Hämtar en liga baserat på dess ID, inklusive sorterad medlemslista.
     *
     * @param leagueId ID:t för ligan
     * @return League-objekt med medlemmar ifyllda, eller null om ligan inte hittas
     */
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
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        league = new League(
                                rs.getInt("League_Id"),
                                rs.getString("League_Name"),
                                rs.getString("Invite_Code"),
                                rs.getInt("Admin_User"),
                                rs.getTimestamp("Created_at").toLocalDateTime()
                        );
                        league.setMembers(getMembersByLeagueIdSortedByScore(leagueId));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte hämta ligan. Fel: " + e.getMessage());
        }

        return league;
    }

    /**
     * Hämtar en liga baserat på dess invite-kod, inklusive sorterad medlemslista.
     *
     * @param inviteCode ligens unika invite-kod
     * @return League-objekt med medlemmar ifyllda, eller null om koden inte finns
     */
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
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        league = new League(
                                rs.getInt("League_Id"),
                                rs.getString("League_Name"),
                                rs.getString("Invite_Code"),
                                rs.getInt("Admin_User"),
                                rs.getTimestamp("Created_at").toLocalDateTime()
                        );
                        league.setMembers(getMembersByLeagueIdSortedByScore(rs.getInt("League_Id")));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte hämta liga med inbjudningskod. Fel: " + e.getMessage());
        }

        return league;
    }

    /**
     * Hämtar alla ligor som en given användare är medlem i.
     *
     * @param userId ID:t för användaren
     * @return lista med League-objekt, eller tom lista om användaren inte är med i någon liga
     */
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
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        League league = new League(
                                rs.getInt("League_Id"),
                                rs.getString("League_Name"),
                                rs.getString("Invite_Code"),
                                rs.getInt("Admin_User"),
                                rs.getTimestamp("Created_at").toLocalDateTime()
                        );
                        league.setMembers(getMembersByLeagueIdSortedByScore(rs.getInt("League_Id")));
                        userLeagues.add(league);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte hämta användarens ligor. Fel: " + e.getMessage());
        }

        return userLeagues;
    }

    /**
     * Lägger till en användare som medlem i en liga med 0 poäng.
     *
     * @param leagueId ID:t för ligan
     * @param userId   ID:t för användaren som ska läggas till
     */
    public void addMember(int leagueId, int userId) {
        String sql = "INSERT INTO User_Leagues (User_ID, League_ID) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, leagueId);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte lägga till medlem. Fel: " + e.getMessage());
        }
    }

    /**
     * Tar bort en användare från en liga.
     *
     * @param leagueId ID:t för ligan
     * @param userId   ID:t för användaren som ska tas bort
     */
    public void removeMember(int leagueId, int userId) {
        String sql = "DELETE FROM User_Leagues WHERE League_ID = ? AND User_ID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, leagueId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte ta bort medlem. Fel: " + e.getMessage());
        }
    }

    /**
     * Kontrollerar om en liga med ett givet namn redan existerar.
     *
     * @param leagueName namnet som ska kontrolleras
     * @return true om ligan finns, annars false
     */
    public boolean leagueExists(String leagueName) {
        String sql = "SELECT 1 FROM Leagues WHERE League_Name = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return false;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, leagueName);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte kontrollera om liga finns. Fel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kontrollerar om en användare redan är medlem i en liga.
     * Används för att förhindra dubbelmedlemskap.
     *
     * @param leagueId ID:t för ligan
     * @param userId   ID:t för användaren
     * @return true om användaren redan är medlem, annars false
     */
    public boolean isMember(int leagueId, int userId) {
        String sql = "SELECT 1 FROM User_Leagues WHERE League_ID = ? AND User_ID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return false;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, leagueId);
                stmt.setInt(2, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte kontrollera medlemskap. Fel: " + e.getMessage());
        }

        return false;
    }

    /**
     * Räknar antalet medlemmar i en liga.
     * Används för att beräkna potten inför poängutdelning.
     *
     * @param leagueId ID:t för ligan
     * @return antal medlemmar, eller 0 om ligan inte hittas
     */
    public int countMembersByLeagueId(int leagueId) {
        String sql = "SELECT COUNT(*) FROM User_Leagues WHERE League_ID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return 0;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, leagueId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte räkna medlemmar. Fel: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Hämtar alla medlemmar i en liga sorterade efter totalpoäng, högst först.
     * Används för att visa leaderboard.
     *
     * @param leagueId ID:t för ligan
     * @return lista med LeagueMember-objekt sorterade efter totalpoäng
     */
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
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        LeagueMember member = new LeagueMember();
                        member.setUserId(rs.getInt("User_ID"));
                        member.setUsername(rs.getString("Username"));
                        member.setTotalScore(rs.getInt("Total_Score"));
                        members.add(member);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte hämta leaderboard. Fel: " + e.getMessage());
        }

        return members;
    }

    /**
     * Adderar poäng till en spelares totalsumma i en liga.
     * Skriver inte över befintlig poäng utan lägger till ovanpå.
     *
     * @param leagueId    ID:t för ligan
     * @param userId      ID:t för användaren
     * @param pointsToAdd antal poäng som ska adderas
     */
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
        } catch (SQLException e) {
            System.err.println("Kunde inte uppdatera poäng. Fel: " + e.getMessage());
        }
    }
}
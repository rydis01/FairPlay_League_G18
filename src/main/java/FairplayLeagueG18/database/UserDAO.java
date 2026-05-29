package FairplayLeagueG18.database;

import FairplayLeagueG18.model.User;
import FairplayLeagueG18.model.Role;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Hanterar all databasåtkomst för användare.
 * Kommunicerar med tabellen Users.
 */
public class UserDAO {

    /**
     * Sparar en ny användare i databasen.
     *
     * @param user User-objektet som ska sparas
     * @throws RuntimeException om användaren inte kan sparas
     */
    public void saveUser(User user) {
        String sql = "INSERT INTO Users (Username, Email, Password_Hash, Role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Kunde inte spara användare: " + e.getMessage(), e);
        }
    }

    /**
     * Uppdaterar en befintlig användares uppgifter i databasen.
     *
     * @param user User-objektet med uppdaterade värden, måste ha ett giltigt ID
     */
    public void updateUser(User user) {
        String sql = "UPDATE Users SET Username = ?, Email = ?, Password_Hash = ?, Role = ? WHERE User_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());
            stmt.setInt(5, user.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Användaren kunde inte uppdateras. Fel: " + e.getMessage());
        }
    }

    /**
     * Hämtar en användare baserat på dess ID.
     *
     * @param userID ID:t för användaren som ska hämtas
     * @return User-objekt om användaren hittas, annars null
     */
    public User getUserByID(int userID) {
        String sql = "SELECT * FROM Users WHERE User_ID = ?";
        User user = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Användaren kunde inte hämtas. Fel: " + e.getMessage());
        }

        return user;
    }

    /**
     * Hämtar en användare baserat på e-postadress.
     *
     * @param email e-postadressen för användaren som ska hämtas
     * @return User-objekt om användaren hittas, annars null
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        User user = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Användaren kunde inte hämtas. Fel: " + e.getMessage());
        }

        return user;
    }

    /**
     * Hämtar alla användare i systemet.
     * Obs: Endast avsedd för teständamål — ska inte användas i produktion
     * eftersom metoden exponerar lösenordshash för samtliga användare.
     *
     * @return lista med alla User-objekt
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM Users";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Kunde inte hämta alla användare. Fel: " + e.getMessage());
        }

        return users;
    }

    /**
     * Skapar ett User-objekt från en rad i ett ResultSet.
     * Hanterar null-värden för Created_at säkert.
     *
     * @param rs ResultSet positionerat på raden som ska läsas
     * @return ett ifyllt User-objekt
     * @throws SQLException om ett kolumnvärde inte kan läsas
     */
    private User mapUser(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("Created_at");
        LocalDateTime createdAt = (timestamp != null) ? timestamp.toLocalDateTime() : null;

        return new User(
                rs.getInt("User_ID"),
                rs.getString("Username"),
                rs.getString("Email"),
                rs.getString("Password_Hash"),
                Role.valueOf(rs.getString("Role")),
                createdAt
        );
    }
}
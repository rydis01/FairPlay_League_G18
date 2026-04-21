package FairplayLeagueG18.database;

import FairplayLeagueG18.model.User;
import FairplayLeagueG18.model.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * DAO står för Data Access Object
 *      Namn för systemet där en FairplayLeagueG18.service-klass skickar en beställning denna klass för att komma åt data från DB
 */

public class UserDAO {

    // Metod för att skapa ny användare (INSERT into FairplayLeagueG18.database)
    public void saveUser(User user) {

        String sql = "INSERT INTO Users (Username, Email, Password_Hash, Role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());

            stmt.executeUpdate();
            System.out.println("Användaren " + user.getUsername() + " lades till i databsen!");

        } catch (SQLException e) {
            // System.out.println("Användaren kunde inte sparas. Fel: " + e.getMessage()); <– Avkommenterat för att skicka throw istället:
            throw new RuntimeException("Kunde inte spara användare: " + e.getMessage(), e);
        }
    }
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
            System.out.println("Användaren " + user.getUsername() + " uppdaterades i databsen!");

        } catch (SQLException e) {
            System.out.println("Användaren kunde inte uppdateras. Fel: " + e.getMessage());
        }
    }

    // Metod för att hämta en användare (SELECT from FairplayLeagueG18.database)
    public User getUserByID(int userID) {

        String sql = "SELECT * FROM Users WHERE User_ID = ?";
        User user = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userID);
            ResultSet rs = stmt.executeQuery(); // Query används istället för Update när vi vill ha 'tillbaks' data

            /**
             * Förklaring till nedanstående:
             *'rs' står för ResultSet, alltså kollar rs.next() om det finns någon rad med svar att läsa.
             * Om den hittar data flyttar den pekaren dit och returnerar true, vi går då in i if-satsen och hämtar datan
             * ===
             */
            if (rs.next()) {
                /**
                 * Extraherar skapandetiden (java.sql.Timestamp) från ResultSet och
                 * konverterar den säkert till LocalDateTime. Förhindrar
                 * NullPointerException om datum saknas i databasen.
                 */
                Timestamp timestamp = rs.getTimestamp("Created_at");
                var createdAt = (timestamp != null) ? timestamp.toLocalDateTime() : null;

                // Skapar användare-objektet som ska returneras
               user = new User (
                   rs.getInt("User_ID"),
                   rs.getString("Username"),
                   rs.getString("Email"),
                   rs.getString("Password_Hash"),
                       Role.valueOf(rs.getString("Role")),
                   createdAt
               );
            }

        } catch (SQLException e) {
            System.out.println("Användaren kunde inte hämtas. Fel: " + e.getMessage());
        }

        return user; // Returnerar hela användare-objektet till 'Service-klassen'
    }

    public User getUserByEmail(String email) {

        String sql = "SELECT * FROM Users WHERE Email = ?";
        User user = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery(); // Query används istället för Update när vi vill ha 'tillbaks' data

            /**
             * Förklaring till nedanstående:
             *'rs' står för ResultSet, alltså kollar rs.next() om det finns någon rad med svar att läsa.
             * Om den hittar data flyttar den pekaren dit och returnerar true, vi går då in i if-satsen och hämtar datan
             * ===
             */
            if (rs.next()) {
                /**
                 * Extraherar skapandetiden (java.sql.Timestamp) från ResultSet och
                 * konverterar den säkert till LocalDateTime. Förhindrar
                 * NullPointerException om datum saknas i databasen.
                 */
                Timestamp timestamp = rs.getTimestamp("Created_at");
                var createdAt = (timestamp != null) ? timestamp.toLocalDateTime() : null;

                // Skapar användare-objektet som ska returneras
                user = new User (
                        rs.getInt("User_ID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("Password_Hash"),
                        Role.valueOf(rs.getString("Role")),
                        createdAt
                );
            }

        } catch (SQLException e) {
            System.out.println("Användaren kunde inte hämtas. Fel: " + e.getMessage());
        }

        return user; // Returnerar hela användare-objektet till 'Service-klassen'
    }
}

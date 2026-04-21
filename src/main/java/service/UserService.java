package service;

import database.UserDAO;
import model.User;
import model.Role;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Handles users and authentication.
 * Responsible for registration, login and user management.
 *
 * @author Carl
 */
public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Registration

    /**
     * Registers a new user in the system.
     *
     * @param username The username for the new user
     * @param email The email address for the new user (must be unique)
     * @param password The password for the new user
     * @author Carl & Hugo
     */
    public String registerUser(String username, String email, String password) {
        if (password.length() < 8) {
            return "MISSLYCKADES: Lösenordet måste vara minst 8 tecken långt.";
        }

        // Vi ber BCrypt att generera en unik salt och hasha lösenordet.
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            User newUser = new User(username, email, hashedPassword, Role.Player);
            userDAO.saveUser(newUser);
            return "GODKÄNT: Användaren '" + username + "' skapades i databasen!";

        } catch (Exception e) {
            return "MISSLYCKADES (Databasfel): E-posten kanske redan finns?\nInfo: " + e.getMessage();
        }
    }

    // Login

    /**
     * Logs in a user by verifying email and password.
     *
     * @param email The email address of the user
     * @param password The password of the user
     * @return true if login succeeds, false otherwise
     * @author Carl
     */
    public boolean loginUser(String email, String password) {
        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            // Använder BCrypt för att jämföra inmatat lösenord med det krypterade i databasen
            return BCrypt.checkpw(password, user.getPasswordHash());
        }
        return false;
    }

    /**
     * Logs out a user from the system.
     *
     * @param userId The ID of the user to be logged out
     * @return true if logout succeeds
     * @author Carl
     */
    public boolean logoutUser(int userId) {
        return true;
    }

    /**
     * Changes the password for a user.
     *
     * @param userId The ID of the user whose password will be changed
     * @param newPassword The new password
     * @author Carl
     */
    public void changePassword(int userId, String newPassword) {
        User user = userDAO.getUserByID(userId);
        if (user != null) {
            // Hasha det Nya lösenordet innan det sparas!
            String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPasswordHash(hashedNewPassword);
            userDAO.updateUser(user);
        }
    }

    // Profile

    /**
     * Displays the user's profile as a formatted string.
     *
     * @param userId The ID of the user whose profile will be displayed
     * @return A formatted string with the user's profile information, or an error message if the user is not found
     * @author Carl
     */
    public String viewUserProfile(int userId) {
        User user = userDAO.getUserByID(userId);
        if (user != null) {
            return "Användarprofil:\n" +
                    "ID: " + user.getId() + "\n" +
                    "Användarnamn: " + user.getUsername() + "\n" +
                    "Email: " + user.getEmail() + "\n" +
                    "Roll: " + user.getRole().name() + "\n" +
                    "Skapad: " + user.getCreatedAt();
        }
        return "Användaren hittades inte.";
    }

    // Get / Set / Helpers

    /**
     * Retrieves a user based on user ID.
     *
     * @param userId The ID of the user to retrieve
     * @return The User object if it exists, null otherwise
     * @author Carl
     */
    public User getUserById(int userId) {
        return userDAO.getUserByID(userId);
    }

    /**
     * Checks if a user exists in the system.
     *
     * @param userId The ID of the user to check
     * @return true if the user exists, false otherwise
     * @author Carl
     */
    public boolean userExists(int userId) {
        User user = userDAO.getUserByID(userId);
        return user != null;
    }
}
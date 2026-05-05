package FairplayLeagueG18.service;

import FairplayLeagueG18.database.UserDAO;
import FairplayLeagueG18.model.User;
import FairplayLeagueG18.model.Role;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Handles users and authentication.
 * Responsible for registration, login and user management.
 *
 * @author Carl
 */
@Service
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
    public void registerUser(String username, String email, String password) {
        // TODO:  Add password check - must be at least 8 characters long.
        // Fixas här nedan:

        if (password.length() < 8) {
            System.out.println("Lösenordet måste vara minst 8 tecken långt.");
            return;
        }


        // TODO: Password hashing.
        // Fixas här nedan:

        // Vi ber BCrypt att generera en unik "salt" och hasha lösenordet.
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());


        // TODO: Add check so email doesn't already exist in database since email is unique.
        // Fixas här nedan:

        try {
            User newUser = new User(username, email, hashedPassword, Role.Player);
            userDAO.saveUser(newUser);
            System.out.println("TEST GODKÄNT: Användaren skapades i databasen!");

        } catch (Exception e) {
            System.out.println("TEST MISSLYCKADES (Databasfel): " + e.getMessage());
            // Om e.getMessage() klagar på "duplicate key value violates unique constraint",
            // –> Krav F-REG-1.1 = Godkänt!
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
            // TODO: Hash the entered password and compare it with the hashed password in the database
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
            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPasswordHash(hashed);
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

    public User getUserByEmail(String email){
        return userDAO.getUserByEmail(email);
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

    public StringBuilder getUsers() {
        StringBuilder string = new StringBuilder();
        ArrayList<User> users = new ArrayList<User>();

        users = userDAO.getAllUsers();

        for (User us : users) {
            string.append(us.getEmail())
                    .append(" : ")
                    .append(us.getPasswordHash())
                    .append("\n");
        }
        return string;
    }
}

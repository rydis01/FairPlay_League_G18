package service;

import database.UserDAO;
import model.User;
import model.Role;

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
     * @author Carl
     */
    public void registerUser(String username, String email, String password) {
        // TODO: Add check so email doesn't already exist in database since email is unique.
        // TODO: Add password check - must be at least 8 characters long.
        // TODO: Password hashing will come later
        User newUser = new User(username, email, password, Role.Player);
        userDAO.saveUser(newUser);
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
            return user.getPasswordHash().equals(password);
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
            user.setPasswordHash(newPassword);
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
        User user = getUserById(userId);
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
        User user = getUserById(userId);
        return user != null;
    }
}

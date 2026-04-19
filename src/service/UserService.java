package service;

import database.UserDAO;
import model.User;
import model.Role;

/**
 * Hanterar användare och autentisering.
 * Ansvarar för registrering, inloggning och användarhantering.
 */
public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Registrera en ny användare
    public void registerUser(String username, String email, String password) {
        User newUser = new User(username, email, password, Role.Player);
        userDAO.saveUser(newUser);
    }

    // Hämta en användare med ID
    public User getUserById(int userId) {
        return userDAO.getUserByID((long) userId);
    }

    // Kontrollera om en användare existerar
    public boolean userExists(int userId) {
        User user = userDAO.getUserByID((long) userId);
        return user != null;
    }
}

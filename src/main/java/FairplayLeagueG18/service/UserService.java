package FairplayLeagueG18.service;

import FairplayLeagueG18.database.UserDAO;
import FairplayLeagueG18.model.User;
import FairplayLeagueG18.model.Role;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * Hanterar användare och autentisering.
 * Ansvarar för registrering, inloggning, utloggning och användarhantering.
 * @author Hugo Werntoft, Carl Rydengård & Gustav Johnsson
 */
@Service
public class UserService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserDAO userDAO;

    /**
     * Standardkonstruktor som initierar dataåtkomstlagret för användare.
     */
    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Registrerar en ny användare i systemet.
     * Lösenordet hashas med BCrypt innan det sparas.
     *
     * @param username användarnamnet för den nya användaren
     * @param email    e-postadressen för den nya användaren (måste vara unik)
     * @param password lösenordet i klartext
     * @return true om registreringen lyckades, annars false
     */
    public boolean registerUser(String username, String email, String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            System.err.println("Lösenordet måste vara minst " + MIN_PASSWORD_LENGTH + " tecken långt.");
            return false;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            User newUser = new User(username, email, hashedPassword, Role.Player);
            userDAO.saveUser(newUser);
        } catch (RuntimeException e) {
            System.err.println("Kunde inte registrera användare: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Loggar in en användare genom att verifiera e-post och lösenord mot databasen.
     *
     * @param email    användarens e-postadress
     * @param password lösenordet i klartext som ska verifieras
     * @return true om inloggningen lyckades, annars false
     */
    public boolean loginUser(String email, String password) {
        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            return BCrypt.checkpw(password, user.getPasswordHash());
        }
        return false;
    }

    /**
     * Byter lösenord för en användare.
     * Det nya lösenordet hashas med BCrypt innan det sparas.
     *
     * @param userId      ID:t för användaren vars lösenord ska bytas
     * @param newPassword det nya lösenordet i klartext
     */
    public void changePassword(int userId, String newPassword) {
        User user = getUserById(userId);
        if (user != null) {
            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPasswordHash(hashed);
            userDAO.updateUser(user);
        }
    }

    /**
     * Returnerar en användares profilinformation som en formaterad sträng.
     *
     * @param userId ID:t för användaren vars profil ska visas
     * @return formaterad sträng med profilinformation, eller felmeddelande om användaren inte hittas
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

    /**
     * Hämtar en användare baserat på användar-ID.
     *
     * @param userId ID:t för användaren som ska hämtas
     * @return User-objekt om användaren finns, annars null
     */
    public User getUserById(int userId) {
        return userDAO.getUserByID(userId);
    }

    /**
     * Hämtar en användare baserat på e-postadress.
     *
     * @param email e-postadressen för användaren som ska hämtas
     * @return User-objekt om användaren finns, annars null
     */
    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    /**
     * Kontrollerar om en användare med given e-postadress finns i systemet.
     *
     * @param email e-postadressen som ska kontrolleras
     * @return true om användaren finns, annars false
     */
    public boolean userExists(String email) {
        return userDAO.getUserByEmail(email) != null;
    }
}
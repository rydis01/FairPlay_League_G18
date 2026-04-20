package database;

import java.util.Scanner;

import database.UserDAO;
import database.CouponDAO;
import database.LeagueDAO;
import database.MatchDAO;
import database.RoundDAO;

import model.User;
import model.Role;

import org.mindrot.jbcrypt.BCrypt;

public class TestingDatabase {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();

        System.out.println("=== TEST: Registrering ===");

        System.out.print("Ange användarnamn: ");
        String username = scanner.nextLine();

        System.out.print("Ange e-post: ");
        String email = scanner.nextLine();

        System.out.print("Ange lösenord: ");
        String password = scanner.nextLine();

        // ---------------------------------------------------------
        // KRAV: F-REG-1.2 (Lösenordslängd)
        // ---------------------------------------------------------
        if (password.length() < 8) {
            System.out.println("TEST MISSLYCKADES: Lösenordet måste vara minst 8 tecken långt.");
            return; // Avbryter programmet
        }

        // ---------------------------------------------------------
        // KRAV: F-REG-1.3 (Hashning)
        // ---------------------------------------------------------
        // Vi ber BCrypt att generera en unik "salt" och hasha lösenordet.
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        System.out.println("Det riktiga lösenordet var: " + password);
        System.out.println("Det som (skulle) sparas i databasen är: " + hashedPassword);

        System.out.println("Lösenordet är hashat!");

        // ---------------------------------------------------------
        // KRAV: F-REG-1.0 & F-REG-1.1 (Spara & Unik E-post)
        // ---------------------------------------------------------
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
}

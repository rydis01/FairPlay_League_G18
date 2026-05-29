package FairplayLeagueG18.model;

import java.time.LocalDateTime;

/**
 * Representerar en användare i systemet.
 * Innehåller inloggningsuppgifter, roll (t.ex. spelare eller admin) och grundläggande profilinformation.
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private LocalDateTime createdAt;

    /**
     * Standardkonstruktor som skapar en tom användare.
     */
    public User() {
    }

    /**
     * Skapar en ny användare. Används vid registrering i systemet.
     * Tiden för skapandet sätts automatiskt till aktuell tid.
     *
     * @param username     användarens valda användarnamn
     * @param email        användarens e-postadress (unik för inloggning)
     * @param passwordHash det BCrypt-hashade lösenordet
     * @param role         användarens roll i systemet
     */
    public User(String username, String email, String passwordHash, Role role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role; // (Notera: Tänk på UPPER_SNAKE_CASE för enums om ni ska ändra till PLAYER/ADMIN)
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Skapar ett befintligt användarobjekt. Används främst vid inläsning från databasen.
     *
     * @param id           användarens unika ID i databasen
     * @param username     användarnamn
     * @param email        e-postadress
     * @param passwordHash det hashade lösenordet
     * @param role         användarens roll
     * @param createdAt    tidpunkten då kontot skapades
     */
    public User(int id, String username, String email, String passwordHash, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
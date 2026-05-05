package FairplayLeagueG18.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private FairplayLeagueG18.model.Role role;
    private LocalDateTime createdAt;

    public User() {
    }

    // Skapa ny användare (id sätts av databasen)
    public User(String username, String email, String passwordHash, FairplayLeagueG18.model.Role role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Läsa från databasen (alla fält)
    public User(int id, String username, String email, String passwordHash, FairplayLeagueG18.model.Role role, LocalDateTime createdAt) {
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

    public void setPasswordHash(String password) { this.passwordHash = password; }

    public FairplayLeagueG18.model.Role getRole() { return role; }

    public void setRole(FairplayLeagueG18.model.Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "UserInfo:" + '\'' +
                "id=" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
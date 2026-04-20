package model;

import java.time.LocalDateTime;

public class League {
    private int id;
    private String name;
    private String inviteCode;
    private int createdByUserId;
    private LocalDateTime createdAt;

    public League() {
    }

    // Skapa ny liga
    public League(String name, String inviteCode, int createdByUserId) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdByUserId = createdByUserId;
        this.createdAt = LocalDateTime.now();
    }

    // Läsa från databasen
    public League(int id, String name, String inviteCode, int createdByUserId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdByUserId = createdByUserId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getInviteCode() { return inviteCode; }

    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public int getCreatedBy() { return createdByUserId; }

    public void setCreatedBy(int createdBy) { this.createdByUserId = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
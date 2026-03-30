package model;

import java.time.LocalDateTime;

public class League {
    private Long id;
    private String name;
    private String inviteCode;
    private Long createdBy;
    private LocalDateTime createdAt;

    public League() {
    }

    // Skapa ny liga
    public League(String name, String inviteCode, Long createdBy) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    // Läsa från databasen
    public League(Long id, String name, String inviteCode, Long createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getInviteCode() { return inviteCode; }

    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public Long getCreatedBy() { return createdBy; }

    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
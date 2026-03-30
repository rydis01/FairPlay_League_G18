package model;

import java.time.LocalDateTime;

public class Round {
    private Long id;
    private int gameweek;
    private RoundStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;

    public Round() {
    }

    // Skapa ny omgång
    public Round(int gameweek, LocalDateTime deadline) {
        this.gameweek = gameweek;
        this.status = RoundStatus.Open;
        this.deadline = deadline;
        this.createdAt = LocalDateTime.now();
    }

    // Läsa från databasen
    public Round(Long id, int gameweek, RoundStatus status, LocalDateTime deadline, LocalDateTime createdAt) {
        this.id = id;
        this.gameweek = gameweek;
        this.status = status;
        this.deadline = deadline;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public int getGameweek() { return gameweek; }

    public void setGameweek(int gameweek) { this.gameweek = gameweek; }

    public RoundStatus getStatus() { return status; }

    public void setStatus(RoundStatus status) { this.status = status; }

    public LocalDateTime getDeadline() { return deadline; }

    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
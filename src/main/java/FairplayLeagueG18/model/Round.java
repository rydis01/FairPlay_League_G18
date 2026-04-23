package FairplayLeagueG18.model;

import java.time.LocalDateTime;
import java.util.List;

public class Round {
    private int id;
    private int leagueId;
    private int gameweek;
    private FairplayLeagueG18.model.RoundStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private List<Match> matches;

    public Round() {
    }

    //Skapa ny omgång
    public Round(int leagueId, int gameweek, LocalDateTime deadline) {
        this.leagueId = leagueId;
        this.gameweek = gameweek;
        this.status = RoundStatus.Open;
        this.deadline = deadline;
        this.createdAt = LocalDateTime.now();
        this.matches = null;
    }

    //Läsa från databasen
    public Round(int id, int leagueId, int gameweek, FairplayLeagueG18.model.RoundStatus status, LocalDateTime deadline, LocalDateTime createdAt) {
        this.id = id;
        this.leagueId = leagueId;
        this.gameweek = gameweek;
        this.status = status;
        this.deadline = deadline;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLeagueId() { return leagueId; }
    public void setLeagueId(int leagueId) { this.leagueId = leagueId; }

    public int getGameweek() { return gameweek; }
    public void setGameweek(int gameweek) { this.gameweek = gameweek; }

    public FairplayLeagueG18.model.RoundStatus getStatus() { return status; }
    public void setStatus(FairplayLeagueG18.model.RoundStatus status) { this.status = status; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Match> getMatches() { return matches; }
    public void setMatches(List<Match> matches) { this.matches = matches; }
}
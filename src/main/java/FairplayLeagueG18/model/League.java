package FairplayLeagueG18.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class League {
    private int id;
    private String name;
    private String inviteCode;
    private int createdBy;
    private LocalDateTime createdAt;
    private List<LeagueMember> members;

    public League() {
    }

    // Skapa ny liga
    public League(String name, String inviteCode, int createdBy) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.members = new ArrayList<>();
    }

    // Läsa från databasen
    public League(int id, String name, String inviteCode, int createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getInviteCode() { return inviteCode; }

    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public int getCreatedBy() { return createdBy; }

    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setMembers(List<LeagueMember> members){
        this.members = members;
    }

    public List<LeagueMember> getMembers(){
        return members;
    }
}
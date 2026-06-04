package FairplayLeagueG18.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representerar en liga i systemet där användare kan tävla mot varandra.
 * Innehåller ligans grunduppgifter samt en lista över dess medlemmar.
 * @author Carl Rydengård & Gustav Johnsson
 */
public class League {
    private int id;
    private String name;
    private String inviteCode;
    private int createdBy;
    private LocalDateTime createdAt;
    private List<LeagueMember> members;

    /**
     * Standardkonstruktor som skapar en tom liga.
     */
    public League() {
    }

    /**
     * Skapar en ny liga. Används när en användare grundar en ny liga i systemet.
     * Skapandetid sätts automatiskt till aktuell tid och medlemslistan initieras.
     *
     * @param name       namnet på den nya ligan
     * @param inviteCode en unik kod som används för att bjuda in nya medlemmar
     * @param createdBy  ID för användaren som skapar ligan (admin)
     */
    public League(String name, String inviteCode, int createdBy) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.members = new ArrayList<>();
    }

    /**
     * Skapar ett befintligt liga-objekt. Används främst vid inläsning från databasen.
     *
     * @param id         ligans unika ID i databasen
     * @param name       ligans namn
     * @param inviteCode ligans inbjudningskod
     * @param createdBy  ID för användaren som skapade ligan
     * @param createdAt  tidpunkten då ligan skapades
     */
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

    public List<LeagueMember> getMembers() { return members; }
    public void setMembers(List<LeagueMember> members) { this.members = members; }
}
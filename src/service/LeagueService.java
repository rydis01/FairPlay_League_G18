package service;

import database.LeagueDAO;
import model.League;

/**
 * Hanterar ligor och medlemskap.
 * Ansvarar för att skapa ligor, hantera invite codes, lägga till/ta bort medlemmar.
 */
public class LeagueService {
    private LeagueDAO leagueDAO;

    public LeagueService() {
        this.leagueDAO = new LeagueDAO();
    }
    // Skapa en ny liga
    public void createLeague(String leagueName, int creatorId) {
        League newLeague = new League(leagueName, generateInviteCode(), creatorId);
    }

    // Generera en invite code för en liga
    public void addMemberToLeague(int leagueId, int userId) {
        // Logik för att lägga till en medlem i ligan
    }

    // Lägg till en medlem i ligan med en invite code
    public void removeMemberFromLeague(int leagueId, int userId) {
        // Logik för att ta bort en medlem från ligan
    }

    public String generateInviteCode(){
        return "1";
    }
}

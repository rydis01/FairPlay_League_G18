package service;

import database.LeagueDAO;
import model.League;
import model.LeagueMember;

import java.util.List;
import java.util.UUID;

// Hanterar ligor och medlemskap.
// Pratar med LeagueDAO för databasanrop.
public class LeagueService {
    private LeagueDAO leagueDAO;

    public LeagueService() {
        this.leagueDAO = new LeagueDAO();
    }

    // Skapar en ny liga — invite code genereras automatiskt
    // createLeague i DAO:n hanterar både ligan och skaparens medlemskap
    public void createLeague(String leagueName, int creatorUserId) {
        String inviteCode = generateInviteCode();
        leagueDAO.createLeague(leagueName, creatorUserId, inviteCode);
    }

    // Gå med i en liga via invite code
    // Returnerar false om koden inte finns eller om användaren redan är med
    public boolean joinLeague(String inviteCode, int userId) {
        League league = leagueDAO.getLeagueByInviteCode(inviteCode);
        if (league == null) {
            return false;
        }

        if (leagueDAO.isMember(league.getId(), userId)) {
            return false;
        }

        leagueDAO.addMember(league.getId(), userId);
        return true;
    }

    // Tar bort en användare från en liga
    public void removeMember(int leagueId, int userId) {
        leagueDAO.removeMember(leagueId, userId);
    }

    // Hämtar leaderboard — sorterad på poäng, högst först
    public List<LeagueMember> getLeaderboard(int leagueId) {
        return leagueDAO.getMembersByLeagueIdSortedByScore(leagueId);
    }

    // Hämtar alla ligor som en användare är med i
    public List<League> getLeaguesByUserId(int userId) {
        return leagueDAO.getLeaguesByUserId(userId);
    }

    // Hämtar en liga baserat på id
    public League getLeagueById(int leagueId) {
        return leagueDAO.getLeagueById(leagueId);
    }

    // Räknar antal medlemmar — används av ScoringService för potten
    public int getMemberCount(int leagueId) {
        return leagueDAO.countMembersByLeagueId(leagueId);
    }

    // Genererar en unik invite code på 8 tecken
    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
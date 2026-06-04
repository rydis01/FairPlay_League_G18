package FairplayLeagueG18.service;

import FairplayLeagueG18.database.LeagueDAO;
import FairplayLeagueG18.model.League;
import FairplayLeagueG18.model.LeagueMember;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Hanterar affärslogik för ligor och ligamedlemskap.
 * Kommunicerar med LeagueDAO för databasoperationer.
 * @author Carl Rydengård & Gustav Johnsson
 */
@Service
public class LeagueService {

    // Konstant för att undvika "magiska nummer" i koden
    private static final int INVITE_CODE_LENGTH = 8;

    private final LeagueDAO leagueDAO;

    /**
     * Standardkonstruktor som initierar dataåtkomstlagret.
     */
    public LeagueService() {
        this.leagueDAO = new LeagueDAO();
    }

    /**
     * Skapar en ny liga. En unik inbjudningskod genereras automatiskt.
     * Skaparen läggs automatiskt till som första medlem via databaslagret.
     *
     * @param leagueName    namnet på den nya ligan
     * @param creatorUserId ID för användaren som skapar ligan
     */
    public void createLeague(String leagueName, int creatorUserId) {
        String inviteCode = generateInviteCode();
        leagueDAO.createLeague(leagueName, creatorUserId, inviteCode);
    }

    /**
     * Låter en användare gå med i en befintlig liga via en inbjudningskod.
     *
     * @param inviteCode den unika koden för ligan
     * @param userId     ID för användaren som vill gå med
     * @return true om användaren lades till, false om koden är ogiltig eller användaren redan är medlem
     */
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

    /**
     * Hämtar alla ligor i systemet.
     *
     * @return en lista med alla League-objekt
     */
    public List<League> getAllLeagues() {
        return leagueDAO.getAllLeagues();
    }

    /**
     * Kontrollerar om en liga med det angivna namnet redan existerar.
     *
     * @param leagueName namnet att kontrollera
     * @return true om ligan finns, annars false
     */
    public boolean leagueExists(String leagueName) {
        return leagueDAO.leagueExists(leagueName);
    }

    /**
     * Tar bort en medlem från en liga.
     *
     * @param leagueId ID för ligan
     * @param userId   ID för användaren som ska tas bort
     */
    public void removeMember(int leagueId, int userId) {
        leagueDAO.removeMember(leagueId, userId);
    }

    /**
     * Hämtar ledartavlan (leaderboard) för en specifik liga.
     *
     * @param leagueId ID för ligan
     * @return en lista med medlemmar sorterade efter poäng, högst först
     */
    public List<LeagueMember> getLeaderboard(int leagueId) {
        return leagueDAO.getMembersByLeagueIdSortedByScore(leagueId);
    }

    /**
     * Hämtar alla ligor som en specifik användare är medlem i.
     *
     * @param userId ID för användaren
     * @return en lista med ligor där användaren är medlem
     */
    public List<League> getLeaguesByUserId(int userId) {
        return leagueDAO.getLeaguesByUserId(userId);
    }

    /**
     * Hämtar en specifik liga baserat på dess ID.
     *
     * @param leagueId ID för ligan
     * @return ett League-objekt, eller null om ligan inte hittas
     */
    public League getLeagueById(int leagueId) {
        return leagueDAO.getLeagueById(leagueId);
    }

    /**
     * Hämtar antalet medlemmar i en specifik liga.
     * Används till exempel av ScoringService för att beräkna prispotten.
     *
     * @param leagueId ID för ligan
     * @return antal medlemmar i ligan
     */
    public int getMemberCount(int leagueId) {
        return leagueDAO.countMembersByLeagueId(leagueId);
    }

    /**
     * Genererar en slumpmässig inbjudningskod.
     *
     * @return en unik sträng med versaler
     */
    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, INVITE_CODE_LENGTH).toUpperCase();
    }
}
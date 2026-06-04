package FairplayLeagueG18.service;

import FairplayLeagueG18.database.RoundDAO;
import FairplayLeagueG18.model.Round;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Hanterar spelomgångar (gameweeks).
 * Ansvarar för att skapa omgångar, kolla deadline och hantera status.
 * @author Carl Rydengård
 */
@Service
public class RoundService {

    private final RoundDAO roundDAO;

    /**
     * Standardkonstruktor som initierar dataåtkomstlagret för omgångar.
     */
    public RoundService() {
        this.roundDAO = new RoundDAO();
    }

    /**
     * Hämtar de faktiska resultaten för alla matcher i en specifik spelomgång.
     *
     * @param roundId ID för omgången
     * @return en lista med resultattecken (t.ex. "1", "X", "2") för omgångens matcher
     */
    public List<String> getMatchResults(int roundId) {
        return roundDAO.getResultsFromRound(roundId);
    }

    /**
     * Hämtar en specifik spelomgång inklusive dess tillhörande matcher och status.
     *
     * @param roundId ID för omgången som ska hämtas
     * @return ett Round-objekt som representerar spelomgången
     */
    public Round getRound(int roundId) {
        return roundDAO.getRound(roundId);
    }
}
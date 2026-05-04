package FairplayLeagueG18.service;

import FairplayLeagueG18.database.RoundDAO;
import FairplayLeagueG18.model.Round;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Hanterar spelomgångar (gameweeks).
 * Ansvarar för att skapa omgångar, kolla deadline och hantera status.
 */
@Service
public class RoundService {
    private RoundDAO roundDAO;

    public RoundService(){
        this.roundDAO = new RoundDAO();
    }

    public List <String> getMatchResults(int roundId){
        return roundDAO.getResultsFromRound(roundId);
    }

    public Round getRound(int roundId){
        return roundDAO.getRound(roundId);
    }
}

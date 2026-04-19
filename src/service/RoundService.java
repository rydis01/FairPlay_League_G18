package service;

import database.MatchDAO;

import java.util.List;

/**
 * Hanterar spelomgångar (gameweeks).
 * Ansvarar för att skapa omgångar, kolla deadline och hantera status.
 */
public class RoundService {
    private MatchDAO matchDAO;

    public RoundService(){
        this.matchDAO = new MatchDAO();
    }
    public List<String> getMatchResults(int roundId){
        return matchDAO.getResultsFromRound(roundId);
    }
}

package service;

import database.RoundDAO;
import model.Round;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoundService {

    private final RoundDAO roundDAO;

    @Autowired
    public RoundService(RoundDAO roundDAO){
        this.roundDAO = roundDAO;
    }

    public List<String> getMatchResults(int roundId){
        return roundDAO.getResultsFromRound(roundId);
    }

    public Round getRound(int roundId){
        return roundDAO.getRound(roundId);
    }
}
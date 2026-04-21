package FairplayLeagueG18.service;

import FairplayLeagueG18.api.LiveScoreService;
import FairplayLeagueG18.api.LiveScoreMapper;
import FairplayLeagueG18.database.MatchDAO;
import FairplayLeagueG18.model.Match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchService {

    private final LiveScoreService apiService;
    private final MatchDAO matchDao;

    // @Autowired säger till Spring Boot att automatiskt skicka in dessa klasser här
    @Autowired
    public MatchService(LiveScoreService apiService, MatchDAO matchDao) {
        this.apiService = apiService;
        this.matchDao = matchDao;
    }

    // Denna annotation ersätter din gamla ScheduledExecutorService!
    // fixedRate = 300000 betyder att den körs var 300 000:e millisekund (dvs var 5:e minut)
    @Scheduled(fixedRate = 300000)
    public void fetchAndProcessMatches() {
        System.out.println("\n[" + LocalDateTime.now() + "] MatchService: Skrapar API efter matcher...");

        try {
            String rawJsonData = apiService.fetchAllsvenskanData();

            if (rawJsonData != null) {
                List<Match> cleanMatches = LiveScoreMapper.parseMatches(rawJsonData);

                // Här anropas din DAO som lagrar i databasen!
                matchDao.saveMatches(cleanMatches);

                System.out.println("-> MatchService sparade " + cleanMatches.size() + " matcher till databasen.");
            }
        } catch (Exception e) {
            System.err.println("-> Ett fel uppstod i MatchService: " + e.getMessage());
        }
    }
}
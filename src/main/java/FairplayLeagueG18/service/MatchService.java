package FairplayLeagueG18.service;

import FairplayLeagueG18.api.LiveScoreService;
import FairplayLeagueG18.api.LiveScoreMapper;

import FairplayLeagueG18.database.CouponDAO;
import FairplayLeagueG18.database.LeagueDAO;
import FairplayLeagueG18.database.MatchDAO;
import FairplayLeagueG18.database.RoundDAO;
import FairplayLeagueG18.model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MatchService {

    private final LiveScoreService apiService;
    private final MatchDAO matchDao;
    private final ScheduledExecutorService scheduler;
    private final ScoreService scoreService;
    private final RoundDAO roundDAO;
    private final CouponDAO couponDAO;

    public MatchService(ScoreService scoreService) {
        this.apiService = new LiveScoreService();
        this.matchDao = new MatchDAO();
        this.roundDAO = new RoundDAO();
        this.couponDAO = new CouponDAO();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.scoreService = scoreService;
    }

    public void startAutoUpdate() {
        Runnable task = () -> {
            System.out.println("\n[" + LocalDateTime.now() + "] MatchService: Skrapar API efter matcher...");
            fetchAndProcessMatches();
        };

        // Kör direkt (0), sedan var 5:e minut
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);
    }

    private void fetchAndProcessMatches() {
        try {
            String rawJsonData = apiService.fetchAllsvenskanData();

            if (rawJsonData != null) {
                List<Match> cleanMatches = LiveScoreMapper.parseMatches(rawJsonData);
                matchDao.saveMatches(cleanMatches);
                System.out.println("-> MatchService sparade " + cleanMatches.size() + " matcher till databasen.");
            }
        } catch (Exception e) {
            System.err.println("-> Ett fel uppstod i MatchService: " + e.getMessage());
        }
    }

    public void checkAndSettleFinishedRounds() {
        List<Integer> finishedGameweeks = roundDAO.getFinishedGameweekIds();

        for (int gameweekId : finishedGameweeks) {
            if (!roundDAO.isAlreadySettled(gameweekId)) {
                List<Integer> leagueIds = couponDAO.getLeagueIdsForGameweek(gameweekId);

                for (int leagueId : leagueIds) {
                    System.out.println("-> Rättar omgång " + gameweekId + " för liga " + leagueId);
                    scoreService.settleRound(gameweekId, leagueId);
                }

                roundDAO.markAsSettled(gameweekId);
            }
        }
    }

    public List<Match> getMatchesByGameweek(int gameweekId) {
        return matchDao.getMatchesByGameweek(gameweekId);
    }
}
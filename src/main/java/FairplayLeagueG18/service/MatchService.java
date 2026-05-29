package FairplayLeagueG18.service;

import FairplayLeagueG18.api.LiveScoreService;
import FairplayLeagueG18.api.LiveScoreMapper;
import FairplayLeagueG18.database.CouponDAO;
import FairplayLeagueG18.database.MatchDAO;
import FairplayLeagueG18.database.RoundDAO;
import FairplayLeagueG18.model.Match;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hanterar affärslogiken för matcher.
 * Ansvarar för att regelbundet hämta in matchuppdateringar från externa API:er
 * samt för att trigga rättning av omgångar när alla matcher är färdigspelade.
 */
@Service
public class MatchService {

    // Konstanter för schemaläggaren (ersätter tidigare "magiska nummer")
    private static final int THREAD_POOL_SIZE = 1;
    private static final int INITIAL_DELAY = 0;
    private static final int UPDATE_INTERVAL_MINUTES = 5;

    private final LiveScoreService apiService;
    private final MatchDAO matchDao;
    private final ScheduledExecutorService scheduler;
    private final ScoreService scoreService;
    private final RoundDAO roundDAO;
    private final CouponDAO couponDAO;

    /**
     * Standardkonstruktor som initierar nödvändiga service- och DAO-klasser
     * samt trådpoolen för schemalagda API-anrop.
     *
     * @param scoreService tjänsten som hanterar poängutdelning och rättning
     */
    public MatchService(ScoreService scoreService) {
        this.apiService = new LiveScoreService();
        this.matchDao = new MatchDAO();
        this.roundDAO = new RoundDAO();
        this.couponDAO = new CouponDAO();
        this.scheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        this.scoreService = scoreService;
    }

    /**
     * Startar en automatisk bakgrundsprocess som regelbundet skrapar API:et efter nya matchresultat.
     */
    public void startAutoUpdate() {
        Runnable task = () -> {
            System.out.println("\n[" + LocalDateTime.now() + "] MatchService: Skrapar API efter matcher...");
            fetchAndProcessMatches();
        };

        scheduler.scheduleAtFixedRate(task, INITIAL_DELAY, UPDATE_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Hämtar rådata från LiveScore API, mappar den till Match-objekt och sparar i databasen.
     */
    private void fetchAndProcessMatches() {
        try {
            String rawJsonData = apiService.fetchAllsvenskanData();

            if (rawJsonData != null) {
                List<Match> cleanMatches = LiveScoreMapper.parseMatches(rawJsonData);
                matchDao.saveMatches(cleanMatches);
            }
        } catch (RuntimeException e) {
            System.err.println("-> Ett fel uppstod i MatchService vid tolkning av matchdata: " + e.getMessage());
        }
    }

    /**
     * Kontrollerar om det finns omgångar där alla matcher är färdigspelade,
     * och rättar därefter samtliga kuponger kopplade till dessa omgångar.
     */
    public void checkAndSettleFinishedRounds() {
        List<Integer> finishedGameweeks = roundDAO.getFinishedGameweekIds();

        for (int gameweekId : finishedGameweeks) {
            if (!roundDAO.isAlreadySettled(gameweekId)) {
                List<Integer> leagueIds = couponDAO.getLeagueIdsForGameweek(gameweekId);

                for (int leagueId : leagueIds) {
                    scoreService.settleRound(gameweekId, leagueId);
                }

                roundDAO.markAsSettled(gameweekId);
            }
        }
    }

    /**
     * Hämtar alla matcher som tillhör en viss omgång.
     *
     * @param gameweekId ID för omgången
     * @return en lista med Match-objekt
     */
    public List<Match> getMatchesByGameweek(int gameweekId) {
        return matchDao.getMatchesByGameweek(gameweekId);
    }
}
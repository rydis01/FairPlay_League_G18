package api;

import api.model.Match;
import database.MatchDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LiveScoreManager {

    private final LiveScoreService service;
    private final ScheduledExecutorService scheduler;
    private final MatchDAO matchDao; // Vår koppling till databasen

    public LiveScoreManager() {
        this.service = new LiveScoreService();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.matchDao = new MatchDAO(); // Initierar databaskopplingen
    }

    public void startAutoUpdate() {
        Runnable task = () -> {
            System.out.println("\n[" + LocalDateTime.now() + "] Skrapar matcher...");
            fetchAndProcessData();
        };

        // Här ställer vi in att den körs omedelbart (0) och sedan var 5:e minut
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);
    }

    private void fetchAndProcessData() {
        try {
            // 1. Hämta data från nätet
            String rawJsonData = service.fetchAllsvenskanData();

            if (rawJsonData != null) {
                // 2. Mappa om det till en lista med våra snygga Java-objekt
                List<Match> cleanMatches = api.LiveScoreMapper.parseMatches(rawJsonData);

                // 3. Skicka Java-objekten direkt till Hugos databas
                matchDao.saveMatches(cleanMatches);

                System.out.println("-> Skickade " + cleanMatches.size() + " matcher till databasen.");
            }
        } catch (Exception e) {
            System.out.println("-> Ett fel uppstod vid skrapning/sparning: " + e.getMessage());
        }
    }
}
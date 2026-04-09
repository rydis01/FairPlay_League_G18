package api;

import api.model.Match;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LiveScoreManager {

    private final LiveScoreService service;
    private final ScheduledExecutorService scheduler;
    private final Gson gson;

    public LiveScoreManager() {
        this.service = new LiveScoreService();
        this.scheduler = Executors.newScheduledThreadPool(1);
        // GsonBuilder sätter upp så att JSON-filen blir radbruten och snygg ("Pretty Print")
        this.gson = new GsonBuilder().setPrettyPrinting().create();
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
            // 1. Hämta rörig data från nätet
            String rawJsonData = service.fetchAllsvenskanData();

            if (rawJsonData != null) {
                // 2. Mappa om det till en lista med våra snygga Java-objekt
                List<Match> cleanMatches = api.LiveScoreMapper.parseMatches(rawJsonData);

                // 3. Gör om vår snygga Java-lista tillbaka till JSON
                String cleanJsonOutput = gson.toJson(cleanMatches);

                // 4. Spara ner den tvättade datan till filen
                Path filePath = Paths.get("src/api/allsvenskan_livescore.json");
                if (filePath.getParent() != null) {
                    Files.createDirectories(filePath.getParent());
                }
                Files.writeString(filePath, cleanJsonOutput);

                System.out.println("-> Sparade " + cleanMatches.size() + " tvättade matcher till API:et.");
            }
        } catch (Exception e) {
            System.out.println("-> Ett fel uppstod: " + e.getMessage());
        }
    }
}
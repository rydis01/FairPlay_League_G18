package api;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LiveScoreManager {

    private final LiveScoreService service;
    private final ScheduledExecutorService scheduler;

    public LiveScoreManager() {
        this.service = new LiveScoreService();
        // Skapar en "arbetare" i bakgrunden som kan hantera våra timers
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * 1. TIMER-FUNKTIONEN
     * Denna startas när er server startar och snurrar tyst i bakgrunden.
     */
    public void startAutoUpdate() {
        System.out.println("Startar diskret schemaläggare...");

        Runnable task = () -> {
            System.out.println("\n[" + LocalDateTime.now() + "] AUTO-UPDATE: Hämtar schemalagd data...");
            fetchAndProcessData();
        };

        // Parametrar: (Uppgiften, Fördröjning till första körning, Tid mellan körningar, Tidsenhet)
        // Här ställer vi in den på att köra var 30:e minut. Extremt diskret!
        scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.MINUTES);
    }

    /**
     * 2. ADMIN-FUNKTIONEN (Manuell tvingad uppdatering)
     * Denna metod kan ni senare koppla till en knapp på er admin-hemsida.
     */
    public void forceUpdateFromAdmin() {
        System.out.println("\n[" + LocalDateTime.now() + "] ADMIN-KOMMANDO: Tvingar fram en omedelbar hämtning!");
        fetchAndProcessData();
    }

    /**
     * Den faktiska logiken för att hämta och spara datan.
     * Används av både timern och admin-knappen.
     */
    private void fetchAndProcessData() {
        try {
            String jsonData = service.fetchAllsvenskanData();

            if (jsonData != null) {
                // Spara datan till er fil (eller i framtiden: till er databas)
                Path filePath = Paths.get("src/api/allsvenskan_livescore.json");
                if (filePath.getParent() != null) {
                    Files.createDirectories(filePath.getParent());
                }
                Files.writeString(filePath, jsonData);

                System.out.println("-> Data hämtad och sparad (" + jsonData.length() + " tecken).");

                // Här kan ni också kalla på er LiveScoreMapper för att göra om det till Java-objekt
                // List<Match> matches = LiveScoreMapper.parseMatches(jsonData);

            } else {
                System.out.println("-> Misslyckades att hämta data.");
            }
        } catch (Exception e) {
            System.out.println("-> Ett fel uppstod: " + e.getMessage());
        }
    }

    // Metod för att stänga ner timern snyggt om programmet ska avslutas
    public void shutdown() {
        scheduler.shutdown();
    }
}
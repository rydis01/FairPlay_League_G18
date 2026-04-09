package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LiveScoreService {

    //"details/2" är för Results
    private static final String ALLSVENSKAN_URL = "https://prod-public-api.livescore.com/v1/api/app/competition/302/details/2?locale=en";

    private final HttpClient httpClient;

    public LiveScoreService() {
        // Skapa en HTTP-klient som vi kan återanvända. Vi sätter en timeout så koden inte hänger sig om nätet är nere.
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Hämtar rå JSON-data från LiveScore.
     * @return En JSON-sträng om det lyckas, annars null.
     */
    public String fetchAllsvenskanData() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ALLSVENSKAN_URL))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)") // här låtsas vi vara en vanlig användare
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("API Fel: Fick HTTP status " + response.statusCode());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Nätverksfel vid hämtning från LiveScore: " + e.getMessage());
            return null;
        }
    }
}
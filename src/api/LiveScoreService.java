package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LiveScoreService {

    private static final String ALLSVENSKAN_URL = "https://prod-public-api.livescore.com/v1/api/app/competition/302/details/2?locale=en";
    private final HttpClient httpClient;

    public LiveScoreService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String fetchAllsvenskanData() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ALLSVENSKAN_URL))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("Fel från LiveScore: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Nätverksfel: " + e.getMessage());
            return null;
        }
    }
}
package FairplayLeagueG18.api;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Ansvarar för att hämta matchdata från LiveScore API:et.
 * Skickar HTTP-anrop mot Allsvenskan-endpointen och returnerar rådata som JSON-sträng.
 * @author Theo Andersson
 */
@Service
public class LiveScoreService {

    private static final String ALLSVENSKAN_URL = "https://prod-public-api.livescore.com/v1/api/app/competition/302/details/2?locale=en";
    private final HttpClient httpClient;

    public LiveScoreService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Hämtar aktuell matchdata för Allsvenskan från LiveScore API:et.
     *
     * @return JSON-sträng med matchdata, eller null om anropet misslyckas
     * @throws IOException om ett nätverksfel uppstår
     * @throws InterruptedException om tråden avbryts under anropet
     */
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
        } catch (IOException e) {
            System.err.println("Nätverksfel vid anrop till LiveScore: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            System.err.println("Anropet till LiveScore avbröts: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
package api;

import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class ApiService {

    private static final String BASE_URL = "https://v3.football.api-sports.io/";
    private final HttpClient httpClient;
    private final String apiKey; // Nu är denna inte hårdkodad längre

    public ApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = loadApiKey(); // Laddar nyckeln säkert från filen
    }

    // Ny säker metod för att läsa in nyckeln
    private String loadApiKey() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            return properties.getProperty("api_key");
        } catch (Exception e) {
            System.out.println("Varning: Kunde inte läsa config.properties. Felet: " + e.getMessage());
            return "";
        }
    }

    public String fetchCountries() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "countries"))
                .header("x-apisports-key", this.apiKey) // Använder den inlästa nyckeln
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "HTTP Fel: " + response.statusCode();
            }
        } catch (Exception e) {
            return "Nätverksfel: " + e.getMessage();
        }
    }
}
package api;

import api.model.Match;
import java.util.List;

public class RunLiveScoreTest {

    public static void main(String[] args) {
        System.out.println("Initierar LiveScore Service...");
        LiveScoreService service = new LiveScoreService();

        System.out.println("Hämtar Allsvenskan-data från LiveScore...");
        String jsonData = service.fetchAllsvenskanData();

        if (jsonData != null) {
            System.out.println("Hämtning lyckades! Mappar om JSON till Java-objekt...\n");

            // Skicka in texten i vår tolk och få tillbaka en fin lista med Matcher!
            List<Match> allMatches = api.LiveScoreMapper.parseMatches(jsonData);

            // Loopa igenom listan och skriv ut varje match i konsolen
            for (Match match : allMatches) {
                System.out.println(match.toString()); // Använder toString() metoden vi skrev i Match.java
            }

            System.out.println("\nTotalt antal matcher inlästa: " + allMatches.size());

        } else {
            System.out.println("Kunde inte hämta data.");
        }
    }
}
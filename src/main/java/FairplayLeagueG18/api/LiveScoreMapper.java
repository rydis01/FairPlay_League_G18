package FairplayLeagueG18.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import FairplayLeagueG18.model.Match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ansvarar för att tolka JSON-data från LiveScore API:et och omvandla den till Match-objekt.
 * Hanterar även omgångslogik och normalisering av svenska lagnamn.
 * @author Theo Andersson
 */
public class LiveScoreMapper {

    /**
     * Tolkar en JSON-sträng från LiveScore API:et och returnerar en lista med Match-objekt.
     * Räknar ut omgångsnummer antingen från API-fältet ErnInf eller baserat på
     * hur många matcher varje lag redan spelat.
     *
     * @param jsonString rådata i JSON-format från LiveScore API:et
     * @return lista med parsade Match-objekt, eller tom lista om parsing misslyckas
     */
    public static List<Match> parseMatches(String jsonString) {
        List<Match> matchesList = new ArrayList<>();

        // Håller koll på matcher per lag
        Map<String, Integer> teamMatchCount = new HashMap<>();

        try {
            JsonObject rootObject = JsonParser.parseString(jsonString).getAsJsonObject();
            JsonArray stages = rootObject.getAsJsonArray("Stages");
            JsonObject firstStage = stages.get(0).getAsJsonObject();
            JsonArray events = firstStage.getAsJsonArray("Events");

            for (JsonElement element : events) {
                JsonObject event = element.getAsJsonObject();

                String homeTeam = fixTeamName(event.getAsJsonArray("T1").get(0).getAsJsonObject().get("Nm").getAsString());
                String awayTeam = fixTeamName(event.getAsJsonArray("T2").get(0).getAsJsonObject().get("Nm").getAsString());

                String homeScore = event.has("Tr1") ? event.get("Tr1").getAsString() : "-";
                String awayScore = event.has("Tr2") ? event.get("Tr2").getAsString() : "-";
                String status = event.get("Eps").getAsString();
                String time = event.get("Esd").getAsString();

                // omgångslogik
                int gameweek = 0;

                if (event.has("ErnInf")) {
                    String roundStr = event.get("ErnInf").getAsString();
                    try {
                        // Försök hämta siffran
                        gameweek = Integer.parseInt(roundStr);
                    } catch (NumberFormatException e) {
                        // Om det står t.ex. Regular Season, räkna ut omgången själv
                        int homePlayed = teamMatchCount.getOrDefault(homeTeam, 0);
                        int awayPlayed = teamMatchCount.getOrDefault(awayTeam, 0);
                        gameweek = Math.max(homePlayed, awayPlayed) + 1;
                    }
                }

                // Uppdatera hur många matcher lagen spelat
                teamMatchCount.put(homeTeam, gameweek);
                teamMatchCount.put(awayTeam, gameweek);

                // Skapa matchen och lägg till omgången (RoundId)
                Match match = new Match(homeTeam, awayTeam, homeScore, awayScore, status, time);
                match.setGameweekId(gameweek); // Sätter omgången för databasen

                matchesList.add(match);
            }
        } catch (JsonParseException e) {
            System.err.println("Ogiltig JSON från LiveScore API: " + e.getMessage());
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            System.err.println("Oväntat JSON-format från LiveScore API: " + e.getMessage());
        }

        return matchesList;
    }

    /**
     * Normaliserar lagnamn från LiveScore API:ets engelska format till svenska namn.
     * Returnerar namnet oförändrat om det inte finns i mappningen.
     *
     * @param name lagnamn som det returneras av API:et
     * @return korrekt stavat svenskt lagnamn
     * @author carl
     */
    private static String fixTeamName(String name) {
        if (name.equals("Malmo FF")) return "Malmö FF";
        if (name.equals("IFK Gothenburg")) return "IFK Göteborg";
        if (name.equals("Oergryte")) return "Örgryte";
        if (name.equals("Vasteraas SK")) return "Västerås SK";
        if (name.equals("Mjaellby")) return "Mjällby";
        if (name.equals("BK Haecken")) return "BK Häcken";
        if (name.equals("Djurgaarden")) return "Djurgården";
        return name;
    }
}
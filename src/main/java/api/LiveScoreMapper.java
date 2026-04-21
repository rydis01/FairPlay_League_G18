package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveScoreMapper {

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

                String homeTeam = event.getAsJsonArray("T1").get(0).getAsJsonObject().get("Nm").getAsString();
                String awayTeam = event.getAsJsonArray("T2").get(0).getAsJsonObject().get("Nm").getAsString();

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
                match.setRoundId(gameweek); // Sätter omgången för databasen

                matchesList.add(match);
            }
        } catch (Exception e) {
            System.err.println("Kunde inte tolka JSON-filen: " + e.getMessage());
        }

        return matchesList;
    }
}
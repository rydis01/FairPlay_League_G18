package FairplayLeagueG18.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
<<<<<<<< HEAD:src/FairplayLeagueG18/api/LiveScoreMapper.java
import FairplayLeagueG18.model.Match; // <-- NY IMPORT HÄR!
========
import model.Match;
>>>>>>>> carl_after_springboot:src/main/java/api/LiveScoreMapper.java

import java.util.ArrayList;
import java.util.List;

public class LiveScoreMapper {

    public static List<Match> parseMatches(String jsonString) {
        List<Match> matchesList = new ArrayList<>();

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

                matchesList.add(new Match(homeTeam, awayTeam, homeScore, awayScore, status, time));
            }
        } catch (Exception e) {
            System.err.println("Kunde inte tolka JSON-filen: " + e.getMessage());
        }

        return matchesList;
    }
}
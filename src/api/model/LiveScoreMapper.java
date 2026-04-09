package api;

import api.model.Match;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class LiveScoreMapper {

    // Tar emot hela den långa JSON-texten och returnerar en lista med färdiga Java-objekt!
    public static List<Match> parseMatches(String jsonString) {
        List<Match> matchesList = new ArrayList<>();

        // 1. Gör om texten till ett Gson-objekt som vi kan navigera i
        JsonObject rootObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // 2. Navigera ner till "Events" (där alla matcher ligger)
        // json -> Stages[0] -> Events
        JsonArray stages = rootObject.getAsJsonArray("Stages");
        JsonObject firstStage = stages.get(0).getAsJsonObject();
        JsonArray events = firstStage.getAsJsonArray("Events");

        // 3. Loopa igenom varje match
        for (JsonElement element : events) {
            JsonObject event = element.getAsJsonObject();

            // Plocka ut lagnamnen (ligger inuti T1 -> index 0 -> Nm)
            String homeTeam = event.getAsJsonArray("T1").get(0).getAsJsonObject().get("Nm").getAsString();
            String awayTeam = event.getAsJsonArray("T2").get(0).getAsJsonObject().get("Nm").getAsString();

            // Plocka ut mål. Om matchen inte startat (NS) finns ibland inte Tr1/Tr2, så vi sätter "-" då.
            String homeScore = event.has("Tr1") ? event.get("Tr1").getAsString() : "-";
            String awayScore = event.has("Tr2") ? event.get("Tr2").getAsString() : "-";

            // Plocka ut status (FT, NS osv) och tid
            String status = event.get("Eps").getAsString();
            String time = event.get("Esd").getAsString();

            // 4. Skapa ett Match-objekt av datan och lägg i vår lista
            Match match = new Match(homeTeam, awayTeam, homeScore, awayScore, status, time);
            matchesList.add(match);
        }

        return matchesList;
    }
}
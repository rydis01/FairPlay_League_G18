package controller;

import api.LiveScoreManager; // Se till att importerna stämmer med dina mappar
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainServer {

    public static void main(String[] args) throws IOException {
        // 1. Starta skrapare i bakgrunden
        LiveScoreManager manager = new LiveScoreManager();
        manager.startAutoUpdate();

        // 2. Skapa en webbserver på port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 3. Skapa länken "/api/matches"
        server.createContext("/api/matches", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Säg till webbläsaren att vi skickar JSON, och tillåt att andra sidor (CORS) läser den
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                // Läs in datan från filen din scraper sparar till
                String responseData = "[]";
                try {
                    Path filePath = Paths.get("src/api/allsvenskan_livescore.json");
                    if (Files.exists(filePath)) {
                        responseData = Files.readString(filePath);
                    } else {
                        responseData = "{\"error\": \"Datan har inte hunnit laddas ner ännu, vänta lite!\"}";
                    }
                } catch (Exception e) {
                    responseData = "{\"error\": \"Kunde inte läsa filen\"}";
                }

                // Skicka tillbaka datan till webbläsaren
                byte[] responseBytes = responseData.getBytes("UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            }
        });

        // 4. Starta servern
        server.start();
        System.out.println("====== FAIRPLAY LEAGUE SERVER ÄR IGÅNG ======");
        System.out.println("Gå in på: http://localhost:8080/api/matches");
    }
}
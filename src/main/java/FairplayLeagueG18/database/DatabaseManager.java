package FairplayLeagueG18.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Hanterar databasanslutningar för applikationen.
 * Läser anslutningsinformation från konfigurationsfilen configDatabase.properties.
 */
public class DatabaseManager {

    /**
     * Skapar och returnerar en ny databasanslutning baserad på konfigurationsfilen.
     *
     * @return en aktiv Connection, eller null om anslutningen misslyckas
     * @throws RuntimeException om konfigurationsfilen inte hittas eller kan läsas
     */
    public static Connection getConnection() {
        try {
            Properties props = new Properties();

            InputStream input = DatabaseManager.class
                    .getClassLoader()
                    .getResourceAsStream("configDatabase.properties");

            if (input == null) {
                System.err.println("Kunde inte hitta configDatabase.properties i classpath.");
                return null;
            }

            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, pass);

        } catch (IOException e) {
            System.err.println("Kunde inte läsa configDatabase.properties: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Kunde inte ansluta till databasen: " + e.getMessage());
            return null;
        }
    }
}
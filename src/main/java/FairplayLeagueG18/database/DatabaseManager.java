package FairplayLeagueG18.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseManager {
    public static Connection getConnection() {
        try {
            Properties props = new Properties();

            InputStream input = DatabaseManager.class
                    .getClassLoader()
                    .getResourceAsStream("configDatabase.properties");

            if (input == null) {
                System.out.println("Failed to load configDatabase.properties");
                return null;
            }

            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
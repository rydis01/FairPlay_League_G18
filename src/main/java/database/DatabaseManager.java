package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseManager {
    public static Connection getConnection() {
        Properties props = new Properties();

        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("configDatabase.properties")) {
            if (input == null) {
                System.out.println("Failed to load configDatabase.properties");
                return null;
            }
            props.load(input);
        } catch (IOException e) {
            System.out.println("Failed to load configDatabase.properties");
            e.printStackTrace();
            return null;
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established.");
            return conn;
        } catch (Exception e) {
            System.out.println("Failed to connect to FairplayLeagueG18.database.");
            e.printStackTrace();
            return null;
        }
    }
}
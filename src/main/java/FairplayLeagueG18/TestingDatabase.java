package FairplayLeagueG18;

import FairplayLeagueG18.database.DatabaseManager;

import java.sql.Connection;
import java.sql.Statement;

public class TestingDatabase {

    public static void main(String[] args) {

        String[] sqlCommands = {

                // Drop all tables
                "DROP TABLE IF EXISTS Picks CASCADE;",
                "DROP TABLE IF EXISTS Coupons CASCADE;",
                "DROP TABLE IF EXISTS Gameweek_scores CASCADE;",
                "DROP TABLE IF EXISTS User_Leagues CASCADE;",
                "DROP TABLE IF EXISTS Leagues CASCADE;",
                "DROP TABLE IF EXISTS Matches CASCADE;",
                "DROP TABLE IF EXISTS Gameweeks CASCADE;",
                "DROP TABLE IF EXISTS Users CASCADE;",

                // Users
                "CREATE TABLE Users(" +
                        "User_ID SERIAL PRIMARY KEY," +
                        "Username VARCHAR(20) NOT NULL," +
                        "Email VARCHAR(50) UNIQUE NOT NULL," +
                        "Password_Hash VARCHAR(255) NOT NULL," +
                        "Role VARCHAR(20) DEFAULT 'PLAYER' NOT NULL," +
                        "Created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");",

                // Leagues
                "CREATE TABLE Leagues(" +
                        "League_ID SERIAL PRIMARY KEY," +
                        "League_Name VARCHAR(20) NOT NULL," +
                        "Admin_User INT," +
                        "Invite_Code VARCHAR(10) UNIQUE NOT NULL," +
                        "Created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "CONSTRAINT fk_admin FOREIGN KEY (Admin_User) REFERENCES Users(User_ID)" +
                        ");",

                // User_Leagues
                // User_Leagues
                "CREATE TABLE User_Leagues(" +
                        "User_ID INT," +
                        "League_ID INT," +
                        "Total_Score INT DEFAULT 0," +
                        "Joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "PRIMARY KEY (User_ID, League_ID)," +
                        "CONSTRAINT fk_ul_user FOREIGN KEY (User_ID) REFERENCES Users(User_ID)," +
                        "CONSTRAINT fk_ul_league FOREIGN KEY (League_ID) REFERENCES Leagues(League_ID)" +
                        ");",


                // Gameweeks
                "CREATE TABLE Gameweeks(" +
                        "Gameweek_ID SERIAL PRIMARY KEY," +
                        "Round_number INT UNIQUE," +
                        "Lock_time TIMESTAMP" +
                        ");",

                // Matches
                "CREATE TABLE Matches(" +
                        "Match_ID SERIAL PRIMARY KEY," +
                        "Gameweek_ID INT," +
                        "Home_team VARCHAR(20) NOT NULL," +
                        "Away_team VARCHAR(20) NOT NULL," +
                        "Kickoff_time TIMESTAMP," +
                        "Actual_result CHAR(1)," +
                        "CONSTRAINT fk_match_gameweek FOREIGN KEY (Gameweek_ID) REFERENCES Gameweeks(Gameweek_ID)" +
                        ");",

                // Coupons
                "CREATE TABLE Coupons(" +
                        "Coupon_ID SERIAL PRIMARY KEY," +
                        "User_ID INT NOT NULL," +
                        "Gameweek_ID INT NOT NULL," +
                        "League_ID INT NOT NULL," +
                        "Correct_count INT DEFAULT 0," +
                        "Created_At TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "CONSTRAINT fk_coupon_user FOREIGN KEY (User_ID) REFERENCES Users(User_ID)," +
                        "CONSTRAINT fk_coupon_gameweek FOREIGN KEY (Gameweek_ID) REFERENCES Gameweeks(Gameweek_ID)," +
                        "CONSTRAINT fk_coupon_league FOREIGN KEY (League_ID) REFERENCES Leagues(League_ID)" +
                        ");",

                // Picks
                "CREATE TABLE Picks(" +
                        "Pick_ID SERIAL PRIMARY KEY," +
                        "Coupon_ID INT NOT NULL," +
                        "Match_ID INT NOT NULL," +
                        "Guess CHAR(1) NOT NULL," +
                        "CONSTRAINT fk_pick_coupon FOREIGN KEY (Coupon_ID) REFERENCES Coupons(Coupon_ID)," +
                        "CONSTRAINT fk_pick_match FOREIGN KEY (Match_ID) REFERENCES Matches(Match_ID)" +
                        ");",

                // Gameweek_scores
                "CREATE TABLE Gameweek_scores(" +
                        "Score_ID SERIAL PRIMARY KEY," +
                        "User_ID INT," +
                        "League_ID INT," +
                        "Gameweek_ID INT," +
                        "Correct_picks_count INT NOT NULL," +
                        "Points_earned INT," +
                        "CONSTRAINT fk_gws_user FOREIGN KEY (User_ID) REFERENCES Users(User_ID)," +
                        "CONSTRAINT fk_gws_league FOREIGN KEY (League_ID) REFERENCES Leagues(League_ID)," +
                        "CONSTRAINT fk_gws_gameweek FOREIGN KEY (Gameweek_ID) REFERENCES Gameweeks(Gameweek_ID)" +
                        ");"
        };


        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : sqlCommands) {
                stmt.execute(sql);
                System.out.println("Körde: " + sql);
            }

        } catch (Exception e) {
            System.out.println("Fel vid ändring: " + e.getMessage());
        }
    }
}

DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS Leagues CASCADE;
DROP TABLE IF EXISTS User_Leagues CASCADE;
DROP TABLE IF EXISTS GameWeeks CASCADE;
DROP TABLE IF EXISTS Matches CASCADE;
DROP TABLE IF EXISTS Picks CASCADE;
DROP TABLE IF EXISTS Gameweek_scores CASCADE;

-- Hanterar inloggning och identitet
CREATE TABLE Users(
    User_ID SERIAL PRIMARY KEY,
    Username VARCHAR (20) NOT NULL,
    Email VARCHAR (50) UNIQUE NOT NULL,
    Password_Hash VARCHAR (255) NOT NULL,
    Role VARCHAR (20) DEFAULT 'PLAYER' NOT NULL,
    Created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Hanterar det privata rummet
CREATE TABLE Leagues(
    League_Id SERIAL PRIMARY KEY,
    League_Name VARCHAR (20) NOT NULL,
    Admin_User INT,
    Invite_Code VARCHAR (10) UNIQUE NOT NULL,
    Created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_admin FOREIGN KEY (Admin_user) REFERENCES Users(User_ID)
);

-- Kopplar ihop användare med ligor
-- Eftersom spelare (användare) kan vara med i flera olika ligor
CREATE TABLE User_Leagues(
    User_ID INT,
    League_ID INT,
    Joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (User_ID, League_ID),

    CONSTRAINT fk_user FOREIGN KEY (User_ID) REFERENCES Users(User_ID),
    CONSTRAINT fk_league FOREIGN KEY (League_ID) REFERENCES Leagues(League_ID)
);

-- Grupperar matcherna
-- Sätter deadline på när databasen ska stängas
CREATE TABLE Gameweeks(
    Gameweek_ID SERIAL PRIMARY KEY,
    Round_number INT UNIQUE,
    Lock_time TIMESTAMP
);

-- Spelschemat och verkligheten
-- Tar emot datan från API:n
CREATE TABLE Matches(
    Match_ID SERIAL PRIMARY KEY,
    Gameweek_ID INT,
    Home_team VARCHAR (20) NOT NULL,
    Away_team VARCHAR (20) NOT NULL,
    Kickoff_time TIMESTAMP,
    Actual_result CHAR (1),

    CONSTRAINT fk_gameweek FOREIGN KEY (Gameweek_ID) REFERENCES Gameweeks(Gameweek_ID)
);

-- Spelarnas kuponger
-- Används även för att räkna ut spelarnas totala antal rätt
CREATE TABLE Picks(
    Pick_ID SERIAL PRIMARY KEY,
    User_ID INT,
    Match_ID INT,
    Guess CHAR (1) NOT NULL,

    CONSTRAINT fk_user FOREIGN KEY (User_ID) REFERENCES Users(User_ID),
    CONSTRAINT fk_match FOREIGN KEY (Match_ID) REFERENCES Matches(Match_ID)
);

-- Poängutdelning
-- Efter en gameweeks slut kollar java-koden hur många antal rätt en spelare har,
--      räknar sedan ut potten och sedan sparar vinsten här
CREATE TABLE Gameweek_scores(
    Score_ID SERIAL PRIMARY KEY,
    User_ID INT,
    Leauge_ID INT,
    Gameweek_ID INT,
    Correct_picks_count INT NOT NULL,
    Points_earned INT,

    CONSTRAINT fk_user FOREIGN KEY (User_ID) REFERENCES Users(User_ID),
    CONSTRAINT fk_league FOREIGN KEY (Leauge_ID) REFERENCES Leauges(Leauge_ID),
    CONSTRAINT fk_gameweek FOREIGN KEY (Gameweek_ID) REFERENCES Gameweeks(Gameweek_ID)
);

DROP TABLE IF EXISTS UserID CASCADE;
DROP TABLE IF EXISTS Leauge CASCADE;
DROP TABLE IF EXISTS User_Leauges CASCADE;
DROP TABLE IF EXISTS GameWeeks CASCADE;
DROP TABLE IF EXISTS Matches CASCADE;
DROP TABLE IF EXISTS Picks CASCADE;
DROP TABLE IF EXISTS Gameweek_score CASCADE;

-- Hanterar inloggning och identitet
CREATE TABLE User(
    User_ID SERIAL PRIMARY KEY,
    Username VARCHAR (20) NOT NULL,
    Email VARCHAR EUNIQUE NOT NULL,
    Password_Hash NOT NULL,
    Created_at NOT NULL
);

-- Hanterar det privata rummet
CREATE TABLE Leauge(
    Leauge_Id SERIAL PRIMARY KEY,
    Leauge_Name VARCHAR (20) NOT NULL,
    Admin_User SERIAL FOREIGN KEY,
    Invite_Code VARCHAR (10) UNIQUE NOT NULL

    CONSTRAINT 
);

-- Kopplar ihop användare med ligor
-- Eftersom spelare (användare) kan vara med i flera olika ligor
CREATE TABLE User_Leauges(
    User_ID Reference
);


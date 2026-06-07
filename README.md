# Fairplay League - G18

Git-repository

Källkoden finns på GitHub:
https://github.com/rydis01/FairPlay_League_G18

Förkrav

För att kunna köra produkten behöver du:

Java JDK 21
IntelliJ IDEA
PostgreSQL (databasen körs mot en PostgreSQL-server)
-Internetuppkoppling (krävs för att hämta matchdata från LiveScore API:et)

Externa bibliotek

Projektet använder Maven, så alla externa bibliotek laddas ner automatiskt när du öppnar
projektet i IntelliJ IDEA. Du behöver alltså inte ladda ner något manuellt.

Om biblioteken inte laddas ner automatiskt: högerklicka på pom.xml och välj
Maven → Reload Project.

Följande externa bibliotek används:

Spring Boot
PostgreSQL JDBC Driver
Gson
jBCrypt

Databas
SQL-koden för att skapa databasens tabeller finns i:

src/main/java/FairplayLeagueG18/database/SQL-kod.sql

Kör innehållet i denna fil mot din PostgreSQL-databas för att skapa tabellerna
(t.ex. via IntelliJ:s inbyggda databasverktyg eller pgAdmin).

Konfiguration av databasanslutning

Applikationen läser anslutningsuppgifter från en fil som heter configDatabase.properties.
Skapa filen i mappen:

src/main/resources/

Lägg in följande innehåll i filen
db.url=jdbc:postgresql://postgres.mau.se:55432/g_18
db.user=ar9532
db.password=ub9eif2w


Köra produkten
Öppna projektet i IntelliJ IDEA (vänta tills Maven laddat klart alla bibliotek).
Skapa filen configDatabase.properties enligt instruktionerna ovan.
Skapa databasens tabeller genom att köra SQL-koden i SQL-kod.sql.
Öppna klassen Application (i src/main/java/FairplayLeagueG18/) och klicka på den gröna Run-pilen bredvid main-metoden.
När applikationen startat, öppna webbläsaren och gå till:
http://localhost:8080/
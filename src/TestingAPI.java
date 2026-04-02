import api.ApiController;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestingAPI {
    public static void main(String[] args) {
        ApiController controller = new ApiController();

        System.out.println("Hämtar lag för Allsvenskan 2024");

        // 1. Hämta datan
        String result = controller.getAllsvenskanTeams();

        // 2. var filen ska sparas
        Path filePath = Paths.get("src/api/allsvenskan_teams_2024.json");

        try {
            // 3. Skriv resultatet till filen
            Files.writeString(filePath, result);
            System.out.println("Datan har sparats i filen: " + filePath.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Kunde inte spara filen: " + e.getMessage());
        }
    }
}
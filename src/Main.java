import Api.ApiController;

public class Main {
    public static void main(String[] args) {
        ApiController controller = new ApiController();

        System.out.println("Hämtar data från API-Football...");

        String result = controller.getCountriesData();

        System.out.println("Resultat:");
        System.out.println(result);
    }
}
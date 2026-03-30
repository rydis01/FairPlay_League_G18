package api;

public class ApiController {

    private final ApiService apiService;

    public ApiController() {
        this.apiService = new ApiService();
    }

    public String getCountriesData() {
        return apiService.fetchCountries();
    }
}
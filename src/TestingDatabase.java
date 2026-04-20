import database.DatabaseManager;
import service.UserService;

public class TestingDatabase {
    public static void main(String[] args) {
        createTestUser();
    }

    /**
     * Creates a test user with username "Carl" and password "MAU123".
     */
    public static void createTestUser() {
        UserService userService = new UserService();
        userService.registerUser("Carl", "carl@example.com", "MAU123");
    }
}

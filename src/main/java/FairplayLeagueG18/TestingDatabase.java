package FairplayLeagueG18;

import FairplayLeagueG18.model.User;
import FairplayLeagueG18.service.UserService;

public class TestingDatabase {
    public static void main(String[] args) {
        UserService user = new UserService();

        //StringBuilder String = user.getUsers();
        //System.out.println(String);

        User carl = user.getUserByEmail("carl@example.com");
        System.out.println(carl.toString());

    }
}

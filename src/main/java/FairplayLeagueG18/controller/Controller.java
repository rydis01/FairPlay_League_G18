package FairplayLeagueG18.controller;

import FairplayLeagueG18.model.Round;
import FairplayLeagueG18.model.User;
import FairplayLeagueG18.service.RoundService;
import org.springframework.web.bind.annotation.*;
import FairplayLeagueG18.service.UserService;

@RestController
@RequestMapping("/api")
public class Controller {

    private final UserService userService;
    private final RoundService roundService;
    private User currentUser;

    public Controller(UserService userService, RoundService roundService) {
        this.userService = userService;
        this.roundService = roundService;
    }

    @PostMapping("/login")
    public boolean login(@RequestParam String email, @RequestParam String password) {
        boolean answer = userService.loginUser(email, password);

        if (answer) {
            currentUser = userService.getUserByEmail(email);
        }
        return answer;
    }

    @GetMapping("/gameweek")
    public Round gameweekInfo() {
        return roundService.getRound(4);
    }

    @GetMapping("/userinfo")
    public String userInfo() {
        return currentUser.toString();
    }
}


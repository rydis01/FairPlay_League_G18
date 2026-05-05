package FairplayLeagueG18.controller;

import FairplayLeagueG18.model.Round;
import FairplayLeagueG18.model.User;
import FairplayLeagueG18.service.CouponService;
import FairplayLeagueG18.service.RoundService;
import FairplayLeagueG18.service.UserService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class Controller {

    private final UserService userService;
    private final RoundService roundService;
    private final CouponService couponService;
    private User currentUser;

    public Controller(UserService userService, RoundService roundService, CouponService couponService) {
        this.userService = userService;
        this.roundService = roundService;
        this.couponService = couponService;
    }

    @GetMapping("/login")
    public boolean login(@RequestParam String email, @RequestParam String password) {
        boolean answer = userService.loginUser(email, password);

        if (answer) {
            currentUser = userService.getUserByEmail(email);
        }
        return answer;
    }

    @GetMapping("/submitTips")
    public String submitTips(@RequestParam int roundId,
                             @RequestParam String tip1,
                             @RequestParam String tip2,
                             @RequestParam String tip3,
                             @RequestParam String tip4,
                             @RequestParam String tip5,
                             @RequestParam String tip6,
                             @RequestParam String tip7,
                             @RequestParam String tip8
    ) {
        if (currentUser == null) {
            return "Ingen användare inloggad";
        }

        Map<Integer, String> tips = Map.of(
                1, tip1,
                2, tip2,
                3, tip3,
                4, tip4,
                5, tip5,
                6, tip6,
                7, tip7,
                8, tip8
        );

        couponService.submitCoupon(currentUser.getId(), roundId, tips);

        return "Kupong sparad!";
    }

    @GetMapping("/gameweek")
    public Round gameweekInfo(@RequestParam int roundId) {
        return roundService.getRound(roundId);
    }

    @GetMapping("/userinfo")
    public String userInfo() {
        return currentUser.toString();
    }
}


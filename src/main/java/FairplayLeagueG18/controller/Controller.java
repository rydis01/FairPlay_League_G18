package FairplayLeagueG18.controller;

import FairplayLeagueG18.model.Role;
import FairplayLeagueG18.model.Round;
import FairplayLeagueG18.model.User;
import FairplayLeagueG18.service.CouponService;
import FairplayLeagueG18.service.RoundService;
import FairplayLeagueG18.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class Controller {

    private final UserService userService;
    private final RoundService roundService;
    private final CouponService couponService;

    public Controller(UserService userService, RoundService roundService, CouponService couponService) {
        this.userService = userService;
        this.roundService = roundService;
        this.couponService = couponService;
    }

    //LOGIN

    @GetMapping("/login")
    public boolean login(HttpSession session,
                         @RequestParam String email,
                         @RequestParam String password) {

        boolean ok = userService.loginUser(email, password);

        if (ok) {
            session.setAttribute("user", userService.getUserByEmail(email));
        }

        return ok;
    }

    // REGISTER

    @GetMapping("/register")
    public boolean register(@RequestParam String username,
                            @RequestParam String email,
                            @RequestParam String password) {

        if (userService.getUserByEmail(email) != null) {
            return false;
        }
        userService.registerUser(username, email, password);

        return true;
    }

    // GAMEWEEK

    @GetMapping("/submitTips")
    public String submitTips(HttpSession session,
                             @RequestParam int roundId,
                             @RequestParam String tip1,
                             @RequestParam String tip2,
                             @RequestParam String tip3,
                             @RequestParam String tip4,
                             @RequestParam String tip5,
                             @RequestParam String tip6,
                             @RequestParam String tip7,
                             @RequestParam String tip8) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
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

        couponService.submitCoupon(user.getId(), roundId, tips);

        return "Kupong sparad!";
    }

    @GetMapping("/gameweek")
    public Round gameweekInfo(@RequestParam int roundId) {
        return roundService.getRound(roundId);
    }

    // PROFILE
    @GetMapping("/userinfo")
    public User userInfo(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}



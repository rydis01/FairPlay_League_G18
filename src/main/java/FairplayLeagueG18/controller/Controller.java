package FairplayLeagueG18.controller;

import FairplayLeagueG18.model.*;
import FairplayLeagueG18.service.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Controller {

    private final UserService userService;
    private final RoundService roundService;
    private final CouponService couponService;
    private final LeagueService leagueService;
    private final MatchService matchService;

    public Controller(UserService userService, RoundService roundService, CouponService couponService, LeagueService leagueService, MatchService matchService) {
        this.userService = userService;
        this.roundService = roundService;
        this.couponService = couponService;
        this.leagueService = leagueService;
        this.matchService = matchService;
    }

    //LOGIN & REGISTER

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

    @GetMapping("/register")
    public boolean register(@RequestParam String username,
                            @RequestParam String email,
                            @RequestParam String password) {

        if (userService.getUserByEmail(email) != null) {
            return false;
        }
        return userService.registerUser(username, email, password);
    }

    // GAMEWEEK

    @GetMapping("/submitTips")
    public String submitTips(HttpSession session,
                             @RequestParam int roundId,
                             @RequestParam int leagueId,
                             @RequestParam String tip1,
                             @RequestParam String tip2,
                             @RequestParam String tip3,
                             @RequestParam String tip4,
                             @RequestParam String tip5,
                             @RequestParam String tip6,
                             @RequestParam String tip7,
                             @RequestParam String tip8) {

        User user = (User) session.getAttribute("user");
        System.out.println("roundId: " + roundId + ", leagueId: " + leagueId);

        if (user == null) {
            return "Ingen användare inloggad";
        }

        List<Match> matches = matchService.getMatchesByGameweek(roundId);
        if (matches.size() < 8) {
            return "Fel antal matcher för omgången";
        }

        Map<Integer, String> tips = Map.of(
                matches.get(0).getId(), tip1,
                matches.get(1).getId(), tip2,
                matches.get(2).getId(), tip3,
                matches.get(3).getId(), tip4,
                matches.get(4).getId(), tip5,
                matches.get(5).getId(), tip6,
                matches.get(6).getId(), tip7,
                matches.get(7).getId(), tip8
        );
        
        couponService.submitCoupon(user.getId(), roundId, leagueId, tips);

        return "Kupong sparad!";
    }

    @GetMapping("/gameweek")
    public Round gameweekInfo(@RequestParam int roundId) {
        return roundService.getRound(roundId);
    }

    @PostMapping("/updateGameweek")
    public String updateGameweek() {
        matchService.startAutoUpdate();
        return "OK";
    }

    // LEAGUE

    @GetMapping("/createLeague")
    public boolean createLeague(HttpSession session, @RequestParam String leagueName) {
        User user = (User) session.getAttribute("user");

        if(leagueName == null || leagueService.leagueExists(leagueName)){
            return false;
        }
        leagueService.createLeague(leagueName, user.getId());

        return true;
    }
    @GetMapping("/joinLeague")
    public boolean joinLeague(HttpSession session, @RequestParam String inviteCode) {
        User user = (User) session.getAttribute("user");

        return leagueService.joinLeague(inviteCode, user.getId());
    }

    @GetMapping("/loadAllLeagues")
    public List<League> getAllLeaguesInfo() {
        return leagueService.getAllLeagues();
    }

    @GetMapping("/loadPlayerLeagues")
    public List<League> getPlayerLeaguesInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");

        return leagueService.getLeaguesByUserId(user.getId());
    }

    @GetMapping("/loadLeaderboard")
    public List<LeagueMember> getLeagueLeaderboard(@RequestParam int leagueId){
        return leagueService.getLeaderboard(leagueId);
    }

    // PROFILE
    @GetMapping("/getCoupons")
    public List<Coupon> couponsList(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return List.of();
        }

        return couponService.getCouponsByUserId(user.getId());
    }

    @GetMapping("/getCoupon")
    public Map<String, Object> getCoupon(@RequestParam int couponId) {
        return couponService.getCouponDetails(couponId);
    }

    @GetMapping("/userinfo")
    public User userInfo(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}



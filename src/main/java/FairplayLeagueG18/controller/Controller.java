package FairplayLeagueG18.controller;

import FairplayLeagueG18.model.*;
import FairplayLeagueG18.service.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST-kontroller som hanterar alla inkommande HTTP-anrop från frontend.
 * Exponerar endpoints för inloggning, registrering, kuponger, ligor och användarprofil.
 */
@RestController
@RequestMapping("/api")
public class Controller {

    private final UserService userService;
    private final RoundService roundService;
    private final CouponService couponService;
    private final LeagueService leagueService;
    private final MatchService matchService;
    private final ScoreService scoreService;

    public Controller(UserService userService, RoundService roundService, CouponService couponService, LeagueService leagueService, MatchService matchService, ScoreService scoreService) {
        this.userService = userService;
        this.roundService = roundService;
        this.couponService = couponService;
        this.leagueService = leagueService;
        this.matchService = matchService;
        this.scoreService = scoreService;
    }

    //LOGIN & REGISTER

    /**
     * Loggar in en användare.
     *
     * @param session   HTTP-sessionen där användaren sparas vid lyckad inloggning.
     * @param email     Användarens e-postadress.
     * @param password  Användarens lösenord i klartext.
     * @return true om inloggningen lyckades, annars false.
     */
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

    /**
     * Registrerar en ny användare.
     *
     * @param username  Önskat användarnamn.
     * @param email     E-postadress (måste vara unik).
     * @param password  Lösenord (minst 8 tecken).
     * @return true om registreringen lyckades, annars false.
     */
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

    /**
     * Tar emot en användares tips för en specifik omgång och liga.
     * Hämtar matchernas riktiga ID:n från databasen och mappar tipsen mot dem.
     *
     * @param session   HTTP-sessionen för att identifiera inloggad användare.
     * @param roundId   ID:t för den omgång kupongen gäller.
     * @param leagueId  ID:t för den liga kupongen tillhör.
     * @param tip1      Tips för match 1 ("1", "X" eller "2").
     * @param tip2      Tips för match 2 ("1", "X" eller "2").
     * @param tip3      Tips för match 3 ("1", "X" eller "2").
     * @param tip4      Tips för match 4 ("1", "X" eller "2").
     * @param tip5      Tips för match 5 ("1", "X" eller "2").
     * @param tip6      Tips för match 6 ("1", "X" eller "2").
     * @param tip7      Tips för match 7 ("1", "X" eller "2").
     * @param tip8      Tips för match 8 ("1", "X" eller "2").
     * @return bekräftelsemeddelande eller felmeddelande.
     */
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

    /**
     * Hämtar matchdata för en specifik omgång.
     *
     * @param roundId ID:t för omgången.
     * @return ett Round-objekt med matchinformation.
     */
    @GetMapping("/gameweek")
    public Round gameweekInfo(@RequestParam int roundId) {
        return roundService.getRound(roundId);
    }

    /**
     * Triggar en manuell uppdatering av matchdata från LiveScore API:et.
     *
     * @return bekräftelsesträng "OK".
     */
    @PostMapping("/updateGameweek")
    public String updateGameweek() {
        matchService.startAutoUpdate();
        return "OK";
    }

    // LEAGUE

    /**
     * Skapar en ny liga med automatiskt genererad invite-kod.
     * Returnerar false om användaren inte är inloggad, namnet är null eller ligan redan finns.
     *
     * @param session     HTTP-sessionen för att identifiera skaparen.
     * @param leagueName  Namnet på den nya ligan.
     * @return true om ligan skapades, annars false.
     */
    @GetMapping("/createLeague")
    public boolean createLeague(HttpSession session, @RequestParam String leagueName) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return false;
        }

        if (leagueName == null || leagueService.leagueExists(leagueName)) {
            return false;
        }

        leagueService.createLeague(leagueName, user.getId());
        return true;
    }

    /**
     * Låter en inloggad användare gå med i en liga via invite-kod.
     * Returnerar false om användaren inte är inloggad eller koden är ogiltig.
     *
     * @param session     HTTP-sessionen för att identifiera användaren.
     * @param inviteCode  Ligens unika invite-kod.
     * @return true om användaren gick med, annars false.
     */
    @GetMapping("/joinLeague")
    public boolean joinLeague(HttpSession session, @RequestParam String inviteCode) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return false;
        }

        return leagueService.joinLeague(inviteCode, user.getId());
    }

    /**
     * Hämtar alla ligor i systemet.
     *
     * @return en lista med alla League-objekt.
     */
    @GetMapping("/loadAllLeagues")
    public List<League> getAllLeaguesInfo() {
        return leagueService.getAllLeagues();
    }

    /**
     * Hämtar alla ligor som den inloggade användaren är med i.
     * Returnerar en tom lista om användaren inte är inloggad.
     *
     * @param session HTTP-sessionen för att identifiera användaren.
     * @return en lista med {@link League}-objekt som användaren tillhör.
     */
    @GetMapping("/loadPlayerLeagues")
    public List<League> getPlayerLeaguesInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return List.of();
        }

        return leagueService.getLeaguesByUserId(user.getId());
    }

    /**
     * Hämtar leaderboard för en specifik liga, sorterad på poäng.
     * Rättar automatiskt avslutade omgångar innan listan returneras.
     *
     * @param leagueId ID:t för ligan.
     * @return en lista med LeagueMember-objekt sorterade efter totalpoäng.
     */
    @GetMapping("/loadLeaderboard")
    public List<LeagueMember> getLeagueLeaderboard(@RequestParam int leagueId) {
        matchService.checkAndSettleFinishedRounds();
        return leagueService.getLeaderboard(leagueId);
    }

    // PROFILE

    /**
     * Hämtar alla kuponger för den inloggade användaren.
     *
     * @param session HTTP-sessionen för att identifiera användaren.
     * @return en lista med Coupon-objekt, eller tom lista om ingen är inloggad.
     */
    @GetMapping("/getCoupons")
    public List<Coupon> couponsList(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return List.of();
        }

        return couponService.getCouponsByUserId(user.getId());
    }

    /**
     * Hämtar detaljerad information om en specifik kupong, inklusive matchnamn och tips.
     *
     * @param couponId ID:t för kupongen.
     * @return en Map med kupongdetaljer (id, roundId, tips).
     */
    @GetMapping("/getCoupon")
    public Map<String, Object> getCoupon(@RequestParam int couponId) {
        return couponService.getCouponDetails(couponId);
    }

    /**
     * Hämtar profilinformation för den inloggade användaren.
     *
     * @param session HTTP-sessionen.
     * @return User-objektet från sessionen, eller null om ingen är inloggad.
     */
    @GetMapping("/userinfo")
    public User userInfo(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    /**
     * Loggar ut den inloggade användaren genom att invalidera sessionen.
     *
     * @param session HTTP-sessionen som ska avslutas.
     */
    @GetMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}